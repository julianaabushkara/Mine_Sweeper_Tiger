package minesweeper.model.scoring;

/**
 * Immutable result object containing the outcome of a scoring calculation.
 * Used by ScoringStrategy implementations to return both points and lives changes.
 */
public class ScoreResult {
    private final int points;
    private final int lives;

    public ScoreResult(int points, int lives) {
        this.points = points;
        this.lives = lives;
    }

    public int getPoints() {
        return points;
    }

    public int getLives() {
        return lives;
    }
}
