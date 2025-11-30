package minesweeper.view.components;

import javax.swing.*;
import java.awt.*;

public class NeonDialog {

    public static void showNeonDialog(Window parent, String title, String htmlMessage, String iconPath) {

        // TOP ICON (optional)
        JLabel iconLabel = null;
        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(NeonDialog.class.getResource(iconPath));
                Image scaled = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
                iconLabel = new JLabel(new ImageIcon(scaled));
            } catch (Exception e) { iconLabel = new JLabel(); }
        } else {
            iconLabel = new JLabel();
        }

        // ---- MAIN PANEL ----
        JPanel panel = new JPanel(new BorderLayout(20, 15));
        panel.setBackground(new Color(20, 20, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---- TOP ROW ----
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRow.setBackground(new Color(20, 20, 30));
        topRow.add(iconLabel);
        panel.add(topRow, BorderLayout.NORTH);

        // ---- RESPONSIVE TEXT AREA ----
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);// Force white text via inline CSS
        String styledHtml =
                "<html><body style='color:#FFFFFF; font-family:Segoe UI; font-size:14px;'>"
                        + htmlMessage +
                        "</body></html>";

        textPane.setText(styledHtml);
        textPane.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        panel.add(scrollPane, BorderLayout.CENTER);

        // ---- BIGGER NEON OK BUTTON ----
        JButton ok = new JButton("OK");
        ok.setFont(new Font("Segoe UI", Font.BOLD, 20));
        ok.setForeground(Color.WHITE);
        ok.setBackground(new Color(25, 25, 35));
        ok.setPreferredSize(new Dimension(140, 55));   // BIGGER BUTTON

        // Thick neon glow
        ok.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 180), 4, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        ok.addActionListener(e -> SwingUtilities.getWindowAncestor(ok).dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(20, 20, 30));
        btnPanel.add(ok);

        panel.add(btnPanel, BorderLayout.SOUTH);

        // ---- CREATE DIALOG ----
        JDialog dialog = new JDialog(parent, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(panel);

        dialog.setMinimumSize(new Dimension(500, 450));  // Responsive minimum size
        dialog.setPreferredSize(new Dimension(350, 650)); // Auto-scaled default
        dialog.setMaximumSize(new Dimension(900, 700));   // Prevent becoming huge

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
