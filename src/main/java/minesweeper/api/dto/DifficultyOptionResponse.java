package minesweeper.api.dto;

public record DifficultyOptionResponse(
        String name,
        int rows,
        int cols,
        int mineCount
) {
}
