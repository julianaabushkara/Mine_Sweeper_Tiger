package minesweeper;

import minesweeper.controller.LoginController;
import minesweeper.model.MinesweeperApp;
import minesweeper.view.LoginView;

/**
 * Main - Application Entry Point
 *
 * Launches the Minesweeper Tiger Edition application
 *
 * @author Group Tiger
 * @version 1.0
 */
public class Main {

    /**
     * Main method - entry point of the application
     * @param args Command line arguments (not used)G
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   MINESWEEPER - TIGER EDITION v1.0    ║");
        System.out.println("║             Tiger Group               ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        System.out.println("                       ___......----:'\"\":--....(\\\n" +
                "                .-':'\"\":   :  :  :   :  :  :.(1\\.`-.\n" +
                "              .'`.  `.  :  :  :   :   : : : : : :  .';\n" +
                "             :-`. :   .  : :  `.  :   : :.   : :`.`. a;\n" +
                "             : ;-. `-.-._.  :  :   :  ::. .' `. `., =  ;\n" +
                "             :-:.` .-. _-.,  :  :  : ::,.'.-' ;-. ,'''\"\n" +
                "           .'.' ;`. .-' `-.:  :  : : :;.-'.-.'   `-'\n" +
                "    :.   .'.'.-' .'`-.' -._;..:---'''\"~;._.-;\n" +
                "    :`--'.'  : :'     ;`-.;            :.`.-'`.\n" +
                "     `'\"`    : :      ;`.;             :=; `.-'`.\n" +
                "             : '.    :  ;              :-:   `._-`.\n" +
                "              `'\"'    `. `.            `--'     `._;\n" +
                "                        `'\"'\n");

        // Create and start the application
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);
    }
}