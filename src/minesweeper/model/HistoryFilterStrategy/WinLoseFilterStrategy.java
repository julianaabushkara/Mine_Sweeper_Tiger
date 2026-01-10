package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;

public class WinLoseFilterStrategy implements HistoryFilterStrategy {
    private final String mode; // "ALL", "WIN", "LOSE"

    public WinLoseFilterStrategy(String mode) {
        this.mode = mode;
    }

    @Override
    public boolean matches(GameHistory h) {
        if (mode == null || mode.isBlank() || "ALL".equalsIgnoreCase(mode)) return true;

        if ("WIN".equalsIgnoreCase(mode)) return h.isCoopWin();
        if ("LOSE".equalsIgnoreCase(mode)) return !h.isCoopWin();

        return true; // fallback
    }
}
