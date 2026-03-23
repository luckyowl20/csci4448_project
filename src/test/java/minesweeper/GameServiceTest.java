package minesweeper;

import minesweeper.api.GameService;
import minesweeper.api.dto.GameStateResponse;
import minesweeper.domain.board.BoardFactory;
import minesweeper.domain.difficulty.EasyDifficulty;
import minesweeper.domain.timer.GameTimer;
import minesweeper.game.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService service;

    @BeforeEach
    void setUp() {
        GameTimer.INSTANCE.reset();
        service = new GameService(
                new GameController(new BoardFactory(), GameTimer.INSTANCE, new EasyDifficulty())
        );
    }

    @Test
    void constructorThrowsOnNullController() {
        assertThrows(IllegalArgumentException.class, () -> new GameService(null));
    }

    @Test
    void getStateReturnsInitialState() {
        GameStateResponse state = service.getState();
        assertFalse(state.activeGame());
        assertEquals("Easy", state.selectedDifficulty());
        assertEquals("NOT_STARTED", state.status());
        assertEquals(3, state.difficulties().size());
    }

    @Test
    void startGameWithEasyDifficulty() {
        GameStateResponse state = service.startGame("Easy");
        assertTrue(state.activeGame());
        assertEquals("Easy", state.selectedDifficulty());
        assertEquals(9, state.rows());
        assertEquals(9, state.cols());
        assertEquals(10, state.mineCount());
    }

    @Test
    void startGameWithMediumDifficulty() {
        GameStateResponse state = service.startGame("Medium");
        assertEquals("Medium", state.selectedDifficulty());
        assertEquals(16, state.rows());
        assertEquals(16, state.cols());
        assertEquals(40, state.mineCount());
    }

    @Test
    void startGameWithHardDifficulty() {
        GameStateResponse state = service.startGame("Hard");
        assertEquals("Hard", state.selectedDifficulty());
        assertEquals(16, state.rows());
        assertEquals(30, state.cols());
        assertEquals(99, state.mineCount());
    }

    @Test
    void startGameIsCaseInsensitive() {
        GameStateResponse state = service.startGame("easy");
        assertEquals("Easy", state.selectedDifficulty());
    }

    @Test
    void startGameThrowsOnNullDifficulty() {
        assertThrows(IllegalArgumentException.class, () -> service.startGame(null));
    }

    @Test
    void startGameThrowsOnBlankDifficulty() {
        assertThrows(IllegalArgumentException.class, () -> service.startGame("  "));
    }

    @Test
    void startGameThrowsOnUnknownDifficulty() {
        assertThrows(IllegalArgumentException.class, () -> service.startGame("Extreme"));
    }

    @Test
    void resetGameReturnsNewBoard() {
        service.startGame("Easy");
        GameStateResponse state = service.resetGame();
        assertTrue(state.activeGame());
        assertEquals("NOT_STARTED", state.status());
    }

    @Test
    void revealCellUpdatesState() {
        service.startGame("Easy");
        GameStateResponse state = service.revealCell(0, 0);
        assertNotEquals("NOT_STARTED", state.status());
    }

    @Test
    void flagCellUpdatesState() {
        service.startGame("Easy");
        GameStateResponse state = service.flagCell(0, 0);
        assertTrue(state.cells().stream().anyMatch(c -> c.row() == 0 && c.col() == 0 && c.flagged()));
    }

    @Test
    void getStateReturnsDifficultyOptions() {
        GameStateResponse state = service.getState();
        assertTrue(state.difficulties().stream().anyMatch(d -> d.name().equals("Easy")));
        assertTrue(state.difficulties().stream().anyMatch(d -> d.name().equals("Medium")));
        assertTrue(state.difficulties().stream().anyMatch(d -> d.name().equals("Hard")));
    }
}