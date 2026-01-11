
package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;

public interface HistoryFilterStrategy {
    boolean matches(GameHistory h);
}
