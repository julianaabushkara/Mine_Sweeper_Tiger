package minesweeper.view.components;

import javax.swing.*;
import java.awt.*;

public class NeonDialog {

    public static void showNeonDialog(Window parent, String title, String htmlMessage, ImageIcon imageIcon, boolean showOkBtn, boolean showCancelBtn) {

        // ---- MAIN PANEL ----
        JPanel panel = new JPanel(new BorderLayout(20, 15));
        panel.setBackground(new Color(15, 15, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---- RESPONSIVE TEXT AREA ----
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);// Force white text via inline CSS
        String styledHtml =
                "<html><body style='color:#FFFFFF; font-family:Segoe UI; font-size:14px;'>"
                        + htmlMessage +
                        "</body></html>";

        textPane.setText(styledHtml);
        textPane.setSize(300,50);
        textPane.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(15, 15, 20));

        // ---- BUTTONS ----
        if (showOkBtn) {

            JButton ok = NeonButtonFactory.createNeonButton("OK", Color.WHITE);
            ok.addActionListener(e -> SwingUtilities.getWindowAncestor(ok).dispose());
            btnPanel.add(ok);
        }
        if (showCancelBtn) {
            JButton cancel = NeonButtonFactory.createNeonButton("Cancel", Color.WHITE);
            cancel.addActionListener(e -> SwingUtilities.getWindowAncestor(cancel).dispose());
            btnPanel.add(cancel);
        }
        panel.add(btnPanel, BorderLayout.SOUTH);

        // ---- CREATE DIALOG ----
        JDialog dialog = new JDialog(parent, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(300,100);
        if (imageIcon != null) {
            dialog.setIconImage(imageIcon.getImage());
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
