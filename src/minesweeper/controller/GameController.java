package minesweeper.controller;
import minesweeper.model.SessionContext;

import minesweeper.model.*;
import minesweeper.view.MinesweeperGame;
import javax.swing.JOptionPane;
import java.util.*;

public class GameController {
    private GameSession gameSession;
    private MinesweeperGame view;
    private boolean historySaved = false;


    public GameController() {
    }

    /*public void startNewGame(GameSession.Difficulty difficulty) {
        this.gameSession = new GameSession(difficulty);
    }*/

    public void startNewGame(String playerA, String playerB, GameSession.Difficulty difficulty) {
        this.gameSession = new GameSession(playerA, playerB, difficulty);
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
                    // Cascade: reveal all adjacent cells when clicking an empty cell
                    revealAdjacentCells(row, col, board);
                    break;
                case QUESTION:
                case SURPRISE:
                    gameSession.addScore(1);
                    break;
            }
        }
    }
    public void handleGameEndIfNeeded() {
        if (historySaved) return;
        if (!isGameOver()) return;

        historySaved = true;

        boolean victory = isVictory();
        int finalScore = calculateFinalScore();

        //GameHistoryManager historyManager = new GameHistoryManager();

       // String username = SessionContext.currentUser.getUsername();
        String username = (SessionContext.currentUser != null)
                ? SessionContext.currentUser.getUsername()
                : "ANONYMOUS";

        GameHistory history = new GameHistory(
                gameSession.getDifficulty(),
                username,
                gameSession.getPlayerAName(),
                gameSession.getPlayerBName(),
                finalScore,
                victory,
                gameSession.getFormattedDuration(),
                java.time.LocalDateTime.now()
        );

       //historyManager.addGameForUser(username, history);
        GameHistoryLogic.getInstance()
                .saveHistoryForUser(username, history);

        System.out.println("✔ Game history saved");
    }




    /**
     * Recursively reveals adjacent cells when an empty cell is clicked.
     * Cascade Rules:
     * - EMPTY cells: Revealed and cascade continues recursively
     * - NUMBER cells: Revealed but cascade stops (act as boundary)
     * - MINE cells: not revelaed by cascade
     *
     * @param row The row of the empty cell
     * @param col The column of the empty cell
     * @param board The game board
     */
    private void revealAdjacentCells(int row, int col, Board board) {
        // Check all 8 adjacent cells
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                // Skip the center cell
                if (dr == 0 && dc == 0) continue;

                int newRow = row + dr;
                int newCol = col + dc;

                // Check if the adjacent cell is valid
                if (board.isValid(newRow, newCol)) {
                    Cell adjacentCell = board.getCell(newRow, newCol);

                    // Only reveal if not already revealed and not flagged
                    if (!adjacentCell.isRevealed() && !adjacentCell.isFlagged()) {

                        Cell.CellType type = adjacentCell.getType();

                        // CASCADE RULE: Only reveal EMPTY and NUMBER tiles
                        // Do NOT reveal MINE, QUESTION, or SURPRISE during cascade
                        if (type == Cell.CellType.EMPTY || type == Cell.CellType.NUMBER) {
                            adjacentCell.setRevealed(true);
                            gameSession.addScore(1);

                            // Recursively continue ONLY for EMPTY cells
                            if (type == Cell.CellType.EMPTY) {
                                revealAdjacentCells(newRow, newCol, board);
                            }
                            // NUMBER cells are revealed but stop the cascade (act as boundary)
                        }

                    }
                }
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

// Get activation cost
        int activationCost = gameSession.getDifficulty().activationCost;

        // Calculate net points (after activation cost deduction)
        int netPoints = cachedPoints - activationCost;

        // Build feedback message
        StringBuilder feedback = new StringBuilder();
        feedback.append(correct ? "✓ CORRECT!\n\n" : "✗ WRONG!\n\n");

        // Show activation cost deduction
        feedback.append("Activation Cost: -").append(activationCost).append(" points\n");

        // Show appropriate feedback for points and lives
        if (cachedPoints == 0 && cachedLives == 0) {
            feedback.append("No additional changes\n");
            feedback.append("\nNet Points: -").append(activationCost);
        } else {
            if (cachedPoints > 0) {
                feedback.append("Points Gained: +").append(cachedPoints).append("\n");
            } else if (cachedPoints < 0) {
                feedback.append("Points Lost: ").append(cachedPoints).append("\n");
            }

            if (cachedLives > 0) {
                feedback.append("Lives: +").append(cachedLives).append(" 💖\n");
            } else if (cachedLives < 0) {
                feedback.append("Lives: ").append(cachedLives).append(" 💔\n");
            }

            // Show net points calculation
            feedback.append("\nNet Points: ");
            if (netPoints > 0) {
                feedback.append("+");
            }
            feedback.append(netPoints).append(" (").append(cachedPoints).append(" - ").append(activationCost).append(")");
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
        //gameSession.addScore(lifeBonus);
        return gameSession.getSharedScore()+ lifeBonus;
    }
}