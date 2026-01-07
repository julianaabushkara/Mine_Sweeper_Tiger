
package minesweeper.model;

import java.util.*;

import minesweeper.observer.BoardObserver;

public class Board {
    private int size;
    private Cell[][] cells;
    private int totalMines;
    private int revealedMines;
    private List<BoardObserver> observers;
    private int currentScore;
    

    public Board(int size, int mineCount, int questionCount, int surpriseCount) {
        this.size = size;
        this.totalMines = mineCount;
        this.revealedMines = 0;
        this.cells = new Cell[size][size];

        
        this.observers = new ArrayList<>();
        this.currentScore = 0;
        
        // Initialize cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
        
        

        // Generate board
        placeMines(mineCount);
        calculateNumbers();
        placeSpecialTiles(questionCount, surpriseCount);
    }
    
    
    
    public void addObserver(BoardObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(BoardObserver observer) {
        observers.remove(observer);
    }
    
   
    
    public void notifyCellRevealed(int row, int col) {
        Cell cell = cells[row][col];
        for (BoardObserver observer : observers) {
            observer.onCellRevealed(row, col, cell);
        }
    }
    
    public void notifyGameOver(boolean won) {
        for (BoardObserver observer : observers) {
            observer.onGameOver(won);
        }
    }
    
    public void notifyScoreChanged(int points) {
        currentScore += points;
        for (BoardObserver observer : observers) {
            observer.onScoreChanged(currentScore);
        }
    }
    
    public int getCurrentScore() {
        return currentScore;
    }
    
    
    
    
    

    private void placeMines(int count) {
        Random rand = new Random();
        int placed = 0;
        
        while (placed < count) {
            int row = rand.nextInt(size);
            int col = rand.nextInt(size);
            
            if (cells[row][col].getType() != Cell.CellType.MINE) {
                cells[row][col].setType(Cell.CellType.MINE);
                placed++;
            }
        }
    }

    private void calculateNumbers() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (cells[row][col].getType() != Cell.CellType.MINE) {
                    int count = countAdjacentMines(row, col);
                    cells[row][col].setAdjacentMines(count);
                    if (count > 0) {
                        cells[row][col].setType(Cell.CellType.NUMBER);
                    }
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int newRow = row + dr;
                int newCol = col + dc;
                if (isValid(newRow, newCol) && cells[newRow][newCol].getType() == Cell.CellType.MINE) {
                    count++;
                }
            }
        }
        return count;
    }

    private void placeSpecialTiles(int questionCount, int surpriseCount) {
        List<int[]> emptyPositions = new ArrayList<>();
        
        // Find all empty tiles
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (cells[row][col].getType() == Cell.CellType.EMPTY) {
                    emptyPositions.add(new int[]{row, col});
                }
            }
        }

        // Place question tiles
        Collections.shuffle(emptyPositions);
        for (int i = 0; i < Math.min(questionCount, emptyPositions.size()); i++) {
            int[] pos = emptyPositions.get(i);
            cells[pos[0]][pos[1]].setType(Cell.CellType.QUESTION);
        }

        // Place surprise tiles
        emptyPositions.removeIf(pos -> cells[pos[0]][pos[1]].getType() == Cell.CellType.QUESTION);
        Collections.shuffle(emptyPositions);
        for (int i = 0; i < Math.min(surpriseCount, emptyPositions.size()); i++) {
            int[] pos = emptyPositions.get(i);
            cells[pos[0]][pos[1]].setType(Cell.CellType.SURPRISE);
        }
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    // Getters
    public int getSize() { return size; }
    public Cell[][] getCells() { return cells; }
    public Cell getCell(int row, int col) { return cells[row][col]; }
    public int getTotalMines() { return totalMines; }
    public int getRevealedMines() { return revealedMines; }
    public void incrementRevealedMines() { revealedMines++; }
}
