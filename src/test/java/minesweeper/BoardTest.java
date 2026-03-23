package minesweeper;

import minesweeper.game.GameObserver;
import minesweeper.domain.board.Board;
import minesweeper.domain.cell.ICell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void initializeCreatesBoardWithRequestedDimensions() {
        board.initialize(4, 5, 3);

        assertEquals(4, board.getRows());
        assertEquals(5, board.getCols());
        assertEquals(3, board.getMineCount());
        assertNotNull(board.getCell(0, 0));
        assertNotNull(board.getCell(3, 4));
    }

    @Test
    void firstRevealIsAlwaysSafeAndPlacesExactMineCount() {
        board.initialize(3, 3, 4);

        board.revealCell(0, 0);

        assertFalse(board.getCell(0, 0).isMine());
        assertTrue(board.getCell(0, 0).isRevealed());
        assertEquals(4, countMines(board));
    }

    @Test
    void oneSafeCellBoardWinsOnFirstReveal() {
        board.initialize(1, 2, 1);

        board.revealCell(0, 0);

        assertTrue(board.isGameWon());
        assertFalse(board.isGameLost());
    }

    @Test
    void revealingRemainingMineLosesGame() {
        board.initialize(2, 2, 2);

        board.revealCell(0, 0);

        int mineRow = -1;
        int mineCol = -1;
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getCell(row, col).isMine()) {
                    mineRow = row;
                    mineCol = col;
                    break;
                }
            }
            if (mineRow != -1) {
                break;
            }
        }

        assertNotEquals(-1, mineRow);
        board.revealCell(mineRow, mineCol);

        assertTrue(board.isGameLost());
        assertFalse(board.isGameWon());
    }

    @Test
    void zeroMineBoardRevealsAllCellsFromFirstClick() {
        board.initialize(3, 3, 0);

        board.revealCell(1, 1);

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                assertTrue(board.getCell(row, col).isRevealed());
                assertEquals(0, board.getCell(row, col).getAdjacentMines());
            }
        }
        assertTrue(board.isGameWon());
    }

    @Test
    void adjacentMineCountIsComputedAfterFirstReveal() {
        board.initialize(2, 2, 3);

        board.revealCell(0, 0);

        ICell safeCell = board.getCell(0, 0);
        assertFalse(safeCell.isMine());
        assertEquals(3, safeCell.getAdjacentMines());
    }

    private int countMines(Board board) {
        int mines = 0;
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getCell(row, col).isMine()) {
                    mines++;
                }
            }
        }
        return mines;
    }

    @Test
    void addObserverNotifiesOnCellRevealed() {
        board.initialize(3, 3, 0);
        FakeObserver observer = new FakeObserver();
        board.addObserver(observer);
        board.revealCell(0, 0);
        assertTrue(observer.cellRevealedCalled);
    }

    @Test
    void addObserverIgnoresNull() {
        assertDoesNotThrow(() -> board.addObserver(null));
    }

    @Test
    void addObserverIgnoresDuplicates() {
        board.initialize(3, 3, 0);
        FakeObserver observer = new FakeObserver();
        board.addObserver(observer);
        board.addObserver(observer);
        board.revealCell(0, 0);
        assertTrue(observer.cellRevealedCount >= 1);
    }

    @Test
    void removeObserverStopsNotifications() {
        board.initialize(3, 3, 0);
        FakeObserver observer = new FakeObserver();
        board.addObserver(observer);
        board.removeObserver(observer);
        board.revealCell(0, 0);
        assertFalse(observer.cellRevealedCalled);
    }

    @Test
    void flagCellNotifiesObserverOnFlagPlaced() {
        board.initialize(3, 3, 0);
        FakeObserver observer = new FakeObserver();
        board.addObserver(observer);
        board.flagCell(0, 0);
        assertTrue(observer.flagPlacedCalled);
    }

    @Test
    void flagCellNotifiesObserverOnFlagRemoved() {
        board.initialize(3, 3, 0);
        FakeObserver observer = new FakeObserver();
        board.addObserver(observer);
        board.flagCell(0, 0);
        board.flagCell(0, 0);
        assertTrue(observer.flagRemovedCalled);
    }

    @Test
    void initializeThrowsOnNegativeRows() {
        assertThrows(IllegalArgumentException.class, () -> board.initialize(-1, 3, 0));
    }

    @Test
    void initializeThrowsOnTooManyMines() {
        assertThrows(IllegalArgumentException.class, () -> board.initialize(3, 3, 10));
    }

    @Test
    void revealThrowsWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> board.revealCell(0, 0));
    }

    @Test
    void revealThrowsOnOutOfBoundsPosition() {
        board.initialize(3, 3, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> board.revealCell(5, 5));
    }

    @Test
    void observerNotifiedOnGameLost() {
        board.initialize(2, 2, 3);
        FakeObserver observer = new FakeObserver();
        board.addObserver(observer);
        board.revealCell(0, 0);
        if (board.isGameLost()) {
            assertTrue(observer.gameLostCalled);
        }
    }

    @Test
    void observerNotifiedOnGameWon() {
        board.initialize(1, 2, 1);
        FakeObserver observer = new FakeObserver();
        board.addObserver(observer);
        board.revealCell(0, 0);
        if (board.isGameWon()) {
            assertTrue(observer.gameWonCalled);
        }
    }

    private static final class FakeObserver implements GameObserver {
        boolean cellRevealedCalled = false;
        int cellRevealedCount = 0;
        boolean flagPlacedCalled = false;
        boolean flagRemovedCalled = false;
        boolean gameWonCalled = false;
        boolean gameLostCalled = false;

        @Override
        public void onCellRevealed(int row, int col) {
            cellRevealedCalled = true;
            cellRevealedCount++;
        }

        @Override public void onGameWon() { gameWonCalled = true; }
        @Override public void onGameLost() { gameLostCalled = true; }
        @Override public void onFlagPlaced(int row, int col) { flagPlacedCalled = true; }
        @Override public void onFlagRemoved(int row, int col) { flagRemovedCalled = true; }
    }
}
