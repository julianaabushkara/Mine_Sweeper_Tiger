package minesweeper;

import minesweeper.controller.LoginController;
import minesweeper.view.LoginView;

import javax.swing.*;

public class LoginViewApp {
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
            System.out.println("Using default look and feel");
        }
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);
    }
}
