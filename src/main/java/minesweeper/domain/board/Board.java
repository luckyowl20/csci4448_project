package minesweeper.domain.board;

import java.util.ArrayList;
import java.util.List;
import minesweeper.domain.cell.Cell;
import minesweeper.domain.cell.ICell;
import minesweeper.game.GameObserver;

public class Board implements IBoard {
    private int rows;
    private int cols;
    private int mineCount;
    private ICell[][] cells; // Game board containing all the mines / cells
    private boolean gameWon;
    private boolean gameLost;
    private final List<GameObserver> observers = new ArrayList<>();

    @Override
    public void initialize(int rows, int cols, int mineCount) {
        validateDimensions(rows, cols, mineCount);

        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
        this.cells = new ICell[rows][cols];
        this.gameWon = false;
        this.gameLost = false;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                cells[row][col] = new Cell();
            }
        }
    }

    @Override
    public void revealCell(int row, int col) {
        ensureInitialized();
        validatePosition(row, col);

        if (gameWon || gameLost) {
            return;
        }

        ICell cell = cells[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }

        cell.reveal();
        notifyCellRevealed(row, col);

        if (cell.isMine()) {
            gameLost = true;
            notifyGameLost();
            return;
        }

        if (hasWon()) {
            gameWon = true;
            notifyGameWon();
        }
    }

    @Override
    public void flagCell(int row, int col) {
        ensureInitialized();
        validatePosition(row, col);

        if (gameWon || gameLost) {
            return;
        }

        ICell cell = cells[row][col];
        boolean wasFlagged = cell.isFlagged();
        cell.toggleFlag();

        if (cell.isFlagged() && !wasFlagged) {
            notifyFlagPlaced(row, col);
        } else if (!cell.isFlagged() && wasFlagged) {
            notifyFlagRemoved(row, col);
        }
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public boolean isGameLost() {
        return gameLost;
    }

    @Override
    public ICell getCell(int row, int col) {
        ensureInitialized();
        validatePosition(row, col);
        return cells[row][col];
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public int getMineCount() {
        return mineCount;
    }

    @Override
    public void addObserver(GameObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void validateDimensions(int rows, int cols, int mineCount) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive.");
        }

        if (mineCount < 0 || mineCount > rows * cols) {
            throw new IllegalArgumentException("Mine count must fit within the board.");
        }
    }

    private void validatePosition(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new IndexOutOfBoundsException("Cell position is outside the board.");
        }
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    private void ensureInitialized() {
        if (cells == null) {
            throw new IllegalStateException("Board has not been initialized.");
        }
    }

    private boolean hasWon() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ICell cell = cells[row][col];
                if (!cell.isMine() && !cell.isRevealed()) {
                    return false;
                }
            }
        }

        return true;
    }

    private void notifyCellRevealed(int row, int col) {
        for (GameObserver observer : observers) {
            observer.onCellRevealed(row, col);
        }
    }

    private void notifyGameWon() {
        for (GameObserver observer : observers) {
            observer.onGameWon();
        }
    }

    private void notifyGameLost() {
        for (GameObserver observer : observers) {
            observer.onGameLost();
        }
    }

    private void notifyFlagPlaced(int row, int col) {
        for (GameObserver observer : observers) {
            observer.onFlagPlaced(row, col);
        }
    }

    private void notifyFlagRemoved(int row, int col) {
        for (GameObserver observer : observers) {
            observer.onFlagRemoved(row, col);
        }
    }
}
