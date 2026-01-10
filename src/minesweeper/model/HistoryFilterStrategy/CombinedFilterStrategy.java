package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;
import java.util.List;

public class CombinedFilterStrategy implements HistoryFilterStrategy {

    private final List<HistoryFilterStrategy> filters;

    public CombinedFilterStrategy(List<HistoryFilterStrategy> filters) {
        this.filters = filters;
    }

    @Override
    public boolean matches(GameHistory h) {
        for (HistoryFilterStrategy f : filters) {
            if (f != null && !f.matches(h)) return false;
        }
        return true;
    }
}
