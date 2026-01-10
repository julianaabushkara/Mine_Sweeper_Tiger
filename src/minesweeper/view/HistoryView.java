package minesweeper.view;

import minesweeper.controller.GameHistoryLogic;
import minesweeper.model.GameHistory;
import minesweeper.view.components.*;

import minesweeper.model.HistoryFilterStrategy.*;
import java.util.List;

import java.awt.Color;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class HistoryView extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;


    // --- FILTER CONTROLS ---
    private JComboBox<String> userCombo;
    private JComboBox<String> resultCombo;      // ALL / WIN / LOSE
    private JComboBox<String> difficultyCombo;  // ALL / EASY / MEDIUM / HARD

    private JButton scoreSortBtn;
    private enum SortMode { JSON, SCORE_ASC, SCORE_DESC }
    private SortMode sortMode = SortMode.JSON;   // start with JSON order

    JPanel topArea;
    JPanel filtersPanel;


    // remember current sort
    private String scoreOrder = "DESC";

    private JButton refreshButton;
    private JButton backButton;
    private JLabel title;
    private JLabel userLabel;
    private JLabel ResultLabel;
    private JLabel DifficultyLabel;
    private String username;
    private boolean isUpdatingCombos = false;


    JScrollPane scrollPane;
    JPanel buttonPanel;
    JTableHeader header;
    JPanel titlePanel;

    public HistoryView() {
        initComponents();
        rebuildUserCombo();

        fetchAndRefresh();
        createEvents();


    }

    private void initComponents() {

        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 20));

        // =======================
        // TITLE
        // =======================
        title = new JLabel("GAME HISTORY", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(20, 20, 30));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0)); // spacing above and below

        titlePanel.add(title, BorderLayout.CENTER);
        //add(titlePanel, BorderLayout.NORTH);

        // ---- TOP AREA: title + filters ----
        topArea = new JPanel(new BorderLayout());
        topArea.setBackground(new Color(20, 20, 30));
        topArea.add(titlePanel, BorderLayout.NORTH);

        // Filters row
        filtersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        filtersPanel.setBackground(new Color(20, 20, 30));

        /*
        userCombo = new JComboBox<>(new String[]{"ALL"});
        resultCombo = new JComboBox<>(new String[]{"ALL", "WIN", "LOSE"});
        difficultyCombo = new JComboBox<>(new String[]{"ALL", "EASY", "MEDIUM", "HARD"});

         */
        userCombo = NeonComboBoxFactory.createNeonComboBox(new String[]{"ALL"}, new Color(0, 180, 255));
        resultCombo = NeonComboBoxFactory.createNeonComboBox(new String[]{"ALL","WIN","LOSE"}, new Color(0, 180, 255));
        difficultyCombo = NeonComboBoxFactory.createNeonComboBox(new String[]{"ALL","EASY","MEDIUM","HARD"}, new Color(0, 180, 255));

        //scoreAscBtn = NeonButtonFactory.createNeonButton("Score ↑", new Color(0, 180, 255));
        //scoreDescBtn = NeonButtonFactory.createNeonButton("Score ↓", new Color(0, 180, 255));
        scoreSortBtn = NeonButtonFactory.createNeonButton("Score", new Color(0, 180, 255));
        scoreSortBtn.setFont(new Font("Dialog", Font.BOLD, 18)); // now it's not null

        // optional: make combos look nicer on dark bg
        userCombo.setPreferredSize(new Dimension(140, 30));
        resultCombo.setPreferredSize(new Dimension(110, 30));
        difficultyCombo.setPreferredSize(new Dimension(130, 30));

        userLabel = new JLabel("User:");
        userLabel.setForeground(Color.WHITE);
        filtersPanel.add(userLabel);

        //filtersPanel.add(new JLabel("User:"));

        filtersPanel.add(userCombo);
        ResultLabel = new JLabel("Result:");
        ResultLabel.setForeground(Color.WHITE);
        filtersPanel.add(ResultLabel);

        filtersPanel.add(resultCombo);
        DifficultyLabel = new JLabel("Difficulty:");
        DifficultyLabel.setForeground(Color.WHITE);
        filtersPanel.add(DifficultyLabel);

        filtersPanel.add(difficultyCombo);
        //filtersPanel.add(scoreAscBtn);
        //filtersPanel.add(scoreDescBtn);

        filtersPanel.add(scoreSortBtn);

        topArea.add(filtersPanel, BorderLayout.SOUTH);

        // Put topArea in NORTH
        add(topArea, BorderLayout.NORTH);



        // =======================
        // TABLE MODEL
        // =======================
        String[] columns = {
                "User","Date/Time", "Difficulty", "Player 1", "Player 2", "Score", "Duration", "Winner"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        historyTable.setForeground(Color.WHITE);
        historyTable.setBackground(new Color(30, 30, 40));


        /* === REMOVE WHITE LINE ARTIFACTS === */
        historyTable.setShowHorizontalLines(false);
        historyTable.setShowVerticalLines(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));

        // =======================
        // SCROLLPANE (Neon glow)
        // =======================
        scrollPane = new JScrollPane(historyTable);
        //scrollPane.getViewport().setBackground(new Color(30, 30, 40));

        // ⭐ Neon blue outline/*
        scrollPane.setBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 255), 4, true)
        );

        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollPane.getVerticalScrollBar()
                .setUI(new NeonScrollBarUI(new Color(0, 200, 255)));

        scrollPane.getHorizontalScrollBar()
                .setUI(new NeonScrollBarUI(new Color(0, 200, 255)));

        Color dark = new Color(30, 30, 40);

        scrollPane.getViewport().setBackground(dark);
        scrollPane.setBackground(dark);
        historyTable.setFillsViewportHeight(true); // makes table extend to fill empty space



        //historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // ---- COLUMN WIDTH TUNING (CRITICAL) ----
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(110); // User
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(210); // Date/Time
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(170); // Difficulty (PILL)
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(130); // Player 1
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(130); // Player 2
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Score
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Duration
        historyTable.getColumnModel().getColumn(7).setPreferredWidth(130); // Winner



        add(scrollPane, BorderLayout.CENTER);



        // =======================
        // HEADER (Neon Blue)
        // =======================
        header = historyTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // ⭐ Apply neon header renderer
        header.setDefaultRenderer(new NeonHeaderRenderer());


        // =======================
        // NEON CELL RENDERER
        // =======================
        NeonTableCellRenderer renderer = new NeonTableCellRenderer();

        // Apply renderer to all columns
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Increase spacing and row height
        historyTable.setIntercellSpacing(new Dimension(0, 20));
        historyTable.setRowHeight(72);



        // =======================
        // BUTTON PANEL
        // =======================
        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 30));


        //refreshButton = NeonButtonFactory.createNeonButton("REFRESH", new Color(0, 180, 255));
        refreshButton = NeonButtonFactory.createNeonButton("CLEAR & REFRESH", new Color(0, 180, 255));

        backButton    = NeonButtonFactory.createNeonButton("BACK TO MENU", new Color(180, 80, 255));

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);




        add(buttonPanel, BorderLayout.SOUTH);
    }



    // ===== GETTERS FOR CONTROLLER =====
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getBackButton() { return backButton; }
    public JTable getHistoryTable() { return historyTable; }

    private void createEvents() {

        /*refreshButton.addActionListener(e -> {
            rebuildUserCombo();     // refresh possible users
            loadHistoryFromLogic(); // apply filters + refresh table
        });*/

        //userCombo.addActionListener(e -> loadHistoryFromLogic());
        /*userCombo.addActionListener(e -> {
            if (!isUpdatingCombos) loadHistoryFromLogic();
        });*/
        refreshButton.addActionListener(e -> {
            isUpdatingCombos = true;

            // reset filters
            resultCombo.setSelectedItem("ALL");
            difficultyCombo.setSelectedItem("ALL");

            rebuildUserCombo();
            userCombo.setSelectedItem("ALL");

            // reset sort to JSON order
            sortMode = SortMode.JSON;
            scoreSortBtn.setText("Score");

            isUpdatingCombos = false;

            loadHistoryFromLogic();
        });


        userCombo.addActionListener(e -> { if (!isUpdatingCombos) loadHistoryFromLogic(); });
        resultCombo.addActionListener(e -> { if (!isUpdatingCombos) loadHistoryFromLogic(); });
        difficultyCombo.addActionListener(e -> { if (!isUpdatingCombos) loadHistoryFromLogic(); });



        //resultCombo.addActionListener(e -> loadHistoryFromLogic());
       // difficultyCombo.addActionListener(e -> loadHistoryFromLogic());

        /*scoreAscBtn.addActionListener(e -> {
            scoreOrder = "ASC";
            loadHistoryFromLogic();
        });

        scoreDescBtn.addActionListener(e -> {
            scoreOrder = "DESC";
            loadHistoryFromLogic();
        });*/
        scoreSortBtn.addActionListener(e -> {
            sortMode = switch (sortMode) {
                case JSON -> SortMode.SCORE_ASC;
                case SCORE_ASC -> SortMode.SCORE_DESC;
                case SCORE_DESC -> SortMode.JSON;
            };

            scoreSortBtn.setText(switch (sortMode) {
                case JSON -> "Score";          // (no ↕)
                case SCORE_ASC -> "Score ↑";
                case SCORE_DESC -> "Score ↓";
            });

            loadHistoryFromLogic();
        });


        //refreshButton.addActionListener(e -> loadHistoryFromLogic());

    }

    private void fetchAndRefresh() {
        // TODO Auto-generated method stub
        loadHistoryFromLogic();
    }
/*
    //loads it into the table
    public void loadHistoryFromLogic() {
        tableModel.setRowCount(0); // clear table

        for (GameHistory h : GameHistoryLogic.getInstance().getAllHistory()) {
            tableModel.addRow(new Object[]{
                    h.getUsername(),
                    h.getFormattedDate(),
                    h.getDifficulty(),
                    h.getPlayer1(),
                    h.getPlayer2(),
                    h.getFinalScore(),
                    h.getDuration(),
                    h.isCoopWin() ? "Co-op Win" : "Loss"
            });
        }
    }*/

    public void loadHistoryFromLogic() {
        tableModel.setRowCount(0);

        String selectedUser = (String) userCombo.getSelectedItem();
        String selectedResult = (String) resultCombo.getSelectedItem();        // ALL/WIN/LOSE
        String selectedDiff = (String) difficultyCombo.getSelectedItem();      // ALL/EASY/MEDIUM/HARD


        // Build filter strategy (combined)
        HistoryFilterStrategy filter = new CombinedFilterStrategy(List.of(
                new UserFilterStrategy(selectedUser),
                new WinLoseFilterStrategy(selectedResult),
                new DifficultyFilterStrategy(selectedDiff)
        ));

        HistorySortStrategy sort = null;
        if (sortMode == SortMode.SCORE_ASC) sort = new ScoreAscSortStrategy();
        if (sortMode == SortMode.SCORE_DESC) sort = new ScoreDescSortStrategy();


        // Get filtered + sorted list from controller logic
        List<GameHistory> rows = GameHistoryLogic.getInstance()
                .getHistoryFilteredSorted(filter, sort);

        for (GameHistory h : rows) {
            tableModel.addRow(new Object[]{
                    h.getUsername(),
                    h.getFormattedDate(),
                    h.getDifficulty(),
                    h.getPlayer1(),
                    h.getPlayer2(),
                    h.getFinalScore(),
                    h.getDuration(),
                    h.isCoopWin() ? "Co-op Win" : "Loss"
            });
        }
    }
    /*
    private void rebuildUserCombo() {
        List<GameHistory> all = GameHistoryLogic.getInstance().getAllHistory();

        java.util.Set<String> users = new java.util.TreeSet<>();
        for (GameHistory h : all) {
            if (h.getUsername() != null && !h.getUsername().isBlank()) {
                users.add(h.getUsername());
            }
        }

        userCombo.removeAllItems();
        userCombo.addItem("ALL");
        for (String u : users) userCombo.addItem(u);
    }*/

    private void rebuildUserCombo() {
        isUpdatingCombos = true;

        List<GameHistory> all = GameHistoryLogic.getInstance().getAllHistory();
        java.util.Set<String> users = new java.util.TreeSet<>();
        for (GameHistory h : all) {
            if (h.getUsername() != null && !h.getUsername().isBlank()) {
                users.add(h.getUsername());
            }
        }

        userCombo.removeAllItems();
        userCombo.addItem("ALL");
        for (String u : users) userCombo.addItem(u);

        isUpdatingCombos = false;
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("History Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setContentPane(new HistoryView());
            frame.setVisible(true);
        });
    }

}
