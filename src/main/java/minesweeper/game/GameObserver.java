package minesweeper.game;

public interface GameObserver {
    default void onGameStarted() {
    }

    void onCellRevealed(int row, int col);
    void onGameWon();
    void onGameLost();
    void onFlagPlaced(int row, int col);
    void onFlagRemoved(int row, int col);
}
