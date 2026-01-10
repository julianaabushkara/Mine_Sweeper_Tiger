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

/**
 * @version 1.1
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
 **/

public class LoginController {
    static LoginView view;
    private static final Path USER_DATA_FILE = getUserDataPath();

    public LoginController(LoginView view){
        LoginController.view = view;
        AudioBinder.addClickToAllButtons(view); // ✅ add this

        view.setVisible(true);
    }

    // Helper method to get user data file path
    private static Path getUserDataPath() {
        try {
            Path userHome = Paths.get(System.getProperty("user.home"));
            Path appDir = userHome.resolve(".minesweeper");
            Files.createDirectories(appDir);
            Path dataFile = appDir.resolve("UserData.json");

            // If file doesn't exist, try to copy from classpath
            if (!Files.exists(dataFile)) {
                try (InputStream is = LoginController.class.getResourceAsStream("/minesweeper/Data/UserData.json")) {
                    if (is != null) {
                        Files.copy(is, dataFile);
                    } else {
                        // Create empty user data file
                        Files.write(dataFile, "[]".getBytes());
                    }
                } catch (IOException e) {
                    // Create empty file if copy fails
                    Files.write(dataFile, "[]".getBytes());
                }
            }
            return dataFile;
        } catch (IOException e) {
            throw new RuntimeException("Cannot create user data directory", e);
        }
    }

    // Helper method to load images from classpath
    private static ImageIcon loadImageIcon(String path) {
        java.net.URL imageUrl = LoginController.class.getResource(path);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        System.err.println("Warning: Could not load image: " + path);
        return new ImageIcon();
    }

    public static boolean doesUserExist(String username) {
        ImageIcon eyeIcon = loadImageIcon("/assets/eye.png");
        if (retrieveUser(username) == null){
            NeonDialog.showNeonDialog(view, "Password Retrieve Failed", "User: " + username + " Not Found", eyeIcon, true, false);
            return false;
        }
        return true;
    }

    public static boolean addUser(User user, String passwordRepeat) {
        // Check if username already exists
        List<User> users = loadUsers();
        ImageIcon eyeIcon = loadImageIcon("/assets/eye.png");
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                System.out.println("User with username '" + user.getUsername() + "' already exists!");
                NeonDialog.showNeonDialog(view, "Register Failed", "User with username '"
                        + user.getUsername() + "' already exists!", eyeIcon, true, false);
                return false;
            }
        }
        // Check if passwords match
        if (user.getPassword().compareTo(passwordRepeat) != 0) {
            System.out.println("Passwords don't match: " + user.getPassword() + ", " + passwordRepeat);
            NeonDialog.showNeonDialog(view, "Register Failed", "Passwords don't match",
                    eyeIcon, true, false);
            return false;
        } else if (user.getSecurityAnswer().compareTo("Answer") == 0) {
            System.out.println("No security answer");
            NeonDialog.showNeonDialog(view, "Register Failed", "Please insert a security answer",
                    eyeIcon, true, false);
            return false;
        }
        // Add user
        users.add(user);
        saveUsers(users);
        System.out.println("User '" + user.getUsername() + "' added successfully!");
        return true;
    }

    public static boolean retrievePassword(String userName, String securityAnswer) {
        ImageIcon eyeIcon = loadImageIcon("/assets/eye.png");
        if (retrieveUser(userName) == null) {
            NeonDialog.showNeonDialog(view, "Password Retrieve Failed", "User: " + userName + " Not Found",
                    eyeIcon, true, false);
            return false;
        }
        User user = retrieveUser(userName);
        System.out.println(user.toString());
        if (user.getSecurityAnswer().compareTo(securityAnswer) == 0) {
            System.out.println("Retrieving password for user: " + userName + " - password: " + user.getPassword());
            NeonDialog.showNeonDialog(view, "Password Retrieved Successfully", "Password is: " +  user.getPassword(),
                    eyeIcon, true, false);
            return true;
        } else {
            System.out.println("Security answer incorrect");
            NeonDialog.showNeonDialog(view, "Password Retrieve Failed", "Incorrect answer or security question",
                    eyeIcon, true, false);
        }
        return false;
    }

    public static User retrieveUser(String username) {
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        System.out.println("User '" + username + "' not found!");
        return null;
    }

    public List<User> getAllUsers() {
        return loadUsers();
    }

    private static List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE.toFile()))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            String json = jsonContent.toString().trim();

            if (json.isEmpty() || json.equals("[]")) {
                return users;
            }

            // Remove opening and closing brackets
            json = json.substring(1, json.length() - 1).trim();

            if (json.isEmpty()) {
                return users;
            }

            // Parse each user object
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

    private static User parseUser(String json) {
        String username = extractValue(json, "username");
        String password = extractValue(json, "password");
        String securityAnswer = extractValue(json, "securityAnswer");
        String securityQuestion = extractValue(json, "securityQuestion");
        return new User(username, password.toCharArray(),  securityAnswer, securityQuestion);
    }

    private static String extractValue(String json, String key) {
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

    private static String unescapeJson(String str) {
        return str.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private static void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE.toFile()))) {
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

    public static void login(User user){
        System.out.println(retrieveUser(user.getUsername()));
        ImageIcon eyeIcon = loadImageIcon("/assets/eye.png");
        if (retrieveUser(user.getUsername()) == null){
            NeonDialog.showNeonDialog(view, "Login Failed", "User: " + user.getUsername() + " Not Found", eyeIcon, true, false);
        } else if (user.getPassword().compareTo(retrieveUser(user.getUsername()).getPassword()) == 0){
            System.out.println("Successfully logged in with user: " + user.getUsername());
            //  STORE LOGGED-IN USER
            SessionContext.currentUser = retrieveUser(user.getUsername());
            assert SessionContext.currentUser != null;
            System.out.println(SessionContext.currentUser.getUsername());

            view.dispose();
            MinesweeperApp app = new MinesweeperApp();
            app.start();
        } else {
            NeonDialog.showNeonDialog(view, "Login Failed", "Password Incorrect", eyeIcon, true, false);
        }


    }
}