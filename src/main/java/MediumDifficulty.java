public class MediumDifficulty implements IDifficulty {
    private static final int ROWS = 16;
    private static final int COLS = 16;
    private static final int MINE_COUNT = 40;
    private static final String NAME = "Medium";

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
