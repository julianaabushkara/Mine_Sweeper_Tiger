package minesweeper.view;

import minesweeper.controller.LoginController;
import minesweeper.model.User;
import minesweeper.view.components.NeonButtonFactory;
import minesweeper.view.components.PlaceholderTextField;

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
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        ImageIcon tigerIcon = new ImageIcon("src/minesweeper/resources/assets/tiger.png");
        setIconImage(tigerIcon.getImage());
        // Main panel with dark background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(15, 15, 20));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(15, 15, 20));

        // Logo
        JLabel logoLabel = new JLabel(tigerIcon);
        logoLabel.setBounds(200, 120, 100, 100);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Title
        JLabel titleLabel = new JLabel("MINE SWEEPER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 70, 500, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtitle
        JLabel subtitleLabel = new JLabel("TIGER EDITION");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.YELLOW.brighter());
        subtitleLabel.setBounds(0, 120, 500, 30);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Login form panel
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(15, 15, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        formPanel.setLayout(null);
        formPanel.setBounds(75, 210, 350, 250);
        formPanel.setOpaque(false);

        // Username field
        usernameField  = new PlaceholderTextField("Username");
        styleNeonTextField(usernameField, Color.CYAN.brighter());
                //createStyledTextField("Username");
        usernameField.setBounds(25, 30, 300, 45);

        // Password panel (field + toggle button)
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(null);
        passwordPanel.setBounds(25, 90, 300, 45);
        passwordPanel.setOpaque(false);

        passwordField = createStyledPasswordField("Password", Color.CYAN.brighter());
        passwordField.setBounds(0, 0, 255, 45);

        // Eye toggle button
        togglePasswordBtn = new JButton(new ImageIcon("src/minesweeper/resources/assets/eye.png"));
        togglePasswordBtn.setBounds(260, 0, 40, 45);
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setBorderPainted(false);
        togglePasswordBtn.setContentAreaFilled(false);
        togglePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());

        passwordPanel.add(passwordField);
        passwordPanel.add(togglePasswordBtn);

        // Login button
        JButton loginBtn = NeonButtonFactory.createNeonButton("LOGIN", new Color(0, 200, 100));
        loginBtn.setBounds(25, 150, 300, 45);
        loginBtn.addActionListener(e -> {
            LoginController.login(new User(usernameField.getText(), passwordField.getPassword()));
        });

        // Create Account button
        JButton createAccountBtn = NeonButtonFactory.createNeonButton("CREATE ACCOUNT", new Color(20, 155, 200));
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
        forgotLabel.setBounds(0, 470, 500, 20);
        forgotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Footer
        JLabel footerLabel = new JLabel("Group Tiger · 2025");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(80, 80, 80));
        footerLabel.setBounds(0, 520, 500, 20);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(logoLabel);
        mainPanel.add(titleLabel);
        mainPanel.add(subtitleLabel);
        mainPanel.add(formPanel);
        mainPanel.add(forgotLabel);
        mainPanel.add(footerLabel);

        add(mainPanel);
    }

    private JPasswordField createStyledPasswordField(String placeholder, Color borderColor) {
        JPasswordField field = new JPasswordField();

        // Neon styling
        field.setBackground(new Color(25, 25, 30));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setEchoChar('•');

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // Placeholder logic
        field.setText(placeholder);
        field.setForeground(new Color(130, 130, 130));   // placeholder color
        field.setEchoChar((char) 0);                     // show placeholder normally

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                    field.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(new Color(130, 130, 130));
                    field.setEchoChar((char) 0);
                }
            }
        });

        return field;
    }

    private void styleNeonTextField(JTextField field, Color borderColor) {
        field.setBackground(new Color(25, 25, 30));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
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