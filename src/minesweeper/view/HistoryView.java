package minesweeper.view;

import minesweeper.controller.GameHistoryLogic;
import minesweeper.model.GameHistory;
import minesweeper.view.components.NeonButtonFactory;
import minesweeper.view.components.NeonTableCellRenderer;
import minesweeper.view.components.NeonHeaderRenderer;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class HistoryView extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;

    private JButton refreshButton;
    private JButton backButton;
    private JLabel title;
    JScrollPane scrollPane;
    JPanel buttonPanel;
    JTableHeader header;
    JPanel titlePanel;

    public HistoryView() {
        initComponents();
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
        add(titlePanel, BorderLayout.NORTH);


        // =======================
        // TABLE MODEL
        // =======================
        String[] columns = {
                "Date/Time", "Difficulty", "Player 1", "Player 2", "Score", "Winner"
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
        scrollPane.getViewport().setBackground(new Color(30, 30, 40));

        // ⭐ Neon blue outline
        scrollPane.setBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 255), 4, true)
        );

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
        historyTable.setRowHeight(60);


        // =======================
        // BUTTON PANEL
        // =======================
        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 30));

        refreshButton = NeonButtonFactory.createNeonButton("REFRESH", new Color(0, 180, 255));
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
        refreshButton.addActionListener(e -> loadHistoryFromLogic());

    }

    private void fetchAndRefresh() {
        // TODO Auto-generated method stub
        loadHistoryFromLogic();
    }

    //loads it into the table
    public void loadHistoryFromLogic() {
        tableModel.setRowCount(0); // clear table

        for (GameHistory h : GameHistoryLogic.getInstance().getAllHistory()) {
            tableModel.addRow(new Object[]{
                    h.getFormattedDate(),
                    h.getDifficulty(),
                    h.getPlayer1(),
                    h.getPlayer2(),
                    h.getFinalScore(),
                    h.isCoopWin() ? "Co-op Win" : "Loss"
            });
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
