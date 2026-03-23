package minesweeper.api.dto;

import java.util.List;

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
