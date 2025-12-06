package minesweeper.model;

import java.util.Objects;

public class User {
    private String username;
    private String password;
    private String securityAnswer;
    private String securityQuestion;

    public User(String username, char[] password) {
        this.username = username;
        this.password = new String(password);
    }

    public User(String username, char[] password,  String securityAnswer, String securityQuestion) {
        this.username = username;
        this.password = new String(password);
        this.securityAnswer = securityAnswer;
        this.securityQuestion = securityQuestion;
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

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String toJson() {
        return String.format("{\"username\":\"%s\",\"password\":\"%s\",\"securityAnswer\":\"%s\",\"securityQuestion\":\"%s\"}",
                escapeJson(username), escapeJson(password), escapeJson(securityAnswer), escapeJson(securityQuestion));
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public String toString() {
        return ("User: " + username + ", Password: " + password +  ", Security Answer: " + securityAnswer +
                ", Security Question: " + securityQuestion);
    }

    public boolean equals(User user) {
        return Objects.equals(this.username, user.username) && Objects.equals(this.password, user.password)
                && Objects.equals(this.securityAnswer, user.securityAnswer) && Objects.equals(this.securityQuestion, user.securityQuestion);
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
}
