package minesweeper.view;


import minesweeper.model.Question;
import minesweeper.model.QuestionDifficulty;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * View component for the Question Wizard screen.
 * Based on project wireframes athestic
 */
public class QuestionWizardView extends JFrame {

    // ==================== Color Scheme ====================
    private static final Color BACKGROUND_DARK = new Color(13, 17, 23);
    private static final Color BACKGROUND_DARKER = new Color(8, 12, 18);
    private static final Color BACKGROUND_TABLE = new Color(22, 27, 34);
    private static final Color BACKGROUND_ROW_ALT = new Color(28, 33, 40);
    private static final Color BACKGROUND_HEADER = new Color(15, 20, 27);

    private static final Color TEXT_PRIMARY = new Color(230, 237, 243);
    private static final Color TEXT_SECONDARY = new Color(125, 133, 144);
    private static final Color TEXT_HEADER = new Color(88, 166, 255);

    private static final Color ACCENT_CYAN = new Color(56, 189, 248);
    private static final Color ACCENT_GLOW = new Color(56, 189, 248, 100);

    private static final Color DIFFICULTY_EASY = new Color(63, 185, 80);
    private static final Color DIFFICULTY_MEDIUM = new Color(210, 153, 34);
    private static final Color DIFFICULTY_HARD = new Color(248, 81, 73);
    private static final Color DIFFICULTY_EXPERT = new Color(163, 113, 247);

    private static final Color CORRECT_GREEN = new Color(63, 185, 80);
    private static final Color BORDER_COLOR = new Color(48, 54, 61);

    // ==================== UI Components ====================
    private JTable questionTable;
    private QuestionTableModel tableModel;
    private TableRowSorter<QuestionTableModel> sorter;

    private JPanel mainPanel;
    private JPanel emptyStatePanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel statusLabel;

    private JButton uploadButton;
    private JButton backButton;
    private JButton reloadButton;// US-14 Search & Filter
    private JTextField searchField;
    private JComboBox<QuestionDifficulty> difficultyCombo;
    private JButton searchButton;
    private JScrollPane tableScrollPane;

    // CRUD operation buttons
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;

    private String loadedFileName = "";

    // Action listeners
    private ActionListener uploadListener;
    private ActionListener backListener;
    private ActionListener reloadListener;
    private ActionListener searchListener;

    // CRUD action listeners
    private ActionListener createListener;
    private ActionListener editListener;
    private ActionListener deleteListener;

    // Constants
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 650;

    public QuestionWizardView() {
        initializeFrame();
        createComponents();
        layoutComponents();
        showEmptyState();
    }

    private void initializeFrame() {
        setTitle("Question Wizard");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_DARK);
    }

    private void createComponents() {
        // Create header panel with title
        headerPanel = createHeaderPanel();

        // Create table
        createTable();

        // Create buttons with dark theme
        uploadButton = createStyledButton("Upload CSV", ACCENT_CYAN);
        backButton = createStyledButton("← Back to Start", TEXT_SECONDARY);
        reloadButton = createStyledButton("Reload", TEXT_SECONDARY);
        reloadButton.setEnabled(false);

        // CRUD buttons
        createButton = createStyledButton("+ Create", new Color(63, 185, 80)); // Green
        editButton = createStyledButton("Edit", ACCENT_CYAN);
        deleteButton = createStyledButton("Delete", new Color(248, 81, 73)); // Red
        createButton.setEnabled(true); // Always enabled
        editButton.setEnabled(false); // Enabled when row selected
        deleteButton.setEnabled(false); // Enabled when row selected

        // US-14 Search & Filter components
        searchField = new JTextField(16);
        searchField.setBackground(BACKGROUND_TABLE);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.addActionListener(e -> searchButton.doClick());
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        difficultyCombo = new JComboBox<>();
        difficultyCombo.setBackground(BACKGROUND_TABLE);
        difficultyCombo.setForeground(TEXT_PRIMARY);
        difficultyCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        difficultyCombo.addItem(null); // All
        for (QuestionDifficulty d : QuestionDifficulty.values()) {
            difficultyCombo.addItem(d);
        }
        // Custom renderer to show "All" instead of empty for null value
        difficultyCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("All");
                }
                return this;
            }
        });

        searchButton = createStyledButton("Search", ACCENT_CYAN);
        searchButton.addActionListener(e -> {
            System.out.println("SEARCH BUTTON CLICKED ✅");
            if (searchListener != null) searchListener.actionPerformed(e);
        });
        // Wire up button actions
        searchButton.addActionListener(e -> System.out.println("SEARCH CLICK ✅"));

        uploadButton.addActionListener(e -> {
            if (uploadListener != null) uploadListener.actionPerformed(e);
        });
        backButton.addActionListener(e -> {
            if (backListener != null) backListener.actionPerformed(e);
        });
        reloadButton.addActionListener(e -> {
            if (reloadListener != null) reloadListener.actionPerformed(e);
        });

        // CRUD button actions
        createButton.addActionListener(e -> {
            if (createListener != null) createListener.actionPerformed(e);
        });
        editButton.addActionListener(e -> {
            if (editListener != null) editListener.actionPerformed(e);
        });
        deleteButton.addActionListener(e -> {
            if (deleteListener != null) deleteListener.actionPerformed(e);
        });

        // Status label
        statusLabel = new JLabel("No file loaded");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);

        // Empty state panel
        emptyStatePanel = createEmptyStatePanel();

        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_DARK);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 0, 15, 0));

        // Title with glow effect
        titleLabel = new JLabel("QUESTION WIZARD") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Draw glow
                g2d.setColor(ACCENT_GLOW);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent();

                for (int i = 0; i < 3; i++) {
                    g2d.drawString(getText(), x, y);
                }

                // Draw main text
                g2d.setColor(TEXT_PRIMARY);
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        subtitleLabel = new JLabel("No file loaded");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitleLabel);

        return panel;
    }

    private void createTable() {
        tableModel = new QuestionTableModel();
        questionTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                // Alternate row colors
                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        comp.setBackground(BACKGROUND_TABLE);
                    } else {
                        comp.setBackground(BACKGROUND_ROW_ALT);
                    }
                } else {
                    comp.setBackground(new Color(56, 189, 248, 40));
                }

                return comp;
            }
        };

        // Table styling
        questionTable.setBackground(BACKGROUND_TABLE);
        questionTable.setForeground(TEXT_PRIMARY);
        questionTable.setGridColor(BORDER_COLOR);
        questionTable.setRowHeight(45);
        questionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        questionTable.setSelectionBackground(new Color(56, 189, 248, 40));
        questionTable.setSelectionForeground(TEXT_PRIMARY);
        questionTable.setShowHorizontalLines(true);
        questionTable.setShowVerticalLines(false);
        questionTable.setIntercellSpacing(new Dimension(0, 1));
        questionTable.setFillsViewportHeight(true);
        questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header styling
        JTableHeader header = questionTable.getTableHeader();
        header.setBackground(BACKGROUND_HEADER);
        header.setForeground(TEXT_HEADER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        // Custom header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(BACKGROUND_HEADER);
                label.setForeground(TEXT_HEADER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                label.setHorizontalAlignment(column == 0 ? JLabel.LEFT :
                        (column == 1 || column == 2) ? JLabel.LEFT : JLabel.CENTER);
                return label;
            }
        });

        // Enable sorting
        sorter = new TableRowSorter<>(tableModel);
        questionTable.setRowSorter(sorter);

        // Custom comparator for DIFFICULTY column (index 1) to sort by difficulty value
        sorter.setComparator(1, new java.util.Comparator<String>() {
            @Override
            public int compare(String d1, String d2) {
                int v1 = getDifficultyValue(d1);
                int v2 = getDifficultyValue(d2);
                return Integer.compare(v1, v2);
            }

            private int getDifficultyValue(String difficulty) {
                if (difficulty == null) return 0;
                switch (difficulty.toUpperCase()) {
                    case "EASY": return 1;
                    case "MEDIUM": return 2;
                    case "HARD": return 3;
                    case "EXPERT": return 4;
                    default: return 0;
                }
            }
        });

        // Set column widths
        TableColumnModel columnModel = questionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(70);   // ID
        columnModel.getColumn(1).setPreferredWidth(90);   // Difficulty
        columnModel.getColumn(2).setPreferredWidth(250);  // Question
        columnModel.getColumn(3).setPreferredWidth(100);  // A
        columnModel.getColumn(4).setPreferredWidth(100);  // B
        columnModel.getColumn(5).setPreferredWidth(100);  // C
        columnModel.getColumn(6).setPreferredWidth(100);  // D
        columnModel.getColumn(7).setPreferredWidth(70);   // Correct

        // Custom renderers
        columnModel.getColumn(0).setCellRenderer(new IDCellRenderer());
        columnModel.getColumn(1).setCellRenderer(new DifficultyCellRenderer());
        columnModel.getColumn(2).setCellRenderer(new QuestionTextRenderer());
        columnModel.getColumn(3).setCellRenderer(new OptionCellRenderer());
        columnModel.getColumn(4).setCellRenderer(new OptionCellRenderer());
        columnModel.getColumn(5).setCellRenderer(new OptionCellRenderer());
        columnModel.getColumn(6).setCellRenderer(new OptionCellRenderer());
        columnModel.getColumn(7).setCellRenderer(new CorrectAnswerRenderer());

        // Enable/disable Edit and Delete based on table selection
        questionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = questionTable.getSelectedRow() >= 0;
                editButton.setEnabled(rowSelected);
                deleteButton.setEnabled(rowSelected);
            }
        });

        // Scroll pane
        tableScrollPane = new JScrollPane(questionTable);
        tableScrollPane.setBackground(BACKGROUND_DARK);
        tableScrollPane.getViewport().setBackground(BACKGROUND_TABLE);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color);
                } else {
                    g2d.setColor(BACKGROUND_TABLE);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border
                g2d.setColor(isEnabled() ? color : TEXT_SECONDARY.darker());
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2d.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(color);
        button.setBackground(BACKGROUND_TABLE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_DARK);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_DARK);

        // Icon
        JLabel iconLabel = new JLabel("📋") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Glow effect
                g2d.setColor(ACCENT_GLOW);
                g2d.setFont(getFont());
                g2d.drawString(getText(), 2, getHeight() - 5);

                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, getHeight() - 7);
                g2d.dispose();
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setForeground(ACCENT_CYAN);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("No Questions Loaded");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel("Upload a CSV file to view the question bank");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLbl.setForeground(TEXT_SECONDARY);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton emptyUploadBtn = createStyledButton("Upload CSV File", ACCENT_CYAN);
        emptyUploadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyUploadBtn.addActionListener(e -> {
            if (uploadListener != null) uploadListener.actionPerformed(e);
        });

        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(titleLbl);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLbl);
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(emptyUploadBtn);

        panel.add(centerPanel);
        return panel;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_DARK);

        // Top section with header and toolbar
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BACKGROUND_DARK);
        topSection.add(headerPanel, BorderLayout.CENTER);

        // Toolbar
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBackground(BACKGROUND_DARK);
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtons.setBackground(BACKGROUND_DARK);
        leftButtons.add(backButton);
        leftButtons.add(Box.createHorizontalStrut(15)); // Spacer
        leftButtons.add(createButton);
        leftButtons.add(editButton);
        leftButtons.add(deleteButton);
        leftButtons.add(Box.createHorizontalStrut(25)); // Spacer before search

        // Search components in left panel for better positioning
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setForeground(TEXT_SECONDARY);
        leftButtons.add(searchLbl);
        leftButtons.add(searchField);

        JLabel diffLbl = new JLabel("Difficulty:");
        diffLbl.setForeground(TEXT_SECONDARY);
        leftButtons.add(diffLbl);
        leftButtons.add(difficultyCombo);
        leftButtons.add(searchButton);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setBackground(BACKGROUND_DARK);
        rightButtons.add(statusLabel);
        rightButtons.add(Box.createHorizontalStrut(10));
        rightButtons.add(reloadButton);
        rightButtons.add(uploadButton);

        toolbarPanel.add(leftButtons, BorderLayout.WEST);
        toolbarPanel.add(rightButtons, BorderLayout.EAST);

        topSection.add(toolbarPanel, BorderLayout.SOUTH);

        // Content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(BACKGROUND_DARK);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentWrapper.add(mainPanel, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);
    }

    // ==================== Public API ====================

    public void showWindow() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        toFront();
    }

    public void hideWindow() {
        setVisible(false);
    }
    public void bindQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            showEmptyState();
            subtitleLabel.setText("No file loaded");
            statusLabel.setText("No questions loaded");
            return;
        }

        tableModel.setQuestions(questions);

        mainPanel.removeAll();
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        subtitleLabel.setText("Loaded from: " + (loadedFileName.isEmpty() ? "questions.csv" : loadedFileName));
        statusLabel.setText(questions.size() + " questions loaded");
        statusLabel.setForeground(DIFFICULTY_EASY);
        reloadButton.setEnabled(true);
    }

    public void setLoadedFileName(String fileName) {
        this.loadedFileName = fileName;
        subtitleLabel.setText("Loaded from: " + fileName);
    }

    public void showLoadError(String msg) {
        JOptionPane optionPane = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Load Error");
        dialog.setVisible(true);

        statusLabel.setText("Load failed");
        statusLabel.setForeground(DIFFICULTY_HARD);
    }

    public void showEmptyState() {
        mainPanel.removeAll();
        mainPanel.add(emptyStatePanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        subtitleLabel.setText("No file loaded");
        statusLabel.setText("No file loaded");
        statusLabel.setForeground(TEXT_SECONDARY);
        reloadButton.setEnabled(false);
    }

    public File showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Questions CSV File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            loadedFileName = selected.getName();
            return selected;
        }
        return null;
    }

    public void showParseWarnings(List<String> errors) {
        if (errors == null || errors.isEmpty()) return;

        StringBuilder message = new StringBuilder();
        message.append("Some rows could not be parsed:\n\n");

        int displayCount = Math.min(errors.size(), 5);
        for (int i = 0; i < displayCount; i++) {
            message.append("• ").append(errors.get(i)).append("\n");
        }

        if (errors.size() > 5) {
            message.append("\n... and ").append(errors.size() - 5).append(" more errors.");
        }

        JOptionPane.showMessageDialog(this, message.toString(), "Parse Warnings",
                JOptionPane.WARNING_MESSAGE);
    }

    public void hideError() {
        // Error banner removed in this design - errors shown as dialogs
    }

    // ==================== Listener Setters ====================

    public void setUploadListener(ActionListener listener) {
        this.uploadListener = listener;
    }

    public void setBackListener(ActionListener listener) {
        this.backListener = listener;
    }

    public void setReloadListener(ActionListener listener) {
        this.reloadListener = listener;
    }
    // US-14 API
    public void setSearchListener(ActionListener listener) {
        System.out.println("VIEW setSearchListener ✅ searchButton=" + searchButton);

        searchButton.addActionListener(e -> System.out.println("SEARCH CLICK ✅"));
        searchButton.addActionListener(listener);
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public QuestionDifficulty getSelectedDifficulty() {
        return (QuestionDifficulty) difficultyCombo.getSelectedItem();
    }

    public void setCreateListener(ActionListener listener) {
        this.createListener = listener;
    }

    public void setEditListener(ActionListener listener) {
        this.editListener = listener;
    }

    public void setDeleteListener(ActionListener listener) {
        this.deleteListener = listener;
    }

    /**
     * Gets the currently selected question from the table.
     *
     * @return The selected Question, or null if no row is selected
     */
    public Question getSelectedQuestion() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        // Convert view row to model row (in case table is sorted)
        int modelRow = questionTable.convertRowIndexToModel(selectedRow);
        return tableModel.getQuestionAt(modelRow);
    }

    // ==================== Custom Cell Renderers ====================

    /**
     * Renders ID column in format "QST-001"
     */
    private class IDCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setText(String.format("QST-%03d", (Integer) value));
            }

            setForeground(TEXT_SECONDARY);
            setFont(new Font("Consolas", Font.PLAIN, 12));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

            return this;
        }
    }

    /**
     * Renders difficulty with colored badge
     */
    private class DifficultyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            panel.setOpaque(true);

            // Set background based on row
            if (!isSelected) {
                panel.setBackground(row % 2 == 0 ? BACKGROUND_TABLE : BACKGROUND_ROW_ALT);
            } else {
                panel.setBackground(new Color(56, 189, 248, 40));
            }

            if (value != null) {
                String diffText = value.toString().toUpperCase();
                Color badgeColor;

                switch (diffText) {
                    case "EASY":
                        badgeColor = DIFFICULTY_EASY;
                        break;
                    case "MEDIUM":
                        badgeColor = DIFFICULTY_MEDIUM;
                        break;
                    case "HARD":
                        badgeColor = DIFFICULTY_HARD;
                        break;
                    default:
                        badgeColor = DIFFICULTY_EXPERT;
                        diffText = "EXPERT";
                }

                JLabel badge = new JLabel(diffText) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                    }
                };
                badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                badge.setForeground(badgeColor);

                panel.add(badge);
            }

            return panel;
        }
    }

    /**
     * Renders question text
     */
    private class QuestionTextRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setForeground(TEXT_PRIMARY);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            if (value != null) {
                String text = value.toString();
                setText(text);
                setToolTipText(text);
            }

            return this;
        }
    }

    /**
     * Renders option cells (A, B, C, D)
     */
    private class OptionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setForeground(TEXT_PRIMARY);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setHorizontalAlignment(JLabel.CENTER);

            if (value != null) {
                String text = value.toString();
                setText(text);
                setToolTipText(text);
            }

            return this;
        }
    }

    /**
     * Renders correct answer with green highlight
     */
    private class CorrectAnswerRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setForeground(CORRECT_GREEN);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setHorizontalAlignment(JLabel.CENTER);

            return this;
        }
    }

    // ==================== Table Model ====================

    private static class QuestionTableModel extends AbstractTableModel {

        private final String[] columnNames = {
                "ID", "DIFFICULTY", "QUESTION TEXT", "A", "B", "C", "D", "CORRECT"
        };

        private List<Question> questions = new ArrayList<>();

        public void setQuestions(List<Question> questions) {
            this.questions = new ArrayList<>(questions);
            // Auto-sort by difficulty: EASY → MEDIUM → HARD → EXPERT
            this.questions.sort(java.util.Comparator.comparingInt(q -> q.getDifficulty().getValue()));
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return questions.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Integer.class;
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= questions.size()) return null;

            Question q = questions.get(rowIndex);
            switch (columnIndex) {
                case 0: return q.getId();
                case 1: return q.getDifficulty().toString();     // EASY/MEDIUM/HARD
                case 2: return q.getText();                      // ✅ נכון
                case 3: return q.getOptionA();
                case 4: return q.getOptionB();
                case 5: return q.getOptionC();
                case 6: return q.getOptionD();
                case 7: return String.valueOf(q.getCorrectOption()); // ✅ נכון
                default: return null;
            }
        }

        /**
         * Gets the question at the specified row index.
         *
         * @param rowIndex The row index
         * @return The Question at that row, or null if invalid index
         */
        public Question getQuestionAt(int rowIndex) {
            if (rowIndex < 0 || rowIndex >= questions.size()) {
                return null;
            }
            return questions.get(rowIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}