package minesweeper.view;
import minesweeper.view.components.NeonButtonFactory;
import minesweeper.view.components.NeonTooltip;
import minesweeper.view.components.PlaceholderTextField;
import javax.swing.*;
import java.awt.*;
import minesweeper.view.components.NeonDialog;
import minesweeper.model.GameSession;




/**
 * NewGamePanel - a basic JPanel "New Game" setup with a holographic / neon console theme.
 * This is a starting implementation focusing on layout and the two bottom buttons ("Start Game", "Back").
 * Features:
 * - Header title
 * - Two text inputs for Player 1 and Player 2
 * - Difficulty toggle chips (Easy/Medium/Hard)
 * - Right-side info card showing board presets
 * - Two neon buttons at the bottom (Start Game - bright blue, Back - dimmer blue)
 * - Simple dark/cosmic background and soft scanline overlay*/


public class NewGameView extends JPanel {
    // Components
    private JLabel headerLabel;

    private JLabel player1Label;
    //private JTextField player1Field;

    private JLabel player2Label;
    //private JTextField player2Field;
    private PlaceholderTextField player1Field;
    private PlaceholderTextField player2Field;


    private JLabel difficultyLabel;
    private JToggleButton easyToggle;
    private JToggleButton mediumToggle;
    private JToggleButton hardToggle;
    private ButtonGroup difficultyGroup;

    private JButton startButton;
    private JButton backButton;
    private NeonTooltip tooltip;

    /*
    private JPanel infoCardPanel;
    private JLabel infoTitleLabel;
    private JLabel easyInfo;
    private JLabel mediumInfo;
    private JLabel hardInfo;

    private JPanel scanLineOverlay;
    */
    private static final Color NEON_GREEN = new Color(0, 255, 180);
    private static final Color NEON_RED   = new Color(255, 80, 80);

    public NewGameView() {
        initComponents();
        fetchAndRefresh();
        createEvents();
    }
        private void initComponents() {

            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            setBackground(new Color(15, 15, 20));  // dark background
            setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));


            // =========================================
            // HEADER
            // =========================================
            headerLabel = new JLabel("START NEW GAME");
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            headerLabel.setForeground(new Color(255, 255, 255));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 10, 30, 10);
            gbc.anchor = GridBagConstraints.CENTER;
            add(headerLabel, gbc);


            // =========================================
            // PLAYER 1 FIELD (NEON)
            // =========================================
            player1Field = new PlaceholderTextField("Player 1 Name", 20);
            styleNeonTextField(player1Field);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(player1Field, gbc);


            // =========================================
            // PLAYER 2 FIELD (NEON)
            // =========================================
            player2Field = new PlaceholderTextField("Player 2 Name", 20);
            styleNeonTextField(player2Field);

            gbc.gridy = 2;
            add(player2Field, gbc);

            //limitTextLength(player1Field, 20);
            //limitTextLength(player2Field, 20);

            //player1Field.setToolTipText("Name must be 1–20 characters");
            //player2Field.setToolTipText("Name must be 1–20 characters");

            addLiveValidation(player1Field);
            addLiveValidation(player2Field);

            /*tooltip = new NeonTooltip(
                    SwingUtilities.getWindowAncestor(this)
            );*/



            // =========================================
            // DIFFICULTY LABEL
            // =========================================
            difficultyLabel = new JLabel("SELECT DIFFICULTY");
            difficultyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            difficultyLabel.setForeground(Color.WHITE);

            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(25, 10, 10, 10);
            add(difficultyLabel, gbc);


            // =========================================
            // DIFFICULTY BUTTON PANEL
            // =========================================
            JPanel difficultyPanel = new JPanel(new GridBagLayout());
            difficultyPanel.setOpaque(false);  // IMPORTANT
            GridBagConstraints dgbc = new GridBagConstraints();
            dgbc.insets = new Insets(5, 10, 5, 10);

            easyToggle = NeonButtonFactory.createNeonToggle("EASY", new Color(0, 255, 120));
            mediumToggle = NeonButtonFactory.createNeonToggle("MEDIUM", new Color(255, 210, 0));
            hardToggle = NeonButtonFactory.createNeonToggle("HARD", new Color(255, 60, 60));

            difficultyGroup = new ButtonGroup();
            difficultyGroup.add(easyToggle);
            difficultyGroup.add(mediumToggle);
            difficultyGroup.add(hardToggle);

            dgbc.gridx = 0; difficultyPanel.add(easyToggle, dgbc);
            dgbc.gridx = 1; difficultyPanel.add(mediumToggle, dgbc);
            dgbc.gridx = 2; difficultyPanel.add(hardToggle, dgbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(5, 10, 20, 10);
            add(difficultyPanel, gbc);


            // =========================================
            // BUTTON PANEL (START / BACK)
            // =========================================
            JPanel buttonPanel = new JPanel(new GridBagLayout());
            buttonPanel.setOpaque(false); // IMPORTANT
            GridBagConstraints bgbc = new GridBagConstraints();
            bgbc.insets = new Insets(10, 20, 10, 20);

            startButton = NeonButtonFactory.createNeonButton("START GAME", new Color(0, 180, 255));
            backButton  = NeonButtonFactory.createNeonButton("BACK", new Color(180, 80, 255));
            // Button always enabled - validation happens on click with warning message

            bgbc.gridx = 0; buttonPanel.add(startButton, bgbc);
            bgbc.gridx = 1; buttonPanel.add(backButton, bgbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(25, 10, 10, 10);
            add(buttonPanel, gbc);
        }




    private void createEvents() {
        easyToggle.addActionListener(e -> {
            showDifficultyInfo(GameSession.Difficulty.EASY);
            updateStartButtonState();
        });

        mediumToggle.addActionListener(e -> {
            showDifficultyInfo(GameSession.Difficulty.MEDIUM);
            updateStartButtonState();
        });

        hardToggle.addActionListener(e -> {
            showDifficultyInfo(GameSession.Difficulty.HARD);
            updateStartButtonState();
        });

    }

    private void fetchAndRefresh() {}
    // Getters for controller
    public JButton getStartButton() { return startButton; };
    public JButton getBackButton() { return backButton; }
    public JTextField getPlayer1Field() { return player1Field; }
    public JTextField getPlayer2Field() { return player2Field; }
    public JToggleButton getEasyToggle() { return easyToggle; }
    public JToggleButton getMediumToggle() { return mediumToggle; }
    public JToggleButton getHardToggle() { return hardToggle; }


    private void styleNeonTextField(JTextField field) {
        field.setBackground(new Color(25, 25, 30));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 180), 2, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }

    private void showDifficultyInfo(GameSession.Difficulty d) {

        int gridSize = d.gridSize;
        int mines = d.mines;
        int questions = d.questions;
        int surprises = d.surprises;

        int safeCells = gridSize * gridSize - mines - questions - surprises;

        String message =
                "<html><body style='color:white; font-family:Segoe UI; font-size:14px;'>" +

                        "<span style='font-size:20px; font-weight:bold;'>Difficulty: " + d.name() + "</span><br>" +
                        "<b>Grid Size:</b> " + gridSize + " × " + gridSize + "<br><br>" +

                        "<span style='color:#00FFB4; font-size:16px;'><b>Cells Breakdown:</b></span><br><br>" +

                        "<img src='" + getClass().getResource("/assets/mine.png") +
                        "' width='22' height='22'> Mines: " + mines + "<br>" +

                                "<img src='" + getClass().getResource("/assets/question.png") +
                                "' width='22' height='22'> Question Cells: " + questions + "<br>" +

                                        "<img src='" + getClass().getResource("/assets/surprise.png") +
                                        "' width='22' height='22'> Surprise Cells: " + surprises + "<br>" +

                                                "<img src='" + getClass().getResource("/assets/tile.png") +
                                                "' width='22' height='22'> Safe/Number Cells: " + safeCells + "<br><br>" +

                                                        "<span style='color:#00FFB4; font-size:16px;'><b>Additional Info:</b></span><br><br>" +
                                                        "Starting Lives: " + d.startingLives + "<br>" +
                                                        "Activation Cost: " + d.activationCost + "<br>" +

                                                        "</body></html>";

        NeonDialog.showNeonDialog(
                SwingUtilities.getWindowAncestor(this),
                "Difficulty Details",
                message,
                null, true, false
        );
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("New Game Setup Preview");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                NewGameView panel = new NewGameView();
                frame.setContentPane(panel);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private void limitTextLength(JTextField field, int maxLength) {
        field.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;

                if ((getLength() + str.length()) <= maxLength) {
                    super.insertString(offs, str, a);
                }
            }
        });
    }


    private void setFieldError(JTextField field, boolean error) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        error ? NEON_RED : NEON_GREEN,
                        2,
                        true
                ),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }

    private boolean isNameValid(PlaceholderTextField field) {
        if (field.isPlaceholderVisible()) return false;
        String trimmed = field.getText().trim();
        return !trimmed.isEmpty() && trimmed.length() <= 20;
    }


    private void updateStartButtonState() {
        // Button always enabled - validation with warning message happens on click
        // Visual feedback is provided through field borders (red/green)
    }

    private void addLiveValidation(JTextField field) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            private void validate() {
                PlaceholderTextField ptf = (PlaceholderTextField) field;
                boolean invalid = !isNameValid(ptf);
                setFieldError(field, invalid);

                updateStartButtonState();

                if (tooltip != null && invalid && field.hasFocus()) {
                    tooltip.showTooltip(field, "Name must be 1–20 characters");
                } else if (tooltip != null) {
                    tooltip.hideTooltip();
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { validate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validate(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validate(); }
        });

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tooltip != null) tooltip.hideTooltip();
            }
        });
    }


    @Override
    public void addNotify() {
        super.addNotify();
        tooltip = new NeonTooltip(
                SwingUtilities.getWindowAncestor(this)
        );
    }




}


