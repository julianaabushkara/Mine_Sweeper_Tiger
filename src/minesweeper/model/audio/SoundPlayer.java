package minesweeper.model.audio;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundPlayer {
    private Clip clip;

    public SoundPlayer(URL urlSound) {
        try {
            if (urlSound == null) throw new IllegalArgumentException("Sound URL is null");

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(urlSound);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

        } catch (Exception ex) {
            System.err.println("[Audio] clip is not found: " + ex.getMessage());
            clip = null;
        }
    }

    public void playOnceFromStart() {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if (clip == null) return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
    }

    public void close() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
