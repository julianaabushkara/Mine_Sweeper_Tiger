package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;
import java.util.Comparator;

public class ScoreAscSortStrategy implements HistorySortStrategy {
    @Override
    public Comparator<GameHistory> comparator() {
        return Comparator.comparingInt(GameHistory::getFinalScore);
    }
}
