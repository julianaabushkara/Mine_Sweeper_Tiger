package minesweeper.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class NeonHeaderRenderer extends DefaultTableCellRenderer {

    public NeonHeaderRenderer() {
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        JLabel header = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        header.setOpaque(true);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(Color.WHITE);

        // Dark neon base
        header.setBackground(new Color(10, 20, 40));

        // Neon blue glow border
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 255), 3, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return header;
    }
}
