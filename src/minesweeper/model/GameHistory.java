package minesweeper.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameHistory {

    private GameSession.Difficulty difficulty;
    private String player1;
    private String player2;

    private int finalScore;
    private boolean coopWin;            // true = win, false = loss
    private String duration;            // "mm:ss"
    private LocalDateTime dateTime;     // full timestamp

    public GameHistory(GameSession.Difficulty difficulty,
                       String player1,
                       String player2,
                       int finalScore,
                       boolean coopWin,
                       String duration,
                       LocalDateTime dateTime) {

        this.difficulty = difficulty;
        this.player1 = player1;
        this.player2 = player2;
        this.finalScore = finalScore;
        this.coopWin = coopWin;
        this.duration = duration;
        this.dateTime = dateTime;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public GameSession.Difficulty getDifficulty() {
        return difficulty;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public boolean isCoopWin() {
        return coopWin;
    }

    public String getDuration() {
        return duration;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getFormattedDate() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // =====================================================
    // SETTERS (optional)
    // =====================================================

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public void setCoopWin(boolean coopWin) {
        this.coopWin = coopWin;
    }

    // =====================================================
    // Convenience
    // =====================================================
    @Override
    public String toString() {
        return getFormattedDate() + " | " +
                difficulty + " | " +
                player1 + " vs " + player2 +
                " | Score: " + finalScore +
                " | " + (coopWin ? "Co-op Win" : "Co-op Lose") +
                " | Duration: " + duration;
    }
}
