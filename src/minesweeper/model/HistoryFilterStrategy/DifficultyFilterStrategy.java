package minesweeper.model.HistoryFilterStrategy;

import minesweeper.model.GameHistory;

public class DifficultyFilterStrategy implements HistoryFilterStrategy {
    private final String difficulty; // "ALL", "EASY", "MEDIUM", "HARD"

    public DifficultyFilterStrategy(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public boolean matches(GameHistory h) {
        if (difficulty == null || difficulty.isBlank() || "ALL".equalsIgnoreCase(difficulty)) return true;
        return h.getDifficulty() != null && h.getDifficulty().name().equalsIgnoreCase(difficulty);
    }
}
