package minesweeper.view.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class NeonScrollBarUI extends BasicScrollBarUI {

    private final Color glow;

    // ✅ THIS CONSTRUCTOR MUST EXIST
    public NeonScrollBarUI(Color glow) {
        this.glow = glow;
    }

    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 160);
        trackColor = new Color(20, 20, 30);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return zeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return zeroButton();
    }

    private JButton zeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(thumbColor);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);

        g2.setColor(glow);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(r.x, r.y, r.width - 1, r.height - 1, 10, 10);

        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(trackColor);
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.dispose();
    }
}
