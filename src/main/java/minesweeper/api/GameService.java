package minesweeper.api;

import java.util.ArrayList;
import java.util.List;
import minesweeper.api.dto.CellStateResponse;
import minesweeper.api.dto.DifficultyOptionResponse;
import minesweeper.api.dto.GameStateResponse;
import minesweeper.domain.board.IBoard;
import minesweeper.domain.cell.ICell;
import minesweeper.domain.difficulty.EasyDifficulty;
import minesweeper.domain.difficulty.HardDifficulty;
import minesweeper.domain.difficulty.IDifficulty;
import minesweeper.domain.difficulty.MediumDifficulty;
import minesweeper.game.GameController;

/**
 * Service layer for the HTTP API.
 *
 * <p>This class sits between the Javalin route handlers and the underlying game/domain
 * objects. It exists to keep route handlers small and to centralize how backend state is
 * translated into frontend-facing JSON responses.
 *
 * <p>This class should contain:
 * <ul>
 *     <li>request-to-domain orchestration</li>
 *     <li>difficulty lookup by name</li>
 *     <li>response shaping for the frontend</li>
 * </ul>
 *
 * <p>This class should not contain low-level Minesweeper rules. Those belong in the board
 * and game layers.
 */
public class GameService {
    private final GameController controller;
    private final List<IDifficulty> difficulties;

    /**
     * Creates the service with the controller that owns current game state.
     *
     * @param controller the application controller used by all API actions
     */
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

    /**
     * Returns the current game snapshot for the UI.
     *
     * @return the full frontend-facing game state
     */
    public GameStateResponse getState() {
        return toResponse();
    }

    /**
     * Starts a new game using a difficulty name supplied by the client.
     *
     * @param difficultyName difficulty label from the HTTP request body
     * @return the updated game state after a new board is created
     */
    public GameStateResponse startGame(String difficultyName) {
        controller.startNewGame(resolveDifficulty(difficultyName));
        return toResponse();
    }

    /**
     * Resets the current game while keeping the currently selected difficulty.
     *
     * @return the updated game state after reset
     */
    public GameStateResponse resetGame() {
        controller.resetGame();
        return toResponse();
    }

    /**
     * Reveals a single cell and returns the resulting game state.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return the updated game state after the reveal
     */
    public GameStateResponse revealCell(int row, int col) {
        controller.revealCell(row, col);
        return toResponse();
    }

    /**
     * Toggles a flag on a single cell and returns the resulting game state.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return the updated game state after the flag action
     */
    public GameStateResponse flagCell(int row, int col) {
        controller.flagCell(row, col);
        return toResponse();
    }

    /**
     * Resolves a user-facing difficulty name into a concrete difficulty strategy.
     *
     * <p>If you later add a new difficulty, update the {@code difficulties} list in the
     * constructor and this method will pick it up automatically.
     *
     * @param difficultyName the difficulty name supplied by the UI
     * @return the matching difficulty strategy
     */
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

    /**
     * Builds the main response payload returned to the frontend.
     *
     * <p>If the UI needs more state, this is the main method to extend. Add a new field to
     * {@link GameStateResponse}, then populate it here.
     *
     * @return a snapshot of the current game that is easy for the UI to render
     */
    private GameStateResponse toResponse() {
        IBoard board = controller.getBoard();

        return new GameStateResponse(
                controller.getCurrentDifficulty().getName(),
                controller.hasActiveGame(),
                controller.getStatus().name(),
                controller.getElapsedTime(),
                board != null ? board.getRows() : 0,
                board != null ? board.getCols() : 0,
                board != null ? board.getMineCount() : 0,
                buildCellResponses(board),
                buildDifficultyOptions()
        );
    }

    /**
     * Flattens the board into a simple list of cell DTOs.
     *
     * <p>The backend stores the board as a 2D structure, but the UI only needs a predictable
     * JSON representation it can iterate over.
     *
     * @param board the current board, if a game has been started
     * @return a frontend-friendly list of cells
     */
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
                        cell.getAdjacentMines(),
                        cell.isMine()
                ));
            }
        }

        return cells;
    }

    /**
     * Returns the list of available difficulties for the UI.
     *
     * <p>This keeps difficulty metadata defined on the backend instead of duplicated in the
     * frontend.
     *
     * @return all difficulty options exposed by the API
     */
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
