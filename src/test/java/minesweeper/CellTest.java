package minesweeper;

import minesweeper.domain.cell.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    private Cell cell;

    @BeforeEach
    void setup() {
        cell = new Cell();
    }

    @Test
    void testDefaultStateNotMine() {
        assertFalse(cell.isMine());
    }

    @Test
    void testDefaultStateNotRevealed() {
        assertFalse(cell.isRevealed());
    }

    @Test
    void testDefaultStateNotFlagged() {
        assertFalse(cell.isFlagged());
    }

    @Test
    void testRevealCell() {
        cell.reveal();
        assertTrue(cell.isRevealed());
    }

    @Test
    void testCannotRevealFlaggedCell() {
        cell.toggleFlag();
        cell.reveal();
        assertFalse(cell.isRevealed());
    }

    @Test
    void testToggleFlag() {
        cell.toggleFlag();
        assertTrue(cell.isFlagged());
    }

    @Test
    void testToggleFlagOff() {
        cell.toggleFlag();
        cell.toggleFlag();
        assertFalse(cell.isFlagged());
    }

    @Test
    void testCannotFlagRevealedCell() {
        cell.reveal();
        cell.toggleFlag();
        assertFalse(cell.isFlagged());
    }

    @Test
    void testSetMine() {
        cell.setMine(true);
        assertTrue(cell.isMine());
    }

    @Test
    void testSetAdjacentMines() {
        cell.setAdjacentMines(3);
        assertEquals(3, cell.getAdjacentMines());
    }
}
