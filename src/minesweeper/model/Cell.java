package minesweeper.model;

public class Cell {
    // Nested enum for cell types
    public enum CellType {
        EMPTY, NUMBER, MINE, QUESTION, SURPRISE
    }
    
    private CellType type;
    private int adjacentMines;
    private boolean isRevealed;
    private boolean isFlagged;
    private boolean isUsed;

    public Cell() {
        this.type = CellType.EMPTY;
        this.adjacentMines = 0;
        this.isRevealed = false;
        this.isFlagged = false;
        this.isUsed = false;
    }

    // Getters and Setters
    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }
    
    public int getAdjacentMines() { return adjacentMines; }
    public void setAdjacentMines(int adjacentMines) { this.adjacentMines = adjacentMines; }
    
    public boolean isRevealed() { return isRevealed; }
    public void setRevealed(boolean revealed) { this.isRevealed = revealed; }
    
    public boolean isFlagged() { return isFlagged; }
    public void setFlagged(boolean flagged) { this.isFlagged = flagged; }
    
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { this.isUsed = used; }
}
