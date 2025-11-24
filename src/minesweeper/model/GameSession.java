package minesweeper.model;

import java.util.Random;

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
    private int sharedLives;
    private int sharedScore;
    private boolean isPlayerATurn;

    public GameSession(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.sharedLives = difficulty.startingLives;
        this.sharedScore = 0;
        this.isPlayerATurn = new Random().nextBoolean();

        // Create boards for both players
        playerABoard = new Board(difficulty.gridSize, difficulty.mines, 
                                 difficulty.questions, difficulty.surprises);
        playerBBoard = new Board(difficulty.gridSize, difficulty.mines, 
                                 difficulty.questions, difficulty.surprises);
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
}