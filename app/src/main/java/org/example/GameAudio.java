// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: GameAudio - sound effect ve müzik dosyalarını yükler ve çalar.

package org.example;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameAudio {

    private static final Map<String, AudioClip> clips = new HashMap<>();
    private static MediaPlayer vacuumPlayer;
    private static MediaPlayer damagePlayer;
    private static MediaPlayer musicPlayer;
    private static Class<?> appClass;

    // Tüm sound effect ve müzik dosyalarını load eder.
    public static void load(Class<?> ctx) {
        appClass = ctx;

        String[] names = { "health", "range", "eye", "speed", "time",
                           "ghost_death", "ripper_death", "wisp_death", "game_over", "gg", "select", "cheat", "concerta", "token_spawn" };
        for (String name : names) {
            try {
                var url = ctx.getResource("/sounds/" + name + ".mp3");
                if (url != null) clips.put(name, new AudioClip(url.toExternalForm()));
            } catch (Exception ignored) {}
        }

        try {

            var vacUrl = ctx.getResource("/sounds/vacuum_effect.mp3");
            if (vacUrl != null) {
                vacuumPlayer = new MediaPlayer(new Media(vacUrl.toExternalForm()));
                vacuumPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
        } catch (Exception ignored) {}

        try {
            var dmgUrl = ctx.getResource("/sounds/damage.mp3");
            if (dmgUrl != null) {
                damagePlayer = new MediaPlayer(new Media(dmgUrl.toExternalForm()));
                damagePlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
        } catch (Exception ignored) {}
    }

    // Background müziği değiştirir ve çalar, varsa önce eskisini durdurur.
    public static void playMusic(String name) {
        if (appClass == null) return;
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
        }
        try {
            var url = appClass.getResource("/music/" + name + ".mp3");
            if (url != null) {
                musicPlayer = new MediaPlayer(new Media(url.toExternalForm()));
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.play();
            }
        } catch (Exception ignored) {}
    }

    // Verilen isimle AudioClip'i çalar.
    public static void play(String name) {

        AudioClip clip = clips.get(name);
        if (clip != null) clip.play();
    }

    // Vacuum ses efektini başlatır.
    public static void startVacuum() {
        if (vacuumPlayer != null && vacuumPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            vacuumPlayer.play();
        }
    }

    // Vacuum ses efektini durdurur.
    public static void stopVacuum() {
        if (vacuumPlayer != null) vacuumPlayer.stop();
    }

    // Damage ses efektini başlatır.
    public static void startDamage() {
        if (damagePlayer != null && damagePlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            damagePlayer.play();
        }
    }

    // Damage ses efektini durdurur.
    public static void stopDamage() {
        if (damagePlayer != null) {
            damagePlayer.stop();
            damagePlayer.seek(javafx.util.Duration.ZERO);
        }
    }
}
