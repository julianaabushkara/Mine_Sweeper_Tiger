package minesweeper.model.scoring;

import minesweeper.model.QuestionDifficulty;

/**
 * Strategy Pattern: Interface for different scoring rules based on game difficulty.
 * Each difficulty level (EASY, MEDIUM, HARD) has its own scoring strategy implementation.
 */
public interface ScoringStrategy {

    /**
     * Calculate the reward/penalty for answering a question.
     * @param questionDifficulty The difficulty of the question (EASY, MEDIUM, HARD, EXPERT)
     * @param correct Whether the player answered correctly
     * @return ScoreResult containing points and lives changes
     */
    ScoreResult calculateQuestionReward(QuestionDifficulty questionDifficulty, boolean correct);

    /**
     * Calculate the reward/penalty for a surprise tile.
     * @param isGoodSurprise Whether the surprise is good or bad (random)
     * @return ScoreResult containing points and lives changes
     */
    ScoreResult calculateSurpriseReward(boolean isGoodSurprise);

    /**
     * Get the activation cost for special tiles in this difficulty.
     * @return The point cost to activate a question or surprise tile
     */
    int getActivationCost();
}
