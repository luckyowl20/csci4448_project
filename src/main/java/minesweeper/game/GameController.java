package minesweeper.game;

import minesweeper.domain.board.IBoard;
import minesweeper.domain.board.IBoardFactory;
import minesweeper.domain.cell.ICell;
import minesweeper.domain.difficulty.IDifficulty;
import minesweeper.domain.timer.ITimer;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final IBoardFactory boardFactory;
    private final ITimer timer;
    private final List<GameObserver> observers;
    private IDifficulty currentDifficulty;
    private IBoard currentBoard;
    private GameStatus status;

    public GameController(IBoardFactory boardFactory, ITimer timer, IDifficulty defaultDifficulty) {
        this(boardFactory, timer, defaultDifficulty, List.of());
    }

    public GameController(
            IBoardFactory boardFactory,
            ITimer timer,
            IDifficulty defaultDifficulty,
            List<GameObserver> observers
    ) {
        if (boardFactory == null) {
            throw new IllegalArgumentException("Board factory is required.");
        }
        if (timer == null) {
            throw new IllegalArgumentException("Timer is required.");
        }
        if (defaultDifficulty == null) {
            throw new IllegalArgumentException("Default difficulty is required.");
        }

        this.boardFactory = boardFactory;
        this.timer = timer;
        this.observers = new ArrayList<>(observers == null ? List.of() : observers);
        this.currentDifficulty = defaultDifficulty;
        this.status = GameStatus.NOT_STARTED;
    }

    public void startNewGame() {
        startNewGame(currentDifficulty);
    }

    public void startNewGame(IDifficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty is required.");
        }

        currentDifficulty = difficulty;
        currentBoard = boardFactory.createBoard(difficulty);
        registerObservers(currentBoard);
        timer.reset();
        status = GameStatus.NOT_STARTED;
    }

    public void resetGame() {
        ensureDifficultySelected();
        startNewGame(currentDifficulty);
    }

    public void revealCell(int row, int col) {
        ensureActiveBoard();

        if (isGameOver()) {
            return;
        }

        ICell cell = currentBoard.getCell(row, col);
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }

        if (status == GameStatus.NOT_STARTED) {
            timer.start();
            status = GameStatus.IN_PROGRESS;
        }

        currentBoard.revealCell(row, col);
        updateStatusAfterBoardAction();
    }

    public void flagCell(int row, int col) {
        ensureActiveBoard();

        if (isGameOver()) {
            return;
        }

        currentBoard.flagCell(row, col);
    }

    public IBoard getBoard() {
        return currentBoard;
    }

    public IDifficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public GameStatus getStatus() {
        return status;
    }

    public long getElapsedTime() {
        return timer.getElapsed();
    }

    public boolean hasActiveGame() {
        return currentBoard != null;
    }

    public void addObserver(GameObserver observer) {
        if (observer == null || observers.contains(observer)) {
            return;
        }

        observers.add(observer);
        if (currentBoard != null) {
            observer.onGameStarted();
            currentBoard.addObserver(observer);
        }
    }

    private boolean isGameOver() {
        return status == GameStatus.WON || status == GameStatus.LOST;
    }

    private void updateStatusAfterBoardAction() {
        if (currentBoard.isGameLost()) {
            timer.stop();
            status = GameStatus.LOST;
        } else if (currentBoard.isGameWon()) {
            timer.stop();
            status = GameStatus.WON;
        }
    }

    private void ensureDifficultySelected() {
        if (currentDifficulty == null) {
            throw new IllegalStateException("No difficulty has been selected.");
        }
    }

    private void ensureActiveBoard() {
        if (currentBoard == null) {
            throw new IllegalStateException("No game has been started.");
        }
    }

    private void registerObservers(IBoard board) {
        for (GameObserver observer : observers) {
            observer.onGameStarted();
            board.addObserver(observer);
        }
    }
}
