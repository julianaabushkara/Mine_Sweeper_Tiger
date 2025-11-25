package minesweeper.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {

    public LoginView() {
        super("Mine Sweeper - Tiger Edition");
        initUI();
    }

    private void initUI() {
        // Basic frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background panel with subtle gradient
        JPanel bg = new GradientPanel();
        bg.setLayout(new BorderLayout());
        bg.setBorder(new EmptyBorder(40, 40, 40, 40));
        setContentPane(bg);

        // Title (top)
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel mainTitle = new JLabel("MINE SWEEPER", SwingConstants.CENTER);
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setFont(new Font("SansSerif", Font.BOLD, 38));

        JLabel subTitle = new JLabel("TIGER EDITION", SwingConstants.CENTER);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitle.setForeground(new Color(180, 190, 255));
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subTitle);

        bg.add(titlePanel, BorderLayout.NORTH);

        // Center – card
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        bg.add(center, BorderLayout.CENTER);

        RoundedPanel card = new RoundedPanel(30);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setOpaque(false);

        // Username field
        JTextField usernameField = new JTextField();
        styleTextField(usernameField, "Username");

        // Password field + eye icon
        JPasswordField passwordField = new JPasswordField();
        styleTextField(passwordField, "Password");

        JPanel passwordRow = new JPanel(new BorderLayout());
        passwordRow.setOpaque(false);
        passwordRow.add(passwordField, BorderLayout.CENTER);

        JButton eyeButton = new JButton("");
        eyeButton.setIcon(new ImageIcon("eye.png"));
        //eyeButton.setFocusPainted(false);
        eyeButton.setBorderPainted(false);
        eyeButton.setContentAreaFilled(false);
        //eyeButton.setPreferredSize(new Dimension(40, 40));

        // toggle password visibility
        eyeButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) {
                passwordField.setEchoChar('•');
            } else {
                passwordField.setEchoChar((char) 0);
            }
        });

        passwordRow.add(eyeButton, BorderLayout.EAST);

        // Buttons
        JButton loginBtn = new JButton("LOGIN");
        styleMainButton(loginBtn, new Color(0, 220, 130));

        JButton createBtn = new JButton("CREATE ACCOUNT");
        styleMainButton(createBtn, new Color(0, 170, 255));

        // Forgot password
        JLabel forgot = new JLabel("Forgot Password?", SwingConstants.CENTER);
        forgot.setForeground(new Color(160, 160, 170));
        forgot.setFont(new Font("SansSerif", Font.PLAIN, 12));
        forgot.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemble card
        card.add(usernameField);
        card.add(Box.createVerticalStrut(15));
        card.add(passwordRow);
        card.add(Box.createVerticalStrut(25));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(15));
        card.add(createBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(forgot);

        center.add(card); // GridBagLayout centers it automatically

        // Footer
        JLabel footer = new JLabel("Group Tiger · 2025", SwingConstants.CENTER);
        footer.setForeground(new Color(140, 140, 150));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        bg.add(footer, BorderLayout.SOUTH);
    }

    private static void styleTextField(JTextField field, String placeholder) {
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 255)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // simple placeholder behavior
        field.setText(placeholder);
        field.setForeground(new Color(160, 160, 170));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(160, 160, 170));
                }
            }
        });
    }

    private static void styleMainButton(JButton button, Color glow) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(glow);
        button.setBackground(new Color(10, 10, 15));
        button.setBorder(BorderFactory.createLineBorder(glow));
        button.setPreferredSize(new Dimension(0, 40));
    }

    // Background gradient panel
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color top = new Color(5, 10, 25);
            Color bottom = new Color(5, 5, 10);
            GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    // Rounded dark card with subtle glow outline
    private static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // main card
            g2.setColor(new Color(10, 10, 15, 230));
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

            // glow outline
            g2.setColor(new Color(0, 200, 255, 100));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        System.out.println(new ImageIcon("mine.png").getImageLoadStatus());
        SwingUtilities.invokeLater(() -> {
            LoginView frame = new LoginView();
            frame.setVisible(true);
        });
    }
}
