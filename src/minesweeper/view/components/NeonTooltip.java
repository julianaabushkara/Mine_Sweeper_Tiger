package minesweeper.view.components;

import javax.swing.*;
import java.awt.*;

public class NeonTooltip extends JWindow {

    private final JLabel label;

    public NeonTooltip(Window parent) {
        super(parent);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0));

        label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 80, 80), 2, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        label.setBackground(new Color(20, 20, 30));
        label.setOpaque(true);

        add(label);
        pack();
    }

    public void showTooltip(Component owner, String text) {
        label.setText(text);
        pack();

        Point p = owner.getLocationOnScreen();
        setLocation(p.x, p.y + owner.getHeight() + 6);
        setVisible(true);
    }

    public void hideTooltip() {
        setVisible(false);
    }



}
