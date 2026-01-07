package minesweeper.observer;

import minesweeper.model.Board;
import minesweeper.model.Cell;
import minesweeper.view.MinesweeperGame;
import minesweeper.controller.GameController;
import javax.swing.*;
import java.awt.*;

public class ObserverPattern {
    public static void main(String[] args) {
        System.out.println("=== GameUIObserver Pattern Demo ===\n");
        
        // Run Swing on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // 1. Create a minimal game window
            JFrame frame = new JFrame("GameUIObserver Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // 2. Create UI components needed by GameUIObserver
            JPanel topPanel = new JPanel();
            JLabel scoreLabel = new JLabel("SCORE: 0000");
            JLabel livesLabel = new JLabel("LIVES: 💖 💖 💖");
            scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
            livesLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
            topPanel.add(scoreLabel);
            topPanel.add(Box.createHorizontalStrut(20));
            topPanel.add(livesLabel);
            
            // 3. Create button grid (8x10 for the board)
            int rows = 8, cols = 10;
            JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
            JButton[][] buttonGrid = new JButton[rows][cols];
            
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    JButton btn = new JButton("");
                    btn.setPreferredSize(new Dimension(50, 50));
                    btn.setBackground(new Color(20, 30, 40));
                    btn.setForeground(Color.WHITE);
                    btn.setFont(new Font("Sans-Serif", Font.BOLD, 16));
                    buttonGrid[r][c] = btn;
                    gridPanel.add(btn);
                }
            }
            
            frame.add(topPanel, BorderLayout.NORTH);
            frame.add(gridPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // 4. Create Board
            Board board = new Board(rows, cols, 2, 1);
            
            // 5. Create GameUIObserver (with null controller for demo)
            GameUIObserver observer = new GameUIObserver(
                (MinesweeperGame) frame,  // Cast frame as game view (for demo)
                null,  // GameController - can be null for basic demo
                buttonGrid,
                scoreLabel,
                livesLabel,
                true
            );
            
            // 6. Attach observer to board
            board.addObserver(observer);
            System.out.println("GameUIObserver attached to board\n");
            
            // 7. Simulate game events with delays
            Timer eventTimer = new Timer(0, null);
            int[] step = {0};
            
            eventTimer.addActionListener(e -> {
                switch(step[0]) {
                    case 0:
                        System.out.println("Revealing cell (0,0)...");
                        board.notifyCellRevealed(0, 0);
                        break;
                    case 1:
                        System.out.println("Revealing cell (1,1)...");
                        board.notifyCellRevealed(1, 1);
                        break;
                    case 2:
                        System.out.println("Revealing cell (2,2)...");
                        board.notifyCellRevealed(2, 2);
                        break;
                    case 3:
                        System.out.println("Updating score to 50...");
                        board.notifyScoreChanged(50);
                        break;
                    case 4:
                        System.out.println("Updating score to 100...");
                        board.notifyScoreChanged(100);
                        break;
                    case 5:
                        System.out.println("Triggering Game Over (Win)...");
                        board.notifyGameOver(true);
                        eventTimer.stop();
                        break;
                }
                step[0]++;
            });
            
            eventTimer.setDelay(1500);  // 1.5 second between events
            eventTimer.start();
            
            System.out.println("Watch the UI update as events occur!\n");
        });
    }
}