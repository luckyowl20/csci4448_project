package minesweeper.domain.difficulty;

public class EasyDifficulty implements IDifficulty {
    private static final int ROWS = 9;
    private static final int COLS = 9;
    private static final int MINE_COUNT = 10;
    private static final String NAME = "Easy";

    @Override
    public int getRows() {
        return ROWS;
    }

    @Override
    public int getCols() {
        return COLS;
    }

    @Override
    public int getMineCount() {
        return MINE_COUNT;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
