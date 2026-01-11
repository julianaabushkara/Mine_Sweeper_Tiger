package minesweeper.controller;

import minesweeper.model.audio.AudioManager;
import minesweeper.view.StartMenuView;
import javax.swing.*;

/**
 * StartMenuController - Controller for the Start Menu
 *
 * Handles button click events and navigation from start menu to other views
 * Follows MVC pattern - separates business logic from UI
 *
 * @author Group Tiger
 * @version 3.0
 */
public class StartMenuController {

    private StartMenuView view;
    private NavigationController navigationController;

    /**
     * Constructor
     * @param view The StartMenuView instance
     * @param navigationController The navigation controller to handle screen transitions
     */
    public StartMenuController(StartMenuView view, NavigationController navigationController) {
        this.view = view;
        this.navigationController = navigationController;
        attachListeners();
    }

    /**
     * Attach event listeners to all buttons in the view
     */
    private void attachListeners() {
        // New Game button listener
        view.getNewGameButton().addActionListener(e -> handleNewGame());

        // Question Wizard button listener
        view.getQuestionWizardButton().addActionListener(e -> handleQuestionWizard());

        // History button listener
        view.getHistoryButton().addActionListener(e -> handleHistory());

        // Exit button listener
        view.getExitButton().addActionListener(e -> handleExit());
    }

    /**
     * Handle New Game button click
     * Navigate to New Game setup screen
     */
    private void handleNewGame() {
        System.out.println("Navigating to New Game...");
        navigationController.navigateToNewGame();
    }

    /**
     * Handle Question Wizard button click
     * Navigate to Question Wizard screen
     */
    private void handleQuestionWizard() {
        System.out.println("Navigating to Question Wizard...");
        navigationController.navigateToQuestionWizard();
    }

    /**
     * Handle History button click
     * Navigate to Game History screen
     */
    private void handleHistory() {
        System.out.println("Navigating to History...");
        navigationController.navigateToHistory();
    }

    /**
     * Handle Exit button click
     * Show confirmation dialog and exit application if confirmed
     */
    private void handleExit() {
        int result = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            AudioManager.get().shutdown(); // ✅ stop & close everything
            System.out.println("Exiting application...");
            System.exit(0);
        }
    }
}