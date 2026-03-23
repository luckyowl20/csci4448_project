package minesweeper;

import minesweeper.domain.board.BoardFactory;
import minesweeper.domain.board.IBoard;
import minesweeper.domain.board.IBoardFactory;
import minesweeper.domain.difficulty.EasyDifficulty;
import minesweeper.domain.difficulty.HardDifficulty;
import minesweeper.domain.difficulty.IDifficulty;
import minesweeper.domain.timer.ITimer;
import minesweeper.game.GameController;
import minesweeper.game.GameStatsObserver;
import minesweeper.game.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void startNewGameUsesInjectedFactoryAbstraction() {
        RecordingBoardFactory factory = new RecordingBoardFactory();
        GameController injectedController = new GameController(factory, timer, new EasyDifficulty());

        injectedController.startNewGame(new HardDifficulty());

        assertSame(factory.createdBoard, injectedController.getBoard());
        assertEquals("Hard", factory.lastDifficultyName);
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
    void observersReceiveBoardEventsAcrossControllerActions() {
        GameStatsObserver observer = new GameStatsObserver();
        GameController observedController = new GameController(
                new BoardFactory(),
                timer,
                new EasyDifficulty(),
                List.of(observer)
        );
        IDifficulty tinyDifficulty = new FixedDifficulty(1, 2, 1, "Tiny");

        observedController.startNewGame(tinyDifficulty);
        observedController.flagCell(0, 1);
        observedController.flagCell(0, 1);
        observedController.revealCell(0, 0);

        assertEquals(1, observer.getFlagsPlaced());
        assertEquals(1, observer.getFlagsRemoved());
        assertTrue(observer.getRevealedCells() >= 1);
        assertTrue(observer.isGameWon());
        assertFalse(observer.isGameLost());
    }

    private static final class FakeTimer implements ITimer {
        private boolean running;
        private long elapsed;
        private int resetCalls;
        private int stopCalls;

        @Override
        public void start() {
            running = true;
        }

        @Override
        public void stop() {
            running = false;
            stopCalls++;
        }

        @Override
        public void reset() {
            running = false;
            elapsed = 0;
            resetCalls++;
        }

        @Override
        public long getElapsed() {
            return elapsed;
        }
    }

    private static final class RecordingBoardFactory implements IBoardFactory {
        private IBoard createdBoard;
        private String lastDifficultyName;

        @Override
        public IBoard createBoard(IDifficulty difficulty) {
            lastDifficultyName = difficulty.getName();
            BoardFactory delegate = new BoardFactory();
            createdBoard = delegate.createBoard(difficulty);
            return createdBoard;
        }
    }

    private record FixedDifficulty(int rows, int cols, int mineCount, String name) implements IDifficulty {
        @Override
        public int getRows() {
            return rows;
        }

        @Override
        public int getCols() {
            return cols;
        }

        @Override
        public int getMineCount() {
            return mineCount;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
