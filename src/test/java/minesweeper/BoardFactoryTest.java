package minesweeper;

import minesweeper.domain.board.BoardFactory;
import minesweeper.domain.board.IBoard;
import minesweeper.domain.difficulty.EasyDifficulty;
import minesweeper.domain.difficulty.MediumDifficulty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardFactoryTest {
    @Test
    void createBoardUsesDifficultyDimensions() {
        BoardFactory factory = new BoardFactory();

        IBoard board = factory.createBoard(new EasyDifficulty());

        assertEquals(9, board.getRows());
        assertEquals(9, board.getCols());
        assertNotNull(board.getCell(0, 0));
    }

    @Test
    void createBoardReturnsFreshBoardInstances() {
        BoardFactory factory = new BoardFactory();

        IBoard firstBoard = factory.createBoard(new MediumDifficulty());
        IBoard secondBoard = factory.createBoard(new MediumDifficulty());

        assertNotSame(firstBoard, secondBoard);
    }
}
