public class BoardFactory {
    public IBoard createBoard(IDifficulty difficulty) {
        Board board = new Board();
        board.initialize(difficulty.getRows(), difficulty.getCols(), difficulty.getMineCount());
        return board;
    }
}
