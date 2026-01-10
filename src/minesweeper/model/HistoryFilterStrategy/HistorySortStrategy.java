package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;
import java.util.Comparator;

public interface HistorySortStrategy {
    Comparator<GameHistory> comparator();
}
