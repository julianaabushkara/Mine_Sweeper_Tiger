package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;

public class UserFilterStrategy implements HistoryFilterStrategy {
    private final String username; // "ALL" or specific

    public UserFilterStrategy(String username) {
        this.username = username;
    }

    @Override
    public boolean matches(GameHistory h) {
        if (username == null || username.isBlank() || "ALL".equalsIgnoreCase(username)) return true;
        return username.equalsIgnoreCase(h.getUsername());
    }
}
