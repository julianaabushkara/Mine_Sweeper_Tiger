
package minesweeper.controller;

import minesweeper.model.*;
import minesweeper.view.MinesweeperGame;
import javax.swing.JOptionPane;
import java.util.*;

public class GameController {
    private GameSession gameSession;
    private MinesweeperGame view;

    public GameController() {
    }

    public void startNewGame(GameSession.Difficulty difficulty) {
        this.gameSession = new GameSession(difficulty);
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setView(MinesweeperGame view) {
        this.view = view;
    }

    public void handleCellClick(Cell cell, int row, int col, Board board, boolean isLeftClick) {
        if (isLeftClick) {
            handleLeftClick(cell, row, col, board);
        } else {
            handleRightClick(cell, row, col, board);
        }
    }

    private void handleLeftClick(Cell cell, int row, int col, Board board) {
        if (!cell.isRevealed() && !cell.isFlagged()) {
            cell.setRevealed(true);

            switch (cell.getType()) {
                case MINE:
                    gameSession.addLives(-1);
                    board.incrementRevealedMines();
                    break;
                case NUMBER:
                    gameSession.addScore(1);
                    break;
                case EMPTY:
                    gameSession.addScore(1);
                    break;
                case QUESTION:
                case SURPRISE:
                    gameSession.addScore(1);
                    break;
            }
        }
    }

    private void handleRightClick(Cell cell, int row, int col, Board board) {
        if (!cell.isRevealed() && !cell.isFlagged()) {
            cell.setFlagged(true);
            cell.setRevealed(true);

            if (cell.getType() == Cell.CellType.MINE) {
                gameSession.addScore(1);
                board.incrementRevealedMines();
            } else {
                gameSession.addScore(-3);
            }
        }
    }

    public void switchTurn() {
        gameSession.switchTurn();
    }

    // FIX: Check if ONE player has revealed all mines on their board
    public boolean isGameOver() {
        // Defeat: Lives reach 0
        if (gameSession.getSharedLives() <= 0) {
            return true;
        }

        // Victory: Either player reveals all mines on their board
        if (gameSession.getPlayerABoard().getRevealedMines() >=
                gameSession.getPlayerABoard().getTotalMines() ||
                gameSession.getPlayerBBoard().getRevealedMines() >=
                        gameSession.getPlayerBBoard().getTotalMines()) {
            return true;
        }

        return false;
    }

    public boolean isVictory() {
        return gameSession.getSharedLives() > 0 &&
                (gameSession.getPlayerABoard().getRevealedMines() >=
                        gameSession.getPlayerABoard().getTotalMines() ||
                        gameSession.getPlayerBBoard().getRevealedMines() >=
                                gameSession.getPlayerBBoard().getTotalMines());
    }

    public void addLives(int lives) {
        int oldLives = gameSession.getSharedLives();
        gameSession.addLives(lives);

        // Check if maximum lives exceeded
        if (gameSession.getSharedLives() > 10) {
            int extraLives = gameSession.getSharedLives() - 10;
            gameSession.setSharedLives(10);
            int bonusPoints = extraLives * gameSession.getDifficulty().activationCost;
            gameSession.addScore(bonusPoints);

            // Show feedback
            if (view != null) {
                JOptionPane.showMessageDialog(view,
                        "Maximum lives reached!\n" + extraLives + " extra lives × " +
                                gameSession.getDifficulty().activationCost + " = +" + bonusPoints + " bonus points.",
                        "Bonus", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void addScore(int points) {
        gameSession.addScore(points);
    }

    // Cached random values for question outcomes
    private int cachedPoints = 0;
    private int cachedLives = 0;

    // Get feedback for question tile before activation (and cache the outcome)
    public String getQuestionFeedback(QuestionDifficulty qDiff, boolean correct) {
        Random rand = new Random();
        GameSession.Difficulty gameDiff = gameSession.getDifficulty();

        cachedPoints = 0;
        cachedLives = 0;

        // Easy Game Mode
        if (gameDiff == GameSession.Difficulty.EASY) {
            if (qDiff == QuestionDifficulty.EASY) {
                if (correct) {
                    cachedPoints = 3;
                    cachedLives = 1;
                } else {
                    if (rand.nextBoolean()) cachedPoints = -3;
                }
            } else if (qDiff == QuestionDifficulty.MEDIUM) {
                if (correct) {
                    cachedPoints = 6;
                } else {
                    if (rand.nextBoolean()) cachedPoints = -6;
                }
            } else if (qDiff == QuestionDifficulty.HARD) {
                if (correct) {
                    cachedPoints = 10;
                } else {
                    cachedPoints = -10;
                }
            } else { // EXPERT
                if (correct) {
                    cachedPoints = 15;
                    cachedLives = 2;
                } else {
                    cachedPoints = -15;
                    cachedLives = -1;
                }
            }
        }
        // Medium Game Mode
        else if (gameDiff == GameSession.Difficulty.MEDIUM) {
            if (qDiff == QuestionDifficulty.EASY) {
                if (correct) {
                    cachedPoints = 8;
                    cachedLives = 1;
                } else {
                    cachedPoints = -8;
                }
            } else if (qDiff == QuestionDifficulty.MEDIUM) {
                if (correct) {
                    cachedPoints = 10;
                    cachedLives = 1;
                } else {
                    if (rand.nextBoolean()) {
                        cachedPoints = -10;
                        cachedLives = -1;
                    }
                }
            } else if (qDiff == QuestionDifficulty.HARD) {
                if (correct) {
                    cachedPoints = 15;
                    cachedLives = 1;
                } else {
                    cachedPoints = -15;
                    cachedLives = -1;
                }
            } else { // EXPERT
                if (correct) {
                    cachedPoints = 20;
                    cachedLives = 2;
                } else {
                    cachedPoints = -20;
                    cachedLives = rand.nextBoolean() ? -1 : -2;
                }
            }
        }
        // Hard Game Mode
        else if (gameDiff == GameSession.Difficulty.HARD) {
            if (qDiff == QuestionDifficulty.EASY) {
                if (correct) {
                    cachedPoints = 10;
                    cachedLives = 1;
                } else {
                    cachedPoints = -10;
                    cachedLives = -1;
                }
            } else if (qDiff == QuestionDifficulty.MEDIUM) {
                if (correct) {
                    cachedPoints = 15;
                    cachedLives = rand.nextBoolean() ? 1 : 2;
                } else {
                    cachedPoints = -15;
                    cachedLives = rand.nextBoolean() ? -1 : -2;
                }
            } else if (qDiff == QuestionDifficulty.HARD) {
                if (correct) {
                    cachedPoints = 20;
                    cachedLives = 2;
                } else {
                    cachedPoints = -20;
                    cachedLives = -2;
                }
            } else { // EXPERT
                if (correct) {
                    cachedPoints = 40;
                    cachedLives = 3;
                } else {
                    cachedPoints = -40;
                    cachedLives = -3;
                }
            }
        }

        // Build feedback message
        StringBuilder feedback = new StringBuilder();
        feedback.append(correct ? "✓ CORRECT!\n\n" : "✗ WRONG!\n\n");

        // Show appropriate feedback
        if (cachedPoints == 0 && cachedLives == 0) {
            feedback.append("No penalty this time!");
        } else {
            if (cachedPoints > 0) {
                feedback.append("Points: +").append(cachedPoints).append("\n");
            } else if (cachedPoints < 0) {
                feedback.append("Points: ").append(cachedPoints).append("\n");
            }

            if (cachedLives > 0) {
                feedback.append("Lives: +").append(cachedLives).append(" 💖");
            } else if (cachedLives < 0) {
                feedback.append("Lives: ").append(cachedLives).append(" 💔");
            }
        }

        return feedback.toString();
    }

    public void activateQuestionTile(Cell cell, QuestionDifficulty qDiff,
                                     boolean correct, int row, int col, Board board) {
        if (gameSession.getSharedScore() < gameSession.getDifficulty().activationCost) {
            return;
        }

        gameSession.addScore(-gameSession.getDifficulty().activationCost);
        cell.setUsed(true);

        // Use the cached values from getQuestionFeedback
        addScore(cachedPoints);
        addLives(cachedLives);
    }

    public String activateSurpriseTile(Cell cell) {
        if (gameSession.getSharedScore() < gameSession.getDifficulty().activationCost) {
            return "";
        }

        gameSession.addScore(-gameSession.getDifficulty().activationCost);
        cell.setUsed(true);

        Random rand = new Random();
        boolean isGood = rand.nextBoolean();
        int pointChange = 0;
        int lifeChange = 0;

        switch (gameSession.getDifficulty()) {
            case EASY:
                pointChange = isGood ? 8 : -8;
                lifeChange = isGood ? 1 : -1;
                break;
            case MEDIUM:
                pointChange = isGood ? 12 : -12;
                lifeChange = isGood ? 1 : -1;
                break;
            case HARD:
                pointChange = isGood ? 16 : -16;
                lifeChange = isGood ? 1 : -1;
                break;
        }

        addScore(pointChange);
        addLives(lifeChange);

        // Build feedback message
        StringBuilder feedback = new StringBuilder();
        feedback.append(isGood ? "🎉 GOOD SURPRISE!\n\n" : "😱 BAD SURPRISE!\n\n");

        if (pointChange > 0) {
            feedback.append("Points: +").append(pointChange).append("\n");
        } else if (pointChange < 0) {
            feedback.append("Points: ").append(pointChange).append("\n");
        }

        if (lifeChange > 0) {
            feedback.append("Lives: +").append(lifeChange).append(" 💖");
        } else if (lifeChange < 0) {
            feedback.append("Lives: ").append(lifeChange).append(" 💔");
        }

        return feedback.toString();
    }

    public int[] revealRandomMineTile(Board board) {
        List<int[]> hiddenMines = new ArrayList<>();

        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                Cell cell = board.getCell(r, c);
                if (cell.getType() == Cell.CellType.MINE && !cell.isRevealed()) {
                    hiddenMines.add(new int[]{r, c});
                }
            }
        }

        if (!hiddenMines.isEmpty()) {
            Random rand = new Random();
            int[] minePos = hiddenMines.get(rand.nextInt(hiddenMines.size()));
            Cell mineCell = board.getCell(minePos[0], minePos[1]);
            mineCell.setRevealed(true);
            board.incrementRevealedMines();
            return minePos;
        }

        return null;
    }

    public List<int[]> reveal3x3Area(int questionRow, int questionCol, Board board) {
        Random rand = new Random();
        List<int[]> fullyUnrevealedCenters = new ArrayList<>();
        List<int[]> partiallyUnrevealedCenters = new ArrayList<>();

        for (int centerRow = 1; centerRow < board.getSize() - 1; centerRow++) {
            for (int centerCol = 1; centerCol < board.getSize() - 1; centerCol++) {

                boolean hasOverlap = false;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int checkRow = centerRow + dr;
                        int checkCol = centerCol + dc;

                        if (checkRow >= questionRow - 1 && checkRow <= questionRow + 1 &&
                                checkCol >= questionCol - 1 && checkCol <= questionCol + 1) {
                            hasOverlap = true;
                            break;
                        }
                    }
                    if (hasOverlap) break;
                }

                if (hasOverlap) continue;

                boolean allUnrevealed = true;
                boolean hasUnrevealed = false;

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int checkRow = centerRow + dr;
                        int checkCol = centerCol + dc;
                        Cell checkCell = board.getCell(checkRow, checkCol);

                        if (!checkCell.isRevealed() && !checkCell.isFlagged()) {
                            hasUnrevealed = true;
                        } else {
                            allUnrevealed = false;
                        }
                    }
                }

                if (allUnrevealed) {
                    fullyUnrevealedCenters.add(new int[]{centerRow, centerCol});
                } else if (hasUnrevealed) {
                    partiallyUnrevealedCenters.add(new int[]{centerRow, centerCol});
                }
            }
        }

        List<int[]> validCenters = !fullyUnrevealedCenters.isEmpty() ?
                fullyUnrevealedCenters :
                partiallyUnrevealedCenters;

        if (!validCenters.isEmpty()) {
            int[] chosenCenter = validCenters.get(rand.nextInt(validCenters.size()));
            int centerRow = chosenCenter[0];
            int centerCol = chosenCenter[1];

            List<int[]> revealedCells = new ArrayList<>();
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int newRow = centerRow + dr;
                    int newCol = centerCol + dc;

                    Cell neighborCell = board.getCell(newRow, newCol);
                    if (!neighborCell.isRevealed() && !neighborCell.isFlagged()) {
                        neighborCell.setRevealed(true);
                        addScore(1);
                        revealedCells.add(new int[]{newRow, newCol});

                        if (neighborCell.getType() == Cell.CellType.MINE) {
                            board.incrementRevealedMines();
                        }
                    }
                }
            }

            return revealedCells;
        }

        return new ArrayList<>();
    }

    public int calculateFinalScore() {
        int lifeBonus = gameSession.getSharedLives() *
                gameSession.getDifficulty().activationCost;
        gameSession.addScore(lifeBonus);
        return gameSession.getSharedScore();
    }
}