package minesweeper.observer;

import minesweeper.model.Cell;
import java.util.*;


public interface BoardObserver {
	
	
	void onCellRevealed(int row, int col, Cell cell);
    void onGameOver(boolean won);
    void onScoreChanged(int newScore);

}
