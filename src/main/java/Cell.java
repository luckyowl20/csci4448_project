public class Cell implements ICell {
    private boolean mine;
    private boolean revealed;
    private boolean flagged;
    private int adjacentMines;

    public Cell() {
        this.mine = false;
        this.revealed = false;
        this.flagged = false;
        this.adjacentMines = 0;
    }

    @Override
    public boolean isMine() {
        return mine;
    }

    @Override
    public boolean isRevealed() {
        return revealed;
    }

    @Override
    public boolean isFlagged() {
        return flagged;
    }

    @Override
    public int getAdjacentMines() {
        return adjacentMines;
    }

    @Override
    public void reveal() {
        if (!flagged) {
            revealed = true;
        }
    }

    @Override
    public void toggleFlag() {
        if (!revealed) {
           flagged = !flagged;
        }
    }

    @Override
    public void setMine(boolean mine) {
        this.mine = mine;
    }

    @Override
    public void setAdjacentMines(int count) {
        this.adjacentMines = count;
    }
}
