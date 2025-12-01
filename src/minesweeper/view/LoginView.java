package minesweeper.view;

import minesweeper.controller.LoginController;
import minesweeper.model.User;
import minesweeper.view.components.NeonButtonFactory;
import minesweeper.view.components.NeonDialog;
import minesweeper.view.components.PlaceholderTextField;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JLabel forgotLabel;
    private JLabel footerLabel;
    private JButton togglePasswordBtn;
    private JTextField securityAnswer;
    private boolean isFirstTime = true;
    //Privacy Questions
    private JComboBox<String> securityQuestion = new  JComboBox<>(new  String[]{
            "Birth Month",
            "First Pet's Name",
            "Birth City"
    });
    private boolean passwordVisible = false;

    public LoginView() {
        setTitle("Mine Sweeper - Tiger Edition");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        ImageIcon tigerIcon = new ImageIcon("resources/assets/tiger.png");
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
        formPanel.setBounds(75, 180, 350, 300);
        formPanel.setOpaque(false);

        // Username field
        usernameField  = new PlaceholderTextField("Username");
        styleNeonTextField(usernameField, Color.CYAN.brighter());
        usernameField.setBounds(25, 30, 300, 45);

        // Password panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(null);
        passwordPanel.setBounds(25, 85, 300, 45);
        passwordPanel.setOpaque(false);

        passwordField = createStyledPasswordField("Password", Color.CYAN.brighter());
        passwordField.setBounds(0, 0, 255, 45);

        repeatPasswordField = createStyledPasswordField("Repeat Password", Color.CYAN.brighter());
        repeatPasswordField.setBounds(0, 55, 300, 45);
        repeatPasswordField.setVisible(false);

        // Eye toggle button
        togglePasswordBtn = new JButton(new ImageIcon("resources/assets/eye.png"));
        togglePasswordBtn.setBounds(260, 0, 40, 45);
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setBorderPainted(false);
        togglePasswordBtn.setContentAreaFilled(false);
        togglePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());

        passwordPanel.add(passwordField);
        passwordPanel.add(repeatPasswordField);
        passwordPanel.add(togglePasswordBtn);

        securityQuestion.setBounds(25,195,300,45);
        styleNeonComboBox(securityQuestion, Color.CYAN.brighter());
        securityQuestion.setVisible(false);

        securityAnswer = new PlaceholderTextField("Answer");
        styleNeonTextField(securityAnswer, Color.CYAN.brighter());
        securityAnswer.setVisible(false);
        securityAnswer.setBounds(25,250,300,45);


        // Login button
        JButton loginBtn = NeonButtonFactory.createNeonButton("LOGIN", new Color(0, 200, 100));
        loginBtn.setBounds(25, 155, 300, 45);
        loginBtn.addActionListener(e -> {
            LoginController.login(new User(usernameField.getText(), passwordField.getPassword()));
        });

        // Create Account button
        JButton createAccountBtn = NeonButtonFactory.createNeonButton("CREATE ACCOUNT", new Color(20, 155, 200));
        createAccountBtn.setBounds(25, 205, 300, 45);
        createAccountBtn.addActionListener(e -> {
            if (isFirstTime) {
                repeatPasswordField.setVisible(true);
                setSize(500, 650);
                footerLabel.setBounds(0,570,500,10);
                loginBtn.setVisible(false);
                formPanel.setBounds(75, 180, 350, 350);
                passwordPanel.setBounds(25, 85, 300, 100);
                createAccountBtn.setBounds(25, 305, 300, 45);
                forgotLabel.setVisible(false);
                securityQuestion.setVisible(true);
                securityAnswer.setVisible(true);
                isFirstTime = false;
            } else {
                LoginController.addUser(new User(usernameField.getText(), passwordField.getPassword(), securityAnswer.getText()),
                        new String(repeatPasswordField.getPassword()));
            }
        });

        formPanel.add(securityQuestion);
        formPanel.add(securityAnswer);
        formPanel.add(usernameField);
        formPanel.add(passwordPanel);
        formPanel.add(loginBtn);
        formPanel.add(createAccountBtn);

        // Forgot Password link
        forgotLabel = new JLabel("Forgot Password?");
        forgotLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotLabel.setForeground(new Color(120, 120, 120));
        forgotLabel.setBounds(0, 490, 500, 20);
        forgotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Footer
        footerLabel = new JLabel("Group Tiger · 2025");
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
        field.setForeground(new Color(130, 130, 130));
        field.setCaretColor(Color.WHITE);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }

    private void styleNeonComboBox(JComboBox<?> comboBox, Color borderColor) {
        comboBox.setBackground(new Color(25, 25, 30));
        comboBox.setForeground(Color.WHITE);
            comboBox.setFocusable(false);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton("▼");
                arrow.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                arrow.setForeground(Color.WHITE);
                arrow.setBackground(new Color(25, 25, 30));
                arrow.setBorder(BorderFactory.createEmptyBorder());
                return arrow;
            }
        });
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
            repeatPasswordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('●');
            repeatPasswordField.setEchoChar('●');
        }
    }
}