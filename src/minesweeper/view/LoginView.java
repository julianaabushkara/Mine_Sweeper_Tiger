package minesweeper.view;

import minesweeper.controller.LoginController;
import minesweeper.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton togglePasswordBtn;
    private boolean passwordVisible = false;

    public LoginView() {
        setTitle("Mine Sweeper - Tiger Edition");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with dark background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(15, 15, 15));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(15, 15, 15));

        // Logo
        JLabel logoLabel = new JLabel(new ImageIcon(""));
        logoLabel.setBounds(300, 40, 100, 100);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Title
        JLabel titleLabel = new JLabel("MINE SWEEPER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 150, 700, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtitle
        JLabel subtitleLabel = new JLabel("TIGER EDITION");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        subtitleLabel.setBounds(0, 200, 700, 30);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Login form panel
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(25, 25, 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        formPanel.setLayout(null);
        formPanel.setBounds(175, 260, 350, 280);
        formPanel.setOpaque(false);

        // Username field
        usernameField = createStyledTextField("Username");
        usernameField.setBounds(25, 30, 300, 45);

        // Password panel (field + toggle button)
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(null);
        passwordPanel.setBounds(25, 90, 300, 45);
        passwordPanel.setOpaque(false);

        passwordField = createStyledPasswordField("Password");
        passwordField.setBounds(0, 0, 255, 45);

        // Eye toggle button
        togglePasswordBtn = new JButton(new ImageIcon("src/minesweeper/view/assets/eye.png"));
        togglePasswordBtn.setBounds(260, 0, 40, 45);
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setBorderPainted(false);
        togglePasswordBtn.setContentAreaFilled(false);
        togglePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());

        passwordPanel.add(passwordField);
        passwordPanel.add(togglePasswordBtn);

        // Login button
        JButton loginBtn = createGlowButton("LOGIN", new Color(0, 200, 100));
        loginBtn.setBounds(25, 150, 300, 45);
        loginBtn.addActionListener(e -> {
            LoginController.login(new User(usernameField.getText(), passwordField.getPassword()));
        });

        // Create Account button
        JButton createAccountBtn = createGlowButton("CREATE ACCOUNT", new Color(0, 150, 255));
        createAccountBtn.setBounds(25, 205, 300, 45);
        createAccountBtn.addActionListener(e -> {
           LoginController.addUser(new User(usernameField.getText(), passwordField.getPassword()));
        });

        formPanel.add(usernameField);
        formPanel.add(passwordPanel);
        formPanel.add(loginBtn);
        formPanel.add(createAccountBtn);

        // Forgot Password link
        JLabel forgotLabel = new JLabel("Forgot Password?");
        forgotLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotLabel.setForeground(new Color(120, 120, 120));
        forgotLabel.setBounds(0, 555, 700, 20);
        forgotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Footer
        JLabel footerLabel = new JLabel("Group Tiger · 2025");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(80, 80, 80));
        footerLabel.setBounds(0, 620, 700, 20);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(logoLabel);
        mainPanel.add(titleLabel);
        mainPanel.add(subtitleLabel);
        mainPanel.add(formPanel);
        mainPanel.add(forgotLabel);
        mainPanel.add(footerLabel);

        add(mainPanel);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(20, 20, 25));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(new Color(0, 255, 255));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0, 255, 255));
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(5, 15, 5, 15));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(100, 100, 100));
                }
            }
        });

        field.setForeground(new Color(100, 100, 100));
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(20, 20, 25));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(new Color(138, 43, 226));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(138, 43, 226));
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(5, 15, 5, 15));
        field.setEchoChar('●');

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(100, 100, 100));
                    field.setEchoChar((char) 0);
                } else {
                    field.setEchoChar('●');
                }
            }
        });

        field.setForeground(new Color(100, 100, 100));
        field.setEchoChar((char) 0);
        return field;
    }

    private JButton createGlowButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            private boolean hover = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (hover) {
                    g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 30));
                    for (int i = 0; i < 15; i++) {
                        g2d.drawRoundRect(i, i, getWidth() - 2 * i - 1, getHeight() - 2 * i - 1, 8, 8);
                    }
                }

                g2d.setColor(new Color(20, 20, 25));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(baseColor);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                try {
                    btn.getClass().getDeclaredField("hover").set(btn, true);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                btn.repaint();
            }
            public void mouseExited(MouseEvent e) {
                try {
                    btn.getClass().getDeclaredField("hover").set(btn, false);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                btn.repaint();
            }
        });

        return btn;
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('●');
        }
    }
}