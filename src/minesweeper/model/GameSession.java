
package minesweeper.model;

import java.util.Random;
import java.time.Duration;
import java.time.LocalDateTime;
import minesweeper.model.factory.BoardFactory;
import minesweeper.model.scoring.ScoringStrategy;


public class GameSession {
    // Nested enum for game difficulty
    public enum Difficulty {
        EASY(9, 10, 6, 2, 10, 5),
        MEDIUM(13, 26, 7, 3, 8, 8),
        HARD(16, 44, 11, 4, 6, 12);

        public final int gridSize;
        public final int mines;
        public final int questions;
        public final int surprises;
        public final int startingLives;
        public final int activationCost;


        Difficulty(int gridSize, int mines, int questions, int surprises, 
                   int startingLives, int activationCost) {
            this.gridSize = gridSize;
            this.mines = mines;
            this.questions = questions;
            this.surprises = surprises;
            this.startingLives = startingLives;
            this.activationCost = activationCost;

        }
    }
    
    private Board playerABoard;
    private Board playerBBoard;
    private Difficulty difficulty;
    private ScoringStrategy scoringStrategy;  // Strategy Pattern: scoring rules vary by difficulty
    private int sharedLives;
    private int sharedScore;
    private boolean isPlayerATurn;
    private String playerAName;
    private String playerBName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;



    public GameSession(String playerAName, String playerBName, Difficulty difficulty) {
        this.playerAName = playerAName;
        this.playerBName = playerBName;

        this.difficulty = difficulty;
        this.sharedLives = difficulty.startingLives;
        this.sharedScore = 0;
        this.isPlayerATurn = new Random().nextBoolean();
        this.startTime = LocalDateTime.now();

        // Factory Pattern: Use BoardFactory to create boards and scoring strategy
        playerABoard = BoardFactory.createBoard(difficulty);
        playerBBoard = BoardFactory.createBoard(difficulty);
        scoringStrategy = BoardFactory.createScoringStrategy(difficulty);
    }

    /**
     * Get the scoring strategy for the current difficulty.
     * Strategy Pattern: Returns the appropriate scoring rules.
     */
    public ScoringStrategy getScoringStrategy() {
        return scoringStrategy;
    }

    // Getters and Setters
    public Board getPlayerABoard() { return playerABoard; }
    public Board getPlayerBBoard() { return playerBBoard; }
    public Difficulty getDifficulty() { return difficulty; }
    
    public int getSharedLives() { return sharedLives; }
    public void setSharedLives(int lives) { this.sharedLives = lives; }
    public void addLives(int lives) { this.sharedLives += lives; }
    
    public int getSharedScore() { return sharedScore; }
    public void setSharedScore(int score) { this.sharedScore = score; }
    public void addScore(int points) { this.sharedScore += points; }
    
    public boolean isPlayerATurn() { return isPlayerATurn; }
    public void setPlayerATurn(boolean turn) { this.isPlayerATurn = turn; }
    public void switchTurn() { this.isPlayerATurn = !this.isPlayerATurn; }
    public String getPlayerAName() {
        return playerAName;
    }

    public void endGame() {
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
    }

    public String getFormattedDuration() {
        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        Duration duration = Duration.between(startTime, end);
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getPlayerBName() {
        return playerBName;
    }



}