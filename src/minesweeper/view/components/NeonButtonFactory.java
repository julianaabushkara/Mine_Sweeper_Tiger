package minesweeper.view.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * NeonButtonFactory
 * Creates glowing neon-style buttons (JButton or JToggleButton)
 * used across the entire Minesweeper Tiger UI.
 */
public class NeonButtonFactory {

    /** Create neon JButton */
    public static JButton createNeonButton(String text, Color neonColor) {
        JButton btn = new JButton(text);
        styleNeon(btn, neonColor);
        return btn;
    }

    /** Create neon JToggleButton */
    public static JToggleButton createNeonToggle(String text, Color neonColor) {
        JToggleButton btn = new JToggleButton(text);
        styleNeon(btn, neonColor);
        return btn;
    }


    /** MAIN STYLE APPLIED TO ALL NEON BUTTONS */
    private static void styleNeon(AbstractButton btn, Color neonColor) {

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Neon rounded border with glow
        btn.setBorder(BorderFactory.createCompoundBorder(
                new NeonRoundBorder(neonColor, 18),
                BorderFactory.createEmptyBorder(12, 26, 12, 26)
        ));

        // Hover effect → intensify glow
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.repaint(); }

            @Override
            public void mouseExited(MouseEvent e) { btn.repaint(); }
        });

        // Selected glow for toggle buttons
        btn.addActionListener(e -> {
            if (btn instanceof JToggleButton) {
                btn.repaint();
            }
        });
    }


    /**
     * Custom rounded neon border + glow painter
     */
    static class NeonRoundBorder implements Border {

        private final Color neonColor;
        private final int radius;

        public NeonRoundBorder(Color neonColor, int radius) {
            this.neonColor = neonColor;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 16, 10, 16);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = radius;

            boolean isSelected =
                    (c instanceof JToggleButton) && ((JToggleButton) c).isSelected();

            // Outer glow
            g2.setColor(new Color(
                    neonColor.getRed(),
                    neonColor.getGreen(),
                    neonColor.getBlue(),
                    isSelected ? 180 : 80
            ));
            g2.setStroke(new BasicStroke(isSelected ? 12f : 9f));
            g2.drawRoundRect(x + 4, y + 4, width - 10, height - 10, arc, arc);

            // Main border
            g2.setColor(neonColor);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(x + 3, y + 3, width - 8, height - 8, arc, arc);

            g2.dispose();
        }
    }
}
