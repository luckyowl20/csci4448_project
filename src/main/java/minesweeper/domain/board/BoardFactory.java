package minesweeper.domain.board;

import minesweeper.domain.difficulty.IDifficulty;

public class BoardFactory implements IBoardFactory {
    @Override
    public IBoard createBoard(IDifficulty difficulty) {
        Board board = new Board();
        board.initialize(difficulty.getRows(), difficulty.getCols(), difficulty.getMineCount());
        return board;
    }
}
