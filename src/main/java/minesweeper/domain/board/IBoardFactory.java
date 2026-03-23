package minesweeper.domain.board;

import minesweeper.domain.difficulty.IDifficulty;

public interface IBoardFactory {
    IBoard createBoard(IDifficulty difficulty);
}
