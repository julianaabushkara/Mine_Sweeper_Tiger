package minesweeper.controller;

import minesweeper.model.User;
import minesweeper.view.LoginView;
import minesweeper.view.components.NeonDialog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    static LoginView view;
    public LoginController(LoginView view){
        LoginController.view = view;
        view.setVisible(true);
    }
    static File UserData = new File("src/minesweeper/data/userData.json");

    public static void addUser(User user) {
        List<User> users = loadUsers();

        // Check if username already exists
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                System.out.println("User with username '" + user.getUsername() + "' already exists!");
                return;
            }
        }

        users.add(user);
        saveUsers(users);
        System.out.println("User '" + user.getUsername() + "' added successfully!");
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

        try (BufferedReader reader = new BufferedReader(new FileReader(UserData))) {
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
        return new User(username, password.toCharArray());
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(UserData))) {
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
        if (retrieveUser(user.getUsername()) == null){
            NeonDialog.showNeonDialog(view, "Login Failed", "User: " + user.getUsername() + " Not Found", "src/minesweeper/view/assets/eye.png");
        } else if (user.getPassword().compareTo(retrieveUser(user.getUsername()).getPassword()) == 0){
            NeonDialog.showNeonDialog(view, "Login Successful","Username: " + user.getUsername() + " Password: " + user.getPassword()
                    ,"src/minesweeper/view/assets/eye.png");
        } else {
            NeonDialog.showNeonDialog(view, "Login Failed", "Password Incorrect", "src/minesweeper/view/assets/eye.png");
        }


    }
}
