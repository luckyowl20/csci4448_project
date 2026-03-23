package minesweeper;

import minesweeper.domain.board.BoardFactory;
import minesweeper.domain.board.IBoard;
import minesweeper.domain.difficulty.EasyDifficulty;
import minesweeper.domain.difficulty.HardDifficulty;
import minesweeper.domain.timer.ITimer;
import minesweeper.game.GameController;
import minesweeper.game.GameObserver;
import minesweeper.game.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {
    private FakeTimer timer;
    private GameController controller;

    @BeforeEach
    void setUp() {
        timer = new FakeTimer();
        controller = new GameController(new BoardFactory(), timer, new EasyDifficulty());
    }

    @Test
    void controllerStartsWithoutActiveBoard() {
        assertFalse(controller.hasActiveGame());
        assertEquals(GameStatus.NOT_STARTED, controller.getStatus());
        assertEquals("Easy", controller.getCurrentDifficulty().getName());
    }

    @Test
    void startNewGameCreatesBoardUsingSelectedDifficulty() {
        controller.startNewGame(new HardDifficulty());

        IBoard board = controller.getBoard();
        assertTrue(controller.hasActiveGame());
        assertEquals(GameStatus.NOT_STARTED, controller.getStatus());
        assertEquals("Hard", controller.getCurrentDifficulty().getName());
        assertEquals(16, board.getRows());
        assertEquals(30, board.getCols());
    }

    @Test
    void firstFlagDoesNotStartTimerOrGameStatus() {
        controller.startNewGame();

        controller.flagCell(0, 0);

        assertEquals(GameStatus.NOT_STARTED, controller.getStatus());
        assertFalse(timer.running);
    }

    @Test
    void firstRevealStartsTimerAndGame() {
        controller.startNewGame();

        controller.revealCell(0, 0);

        assertTrue(timer.running || timer.stopCalls > 0);
        assertNotEquals(GameStatus.NOT_STARTED, controller.getStatus());
    }

    @Test
    void resetGameCreatesNewBoardAndResetsTimer() {
        controller.startNewGame();
        IBoard firstBoard = controller.getBoard();
        controller.revealCell(0, 0);

        controller.resetGame();

        assertNotSame(firstBoard, controller.getBoard());
        assertEquals(GameStatus.NOT_STARTED, controller.getStatus());
        assertEquals(2, timer.resetCalls);
        assertFalse(timer.running);
    }

    @Test
    void getElapsedTimeDelegatesToTimer() {
        timer.elapsed = 500;
        assertEquals(500, controller.getElapsedTime());
    }

    @Test
    void addObserverCallsOnGameStartedIfBoardExists() {
        controller.startNewGame();
        FakeObserver observer = new FakeObserver();
        controller.addObserver(observer);
        assertTrue(observer.gameStartedCalled);
    }

    @Test
    void addObserverDoesNotCallOnGameStartedIfNoBoardExists() {
        FakeObserver observer = new FakeObserver();
        controller.addObserver(observer);
        assertFalse(observer.gameStartedCalled);
    }

    @Test
    void addObserverIgnoresNull() {
        assertDoesNotThrow(() -> controller.addObserver(null));
    }

    @Test
    void addObserverIgnoresDuplicates() {
        controller.startNewGame();
        FakeObserver observer = new FakeObserver();
        controller.addObserver(observer);
        controller.addObserver(observer);
        assertEquals(1, observer.gameStartedCount);
    }

    @Test
    void defaultOnGameStartedDoesNothing() {
        GameObserver observer = new GameObserver() {
            @Override public void onCellRevealed(int row, int col) {}
            @Override public void onGameWon() {}
            @Override public void onGameLost() {}
            @Override public void onFlagPlaced(int row, int col) {}
            @Override public void onFlagRemoved(int row, int col) {}
        };
        assertDoesNotThrow(observer::onGameStarted);
    }

    private static final class FakeObserver implements GameObserver {
        boolean gameStartedCalled = false;
        int gameStartedCount = 0;

        @Override
        public void onGameStarted() {
            gameStartedCalled = true;
            gameStartedCount++;
        }

        @Override public void onCellRevealed(int row, int col) {}
        @Override public void onGameWon() {}
        @Override public void onGameLost() {}
        @Override public void onFlagPlaced(int row, int col) {}
        @Override public void onFlagRemoved(int row, int col) {}
    }

    private static final class FakeTimer implements ITimer {
        private boolean running;
        private long elapsed;
        private int resetCalls;
        private int stopCalls;

        @Override
        public void start() { running = true; }

        @Override
        public void stop() { running = false; stopCalls++; }

        @Override
        public void reset() { running = false; elapsed = 0; resetCalls++; }

        @Override
        public long getElapsed() { return elapsed; }
    }
}