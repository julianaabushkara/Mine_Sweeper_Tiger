package minesweeper.model.factory;

import minesweeper.model.Board;
import minesweeper.model.GameSession.Difficulty;
import minesweeper.model.scoring.*;

/**
 * Factory Pattern: Centralized factory for creating game boards and scoring strategies.
 *
 * This factory encapsulates the creation logic for:
 * 1. Game boards based on difficulty settings
 * 2. Scoring strategies that match the game difficulty
 *
 * Benefits:
 * - Single point of creation for game objects
 * - Easy to extend with new board types or strategies
 * - Decouples GameSession from concrete creation logic
 */
public class BoardFactory {

    /**
     * Creates a game board configured for the specified difficulty.
     * Factory method that encapsulates all board initialization parameters.
     *
     * @param difficulty The game difficulty level
     * @return A fully initialized Board ready for gameplay
     */
    public static Board createBoard(Difficulty difficulty) {
        return new Board(
            difficulty.gridSize,
            difficulty.mines,
            difficulty.questions,
            difficulty.surprises
        );
    }

    /**
     * Creates a custom board with specific parameters.
     * Useful for testing or custom game modes.
     *
     * @param size Grid size (size x size)
     * @param mines Number of mines
     * @param questions Number of question tiles
     * @param surprises Number of surprise tiles
     * @return A custom configured Board
     */
    public static Board createCustomBoard(int size, int mines, int questions, int surprises) {
        return new Board(size, mines, questions, surprises);
    }

    /**
     * Creates the appropriate scoring strategy for the given difficulty.
     * Factory method that selects the correct strategy implementation.
     *
     * @param difficulty The game difficulty level
     * @return The scoring strategy matching the difficulty
     */
    public static ScoringStrategy createScoringStrategy(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return new EasyScoringStrategy();
            case MEDIUM:
                return new MediumScoringStrategy();
            case HARD:
                return new HardScoringStrategy();
            default:
                return new EasyScoringStrategy();
        }
    }
}
