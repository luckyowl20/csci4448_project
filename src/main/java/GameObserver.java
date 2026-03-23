public interface GameObserver {
    void onCellRevealed(int row, int col);
    void onGameWon();
    void onGameLost();
    void onFlagPlaced(int row, int col);
    void onFlagRemoved(int row, int col);
}
