package minesweeper;

import minesweeper.domain.board.Board;
import minesweeper.game.GameStatsObserver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardObserverTest {
    @Test
    void observerTracksFlagRevealAndWinEvents() {
        Board board = new Board();
        GameStatsObserver observer = new GameStatsObserver();
        board.addObserver(observer);
        observer.onGameStarted();
        board.initialize(1, 2, 1);

        board.flagCell(0, 1);
        board.flagCell(0, 1);
        board.revealCell(0, 0);

        assertEquals(1, observer.getFlagsPlaced());
        assertEquals(1, observer.getFlagsRemoved());
        assertEquals(1, observer.getRevealedCells());
        assertTrue(observer.isGameWon());
        assertFalse(observer.isGameLost());
    }
}
