package minesweeper;

import minesweeper.controller.LoginController;
import minesweeper.view.LoginView;

/**
 * Main - Application Entry Point
 * </p>
 * Launches the Minesweeper Tiger Edition application
 *
 * @author Group Tiger
 * @version 2.0
 */
public class Main {

    /**
     * Main method - entry point of the application
     * @param args Command line arguments (not used)G
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   MINESWEEPER - TIGER EDITION v2.0    ║");
        System.out.println("║            \uD83C\uDD63\uD83C\uDD58" +
                                        "\uD83C\uDD56\uD83C\uDD54" +
                                       "\uD83C\uDD61 \uD83C\uDD56" +
                                        "\uD83C\uDD61\uD83C\uDD5E" +
                                      "\uD83C\uDD64\uD83C\uDD5F            ║");
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