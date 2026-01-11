package minesweeper.model.scoring;

import minesweeper.model.QuestionDifficulty;
import java.util.Random;

/**
 * Strategy Pattern: Scoring rules for HARD game difficulty.
 * High risk, high reward scoring with severe penalties.
 */
public class HardScoringStrategy implements ScoringStrategy {

    private final Random rand = new Random();

    @Override
    public ScoreResult calculateQuestionReward(QuestionDifficulty qDiff, boolean correct) {
        int points = 0;
        int lives = 0;

        switch (qDiff) {
            case EASY:
                if (correct) {
                    points = 10;
                    lives = 1;
                } else {
                    points = -10;
                    lives = -1;
                }
                break;
            case MEDIUM:
                if (correct) {
                    points = 15;
                    lives = rand.nextBoolean() ? 1 : 2;
                } else {
                    points = -15;
                    lives = rand.nextBoolean() ? -1 : -2;
                }
                break;
            case HARD:
                if (correct) {
                    points = 20;
                    lives = 2;
                } else {
                    points = -20;
                    lives = -2;
                }
                break;
            case EXPERT:
                if (correct) {
                    points = 40;
                    lives = 3;
                } else {
                    points = -40;
                    lives = -3;
                }
                break;
        }
        return new ScoreResult(points, lives);
    }

    @Override
    public ScoreResult calculateSurpriseReward(boolean isGoodSurprise) {
        int points = isGoodSurprise ? 16 : -16;
        int lives = isGoodSurprise ? 1 : -1;
        return new ScoreResult(points, lives);
    }

    @Override
    public int getActivationCost() {
        return 12;
    }
}
