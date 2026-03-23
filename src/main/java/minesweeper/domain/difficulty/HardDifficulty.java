package minesweeper.domain.difficulty;

public class HardDifficulty implements IDifficulty {
    private static final int ROWS = 16;
    private static final int COLS = 30;
    private static final int MINE_COUNT = 99;
    private static final String NAME = "Hard";

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
