import java.util.ArrayList;
import java.util.List;

public class GameService {
    private final GameController controller;
    private final List<IDifficulty> difficulties;

    public GameService(GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Game controller is required.");
        }

        this.controller = controller;
        this.difficulties = List.of(
                new EasyDifficulty(),
                new MediumDifficulty(),
                new HardDifficulty()
        );
    }

    public GameStateResponse getState() {
        return toResponse();
    }

    public GameStateResponse startGame(String difficultyName) {
        controller.startNewGame(resolveDifficulty(difficultyName));
        return toResponse();
    }

    public GameStateResponse resetGame() {
        controller.resetGame();
        return toResponse();
    }

    public GameStateResponse revealCell(int row, int col) {
        controller.revealCell(row, col);
        return toResponse();
    }

    public GameStateResponse flagCell(int row, int col) {
        controller.flagCell(row, col);
        return toResponse();
    }

    private IDifficulty resolveDifficulty(String difficultyName) {
        if (difficultyName == null || difficultyName.isBlank()) {
            throw new IllegalArgumentException("Difficulty is required.");
        }

        for (IDifficulty difficulty : difficulties) {
            if (difficulty.getName().equalsIgnoreCase(difficultyName)) {
                return difficulty;
            }
        }

        throw new IllegalArgumentException("Unknown difficulty: " + difficultyName);
    }

    private GameStateResponse toResponse() {
        IBoard board = controller.getBoard();

        return new GameStateResponse(
                controller.getCurrentDifficulty().getName(),
                controller.hasActiveGame(),
                controller.getStatus().name(),
                controller.getElapsedTime(),
                board != null ? board.getRows() : 0,
                board != null ? board.getCols() : 0,
                board instanceof Board concreteBoard ? concreteBoard.getMineCount() : 0,
                buildCellResponses(board),
                buildDifficultyOptions()
        );
    }

    private List<CellStateResponse> buildCellResponses(IBoard board) {
        List<CellStateResponse> cells = new ArrayList<>();
        if (board == null) {
            return cells;
        }

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                ICell cell = board.getCell(row, col);
                cells.add(new CellStateResponse(
                        row,
                        col,
                        cell.isRevealed(),
                        cell.isFlagged(),
                        cell.getAdjacentMines()
                ));
            }
        }

        return cells;
    }

    private List<DifficultyOptionResponse> buildDifficultyOptions() {
        return difficulties.stream()
                .map(difficulty -> new DifficultyOptionResponse(
                        difficulty.getName(),
                        difficulty.getRows(),
                        difficulty.getCols(),
                        difficulty.getMineCount()
                ))
                .toList();
    }
}
