package minesweeper.view;

import minesweeper.view.components.NeonButtonFactory;
import javax.swing.*;
import java.awt.*;

/**
 * StartMenuView - Main menu screen for Minesweeper Tiger Edition
 *
 * Displays the main navigation with 4 neon-styled buttons:
 * - NEW GAME (green neon)
 * - QUESTION WIZARD (blue neon)
 * - HISTORY (purple neon)
 * - EXIT (red neon)
 *
 * Follows MVC pattern - View only handles UI rendering
 * Controller will attach event listeners to buttons
 */
public class StartMenuView extends JPanel {

    // UI Components
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JButton newGameButton;
    private JButton questionWizardButton;
    private JButton historyButton;
    private JButton exitButton;
    private JLabel versionLabel;

    /**
     * Constructor - initializes the start menu view
     */
    public StartMenuView() {
        initComponents();
    }

    /**
     * Initialize all UI components and layout
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 15, 20)); // Dark sci-fi background
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        // =========================================
        // TITLE - "MINE SWEEPER"
        // =========================================
        titleLabel = new JLabel("MINE SWEEPER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 255, 255));

        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 5, 20);
        add(titleLabel, gbc);

        // =========================================
        // SUBTITLE - "TIGER EDITION"
        // =========================================
        subtitleLabel = new JLabel("TIGER EDITION", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.YELLOW.brighter());

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(subtitleLabel, gbc);

        // =========================================
        // LOGO - TIGER ICON
        // =========================================
        ImageIcon tigerIcon = new ImageIcon("resources/assets/tiger.png");
        JLabel logoLabel = new JLabel(tigerIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(logoLabel, gbc);

        // =========================================
        // NEW GAME BUTTON (Green)
        // =========================================
        newGameButton = NeonButtonFactory.createNeonButton(
                "▶ NEW GAME",
                new Color(0, 255, 120)
        );
        newGameButton.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(newGameButton, gbc);

        // =========================================
        // QUESTION WIZARD BUTTON (Blue)
        // =========================================
        questionWizardButton = NeonButtonFactory.createNeonButton(
                "? QUESTION WIZARD",
                new Color(0, 180, 255)
        );
        questionWizardButton.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridy = 4;
        add(questionWizardButton, gbc);

        // =========================================
        // HISTORY BUTTON (Purple)
        // =========================================
        historyButton = NeonButtonFactory.createNeonButton(
                "⏱ HISTORY",
                new Color(180, 80, 255)
        );
        historyButton.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridy = 5;
        add(historyButton, gbc);

        // =========================================
        // EXIT BUTTON (Red)
        // =========================================
        exitButton = NeonButtonFactory.createNeonButton(
                "➜ EXIT",
                new Color(255, 60, 80)
        );
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridy = 6;
        add(exitButton, gbc);

        // =========================================
        // VERSION LABEL
        // =========================================
        versionLabel = new JLabel("Group Tiger · Version 2.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(100, 100, 110));

        gbc.gridy = 7;
        gbc.insets = new Insets(40, 20, 10, 20);
        add(versionLabel, gbc);
    }

    // =========================================
    // GETTERS - For controller to access buttons
    // =========================================

    /**
     * Get the New Game button
     * @return JButton for starting a new game
     */
    public JButton getNewGameButton() {
        return newGameButton;
    }

    /**
     * Get the Question Wizard button
     * @return JButton for opening question wizard
     */
    public JButton getQuestionWizardButton() {
        return questionWizardButton;
    }

    /**
     * Get the History button
     * @return JButton for viewing game history
     */
    public JButton getHistoryButton() {
        return historyButton;
    }

    /**
     * Get the Exit button
     * @return JButton for exiting the application
     */
    public JButton getExitButton() {
        return exitButton;
    }

    /**
     * Test main method for standalone preview
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Minesweeper - Start Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new StartMenuView());
            frame.setSize(600, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}