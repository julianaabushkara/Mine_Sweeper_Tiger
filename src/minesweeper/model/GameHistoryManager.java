package minesweeper.model;

import java.util.ArrayList;
import java.util.List;

/**
 * GameHistoryManager - Manages collection of game history sessions
 *
 * This class stores and manages all completed game sessions.
 * Unlike GameHistory which represents a single game, this manages the collection.
 *
 * @author Group Tiger
 * @version 1.0
 */
public class GameHistoryManager {

    private List<GameHistory> gameSessions;
    private static final String HISTORY_FILE = "src/minesweeper/Data/history.json";

    /**
     * Constructor - initializes empty game history collection
     */
    public GameHistoryManager() {
        this.gameSessions = new ArrayList<>();
        loadHistoryFromFile();
    }

    /**
     * Add a completed game to history
     * @param gameHistory The game session to add
     */
    public void addGameSession(GameHistory gameHistory) {
        gameSessions.add(gameHistory);
        saveHistoryToFile();
    }

    /**
     * Get all game sessions
     * @return List of all game sessions
     */
    public List<GameHistory> getAllSessions() {
        return new ArrayList<>(gameSessions); // Return copy for safety
    }

    /**
     * Get number of games played
     * @return Total number of games
     */
    public int getTotalGames() {
        return gameSessions.size();
    }

    /**
     * Clear all history
     */
    public void clearHistory() {
        gameSessions.clear();
        saveHistoryToFile();
    }

    /**
     * Load history from JSON file
     */
    private void loadHistoryFromFile() {
        // TODO: Implement JSON loading
        // For now, start with empty list
        System.out.println("GameHistoryManager: Loading history from file...");
    }

    /**
     * Save history to JSON file
     */
    private void saveHistoryToFile() {
        // TODO: Implement JSON saving
        System.out.println("GameHistoryManager: Saving history to file...");
    }
}