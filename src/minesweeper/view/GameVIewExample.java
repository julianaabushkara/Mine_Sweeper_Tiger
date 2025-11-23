package minesweeper.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameVIewExample {
    private JPanel gameView;
    private JLabel player1Label;
    private JLabel player1NameLabel;
    private JLabel player2Label;
    private JLabel player2NameLabel;
    private JLabel scoreLabel;
    private JLabel scoreValueLabel;
    private JButton backButton;
    private JPanel livesPanel;
    private JLabel livesLabel;
    private JButton flagModeButton;
    private JLabel life1Label;
    private JButton mineModeButton;
    private JPanel controlPanel;
    private JPanel gameInfoPanel;
    private JLabel life2Label;
    private JLabel life3Label;
    private JLabel life4Label;
    private JLabel life5Label;
    private JLabel life6Label;
    private JLabel life7Label;
    private JLabel life8Label;
    private JLabel life9Label;
    private JLabel life10Label;

    private int livesCounter;
    private ArrayList<JLabel> lives;

    public GameVIewExample() {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLives(2);
            }
        });
        flagModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //flagModeButton.setEnabled(false);
                removeLives(1);
                mineModeButton.setEnabled(true);
            }
        });
        mineModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flagModeButton.setEnabled(true);
                mineModeButton.setEnabled(false);
            }
        });


        livesCounter = 10;

        lives = new ArrayList<>();
        lives.add(life1Label);
        lives.add(life2Label);
        lives.add(life3Label);
        lives.add(life4Label);
        lives.add(life5Label);
        lives.add(life6Label);
        lives.add(life7Label);
        lives.add(life8Label);
        lives.add(life9Label);
        lives.add(life10Label);
        initLives();
    }

    private void addLives(int nLivesToAdd) {
        System.out.println(livesCounter);
        for  (int i = 0; i < nLivesToAdd; i++) {
            if (livesCounter == 10)
                return;
            lives.get(livesCounter).setEnabled(true);
            livesCounter++;
        }
    }

    private void removeLives(int nLivesToRemove) {
        System.out.println(livesCounter);
        for  (int i = 0; i < nLivesToRemove; i++) {
            if (livesCounter == 1) {
            gameLost("Ran out of lives");
        }
            livesCounter--;
            lives.get(livesCounter).setEnabled(false);

        }
    }

    private void gameLost (String loseCause) {
        System.out.println(loseCause);
        System.exit(1);
    }

    private void initLives() {
        for (JLabel life : lives) {
            life.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("GameVIewExample");
                GameVIewExample game = new GameVIewExample();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
