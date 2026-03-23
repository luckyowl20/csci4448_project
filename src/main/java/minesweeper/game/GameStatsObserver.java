package minesweeper.game;

public class GameStatsObserver implements GameObserver {
    private int revealedCells;
    private int flagsPlaced;
    private int flagsRemoved;
    private boolean gameWon;
    private boolean gameLost;

    @Override
    public void onGameStarted() {
        revealedCells = 0;
        flagsPlaced = 0;
        flagsRemoved = 0;
        gameWon = false;
        gameLost = false;
    }

    @Override
    public void onCellRevealed(int row, int col) {
        revealedCells++;
    }

    @Override
    public void onGameWon() {
        gameWon = true;
    }

    @Override
    public void onGameLost() {
        gameLost = true;
    }

    @Override
    public void onFlagPlaced(int row, int col) {
        flagsPlaced++;
    }

    @Override
    public void onFlagRemoved(int row, int col) {
        flagsRemoved++;
    }

    public int getRevealedCells() {
        return revealedCells;
    }

    public int getFlagsPlaced() {
        return flagsPlaced;
    }

    public int getFlagsRemoved() {
        return flagsRemoved;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameLost() {
        return gameLost;
    }
}
