public interface IBoard {
    void intialize(int rows, int cols, int mineCount);
    void revealCell(int row, int col);
    void flagCell(int row, int col);
    boolean isGameWon();
    boolean isGameLost();
    ICell getCell(int row, int col);
    int getRows();
    int getCols();
    void addObserver(GameObserver observer);
    void removeObserver(GameObserver observer);
}
