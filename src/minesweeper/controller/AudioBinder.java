package minesweeper.controller;

import minesweeper.model.audio.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AudioBinder {

    private static final String CLICK_BOUND = "CLICK_BOUND";

    public static void addClickToAllButtons(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof Container child) addClickToAllButtons(child);

            if (c instanceof AbstractButton b) {
                // Prevent adding multiple listeners if you call this again
                if (Boolean.TRUE.equals(b.getClientProperty(CLICK_BOUND))) continue;
                b.putClientProperty(CLICK_BOUND, true);

                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        // play BEFORE actionPerformed runs
                        new Thread(() ->
                                AudioManager.get().playSfx("/assets/audio/sfx/click.wav")
                        ).start();
                    }
                });
            }
        }
    }
}
