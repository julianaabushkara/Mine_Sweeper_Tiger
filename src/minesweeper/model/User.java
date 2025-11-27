package minesweeper.model;

import java.util.Objects;

public class User {
    private String username;
    private String password;

    public User(String username, char[] password) {
        this.username = username;
        this.password = new String(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = new String(password);
    }

    public void setPassword(String password) {
        this.password =  password;
    }

    public String toJson() {
        return String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                escapeJson(username), escapeJson(password));
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public String toString() {
        return ("User: " + username + ", Password: " + password);
    }

    public boolean equals(User user) {
        if (Objects.equals(this.username, user.username) &&  Objects.equals(this.password, user.password)) {
            return true;
        }
        return false;
    }
}
