package minesweeper.controller;

import minesweeper.model.audio.AudioManager;
import minesweeper.view.*;
import minesweeper.model.MinesweeperApp;
import minesweeper.model.GameSession;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * NavigationController - Centralized navigation system for the application
 *
 * Manages transitions between different screens/views:
 * - Start Menu (JPanel)
 * - New Game Setup (JPanel)
 * - Game Board (JPanel)
 * - Question Wizard (JFrame - opens in separate window)
 * - History (JPanel)
 *
 * Handles both JPanel views (embedded) and JFrame views (separate windows)
 *
 * Follows MVC pattern and single responsibility principle
 *
 * @author Group Tiger
 * @version 1.3.2 - Fixed game window exit issue completely
 */
public class NavigationController {

    private JFrame mainFrame;
    private MinesweeperApp appModel;

    // View instances (created on demand)
    private StartMenuView startMenuView;
    private StartMenuController startMenuController;
    private NewGameView newGameView;
    private QuestionWizardView questionWizardView;
    private QuestionWizardController questionWizardController;
    private HistoryView historyView;

    /**
     * Constructor
     * @param mainFrame The main application window
     * @param appModel The application model
     */
    public NavigationController(JFrame mainFrame, MinesweeperApp appModel) {
        this.mainFrame = mainFrame;
        this.appModel = appModel;
    }

    /**
     * Navigate to Start Menu
     */
    public void navigateToStartMenu() {
        // Create view and controller only once
        if (startMenuView == null) {
            startMenuView = new StartMenuView();
            AudioBinder.addClickToAllButtons(startMenuView);
            startMenuController = new StartMenuController(startMenuView, this);
        }

        // Switch to the view
        switchToView(startMenuView);
    }

    /**
     * Navigate to New Game Setup screen
     */
    public void navigateToNewGame() {
        if (newGameView == null) {
            newGameView = new NewGameView();
            AudioBinder.addClickToAllButtons(newGameView);

            // Add back button functionality
            newGameView.getBackButton().addActionListener(e -> navigateToStartMenu());

            // Add start game button functionality
            newGameView.getStartButton().addActionListener(e -> {
                handleStartGame();
            });
        }
        switchToView(newGameView);
    }

    /**
     * Handle the Start Game button click
     * Validates inputs and starts a new game
     */
    private void handleStartGame() {
        // Get player names
        String player1Name = newGameView.getPlayer1Field().getText().trim();
        String player2Name = newGameView.getPlayer2Field().getText().trim();

        // Validate player names
        if (player1Name.isEmpty() || player1Name.equals("Player 1 Name")) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Please enter Player 1's name",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (player2Name.isEmpty() || player2Name.equals("Player 2 Name")) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Please enter Player 2's name",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Get selected difficulty
        GameSession.Difficulty difficulty;
        if (newGameView.getEasyToggle().isSelected()) {
            difficulty = GameSession.Difficulty.EASY;
        } else if (newGameView.getMediumToggle().isSelected()) {
            difficulty = GameSession.Difficulty.MEDIUM;
        } else if (newGameView.getHardToggle().isSelected()) {
            difficulty = GameSession.Difficulty.HARD;
        } else {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Please select a difficulty level",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Start the game
        startGame(player1Name, player2Name, difficulty);
    }

    /**
     * Start a new game with the given parameters
     */
    private void startGame(String player1Name, String player2Name, GameSession.Difficulty difficulty) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create game controller
                GameController gameController = new GameController();
                gameController.startNewGame(player1Name, player2Name,difficulty);

                // Create game view (JFrame)
                MinesweeperGame gameView = new MinesweeperGame(gameController, appModel.getQuestionBank());
                AudioBinder.addClickToAllButtons(gameView); // ✅ bind clicks in the whole game window

                // AGGRESSIVE FIX for EXIT_ON_CLOSE issue:
                // 1. Use DISPOSE_ON_CLOSE instead of DO_NOTHING (more reliable)
                gameView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // 2. Remove ALL existing window listeners (clears EXIT_ON_CLOSE behavior)
                for (java.awt.event.WindowListener listener : gameView.getWindowListeners()) {
                    gameView.removeWindowListener(listener);
                }

                // 3. Add our custom window adapter FIRST
                gameView.addWindowListener(new WindowAdapter() {
                    private boolean isClosing = false;

                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (isClosing) return;  // Prevent multiple dialogs
                        isClosing = true;

                        // Show confirmation dialog
                        int result = JOptionPane.showConfirmDialog(
                                gameView,
                                "Are you sure you want to exit the game and return to menu?",
                                "Exit Game",

                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (result == JOptionPane.YES_OPTION) {
                            // User confirmed - dispose window
                            AudioManager.get().stopMusic(); // ✅ stop immediately
                            gameView.dispose();
                            // Return to menu
                            returnToMainMenuFromGame();
                        } else {
                            // User cancelled - reset flag
                            isClosing = false;
                        }
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        // Fallback: if window gets closed without confirmation
                        // (shouldn't happen but just in case)
                        returnToMainMenuFromGame();
                    }
                });

                // 4. Re-set DISPOSE_ON_CLOSE after adding listener
                gameView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Hide main frame and show game
                mainFrame.setVisible(false);
                gameView.setLocationRelativeTo(null);
                gameView.setVisible(true);
                gameView.toFront();
                gameView.requestFocus();

                System.out.println("Game started successfully - " + difficulty + " mode");

            } catch (Exception ex) {
                System.err.println("Error starting game: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Error starting game: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                mainFrame.setVisible(true);
            }
        });
    }

    private boolean isValidPlayerName(String name) {
        if (name == null) return false;

        String trimmed = name.trim();
        return !trimmed.isEmpty() && trimmed.length() <= 20;
    }


    /**
     * Return to main menu from game
     * Called after game window is closed/disposed
     */
    private void returnToMainMenuFromGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Returning to main menu...");
                AudioManager.get().stopMusic(); // if you have this method

                // Show and restore main frame
                mainFrame.setVisible(true);
                mainFrame.setState(JFrame.NORMAL);  // Restore if minimized
                mainFrame.toFront();
                mainFrame.requestFocus();

                // Navigate to start menu
                navigateToStartMenu();

            } catch (Exception ex) {
                System.err.println("Error returning to main menu: " + ex.getMessage());
                ex.printStackTrace();
                // Even if error occurs, try to show main frame
                mainFrame.setVisible(true);
                mainFrame.toFront();
            }
        });
    }

    /**
     * Navigate to Question Wizard screen
     */
    public void navigateToQuestionWizard() {
        if (questionWizardView == null) {
            questionWizardView = new QuestionWizardView();

            AudioBinder.addClickToAllButtons(questionWizardView); // ✅ here (JFrame is Container)


            // Initialize controller with correct parameter order
            try {
                questionWizardController = new QuestionWizardController(
                        appModel.getQuestionBank(),    // First parameter: QuestionBank
                        questionWizardView             // Second parameter: QuestionWizardView
                );


                // Set the callback for when back button is clicked
                questionWizardController.setOnBackToStart(() -> {
                    // Hide Question Wizard
                    questionWizardView.hideWindow();

                    // Show main frame and navigate to start menu
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.setVisible(true);
                        mainFrame.toFront();
                        mainFrame.requestFocus();
                        navigateToStartMenu();
                    });
                });

                // Add window listener to handle the X button close or window disposal
                questionWizardView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Restore main frame visibility when Question Wizard is closed
                        SwingUtilities.invokeLater(() -> {
                            mainFrame.setVisible(true);
                            mainFrame.toFront();
                            mainFrame.requestFocus();
                            navigateToStartMenu();
                        });
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        // Also handle when window is disposed
                        SwingUtilities.invokeLater(() -> {
                            if (!mainFrame.isVisible()) {
                                mainFrame.setVisible(true);
                                mainFrame.toFront();
                                mainFrame.requestFocus();
                                navigateToStartMenu();
                            }
                        });
                    }
                });

            } catch (Exception e) {
                System.err.println("Error initializing Question Wizard: " + e.getMessage());
                e.printStackTrace();
                // If initialization fails, make sure main frame stays visible
                mainFrame.setVisible(true);
                return;
            }
        }

        // Show Question Wizard as a separate window
        showFrameView(questionWizardView);
    }

    /**
     * Navigate to Game History screen
     */
    public void navigateToHistory() {
        if (historyView == null) {
            historyView = new HistoryView();
            // TODO: Create and attach HistoryController
            // new HistoryController(historyView, this, appModel);

            AudioBinder.addClickToAllButtons(historyView);

            // Add back button functionality
            historyView.getBackButton().addActionListener(e -> navigateToStartMenu());
        }

        // History is a JPanel, so embed it in main frame
        switchToView(historyView);
    }

    /**
     * Navigate to active game board
     * @param gameView The game view to display
     */
    public void navigateToGame(JPanel gameView) {
        switchToView(gameView);
    }

    /**
     * Helper method to switch the current view in the main frame
     * Used for JPanel-based views (Start Menu, New Game, History, Game Board)
     * @param view The new JPanel view to display
     */
    private void switchToView(JPanel view) {
        // Use SwingUtilities.invokeLater to ensure proper EDT handling
        SwingUtilities.invokeLater(() -> {
            // Simply replace the content pane
            mainFrame.setContentPane(view);

            // Force the frame to validate and repaint
            mainFrame.validate();
            mainFrame.repaint();

            // Ensure the frame is visible and has focus
            if (!mainFrame.isVisible()) {
                mainFrame.setVisible(true);
            }
            mainFrame.toFront();
            mainFrame.requestFocus();
        });
    }

    /**
     * Helper method to show JFrame views in a separate window
     * Used for JFrame-based views (Question Wizard)
     * @param frameView The JFrame view to show
     */
    private void showFrameView(JFrame frameView) {
        SwingUtilities.invokeLater(() -> {
            // Hide the main frame while showing the separate window
            mainFrame.setVisible(false);

            // Position and show the frame view
            frameView.setLocationRelativeTo(null);  // Center on screen
            frameView.setVisible(true);
            frameView.toFront();
            frameView.requestFocus();
        });
    }

    /**
     * Get the main frame
     * @return The main application frame
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }
}