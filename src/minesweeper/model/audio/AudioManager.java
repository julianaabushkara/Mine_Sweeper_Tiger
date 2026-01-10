package minesweeper.model.audio;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static AudioManager instance;

    public static AudioManager get() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    private final Map<String, SoundPlayer> cache = new HashMap<>();
    private SoundPlayer currentMusic;

    private AudioManager() {}

    private SoundPlayer load(String path) {
        return cache.computeIfAbsent(path, p -> {
            URL url = getClass().getResource(p);
            if (url == null) {
                System.err.println("[Audio] Missing resource: " + p);
                return new SoundPlayer(null); // will be null clip anyway
            }
            return new SoundPlayer(url);
        });
    }

    public void playSfx(String path) {
        SoundPlayer s = load(path);
        s.playOnceFromStart();
    }

    public void playMusic(String path) {
        stopMusic();
        currentMusic = load(path);
        currentMusic.loop();
    }

    public void stopMusic() {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = null;
    }

    public void shutdown() {
        stopMusic();
        cache.values().forEach(SoundPlayer::close);
        cache.clear();
    }
}
