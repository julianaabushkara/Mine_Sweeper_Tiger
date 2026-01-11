package minesweeper.view;

import minesweeper.model.MutableQuestion;
import minesweeper.model.QuestionDifficulty;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;

/**
 * Modal dialog for creating and editing questions.
 * Uses the dark theme consistent with the Question Wizard.
 */
public class QuestionEditorDialog extends JDialog {

    // Color scheme matching QuestionWizardView
    private static final Color BACKGROUND_DARK = new Color(13, 17, 23);
    private static final Color BACKGROUND_DARKER = new Color(8, 12, 18);
    private static final Color TEXT_PRIMARY = new Color(230, 237, 243);
    private static final Color TEXT_SECONDARY = new Color(125, 133, 144);
    private static final Color ACCENT_CYAN = new Color(56, 189, 248);
    private static final Color BORDER_COLOR = new Color(48, 54, 61);
    private static final Color ERROR_RED = new Color(248, 81, 73);

    private MutableQuestion question;
    private boolean confirmed = false;

    // UI Components
    private JTextField idField;
    private JTextArea questionTextArea;
    private JTextField optionAField;
    private JTextField optionBField;
    private JTextField optionCField;
    private JTextField optionDField;
    private JComboBox<Character> correctAnswerCombo;
    private JComboBox<QuestionDifficulty> difficultyCombo;
    private JLabel errorLabel;

    /**
     * Creates a new dialog for creating a question.
     *
     * @param parent The parent frame
     */
    public QuestionEditorDialog(Frame parent) {
        this(parent, new MutableQuestion(), "Create New Question");
    }

    /**
     * Creates a new dialog for editing an existing question.
     *
     * @param parent The parent frame
     * @param question The question to edit
     */
    public QuestionEditorDialog(Frame parent, MutableQuestion question) {
        this(parent, question, "Edit Question");
    }

    /**
     * Internal constructor.
     */
    private QuestionEditorDialog(Frame parent, MutableQuestion question, String title) {
        super(parent, title, true);
        this.question = question;

        initializeDialog();
        createComponents();
        populateFields();
        layoutComponents();
    }

    private void initializeDialog() {
        setSize(750, 700);
        setMinimumSize(new Dimension(600, 550));
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_DARK);
    }

    private void createComponents() {
        // ID Field
        idField = createStyledTextField();
        idField.setToolTipText("Question ID (must be unique and positive)");

        // Question Text Area (larger for longer questions)
        questionTextArea = new JTextArea(8, 50);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        styleTextComponent(questionTextArea);

        // Option Fields
        optionAField = createStyledTextField();
        optionBField = createStyledTextField();
        optionCField = createStyledTextField();
        optionDField = createStyledTextField();

        // Correct Answer Combo
        correctAnswerCombo = new JComboBox<>(new Character[]{'A', 'B', 'C', 'D'});
        styleComboBox(correctAnswerCombo);

        // Difficulty Combo
        difficultyCombo = new JComboBox<>(QuestionDifficulty.values());
        styleComboBox(difficultyCombo);

        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(ERROR_RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void populateFields() {
        idField.setText(String.valueOf(question.getId() > 0 ? question.getId() : ""));
        questionTextArea.setText(question.getText());
        optionAField.setText(question.getOptionA());
        optionBField.setText(question.getOptionB());
        optionCField.setText(question.getOptionC());
        optionDField.setText(question.getOptionD());
        correctAnswerCombo.setSelectedItem(question.getCorrectOption());
        difficultyCombo.setSelectedItem(question.getDifficulty());
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND_DARK);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        int row = 0;

        // ID Field
        addFormRow(formPanel, gbc, row++, "ID:", idField);

        // Question Text
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel questionLabel = createStyledLabel("Question:");
        formPanel.add(questionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane questionScroll = new JScrollPane(questionTextArea);
        questionScroll.setMinimumSize(new Dimension(400, 150));
        questionScroll.setPreferredSize(new Dimension(500, 180));
        styleScrollPane(questionScroll);
        formPanel.add(questionScroll, gbc);
        row++;

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Options
        addFormRow(formPanel, gbc, row++, "Option A:", optionAField);
        addFormRow(formPanel, gbc, row++, "Option B:", optionBField);
        addFormRow(formPanel, gbc, row++, "Option C:", optionCField);
        addFormRow(formPanel, gbc, row++, "Option D:", optionDField);

        // Correct Answer
        addFormRow(formPanel, gbc, row++, "Correct Answer:", correctAnswerCombo);

        // Difficulty
        addFormRow(formPanel, gbc, row++, "Difficulty:", difficultyCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Error Panel
        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        errorPanel.setBackground(BACKGROUND_DARK);
        errorPanel.add(errorLabel);
        mainPanel.add(errorPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                           String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(createStyledLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(component, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(BACKGROUND_DARK);

        JButton saveButton = createStyledButton("Save", ACCENT_CYAN);
        saveButton.addActionListener(e -> handleSave());

        JButton cancelButton = createStyledButton("Cancel", TEXT_SECONDARY);
        cancelButton.addActionListener(e -> dispose());

        panel.add(cancelButton);
        panel.add(saveButton);

        return panel;
    }

    private void handleSave() {
        try {
            // Parse and validate ID
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showError("ID cannot be empty");
                return;
            }

            int id = Integer.parseInt(idText);
            question.setId(id);

            // Set all other fields
            question.setText(questionTextArea.getText().trim());
            question.setOptionA(optionAField.getText().trim());
            question.setOptionB(optionBField.getText().trim());
            question.setOptionC(optionCField.getText().trim());
            question.setOptionD(optionDField.getText().trim());
            question.setCorrectOption((Character) correctAnswerCombo.getSelectedItem());
            question.setDifficulty((QuestionDifficulty) difficultyCombo.getSelectedItem());

            // Validate
            if (!question.isValid()) {
                List<String> errors = question.getValidationErrors();
                showError(String.join("\n", errors));
                return;
            }

            confirmed = true;
            dispose();

        } catch (NumberFormatException ex) {
            showError("ID must be a valid number");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    /**
     * Returns whether the user confirmed the changes.
     *
     * @return true if Save was clicked, false if Cancel or closed
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the edited question.
     *
     * @return The MutableQuestion with user's changes
     */
    public MutableQuestion getQuestion() {
        return question;
    }

    // Styling Methods

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(30);
        styleTextComponent(field);
        return field;
    }

    private void styleTextComponent(JTextComponent component) {
        component.setBackground(BACKGROUND_DARKER);
        component.setForeground(TEXT_PRIMARY);
        component.setCaretColor(ACCENT_CYAN);
        component.setFont(new Font("Consolas", Font.PLAIN, 14));
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(BACKGROUND_DARKER);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBackground(BACKGROUND_DARKER);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(BACKGROUND_DARKER);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_SECONDARY);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setForeground(color);
        button.setBackground(BACKGROUND_DARKER);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(color.getRed(), color.getGreen(),
                        color.getBlue(), 30));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND_DARKER);
            }
        });

        return button;
    }
}
