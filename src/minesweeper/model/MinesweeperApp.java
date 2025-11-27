package minesweeper.model;

import minesweeper.controller.NavigationController;
import javax.swing.*;
import java.awt.*;

/**
 * MinesweeperApp - Main Application Class
 *
 * Coordinates all components of the Minesweeper Tiger application:
 * - Initializes the main window (JFrame)
 * - Creates the navigation controller
 * - Manages the question bank
 * - Provides access to shared resources
 *
 * This is the entry point for the application following MVC pattern
 *
 * @author Group Tiger
 * @version 1.0
 */
public class MinesweeperApp {

    // Application window
    private JFrame mainFrame;

    // Navigation controller
    private NavigationController navigationController;

    // Shared resources
    private QuestionBank questionBank;
    private GameHistoryManager gameHistoryManager;  // FIXED: Changed from GameHistory to GameHistoryManager

    /**
     * Constructor - initializes the application
     */
    public MinesweeperApp() {
        initializeResources();
        initializeMainFrame();
        initializeNavigation();
    }

    /**
     * Initialize shared resources (Question Bank, Game History, etc.)
     */
    private void initializeResources() {
        try {
            // Initialize Question Bank
            questionBank = new QuestionBank();

            // Initialize Game History Manager
            gameHistoryManager = new GameHistoryManager();  // FIXED: Now uses manager class

            System.out.println("Resources initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize the main application window
     */
    private void initializeMainFrame() {
        mainFrame = new JFrame("Minesweeper - Tiger Edition");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 700);
        mainFrame.setMinimumSize(new Dimension(600, 600));

        // Center on screen
        mainFrame.setLocationRelativeTo(null);

        // Set application icon (if available)
        try {
            // TODO: Add application icon
            // ImageIcon icon = new ImageIcon(getClass().getResource("/icons/app_icon.png"));
            // mainFrame.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
    }

    /**
     * Initialize navigation controller and show start menu
     */
    private void initializeNavigation() {
        navigationController = new NavigationController(mainFrame, this);
        navigationController.navigateToStartMenu();
    }

    /**
     * Start the application - make the main window visible
     */
    public void start() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
            System.out.println("Minesweeper Tiger Edition started successfully!");
        });
    }

    // =========================================
    // GETTERS - For accessing shared resources
    // =========================================

    /**
     * Get the question bank
     * @return QuestionBank instance
     */
    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    /**
     * Get the game history manager
     * @return GameHistoryManager instance
     */
    public GameHistoryManager getGameHistoryManager() {  // FIXED: Changed return type
        return gameHistoryManager;
    }

    /**
     * Get the main frame
     * @return Main application JFrame
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Get the navigation controller
     * @return NavigationController instance
     */
    public NavigationController getNavigationController() {
        return navigationController;
    }
}