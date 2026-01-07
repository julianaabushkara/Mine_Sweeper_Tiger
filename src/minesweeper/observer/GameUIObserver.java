package minesweeper.observer;

import minesweeper.model.Cell;
import minesweeper.view.MinesweeperGame;
import minesweeper.controller.GameController;
import javax.swing.*;
import java.awt.Color;

/**
 * Observer implementation that updates the game UI in response to board events.
 * This class bridges the Model (Board) and View (MinesweeperGame) using the Observer pattern.
 */
public class GameUIObserver implements BoardObserver {
    
    private MinesweeperGame gameView;
    private GameController gameController;
    private JButton[][] buttonGrid;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private boolean isPlayerABoard;
    
    
    public GameUIObserver(MinesweeperGame gameView, 
                          GameController gameController,
                          JButton[][] buttonGrid,
                          JLabel scoreLabel,
                          JLabel livesLabel,
                          boolean isPlayerABoard) {
        this.gameView = gameView;
        this.gameController = gameController;
        this.buttonGrid = buttonGrid;
        this.scoreLabel = scoreLabel;
        this.livesLabel = livesLabel;
        this.isPlayerABoard = isPlayerABoard;
    }
    
    /**
     * Called when a cell is revealed on the board.
     * Updates the corresponding UI button to display the cell's content.
     */
    @Override
    public void onCellRevealed(int row, int col, Cell cell) {
        SwingUtilities.invokeLater(() -> {
            if (buttonGrid != null && row < buttonGrid.length && col < buttonGrid[row].length) {
                JButton button = buttonGrid[row][col];
                
                // Update button appearance based on cell type
                if (cell.isFlagged()) {
                    button.setBackground(new Color(50, 30, 30));
                    button.setText("🚩");
                    button.setForeground(new Color(255, 80, 80));
                } else if (cell.isRevealed()) {
                    button.setBackground(new Color(30, 40, 50));
                    
                    switch (cell.getType()) {
                        case MINE:
                            button.setText("💣");
                            button.setForeground(Color.RED);
                            break;
                            
                        case NUMBER:
                            int adjacentMines = cell.getAdjacentMines();
                            button.setText(String.valueOf(adjacentMines));
                            button.setForeground(getNumberColor(adjacentMines));
                            break;
                            
                        case EMPTY:
                            button.setText("");
                            button.setBackground(new Color(40, 50, 60));
                            break;
                            
                        case QUESTION:
                            button.setText(cell.isUsed() ? "✓" : "?");
                            button.setForeground(new Color(255, 200, 50));
                            button.setBackground(cell.isUsed() ? 
                                new Color(50, 60, 40) : new Color(80, 70, 30));
                            break;
                            
                        case SURPRISE:
                            button.setText(cell.isUsed() ? "✓" : "🎁");
                            button.setBackground(cell.isUsed() ? 
                                new Color(50, 40, 60) : new Color(70, 40, 80));
                            break;
                    }
                } else {
                    button.setBackground(new Color(20, 30, 40));
                    button.setText("");
                }
                
                // Visual feedback animation
                animateButtonReveal(button);
            }
        });
    }
    
    /**
     * Called when the game ends (victory or defeat).
     * Displays appropriate dialog and handles game-over UI state.
     */
    @Override
    public void onGameOver(boolean won) {
        SwingUtilities.invokeLater(() -> {
            // Calculate final score with life bonus
            int finalScore = gameController.calculateFinalScore();
            
            // Prepare game over message
            String title = won ? "🎉 VICTORY!" : "💔 GAME OVER";
            String message = buildGameOverMessage(won, finalScore);
            int messageType = won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
            
            // Show game over dialog
            int choice = JOptionPane.showConfirmDialog(
                gameView,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                messageType
            );
            
            // Handle player choice
            if (choice == JOptionPane.YES_OPTION) {
                // Close current game window - user returns to main menu
                gameView.dispose();
            }
            // If NO, just close dialog and stay on game screen
        });
    }
    
    /**
     * Called when the score changes.
     * Updates the score display label.
     */
    @Override
    public void onScoreChanged(int newScore) {
        SwingUtilities.invokeLater(() -> {
            if (scoreLabel != null) {
                scoreLabel.setText("SCORE: " + String.format("%04d", newScore) + 
                    " (" + gameController.getGameSession().getDifficulty().name() + ")");
                
                // Add visual feedback for score changes
                animateScoreChange();
            }
            
            // Also update lives if available
            if (livesLabel != null && gameController != null) {
                int lives = gameController.getGameSession().getSharedLives();
                livesLabel.setText("LIVES: " + getHeartsString(lives));
                
                // Change color based on life count
                if (lives <= 2) {
                    livesLabel.setForeground(Color.RED);
                } else if (lives <= 4) {
                    livesLabel.setForeground(new Color(255, 140, 0));
                } else {
                    livesLabel.setForeground(Color.WHITE);
                }
            }
        });
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Returns appropriate color for numbered cells based on adjacent mine count
     */
    private Color getNumberColor(int num) {
        Color[] colors = {
            null,
            new Color(100, 150, 255), // 1: Blue
            new Color(100, 200, 100), // 2: Green
            new Color(255, 100, 100), // 3: Red
            new Color(150, 100, 255), // 4: Purple
            new Color(255, 150, 50),  // 5: Orange
            new Color(0, 200, 200),   // 6: Cyan
            new Color(255, 100, 200), // 7: Pink
            new Color(255, 255, 100)  // 8: Yellow
        };
        return (num > 0 && num < colors.length) ? colors[num] : Color.WHITE;
    }
    
    /**
     * Generates hearts string for lives display
     */
    private String getHeartsString(int lives) {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            hearts.append("💖 ");
        }
        return hearts.toString().trim();
    }
    
    /**
     * Builds the game over message with statistics
     */
    private String buildGameOverMessage(boolean won, int finalScore) {
        StringBuilder msg = new StringBuilder();
        
        if (won) {
            msg.append("🎉 Congratulations! All mines discovered!\n\n");
        } else {
            msg.append("💔 Game Over! Lives reached 0.\n\n");
        }
        
        // Add game statistics
        msg.append("═══════════════════════════\n");
        msg.append("📊 GAME STATISTICS\n");
        msg.append("═══════════════════════════\n\n");
        
        if (gameController != null && gameController.getGameSession() != null) {
            msg.append("Players: ")
               .append(gameController.getGameSession().getPlayerAName())
               .append(" & ")
               .append(gameController.getGameSession().getPlayerBName())
               .append("\n");
            msg.append("Difficulty: ")
               .append(gameController.getGameSession().getDifficulty())
               .append("\n");
            msg.append("Duration: ")
               .append(gameController.getGameSession().getFormattedDuration())
               .append("\n");
            
            int lives = gameController.getGameSession().getSharedLives();
            int lifeBonus = lives * gameController.getGameSession().getDifficulty().activationCost;
            
            msg.append("Remaining Lives: ").append(lives).append(" 💖\n");
            if (won && lives > 0) {
                msg.append("Life Bonus: ").append(lives).append(" × ")
                   .append(gameController.getGameSession().getDifficulty().activationCost)
                   .append(" = +").append(lifeBonus).append(" points\n");
            }
        }
        
        msg.append("\nFINAL SCORE: ").append(finalScore).append(" points\n");
        msg.append("═══════════════════════════\n\n");
        msg.append("Would you like to play again?");
        
        return msg.toString();
    }
    
    /**
     * Animates button reveal with a brief color flash
     */
    private void animateButtonReveal(JButton button) {
        Color originalColor = button.getBackground();
        
        button.setBackground(Color.WHITE);
        
        Timer timer = new Timer(150, e -> button.setBackground(originalColor));
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Animates score label when score changes
     */
    private void animateScoreChange() {
        if (scoreLabel == null) return;
        
        Color originalColor = scoreLabel.getForeground();
        java.awt.Font originalFont = scoreLabel.getFont();
        
        scoreLabel.setForeground(new Color(0, 255, 100));
        scoreLabel.setFont(originalFont.deriveFont(java.awt.Font.BOLD, originalFont.getSize() + 2));
        
        Timer timer = new Timer(300, e -> {
            scoreLabel.setForeground(originalColor);
            scoreLabel.setFont(originalFont);
        });
        timer.setRepeats(false);
        timer.start();
    }
}