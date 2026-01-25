package minesweeper.view;

import minesweeper.controller.GameHistoryLogic;
import minesweeper.model.GameHistory;
import minesweeper.view.components.*;
import minesweeper.model.GameSession;

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
    private JButton statsButton;
    private JComboBox<String> statsUserCombo;


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
        statsButton = NeonButtonFactory.createNeonButton("STATISTICS", new Color(0, 180, 255));



        backButton    = NeonButtonFactory.createNeonButton("BACK TO MENU", new Color(180, 80, 255));

        buttonPanel.add(refreshButton);
        buttonPanel.add(statsButton);

        buttonPanel.add(backButton);




        add(buttonPanel, BorderLayout.SOUTH);
    }



    // ===== GETTERS FOR CONTROLLER =====
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getBackButton() { return backButton; }
    public JTable getHistoryTable() { return historyTable; }

    private void createEvents() {
        statsButton.addActionListener(e -> openStatsDialog());

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
private void openStatsDialog() {
    JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "History Statistics", true);

// BIGGER window
    dialog.setPreferredSize(new Dimension(1100, 850));
    dialog.setMinimumSize(new Dimension(1000, 780));
    dialog.setResizable(true);
    dialog.pack();
    dialog.setLocationRelativeTo(this);



    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(20, 20, 30));
    panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

    // --- USER FILTER inside popup ---
    JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    controls.setBackground(new Color(20, 20, 30));
    JLabel userLbl = new JLabel("User:");
    userLbl.setForeground(Color.WHITE);

    statsUserCombo = NeonComboBoxFactory.createNeonComboBox(getAllUsersForStats(), new Color(0, 180, 255));
    statsUserCombo.setPreferredSize(new Dimension(180, 30));

    controls.add(userLbl);
    controls.add(statsUserCombo);
    panel.add(controls);
    panel.add(Box.createVerticalStrut(8));

    // --- STATS LABELS (we will update them dynamically) ---
    JLabel totalL = new JLabel();
    JLabel winsL = new JLabel();
    JLabel lossesL = new JLabel();
    JLabel winRateL = new JLabel();
    JLabel bestScoreL = new JLabel();
    JLabel avgScoreL = new JLabel();
    JLabel mostDiffL = new JLabel();
    JLabel lastPlayedL = new JLabel();
    JLabel avgDurL = new JLabel();

    panel.add(makeStatRow("Total games:", totalL));
    panel.add(makeStatRow("Wins:", winsL));
    panel.add(makeStatRow("Losses:", lossesL));
    panel.add(makeStatRow("Win rate:", winRateL));
    panel.add(makeStatRow("Best score:", bestScoreL));
    panel.add(makeStatRow("Average score:", avgScoreL));
    panel.add(makeStatRow("Most played difficulty:", mostDiffL));
    panel.add(makeStatRow("Last played:", lastPlayedL));
    panel.add(makeStatRow("Average duration:", avgDurL));

    panel.add(Box.createVerticalStrut(12));

    // --- CHARTS (diagrams) ---
    BarChartPanel winLossChart = new BarChartPanel();
    BarChartPanel diffChart = new BarChartPanel();

    // IMPORTANT for BoxLayout:
    winLossChart.setAlignmentX(Component.LEFT_ALIGNMENT);
    diffChart.setAlignmentX(Component.LEFT_ALIGNMENT);

    winLossChart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
    diffChart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

    winLossChart.setMinimumSize(new Dimension(360, 110));
    diffChart.setMinimumSize(new Dimension(360, 110));

    panel.add(new JLabel("Win/Loss Diagram") {{
        setForeground(new Color(0, 200, 255));
        setFont(new Font("Segoe UI", Font.BOLD, 14));
    }});
    panel.add(winLossChart);

    panel.add(Box.createVerticalStrut(10));

    panel.add(new JLabel("Difficulty Diagram") {{
        setForeground(new Color(0, 200, 255));
        setFont(new Font("Segoe UI", Font.BOLD, 14));
    }});
    panel.add(diffChart);

    panel.add(Box.createVerticalStrut(12));

    // --- TOP 3 PODIUM ---
    panel.add(new JLabel("TOP 3 (Highest Score)") {{
        setForeground(new Color(0, 200, 255));
        setFont(new Font("Segoe UI", Font.BOLD, 14));
    }});

    PodiumPanel podium = new PodiumPanel();
    podium.setAlignmentX(Component.LEFT_ALIGNMENT);
    podium.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
    panel.add(podium);

    panel.add(Box.createVerticalStrut(14));

    JButton close = NeonButtonFactory.createNeonButton("CLOSE", new Color(180, 80, 255));
    close.addActionListener(e -> dialog.dispose());
    JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    closePanel.setOpaque(false);
    closePanel.add(close);
    panel.add(closePanel);


    // --- Refresh function (updates everything according to popup user filter) ---
    Runnable refresh = () -> {
        List<GameHistory> baseRows = getCurrentFilteredRows(); // respects main filters + sorting
        String u = (String) statsUserCombo.getSelectedItem();

        List<GameHistory> statsRows = baseRows;
        if (u != null && !u.equals("ALL")) {
            statsRows = baseRows.stream().filter(r -> u.equals(r.getUsername())).toList();
        }

        StatsSummary s = StatsSummary.from(statsRows);

        totalL.setText(String.valueOf(s.total));
        winsL.setText(String.valueOf(s.wins));
        lossesL.setText(String.valueOf(s.losses));
        winRateL.setText(String.format("%.1f%%", s.winRate));
        bestScoreL.setText(s.bestScoreText);
        avgScoreL.setText(s.avgScoreText);
        mostDiffL.setText(s.mostPlayedDifficulty);
        lastPlayedL.setText(s.lastPlayedText);
        avgDurL.setText(s.avgDurationText);

        winLossChart.setData(s.wins, s.losses, "Wins", "Losses");

        // show easy vs hard on bar chart (or change to MEDIUM/HARD if you prefer)
        diffChart.setData(s.easyCount, s.hardCount, "Easy", "Hard");

        // TOP 3 by highest score
// TOP 3 by highest score
        List<GameHistory> top3 = statsRows.stream()
                .sorted((a, b) -> Integer.compare(b.getFinalScore(), a.getFinalScore()))
                .limit(3)
                .toList();

        podium.setTop3(top3);

    };

    statsUserCombo.addActionListener(e -> refresh.run());
    refresh.run();

    JScrollPane sc = new JScrollPane(panel);
    sc.setBorder(null);
    sc.getViewport().setBackground(new Color(20, 20, 30));
    sc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    dialog.setContentPane(sc);
    dialog.setVisible(true);
    ;
}
    private List<GameHistory> getCurrentFilteredRows() {
        String selectedUser = (String) userCombo.getSelectedItem();
        String selectedResult = (String) resultCombo.getSelectedItem();
        String selectedDiff = (String) difficultyCombo.getSelectedItem();

        HistoryFilterStrategy filter = new CombinedFilterStrategy(List.of(
                new UserFilterStrategy(selectedUser),
                new WinLoseFilterStrategy(selectedResult),
                new DifficultyFilterStrategy(selectedDiff)
        ));

        HistorySortStrategy sort = null;
        if (sortMode == SortMode.SCORE_ASC) sort = new ScoreAscSortStrategy();
        if (sortMode == SortMode.SCORE_DESC) sort = new ScoreDescSortStrategy();

        return GameHistoryLogic.getInstance().getHistoryFilteredSorted(filter, sort);
    }

    private JPanel makeStatRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));

        valueLabel.setForeground(new Color(0, 200, 255));
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        row.add(l, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

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
    private String[] getAllUsersForStats() {
        java.util.Set<String> users = new java.util.TreeSet<>();
        for (GameHistory h : GameHistoryLogic.getInstance().getAllHistory()) {
            if (h.getUsername() != null && !h.getUsername().isBlank()) users.add(h.getUsername());
        }

        java.util.List<String> items = new java.util.ArrayList<>();
        items.add("ALL");
        items.addAll(users);
        return items.toArray(new String[0]);
    }

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

    static class BarChartPanel extends JPanel {
        private int leftVal, rightVal;
        private String leftLabel = "A", rightLabel = "B";

        BarChartPanel() {
            setPreferredSize(new Dimension(360, 110));
            setBackground(new Color(20, 20, 30));
            setOpaque(true);

        }

        void setData(int leftVal, int rightVal, String leftLabel, String rightLabel) {
            this.leftVal = leftVal;
            this.rightVal = rightVal;
            this.leftLabel = leftLabel;
            this.rightLabel = rightLabel;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int max = Math.max(1, Math.max(leftVal, rightVal));
            int w = getWidth();
            int barW = w - 150;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(Color.WHITE);

            int y1 = 30, y2 = 70;

            g2.drawString(leftLabel + ": " + leftVal, 10, y1 + 12);
            g2.drawString(rightLabel + ": " + rightVal, 10, y2 + 12);

            int lw = (int) (barW * (leftVal / (double) max));
            int rw = (int) (barW * (rightVal / (double) max));

            g2.setColor(new Color(0, 200, 255));
            g2.fillRoundRect(130, y1, lw, 16, 10, 10);

            g2.setColor(new Color(180, 80, 255));
            g2.fillRoundRect(130, y2, rw, 16, 10, 10);
        }
    }

    static class StatsSummary {
        int total, wins, losses;
        double winRate;
        int easyCount, mediumCount, hardCount;

        String bestScoreText, avgScoreText, lastPlayedText, mostPlayedDifficulty, avgDurationText;

        static StatsSummary from(List<GameHistory> records) {
            StatsSummary s = new StatsSummary();
            s.total = records.size();

            if (s.total == 0) {
                s.wins = s.losses = 0;
                s.winRate = 0;
                s.bestScoreText = "-";
                s.avgScoreText = "-";
                s.lastPlayedText = "-";
                s.mostPlayedDifficulty = "-";
                s.avgDurationText = "-";
                return s;
            }

            int best = Integer.MIN_VALUE;
            long sumScore = 0;

            // last played (we’ll keep as string for now)
            String lastPlayed = records.get(0).getFormattedDate();

            java.util.Map<String, Integer> diffCount = new java.util.HashMap<>();

            // duration: we’ll average ONLY if it’s numeric seconds or "mm:ss" (simple parsing)
            long sumDurationSec = 0;
            int durationCount = 0;

            for (GameHistory r : records) {
                if (r.isCoopWin()) s.wins++;
                switch (r.getDifficulty()) {
                    case EASY -> s.easyCount++;
                    case MEDIUM -> s.mediumCount++;
                    case HARD -> s.hardCount++;
                }

                int score = r.getFinalScore();
                sumScore += score;
                best = Math.max(best, score);

                // keep latest as "last row max" (string compare not reliable) -> we’ll just use the latest encountered
                // If your formatted date is consistent, later we can parse properly.
                lastPlayed = r.getFormattedDate();

                //diffCount.merge(r.getDifficulty(), 1, Integer::sum);

                diffCount.merge(r.getDifficulty().name(), 1, Integer::sum);

                Integer durSec = tryParseDurationToSeconds(r.getDuration());
                if (durSec != null) {
                    sumDurationSec += durSec;
                    durationCount++;
                }
            }

            s.losses = s.total - s.wins;
            s.winRate = (s.wins * 100.0) / s.total;

            s.bestScoreText = String.valueOf(best);
            s.avgScoreText = String.format("%.1f", (sumScore * 1.0) / s.total);

            s.lastPlayedText = lastPlayed;

            s.mostPlayedDifficulty = diffCount.entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey)
                    .orElse("-");

            s.avgDurationText = (durationCount == 0)
                    ? "-"
                    : formatSeconds(sumDurationSec / durationCount);

            return s;
        }

        private static Integer tryParseDurationToSeconds(String duration) {
            if (duration == null) return null;

            // if your duration is already seconds:
            try {
                return Integer.parseInt(duration.trim());
            } catch (Exception ignored) {}

            // if it’s "mm:ss"
            try {
                String[] parts = duration.trim().split(":");
                if (parts.length == 2) {
                    int m = Integer.parseInt(parts[0]);
                    int s = Integer.parseInt(parts[1]);
                    return m * 60 + s;
                }
            } catch (Exception ignored) {}

            return null;
        }

        private static String formatSeconds(long sec) {
            long m = sec / 60;
            long s = sec % 60;
            return String.format("%02d:%02d", m, s);
        }
    }

    static class PodiumPanel extends JPanel {
        private final JLabel first = makeBox("#1", "-");
        private final JLabel second = makeBox("#2", "-");
        private final JLabel third = makeBox("#3", "-");

        PodiumPanel() {
            setOpaque(false);
            setLayout(new GridLayout(1, 3, 24, 0));
            first.setPreferredSize(new Dimension(240, 120));
            second.setPreferredSize(new Dimension(240, 120));
            third.setPreferredSize(new Dimension(240, 120));

            add(second); // #2 left
            add(first);  // #1 center
            add(third);  // #3 right
        }

        void setTop3(List<GameHistory> top3) {
            setBox(second, "#2", getText(top3, 1));
            setBox(first,  "#1", getText(top3, 0));
            setBox(third,  "#3", getText(top3, 2));
            revalidate();
            repaint();
        }

        private static String getText(List<GameHistory> top3, int idx) {
            if (idx >= top3.size()) return "-";
            GameHistory r = top3.get(idx);
            return r.getFinalScore() + " pts\n" + r.getDifficulty().name() + "\n" + r.getFormattedDate();
        }

        private static JLabel makeBox(String title, String body) {
            JLabel lbl = new JLabel();
            lbl.setOpaque(true);
            lbl.setBackground(new Color(20, 20, 30));
            lbl.setForeground(Color.WHITE);

            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 200, 255), 3, true),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));

            // BIGGER text
            lbl.setFont(new Font("Monospaced", Font.BOLD, 18));
            lbl.setVerticalAlignment(SwingConstants.TOP);

            // BIGGER box size
            lbl.setPreferredSize(new Dimension(320, 200));

            setBox(lbl, title, body);
            return lbl;
        }


        private static void setBox(JLabel lbl, String title, String body) {
            // Convert newlines to <br> so it always shows nicely in HTML
            String htmlBody = (body == null ? "-" : body).replace("\n", "<br/>");

            lbl.setText(
                    "<html><div style='text-align:center;'>"
                            + "<div style='color:#00C8FF;font-size:18px;font-weight:800;'>" + title + "</div>"
                            + "<div style='margin-top:10px;color:#FFFFFF;font-size:14px;font-weight:600;line-height:1.4;'>"
                            + htmlBody
                            + "</div>"
                            + "</div></html>"
            );
        }
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
