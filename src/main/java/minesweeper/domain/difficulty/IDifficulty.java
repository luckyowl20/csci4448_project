package minesweeper.domain.difficulty;

public interface IDifficulty {
    int getRows();
    int getCols();
    int getMineCount();
    String getName();
}
