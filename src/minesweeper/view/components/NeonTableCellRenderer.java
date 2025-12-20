package minesweeper.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class NeonTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int col) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, col);

        cell.setOpaque(true);
        cell.setHorizontalAlignment(CENTER);
        cell.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Background stripes
        if (isSelected) {
            cell.setBackground(new Color(40, 40, 55));
        } else if (row % 2 == 0) {
            cell.setBackground(new Color(30, 30, 40));
        } else {
            cell.setBackground(new Color(36, 36, 48));
        }

        cell.setForeground(Color.WHITE);

        // -------------------------------
// DIFFICULTY COLUMN PILL EFFECT
// -------------------------------
        if (col == 2 && value != null) {   // ✅ Difficulty column
            String diff = value.toString().toUpperCase();

            Color glow;
            switch (diff) {
                case "EASY":
                    glow = new Color(0, 255, 120);
                    break;
                case "MEDIUM":
                    glow = new Color(255, 210, 0);
                    break;
                case "HARD":
                    glow = new Color(255, 80, 80);
                    break;
                default:
                    glow = Color.GRAY;
            }

            cell.setText("  " + diff + "  ");
            cell.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedGlowBorder(glow, 10),
                    BorderFactory.createEmptyBorder(3, 12, 3, 12)
            ));
            cell.setFont(new Font("Segoe UI", Font.BOLD, 16));
        } else {
            cell.setBorder(null);
        }

        // Extra padding for Date/Time column
        if (col == 1) {
            cell.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        }


        // -------------------------------
        // SCORE GLOWING GREEN
        // -------------------------------
        if (col == 5) {
            cell.setForeground(new Color(0, 255, 120)); // Neon green
            cell.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }

        if (col == 6 && value != null) {   // ✅ Winner
            String result = value.toString();
            if (result.contains("Win")) {
                cell.setForeground(new Color(0, 255, 120));
            } else {
                cell.setForeground(new Color(255, 80, 80));
            }
        }


        return cell;
    }


    /** Rounded glow border */
    static class RoundedGlowBorder implements javax.swing.border.Border {
        private final Color glow;
        private final int radius;

        public RoundedGlowBorder(Color glow, int radius) {
            this.glow = glow;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Outer glow
            g2.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 120));
            g2.setStroke(new BasicStroke(8f));
            g2.drawRoundRect(x + 2, y + 2, w - 6, h - 6, radius, radius);

            // Main border
            g2.setColor(glow);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(x + 3, y + 3, w - 8, h - 8, radius, radius);

            g2.dispose();
        }
    }
}
