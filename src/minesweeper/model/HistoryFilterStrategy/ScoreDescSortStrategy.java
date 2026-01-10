package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;
import java.util.Comparator;

public class ScoreDescSortStrategy implements HistorySortStrategy {
    @Override
    public Comparator<GameHistory> comparator() {
        return (a, b) -> Integer.compare(b.getFinalScore(), a.getFinalScore());
    }
}
