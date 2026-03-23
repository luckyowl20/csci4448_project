package minesweeper.api.dto;

public record CellStateResponse(
        int row,
        int col,
        boolean revealed,
        boolean flagged,
        int adjacentMines
) {
}
