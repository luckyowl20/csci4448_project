package minesweeper.api.dto;

import java.util.List;

/**
 * Main response object returned to the frontend.
 *
 * <p>The Svelte UI renders almost entirely from this DTO. If the frontend needs additional
 * backend state, add a field here and populate it in {@code GameService#toResponse()}.
 *
 * @param selectedDifficulty the currently selected difficulty name
 * @param activeGame whether a board has been created and is currently available
 * @param status current controller/game status
 * @param elapsedTime elapsed time in seconds
 * @param rows board row count
 * @param cols board column count
 * @param mineCount configured number of mines for the board
 * @param cells flattened list of cells for the UI to render
 * @param difficulties available difficulty options for menu rendering
 */
public record GameStateResponse(
        String selectedDifficulty,
        boolean activeGame,
        String status,
        long elapsedTime,
        int rows,
        int cols,
        int mineCount,
        List<CellStateResponse> cells,
        List<DifficultyOptionResponse> difficulties
) {
}
