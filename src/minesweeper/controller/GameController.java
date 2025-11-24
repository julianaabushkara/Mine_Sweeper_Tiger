package minesweeper.controller;

import model.*;
import model.Cell.CellType;
import model.Question.QuestionDifficulty;
import model.GameSession.Difficulty;
import view.MinesweeperGameView;

import java.util.*;

/**
 * CONTROLLER: Handles all game logic and coordinates between Model and View
 * Responsibilities:
 * - Process user actions (clicks, flags)
 * - Manage game flow (turn switching, win/lose conditions)
 * - Handle special tile activations
 * - Cascade logic
 * - Score and lives management
 */
public class GameController {
    private GameSession gameSession;
    private MinesweeperGameView view;
    
    public GameController(Difficulty difficulty) {
        this.gameSession = new GameSession(difficulty);
    }
    
    public void setView(MinesweeperGameView view) {
        this.view = view;
    }
    
    public GameSession getGameSession() {
        return gameSession;
    }
    
    // ========== TURN MANAGEMENT ==========
    
    public void switchTurn() {
        gameSession.switchTurn();
        if (view != null) {
            view.updateDisplay();
            checkGameEnd();
        }
    }
    
    public boolean isCorrectPlayerTurn(boolean isPlayerA) {
        return gameSession.isPlayerATurn() == isPlayerA;
    }
    
    // ========== GAME END LOGIC ==========
    
    private void checkGameEnd() {
        if (gameSession.getSharedLives() <= 0) {
            if (view != null) {
                view.stopTimer();
                view.showGameEnd(false, calculateFinalScore(false));
            }
            return;
        }
        
        if (gameSession.getPlayerABoard().getRevealedMines() >= 
            gameSession.getPlayerABoard().getTotalMines()) {
            if (view != null) {
                view.stopTimer();
                view.showGameEnd(true, calculateFinalScore(true));
            }
            return;
        }
        
        if (gameSession.getPlayerBBoard().getRevealedMines() >= 
            gameSession.getPlayerBBoard().getTotalMines()) {
            if (view != null) {
                view.stopTimer();
                view.showGameEnd(true, calculateFinalScore(true));
            }
            return;
        }
    }
    
    private int calculateFinalScore(boolean victory) {
        if (victory) {
            int lifeBonus = gameSession.getSharedLives() * 
                           gameSession.getDifficulty().activationCost;
            gameSession.addScore(lifeBonus);
        }
        return gameSession.getSharedScore();
    }
    
    // ========== CELL REVEAL LOGIC ==========
    
    public void handleCellReveal(int row, int col, Board board, boolean isPlayerA) {
        Cell cell = board.getCell(row, col);
        
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }
        
        cell.setRevealed(true);
        
        switch (cell.getType()) {
            case MINE:
                handleMineReveal(board);
                break;
            case NUMBER:
                addScore(1);
                break;
            case EMPTY:
                addScore(1);
                performCascade(row, col, board, isPlayerA);
                break;
            case QUESTION:
            case SURPRISE:
                addScore(1);
                performSpecialCascade(row, col, board, isPlayerA);
                break;
        }
        
        if (view != null) {
            view.updateDisplay();
        }
        switchTurn();
    }
    
    private void handleMineReveal(Board board) {
        addLives(-1);
        board.incrementRevealedMines();
        if (view != null) {
            view.showMessage("💣 Mine discovered! -1 life\nMines discovered: " + 
                           board.getRevealedMines() + "/" + board.getTotalMines(),
                           "Mine!", "warning");
        }
    }
    
    // ========== FLAG LOGIC ==========
    
    public void handleCellFlag(int row, int col, Board board) {
        Cell cell = board.getCell(row, col);
        
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }
        
        cell.setFlagged(true);
        cell.setRevealed(true);
        
        if (cell.getType() == CellType.MINE) {
            addScore(1);
            board.incrementRevealedMines();
            if (view != null) {
                view.showMessage("✓ Correct flag! +1 point\nMines discovered: " + 
                               board.getRevealedMines() + "/" + board.getTotalMines(),
                               "Good Job!", "info");
            }
        } else {
            addScore(-3);
            if (view != null) {
                view.showMessage("✗ Wrong flag! -3 points", "Penalty", "error");
            }
        }
        
        if (view != null) {
            view.updateDisplay();
        }
        switchTurn();
    }
    
    // ========== CASCADE LOGIC ==========
    
    private void performCascade(int startRow, int startCol, Board board, boolean isPlayerA) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[board.getSize()][board.getSize()];
        
        queue.offer(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;
        
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int r = pos[0];
            int c = pos[1];
            
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    
                    int newRow = r + dr;
                    int newCol = c + dc;
                    
                    if (board.isValid(newRow, newCol) && !visited[newRow][newCol]) {
                        visited[newRow][newCol] = true;
                        Cell neighbor = board.getCell(newRow, newCol);
                        
                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            neighbor.setRevealed(true);
                            addScore(1);
                            
                            if (neighbor.getType() == CellType.EMPTY ||
                                neighbor.getType() == CellType.QUESTION ||
                                neighbor.getType() == CellType.SURPRISE) {
                                queue.offer(new int[]{newRow, newCol});
                            }
                        }
                    }
                }
            }
        }
        
        if (view != null) {
            view.updateBoardDisplay(isPlayerA);
        }
    }
    
    private void performSpecialCascade(int startRow, int startCol, Board board, boolean isPlayerA) {
        performCascade(startRow, startCol, board, isPlayerA);
    }
    
    // ========== SPECIAL TILE ACTIVATION ==========
    
    public boolean canActivateSpecialTile(Cell cell) {
        return cell.isRevealed() && !cell.isUsed() && !cell.isFlagged() &&
               gameSession.getSharedScore() >= gameSession.getDifficulty().activationCost;
    }
    
    public void activateQuestionTile(Cell cell, int row, int col, Board board, boolean isPlayerA) {
        if (!canActivateSpecialTile(cell)) {
            if (view != null) {
                view.showMessage("Not enough points! Need " + 
                               gameSession.getDifficulty().activationCost + " points.",
                               "Cannot Activate", "warning");
            }
            return;
        }
        
        addScore(-gameSession.getDifficulty().activationCost);
        cell.setUsed(true);
        
        QuestionDifficulty qDiff = Question.getRandomDifficulty();
        
        if (view != null) {
            int answer = view.showQuestionDialog(qDiff);
            boolean correct = (answer == 0);
            applyQuestionOutcome(qDiff, correct, row, col, board, isPlayerA);
            view.updateDisplay();
        }
        
        switchTurn();
    }
    
    private void applyQuestionOutcome(QuestionDifficulty qDiff, boolean correct,
                                     int row, int col, Board board, boolean isPlayerA) {
        Random rand = new Random();
        int points = 0;
        int lives = 0;
        String message = "";
        
        Difficulty gameDiff = gameSession.getDifficulty();
        
        // Easy Game Mode
        if (gameDiff == Difficulty.EASY) {
            if (qDiff == QuestionDifficulty.EASY) {
                if (correct) {
                    points = 3; lives = 1;
                } else {
                    if (rand.nextBoolean()) points = -3;
                }
            } else if (qDiff == QuestionDifficulty.MEDIUM) {
                if (correct) {
                    points = 6;
                    revealRandomMineTile(board, isPlayerA);
                    message = "Revealed mine tile & +6pts";
                } else {
                    if (rand.nextBoolean()) points = -6;
                }
            } else if (qDiff == QuestionDifficulty.HARD) {
                if (correct) {
                    points = 10;
                    reveal3x3Area(row, col, board, isPlayerA);
                    message = "Revealed 3x3 area & +10pts";
                } else {
                    points = -10;
                }
            } else { // EXPERT
                if (correct) {
                    points = 15; lives = 2;
                } else {
                    points = -15; lives = -1;
                }
            }
        }
        // Medium Game Mode
        else if (gameDiff == Difficulty.MEDIUM) {
            if (qDiff == QuestionDifficulty.EASY) {
                if (correct) {
                    points = 8; lives = 1;
                } else {
                    points = -8;
                }
            } else if (qDiff == QuestionDifficulty.MEDIUM) {
                if (correct) {
                    points = 10; lives = 1;
                } else {
                    if (rand.nextBoolean()) {
                        points = -10; lives = -1;
                    }
                }
            } else if (qDiff == QuestionDifficulty.HARD) {
                if (correct) {
                    points = 15; lives = 1;
                } else {
                    points = -15; lives = -1;
                }
            } else { // EXPERT
                if (correct) {
                    points = 20; lives = 2;
                } else {
                    points = -20;
                    lives = rand.nextBoolean() ? -1 : -2;
                }
            }
        }
        // Hard Game Mode
        else if (gameDiff == Difficulty.HARD) {
            if (qDiff == QuestionDifficulty.EASY) {
                if (correct) {
                    points = 10; lives = 1;
                } else {
                    points = -10; lives = -1;
                }
            } else if (qDiff == QuestionDifficulty.MEDIUM) {
                if (correct) {
                    points = 15;
                    lives = rand.nextBoolean() ? 1 : 2;
                } else {
                    points = -15;
                    lives = rand.nextBoolean() ? -1 : -2;
                }
            } else if (qDiff == QuestionDifficulty.HARD) {
                if (correct) {
                    points = 20; lives = 2;
                } else {
                    points = -20; lives = -2;
                }
            } else { // EXPERT
                if (correct) {
                    points = 40; lives = 3;
                } else {
                    points = -40; lives = -3;
                }
            }
        }
        
        addScore(points);
        addLives(lives);
        
        if (view != null) {
            String result = correct ? "✓ Correct!" : "✗ Incorrect!";
            String details = message.isEmpty() ? 
                String.format("%s\nPoints: %+d | Lives: %+d", result, points, lives) :
                String.format("%s\nPoints: %+d | Lives: %+d\n%s", result, points, lives, message);
            view.showMessage(details, "Question Result - " + qDiff.name(), 
                           correct ? "info" : "error");
        }
    }
    
    private void revealRandomMineTile(Board board, boolean isPlayerA) {
        List<int[]> hiddenMines = new ArrayList<>();
        
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                Cell cell = board.getCell(r, c);
                if (cell.getType() == CellType.MINE && !cell.isRevealed()) {
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
            
            if (view != null) {
                view.updateCellDisplay(minePos[0], minePos[1], isPlayerA);
            }
        }
    }
    
    private void reveal3x3Area(int questionRow, int questionCol, Board board, boolean isPlayerA) {
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
                                  fullyUnrevealedCenters : partiallyUnrevealedCenters;
        
        if (!validCenters.isEmpty()) {
            int[] chosenCenter = validCenters.get(rand.nextInt(validCenters.size()));
            int centerRow = chosenCenter[0];
            int centerCol = chosenCenter[1];
            
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int newRow = centerRow + dr;
                    int newCol = centerCol + dc;
                    
                    Cell neighborCell = board.getCell(newRow, newCol);
                    if (!neighborCell.isRevealed() && !neighborCell.isFlagged()) {
                        neighborCell.setRevealed(true);
                        addScore(1);
                        
                        if (neighborCell.getType() == CellType.MINE) {
                            board.incrementRevealedMines();
                        }
                        
                        if (view != null) {
                            view.updateCellDisplay(newRow, newCol, isPlayerA);
                        }
                    }
                }
            }
        }
    }
    
    public void activateSurpriseTile(Cell cell, boolean isPlayerA) {
        if (!canActivateSpecialTile(cell)) {
            if (view != null) {
                view.showMessage("Not enough points! Need " + 
                               gameSession.getDifficulty().activationCost + " points.",
                               "Cannot Activate", "warning");
            }
            return;
        }
        
        addScore(-gameSession.getDifficulty().activationCost);
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
        
        if (view != null) {
            String message = isGood ? 
                "🎉 Good surprise! +" + pointChange