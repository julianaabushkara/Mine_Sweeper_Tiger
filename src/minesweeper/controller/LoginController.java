package minesweeper.controller;

import minesweeper.model.MinesweeperApp;
import minesweeper.model.SessionContext;
import minesweeper.model.User;
import minesweeper.view.LoginView;
import minesweeper.view.components.NeonDialog;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @version 1.2
 *  Login controller - Minesweeper Tiger Edition
 *  Provides the main start menu interface for the application. This view
 *  arranges and displays the title, subtitle, tiger logo, and all primary
 *  navigation buttons using a GridBagLayout. It handles only visual structure;
 *  button actions are delegated to the controller.
 *      ⢀⣴⣿⣷⣶⣄⡀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣠⣤⣤⣤⣴⣶⣶⣶⣶⣶⣶⣶⣶⣶⣶⣤⣤⣤⣤⣀⣀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⣾⣿⣶⡄⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⣼⡏⣶⣄⠉⠛⠿⢿⣿⣿⣷⣶⣿⣿⣿⠿⠟⠋⢉⣩⣷⣿⣿⣿⡿⠛⢿⣿⣿⣿⣾⣍⡉⠙⠻⠿⣿⣿⣿⣶⣶⣿⣿⡿⠿⠛⠉⢀⣀⢹⣷⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⢀⣿⠃⣿⣿⣿⣄⡀⠀⠀⠉⠻⣿⡿⠋⢀⣠⣴⣾⣿⠿⠿⠛⠛⠁⠀⠀⠀⠈⠙⠛⠿⠿⣿⣿⣦⣄⡀⠙⢿⣿⠟⠉⠀⠀⠀⣠⣴⣿⣿⠈⣿⡄⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⣼⡟⠀⣿⣿⣿⣿⣷⣄⠀⠀⢠⠟⠀⠀⣿⣿⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⣿⣿⠀⠀⠻⡆⠀⠀⢠⣾⣿⣿⣿⣿⠀⢹⣧⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⣿⡇⠀⢿⣿⣿⣿⣿⣿⡆⠀⠈⠀⠀⣰⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠹⣿⣧⠀⠀⠈⠀⢈⣿⣿⣿⣿⣿⣿⠀⢸⣿⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⣿⡇⠀⢸⣿⣿⣿⣿⣿⡿⠀⢀⣴⣿⡿⠃⠀⢀⣀⣀⣠⣤⣶⣶⣾⡿⠛⣿⣷⣶⣶⣤⣄⣀⣀⡀⠀⠘⠿⣿⣦⡀⠀⣿⣿⣿⣿⣿⣿⡏⡀⢸⣿⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⣿⣿⣄⠀⢻⣿⣿⡿⠛⠁⣴⣿⡟⠁⢀⣴⣾⡿⠿⠿⠿⠿⠛⠛⠉⠀⠀⠀⠉⠛⠛⠿⠿⠿⠿⢿⣷⣤⡀⠈⢻⣿⣦⠝⢿⣿⣿⣿⣿⡟⢠⣾⣿⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠘⣿⣿⣦⡄⢻⡟⠀⠀⢀⣼⠿⠃⣠⡿⠋⠁⠀⠀⠀⠀⠀⠀⣠⡾⠀⠀⠈⢳⣄⠀⠀⠀⠀⠀⠀⠈⠙⢿⣄⠘⠿⣷⡄⠀⠙⣿⡿⢋⣴⣿⣿⠋⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⢘⣿⣿⠟⣫⠀⢀⣴⠟⠁⠀⠜⠁⠀⠀⠀⣀⣤⣴⣶⣶⣾⡿⠀⠀⠀⠀⠀⣿⣷⣶⣶⣶⣤⣀⠀⠀⠀⠈⠳⠀⠈⠻⣦⡀⠈⢙⠻⣿⣿⡃⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⣠⣿⣿⣿⡟⠁⠠⠞⠁⠀⠀⠀⠀⢀⠤⠴⠿⠿⠛⠋⠀⠀⠉⠛⠀⠀⠀⠀⠀⠛⠉⠀⠈⠙⠛⠿⠿⠦⠤⠄⠀⠀⠀⠀⠀⠳⠄⠈⢻⣿⣿⣿⣦⡀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠈⣿⣿⣿⣿⠁⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠂⠀⠀⠀⠀⠀⢀⣠⣤⡴⠂⠀⠘⢦⣤⣄⡀⠀⠀⠀⠀⠀⠀⢠⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢿⣿⣿⣿⠃⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⣠⣿⣿⡿⠃⠀⠀⠀⠀⠀⠀⠀⣠⣾⠟⠀⠀⣠⣤⣶⣶⡾⠿⠛⠁⠀⠀⠀⠀⠀⠈⠛⠿⢷⣶⣶⣦⣄⠀⠀⠻⣷⣄⠀⠀⠀⠀⠀⠀⠀⠘⢿⣿⣿⣆⠀⠀⠀⠀⠀
⠀⠀⠀⢀⣾⣿⣿⠟⠁⠀⠀⢀⣠⡞⠁⣰⣾⣿⠟⠀⠀⢰⡿⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⢿⡄⠀⠀⠹⣿⣷⣦⠀⢳⣤⡀⠀⠀⠀⠙⣿⣿⣷⣄⠀⠀⠀
⠀⠀⣰⣿⣿⡿⠁⠀⠀⠀⣴⣿⡟⠀⣼⣿⠟⠁⠀⠀⠀⡞⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢳⡀⠀⠀⠈⠻⣿⣧⠀⢻⣿⣦⡀⠀⠀⠈⢻⣿⣿⣆⠀⠀
⠀⣰⣿⣿⠏⠀⠀⠀⢀⣾⣿⣿⡇⢰⣿⡇⢤⣤⣤⣤⣤⣄⣀⠀⠀⡤⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣦⠀⠀⣀⣤⣤⣤⣤⣤⡤⠸⣿⡆⠘⣿⣿⡳⡀⠀⠀⠀⠹⣿⣿⣧⠀
⢠⣿⣿⡏⠀⠀⠀⠀⢘⣾⣿⣿⡇⠘⣿⠀⠀⠹⠿⣿⠟⢻⣿⢿⣾⣷⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣿⣷⡿⣿⡟⠿⣿⠿⠏⠀⠀⣿⠁⢀⣿⣿⣷⡀⠀⠀⠀⠀⢹⣿⣿⡆
⣸⣿⣿⠁⠀⠀⠀⠀⢸⣿⣿⣿⣷⡀⠙⠀⠀⠀⠀⣿⣧⡈⠃⢀⣿⣿⣿⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣿⣿⣿⡀⠘⣁⣼⣿⠀⠀⠀⠀⠋⠀⣼⣿⣿⣿⣧⠀⠀⠀⠀⠀⢿⣿⣷
⣿⣿⡇⢠⡄⠀⠀⠀⢸⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠛⠛⠛⠛⠿⣿⣿⣿⡆⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⠿⠛⠛⠛⠋⠁⠀⠀⠀⠀⠀⣾⣿⣿⣿⣿⡟⠀⠀⠀⠀⡄⢸⣿⣿
⢿⣿⣧⣿⡀⠀⠀⠀⠘⣿⢿⣿⣿⣧⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⣿⡿⠃⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣼⣿⣿⣿⣿⠇⠀⠀⠀⠀⣿⣼⣿⡟
⢸⣿⣿⣿⡇⠀⠀⠀⠀⠙⢸⣿⣿⣿⠛⠷⠤⠀⠀⠀⠀⠀⠀⠀⣼⣿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⣿⣆⠀⠀⠀⠀⠀⠀⠀⠤⠾⠛⣿⣿⣿⡏⠋⠀⠀⠀⠀⢀⣿⣿⣿⡇
⠘⣿⡿⣿⣇⢰⡄⠀⠀⠀⠀⢿⣿⣿⣷⡀⠀⠀⠀⠀⠀⠀⠀⣰⣿⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⣿⣆⠀⠀⠀⠀⠀⠀⠀⢀⣾⣿⣿⣿⠁⠀⠀⠀⢀⡄⢸⣿⢿⣿⠃
⠀⠻⠁⣿⣿⣾⣷⠀⠀⠀⠀⠈⢿⣿⣿⣷⡀⠀⠀⠀⠀⠀⢸⣿⠋⣰⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣆⠹⣿⡇⠀⠀⠀⠀⠀⢀⣾⣿⣿⣿⠁⠀⠀⠀⠀⣼⣧⣿⣿⡀⠛⠀
⠀⠀⠀⣿⣿⣿⣿⡇⠀⣀⠀⠀⠈⢻⣿⣿⣧⢀⡄⠀⠀⠀⣿⡇⠀⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀⢹⣷⠀⠀⠀⢠⡀⣸⣿⣿⡟⠁⠀⠀⠀⠀⢰⣿⣿⣿⣿⠃⠀⠀
⠀⠀⠀⠹⣿⣿⣿⣿⡄⣿⣧⡀⠀⠀⠻⢿⣿⣸⣷⡀⠀⠀⢿⠀⢠⣿⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⡇⠀⣿⠀⠀⠀⣼⣇⣿⣿⠟⠀⠀⢀⣴⣿⢠⣿⣿⣿⣿⡟⠀⠀⠀
⠀⠀⠀⠀⠙⣿⠻⣿⣷⣿⣿⣿⣦⡀⠀⠘⣿⣿⣿⣷⣤⣀⠀⢀⣾⣿⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⣿⣿⡄⢀⣀⣤⣾⣿⣿⣿⠇⠀⠀⣠⣿⣿⣿⣾⣿⠟⣿⠏⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠈⠂⠙⢿⣿⣿⣿⣿⣿⣦⡀⠀⠉⠉⠹⣿⣿⡇⠈⣿⡁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣿⠇⢸⣿⣿⡏⠉⠉⠀⢀⣴⣾⣿⣿⣿⣿⣿⠋⠀⠁⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⠿⣿⡘⣿⣿⣿⣷⣤⣤⣼⣿⠿⣿⡀⢻⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣸⡟⠀⣾⠿⣿⣧⣤⣤⣾⣿⣿⣿⠃⣿⠿⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⠿⠿⢿⣿⣿⣿⡆⠈⠓⠀⠹⢷⣤⣄⣀⡀⠀⠀⠀⠀⣀⣤⣤⣾⠟⠀⠘⠁⢠⣿⣿⣿⡿⠿⠿⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠒⠒⢻⣿⣿⠛⠿⣶⣤⣀⠙⠿⢿⣿⣿⣿⣿⣿⣿⠿⠋⢁⣤⣶⠾⠟⣿⣿⡿⠒⠒⠂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠤⠔⠒⢿⣿⡟⠻⠶⠤⠤⠀⠀⠀⠀⠙⣿⠋⠁⠀⠀⠀⠠⠤⠶⠿⢛⣿⡿⠓⠲⠤⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠒⠚⢿⣿⣟⠳⠶⠄⠀⠀⠀⠀⢀⣿⡀⠀⠀⠀⠀⠰⠶⠾⣛⣿⡿⠛⠒⠦⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⣶⣶⣶⣶⡶⠾⢿⣿⡿⠷⢶⣶⣶⣶⣶⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠛⠿⠿⣿⣿⣦⣀⡀⠀⠀⠀⠀⠀⢀⣀⣤⣾⣿⡿⠿⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠛⠛⠿⢿⣷⣶⣶⡿⠿⠟⠛⠉⠀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀


 * DESIGN PATTERNS APPLIED:
 * 1. SINGLETON PATTERN - Lazy initialization of user data path
 * 2. COMMAND PATTERN - Handler methods encapsulate actions
 * 3. TEMPLATE METHOD PATTERN - Common validation logic
 * 4. STRATEGY PATTERN - Different authentication strategies
 * 5. FACADE PATTERN - Simplifies complex user operations
 */
public class LoginController {
    private LoginView view;
    private static Path userDataFile;
    private ImageIcon dialogIcon;

    // SINGLETON PATTERN: Lazy initialization
    private static synchronized Path getUserDataPath() {
        if (userDataFile == null) {
            try {
                Path userHome = Paths.get(System.getProperty("user.home"));
                Path appDir = userHome.resolve(".minesweeper");
                Files.createDirectories(appDir);
                userDataFile = appDir.resolve("UserData.json");

                if (!Files.exists(userDataFile)) {
                    try (InputStream is = LoginController.class.getResourceAsStream("/minesweeper/Data/UserData.json")) {
                        if (is != null) {
                            Files.copy(is, userDataFile);
                        } else {
                            Files.write(userDataFile, "[]".getBytes());
                        }
                    } catch (IOException e) {
                        Files.write(userDataFile, "[]".getBytes());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot create user data directory", e);
            }
        }
        return userDataFile;
    }

    public LoginController(LoginView view) {
        this.view = view;
        this.view.setController(this);
        this.dialogIcon = loadImageIcon("/assets/eye.png");
        AudioBinder.addClickToAllButtons(view);
        view.setVisible(true);
    }

    // COMMAND PATTERN: Encapsulate login action
    public void handleLogin(String username, char[] password) {
        // TEMPLATE METHOD: Validate inputs
        if (!validateUsername(username)) {
            return;
        }

        User user = findUser(username);
        if (user == null) {
            showErrorDialog("Login Failed", "User: " + username + " Not Found");
            return;
        }

        if (!authenticateUser(user, password)) {
            showErrorDialog("Login Failed", "Password Incorrect");
            return;
        }

        // Success - set session and navigate
        SessionContext.currentUser = user;
        view.dispose();
        new MinesweeperApp().start();
    }

    // COMMAND PATTERN: Encapsulate registration action
    public void handleRegistration(String username, char[] password,
                                   char[] repeatPassword, String securityAnswer,
                                   String securityQuestion) {
        // TEMPLATE METHOD: Validate all inputs
        if (!validateUsername(username)) {
            return;
        }

        if (userExists(username)) {
            showErrorDialog("Register Failed", "User with username '" + username + "' already exists!");
            return;
        }

        if (!validatePasswordMatch(password, repeatPassword)) {
            showErrorDialog("Register Failed", "Passwords don't match");
            return;
        }

        if (securityAnswer.equals("Answer") || securityAnswer.isEmpty()) {
            showErrorDialog("Register Failed", "Please insert a security answer");
            return;
        }

        // Create and save user
        User newUser = new User(username, password, securityAnswer, securityQuestion);
        saveUser(newUser);

        showSuccessDialog("Registration Successful", "You can now login with your credentials");
        view.transitionToState(LoginView.ViewState.NORMAL_LOGIN);
    }

    // COMMAND PATTERN: Encapsulate password recovery action
    public void handlePasswordRecovery(String username, String securityAnswer) {
        User user = findUser(username);

        if (user == null) {
            showErrorDialog("Password Retrieve Failed", "User: " + username + " Not Found");
            return;
        }

        if (!user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {
            showErrorDialog("Password Retrieve Failed", "Incorrect answer or security question");
            return;
        }

        showSuccessDialog("Password Retrieved Successfully", "Password is: " + user.getPassword());
        view.transitionToState(LoginView.ViewState.NORMAL_LOGIN);
    }

    // COMMAND PATTERN: Handle forgot password button
    public void handleForgotPassword(String username, Consumer<User> onSuccess) {
        if (!validateUsername(username)) {
            return;
        }

        User user = findUser(username);
        if (user == null) {
            showErrorDialog("User Not Found", "Please enter a valid username");
            return;
        }

        onSuccess.accept(user);
    }

    // TEMPLATE METHOD PATTERN: Common validation logic
    private boolean validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            showErrorDialog("Validation Error", "Please enter a username");
            return false;
        }
        return true;
    }

    private boolean validatePasswordMatch(char[] password, char[] repeatPassword) {
        String pwd1 = new String(password);
        String pwd2 = new String(repeatPassword);
        return pwd1.equals(pwd2);
    }

    // STRATEGY PATTERN: Different authentication strategies
    private boolean authenticateUser(User user, char[] password) {
        String userPassword = user.getPassword();
        String inputPassword = new String(password);
        return userPassword.equals(inputPassword);
    }

    // FACADE PATTERN: Simplify user operations
    private User findUser(String username) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private boolean userExists(String username) {
        return findUser(username) != null;
    }

    private void saveUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    // Data persistence methods
    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(getUserDataPath().toFile()))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            String json = jsonContent.toString().trim();

            if (json.isEmpty() || json.equals("[]")) {
                return users;
            }

            json = json.substring(1, json.length() - 1).trim();

            if (json.isEmpty()) {
                return users;
            }

            int braceCount = 0;
            StringBuilder userJson = new StringBuilder();

            for (int i = 0; i < json.length(); i++) {
                char c = json.charAt(i);

                if (c == '{') {
                    braceCount++;
                    userJson.append(c);
                } else if (c == '}') {
                    braceCount--;
                    userJson.append(c);

                    if (braceCount == 0) {
                        users.add(parseUser(userJson.toString()));
                        userJson = new StringBuilder();
                    }
                } else if (braceCount > 0) {
                    userJson.append(c);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return users;
    }

    private User parseUser(String json) {
        String username = extractValue(json, "username");
        String password = extractValue(json, "password");
        String securityAnswer = extractValue(json, "securityAnswer");
        String securityQuestion = extractValue(json, "securityQuestion");
        return new User(username, password.toCharArray(), securityAnswer, securityQuestion);
    }

    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return "";
        }

        startIndex += searchKey.length();
        int endIndex = startIndex;

        while (endIndex < json.length()) {
            if (json.charAt(endIndex) == '"' && (endIndex == 0 || json.charAt(endIndex - 1) != '\\')) {
                break;
            }
            endIndex++;
        }

        String value = json.substring(startIndex, endIndex);
        return unescapeJson(value);
    }

    private String unescapeJson(String str) {
        return str.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getUserDataPath().toFile()))) {
            writer.write("[\n");

            for (int i = 0; i < users.size(); i++) {
                writer.write("  " + users.get(i).toJson());

                if (i < users.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("]");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // UI Helper methods
    private void showErrorDialog(String title, String message) {
        NeonDialog.showNeonDialog(view, title, message, dialogIcon, true, false);
    }

    private void showSuccessDialog(String title, String message) {
        NeonDialog.showNeonDialog(view, title, message, dialogIcon, false, true);
    }

    private ImageIcon loadImageIcon(String path) {
        java.net.URL imageUrl = getClass().getResource(path);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        System.err.println("Warning: Could not load image: " + path);
        return new ImageIcon();
    }
}