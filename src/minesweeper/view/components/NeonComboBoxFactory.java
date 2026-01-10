package minesweeper.view.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;

public class NeonComboBoxFactory {

    public static JComboBox<String> createNeonComboBox(String[] items, Color glowColor) {
        Color bg = new Color(25, 25, 35);

        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        combo.setForeground(Color.WHITE);
        combo.setBackground(bg);

        // IMPORTANT: make it actually paint its background
        combo.setOpaque(true);
        combo.setFocusable(false);

        Border border = BorderFactory.createLineBorder(glowColor, 2, true);
        combo.setBorder(border);

        // dropdown list styling
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                c.setForeground(Color.WHITE);
                c.setBackground(isSelected ? new Color(0, 180, 255, 60) : bg);
                c.setOpaque(true);
                return c;
            }
        });

        // custom UI: fixes the WHITE selected area
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton("▾");
                b.setForeground(glowColor);
                b.setBackground(bg);
                b.setOpaque(true);
                b.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                b.setFocusPainted(false);
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(bg);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        // also make the popup list dark
        Object child = combo.getAccessibleContext().getAccessibleChild(0);
        if (child instanceof BasicComboPopup popup) {
            popup.getList().setBackground(bg);
            popup.getList().setForeground(Color.WHITE);
        }

        return combo;
    }
}
