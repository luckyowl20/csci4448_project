public class GameController {
    private final BoardFactory boardFactory;
    private final ITimer timer;
    private IDifficulty currentDifficulty;
    private IBoard currentBoard;
    private GameStatus status;

    public GameController(BoardFactory boardFactory, ITimer timer, IDifficulty defaultDifficulty) {
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
}
