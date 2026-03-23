public interface ICell {
    boolean isMine();
    boolean isRevealed();
    boolean isFlagged();
    int getAdjacentMines();
    void reveal();
    void toggleFlag();
    void setMine(boolean mine);
    void setAdjacentMines(int count);
}