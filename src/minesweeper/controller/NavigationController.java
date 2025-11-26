package minesweeper.controller;

import minesweeper.view.*;
import minesweeper.model.MinesweeperApp;
import javax.swing.*;

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
 * @version 1.0
 */
public class NavigationController {

    private JFrame mainFrame;
    private MinesweeperApp appModel;

    // View instances (created on demand)
    private StartMenuView startMenuView;
    private NewGameView newGameView;
    private QuestionWizardView questionWizardView;
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
        // Make sure main frame is visible
        if (!mainFrame.isVisible()) {
            mainFrame.setVisible(true);
        }

        if (startMenuView == null) {
            startMenuView = new StartMenuView();
            new StartMenuController(startMenuView, this);
        }
        switchToView(startMenuView);
    }

    /**
     * Navigate to New Game Setup screen
     */
    public void navigateToNewGame() {
        if (newGameView == null) {
            newGameView = new NewGameView();
            // TODO: Create and attach NewGameController
            // new NewGameController(newGameView, this, appModel);

            // Temporary: Add back button functionality
            newGameView.getBackButton().addActionListener(e -> navigateToStartMenu());
        }
        switchToView(newGameView);
    }

    /**
     * Navigate to Question Wizard screen
     * Note: Question Wizard is a JFrame, so it opens in a separate window
     */
    public void navigateToQuestionWizard() {
        if (questionWizardView == null) {
            questionWizardView = new QuestionWizardView();

            // Initialize controller with correct parameter order
            try {
                QuestionWizardController controller = new QuestionWizardController(
                        appModel.getQuestionBank(),    // First parameter: QuestionBank
                        questionWizardView             // Second parameter: QuestionWizardView
                );

                // Add back button functionality

            } catch (Exception e) {
                System.err.println("Error initializing Question Wizard: " + e.getMessage());
                e.printStackTrace();
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

            // Temporary: Add back button functionality
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
        mainFrame.getContentPane().removeAll();
        mainFrame.setContentPane(view);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * Helper method to show JFrame views in a separate window
     * Used for JFrame-based views (Question Wizard)
     * @param frameView The JFrame view to show
     */
    private void showFrameView(JFrame frameView) {
        // Hide the main frame while showing the separate window
        mainFrame.setVisible(false);

        // Position and show the frame view
        frameView.setLocationRelativeTo(null);  // Center on screen
        frameView.setVisible(true);
    }

    /**
     * Get the main frame
     * @return The main application frame
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }
}