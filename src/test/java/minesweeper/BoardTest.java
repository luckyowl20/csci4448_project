package minesweeper;

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
}
