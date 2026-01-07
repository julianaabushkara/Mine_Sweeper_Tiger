package minesweeper.model.scoring;

import minesweeper.model.QuestionDifficulty;
import java.util.Random;

/**
 * Strategy Pattern: Scoring rules for EASY game difficulty.
 * Most forgiving scoring with lower risks and moderate rewards.
 */
public class EasyScoringStrategy implements ScoringStrategy {

    private final Random rand = new Random();

    @Override
    public ScoreResult calculateQuestionReward(QuestionDifficulty qDiff, boolean correct) {
        int points = 0;
        int lives = 0;

        switch (qDiff) {
            case EASY:
                if (correct) {
                    points = 3;
                    lives = 1;
                } else {
                    if (rand.nextBoolean()) points = -3;
                }
                break;
            case MEDIUM:
                if (correct) {
                    points = 6;
                } else {
                    if (rand.nextBoolean()) points = -6;
                }
                break;
            case HARD:
                if (correct) {
                    points = 10;
                } else {
                    points = -10;
                }
                break;
            case EXPERT:
                if (correct) {
                    points = 15;
                    lives = 2;
                } else {
                    points = -15;
                    lives = -1;
                }
                break;
        }
        return new ScoreResult(points, lives);
    }

    @Override
    public ScoreResult calculateSurpriseReward(boolean isGoodSurprise) {
        int points = isGoodSurprise ? 8 : -8;
        int lives = isGoodSurprise ? 1 : -1;
        return new ScoreResult(points, lives);
    }

    @Override
    public int getActivationCost() {
        return 5;
    }
}
