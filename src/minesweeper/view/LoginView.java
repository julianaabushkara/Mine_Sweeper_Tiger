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
import java.util.function.Consumer;

/**
 * DESIGN PATTERNS APPLIED:
 * 1. TEMPLATE METHOD PATTERN - setupComponent() methods
 * 2. STRATEGY PATTERN - ViewState enum with behavior
 * 3. OBSERVER PATTERN - Action listeners notify controller
 * 4. FACTORY METHOD PATTERN - createStyledPasswordField()
 * 5. BUILDER PATTERN - Component configuration methods
 */
public class LoginView extends JFrame {
    // Components
    private JTextField usernameField;
    private JPanel passwordPanel;
    private JPanel formPanel;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JButton forgotBtn;
    private JButton createAccountBtn;
    private JButton loginBtn;
    private JLabel footerLabel;
    private JButton togglePasswordBtn;
    private JTextField securityAnswer;
    private JTextField answer;
    private JTextField question;
    private JButton backBtn;
    private JComboBox<String> securityQuestion;
    private boolean passwordVisible = false;

    // STRATEGY PATTERN: Encapsulate different view states
    private ViewState currentState = ViewState.NORMAL_LOGIN;
    private LoginController controller;

    // STRATEGY PATTERN: Define different view behaviors
    public enum ViewState {
        NORMAL_LOGIN {
            @Override
            void configureView(LoginView view) {
                view.repeatPasswordField.setVisible(false);
                view.setSize(500, 600);
                view.footerLabel.setBounds(0, 520, 500, 20);
                view.loginBtn.setVisible(true);
                view.loginBtn.setText("LOGIN");
                view.loginBtn.setBounds(25, 155, 300, 45);
                view.formPanel.setBounds(75, 180, 350, 300);
                view.passwordPanel.setBounds(25, 85, 300, 45);
                view.passwordPanel.setVisible(true);
                view.createAccountBtn.setBounds(25, 205, 300, 45);
                view.createAccountBtn.setVisible(true);
                view.forgotBtn.setVisible(true);
                view.securityQuestion.setVisible(false);
                view.securityAnswer.setVisible(false);
                view.backBtn.setVisible(false);
                view.answer.setVisible(false);
                view.question.setVisible(false);
            }

            @Override
            void handleAction(LoginView view) {
                view.controller.handleLogin(
                        view.usernameField.getText(),
                        view.passwordField.getPassword()
                );
            }
        },

        REGISTRATION {
            @Override
            void configureView(LoginView view) {
                view.repeatPasswordField.setVisible(true);
                view.setSize(500, 700);
                view.footerLabel.setBounds(0, 620, 500, 10);
                view.loginBtn.setVisible(false);
                view.formPanel.setBounds(75, 180, 350, 405);
                view.passwordPanel.setBounds(25, 85, 300, 100);
                view.createAccountBtn.setBounds(25, 305, 300, 45);
                view.forgotBtn.setVisible(false);
                view.securityQuestion.setVisible(true);
                view.securityAnswer.setVisible(true);
                view.backBtn.setBounds(25, 355, 300, 45);
                view.backBtn.setVisible(true);
            }

            @Override
            void handleAction(LoginView view) {
                view.controller.handleRegistration(
                        view.usernameField.getText(),
                        view.passwordField.getPassword(),
                        view.repeatPasswordField.getPassword(),
                        view.securityAnswer.getText().toUpperCase(),
                        (String) view.securityQuestion.getSelectedItem()
                );
            }
        },

        PASSWORD_RECOVERY {
            @Override
            void configureView(LoginView view) {
                view.passwordPanel.setVisible(false);
                view.createAccountBtn.setVisible(false);
                view.forgotBtn.setVisible(false);
                view.answer.setVisible(true);
                view.question.setVisible(true);
                view.backBtn.setVisible(true);
                view.backBtn.setBounds(25, 245, 300, 45);
                view.loginBtn.setBounds(25, 195, 300, 45);
                view.loginBtn.setText("RETRIEVE PASSWORD");
            }

            @Override
            void handleAction(LoginView view) {
                view.controller.handlePasswordRecovery(
                        view.usernameField.getText(),
                        view.answer.getText().toUpperCase()
                );
            }
        };

        abstract void configureView(LoginView view);
        abstract void handleAction(LoginView view);
    }

    public LoginView() {
        setupFrame();
        initializeComponents();
        layoutComponents();
    }

    public void setController(LoginController controller) {
        this.controller = controller;
    }

    // TEMPLATE METHOD PATTERN: Define structure of frame setup
    private void setupFrame() {
        setTitle("Mine Sweeper - Tiger Edition");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        ImageIcon tigerIcon = loadImageIcon("/assets/tiger.png");
        setIconImage(tigerIcon.getImage());
    }

    // TEMPLATE METHOD PATTERN: Define structure of component initialization
    private void initializeComponents() {
        // Main panel
        JPanel mainPanel = createMainPanel();

        // Header components
        ImageIcon tigerIcon = loadImageIcon("/assets/tiger.png");
        JLabel logoLabel = createLogoLabel(tigerIcon);
        JLabel titleLabel = createTitleLabel();
        JLabel subtitleLabel = createSubtitleLabel();

        // Form components
        formPanel = createFormPanel();
        usernameField = createUsernameField();
        passwordPanel = createPasswordPanel();
        securityQuestion = createSecurityQuestionComboBox();
        securityAnswer = createSecurityAnswerField();
        question = createQuestionField();
        answer = createAnswerField();

        // Buttons
        loginBtn = createLoginButton();
        createAccountBtn = createAccountButton();
        backBtn = createBackButton();
        forgotBtn = createForgotPasswordButton();

        // Footer
        footerLabel = createFooterLabel();

        // Add to panels
        addFormComponents();
        addMainComponents(mainPanel, logoLabel, titleLabel, subtitleLabel);

        add(mainPanel);
    }

    // FACTORY METHOD PATTERN: Create styled components
    private JPanel createMainPanel() {
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
        return mainPanel;
    }

    private JLabel createLogoLabel(ImageIcon icon) {
        JLabel label = new JLabel(icon);
        label.setBounds(200, 120, 100, 100);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JLabel createTitleLabel() {
        JLabel label = new JLabel("MINE SWEEPER");
        label.setFont(new Font("Arial", Font.BOLD, 42));
        label.setForeground(Color.WHITE);
        label.setBounds(0, 70, 500, 50);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JLabel createSubtitleLabel() {
        JLabel label = new JLabel("TIGER EDITION");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(Color.YELLOW.brighter());
        label.setBounds(0, 120, 500, 30);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(15, 15, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        panel.setLayout(null);
        panel.setBounds(75, 180, 350, 300);
        panel.setOpaque(false);
        return panel;
    }

    private JTextField createUsernameField() {
        JTextField field = new PlaceholderTextField("Username", 20);
        styleNeonTextField(field, Color.CYAN.brighter());
        field.setBounds(25, 30, 300, 45);
        return field;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(25, 85, 300, 45);
        panel.setOpaque(false);

        passwordField = createStyledPasswordField("Password", Color.CYAN.brighter());
        passwordField.setBounds(0, 0, 255, 45);

        repeatPasswordField = createStyledPasswordField("Repeat Password", Color.CYAN.brighter());
        repeatPasswordField.setBounds(0, 55, 300, 45);
        repeatPasswordField.setVisible(false);

        togglePasswordBtn = new JButton(loadImageIcon("/assets/eye.png"));
        togglePasswordBtn.setBounds(260, 0, 40, 45);
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setBorderPainted(false);
        togglePasswordBtn.setContentAreaFilled(false);
        togglePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());

        panel.add(passwordField);
        panel.add(repeatPasswordField);
        panel.add(togglePasswordBtn);

        return panel;
    }

    private JComboBox<String> createSecurityQuestionComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{
                "Birth Month",
                "First Pet's Name",
                "Birth City"
        });
        comboBox.setBounds(25, 195, 300, 45);
        styleNeonComboBox(comboBox, Color.CYAN.brighter());
        comboBox.setVisible(false);
        return comboBox;
    }

    private JTextField createSecurityAnswerField() {
        JTextField field = new PlaceholderTextField("Answer", 20);
        styleNeonTextField(field, Color.CYAN.brighter());
        field.setVisible(false);
        field.setBounds(25, 250, 300, 45);
        return field;
    }

    private JTextField createQuestionField() {
        JTextField field = new JTextField();
        field.setBounds(25, 85, 300, 45);
        styleNeonTextField(field, Color.CYAN.brighter());
        field.setEnabled(false);
        field.setVisible(false);
        return field;
    }

    private JTextField createAnswerField() {
        JTextField field = new PlaceholderTextField("Answer", 20);
        field.setBounds(25, 140, 300, 45);
        styleNeonTextField(field, Color.CYAN.brighter());
        field.setVisible(false);
        return field;
    }

    // OBSERVER PATTERN: Buttons notify controller of actions
    private JButton createLoginButton() {
        JButton button = NeonButtonFactory.createNeonButton("LOGIN", new Color(0, 200, 100));
        button.setBounds(25, 155, 300, 45);
        button.addActionListener(e -> currentState.handleAction(this));
        return button;
    }

    private JButton createAccountButton() {
        JButton button = NeonButtonFactory.createNeonButton("CREATE ACCOUNT", new Color(20, 155, 200));
        button.setBounds(25, 205, 300, 45);
        button.addActionListener(e -> {
            if (currentState == ViewState.NORMAL_LOGIN) {
                transitionToState(ViewState.REGISTRATION);
            } else {
                currentState.handleAction(this);
            }
        });
        return button;
    }

    private JButton createBackButton() {
        JButton button = NeonButtonFactory.createNeonButton("BACK", Color.RED.brighter());
        button.setVisible(false);
        button.addActionListener(e -> transitionToState(ViewState.NORMAL_LOGIN));
        return button;
    }

    private JButton createForgotPasswordButton() {
        JButton button = new JButton("Forgot Password?");
        styleLinkButton(button);
        button.setBounds(0, 480, 500, 20);
        button.addActionListener(e -> {
            controller.handleForgotPassword(usernameField.getText(),
                    user -> {
                        question.setText(user.getSecurityQuestion());
                        transitionToState(ViewState.PASSWORD_RECOVERY);
                    }
            );
        });
        return button;
    }

    private JLabel createFooterLabel() {
        JLabel label = new JLabel("Group Tiger · Version 3.0 · 2026");
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setForeground(new Color(80, 80, 80));
        label.setBounds(0, 520, 500, 20);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void addFormComponents() {
        formPanel.add(question);
        formPanel.add(answer);
        formPanel.add(securityQuestion);
        formPanel.add(securityAnswer);
        formPanel.add(usernameField);
        formPanel.add(passwordPanel);
        formPanel.add(loginBtn);
        formPanel.add(createAccountBtn);
        formPanel.add(backBtn);
    }

    private void addMainComponents(JPanel mainPanel, JLabel logo, JLabel title, JLabel subtitle) {
        mainPanel.add(logo);
        mainPanel.add(title);
        mainPanel.add(subtitle);
        mainPanel.add(formPanel);
        mainPanel.add(forgotBtn);
        mainPanel.add(footerLabel);
    }

    // STRATEGY PATTERN: Transition between states
    public void transitionToState(ViewState newState) {
        this.currentState = newState;
        newState.configureView(this);
    }

    private void layoutComponents() {
        // Components are already positioned in their factory methods
    }

    // Helper method to load images from classpath
    private ImageIcon loadImageIcon(String path) {
        java.net.URL imageUrl = getClass().getResource(path);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        System.err.println("Warning: Could not load image: " + path);
        return new ImageIcon();
    }

    // FACTORY METHOD PATTERN: Create styled password field
    private JPasswordField createStyledPasswordField(String placeholder, Color borderColor) {
        JPasswordField field = new JPasswordField();

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
        field.setForeground(new Color(130, 130, 130));
        field.setEchoChar((char) 0);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                    field.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(new Color(130, 130, 130));
                    field.setEchoChar((char) 0);
                }
            }
        });
        return field;
    }

    // BUILDER PATTERN: Configure text field styling
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
            passwordField.setEchoChar('•');
            repeatPasswordField.setEchoChar('•');
        }
    }

    private static void styleLinkButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(new Color(150, 150, 150));
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(80, 160, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(150, 150, 150));
            }
        });
    }
}