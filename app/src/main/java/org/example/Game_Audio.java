// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084
// Class: Game_Audio - sound effect ve muzik dosyalarini yukler ve calar.

package org.example;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Game_Audio {

    private static String[] clipNames;
    private static AudioClip[] clipValues;
    private static MediaPlayer vacuumPlayer;
    private static MediaPlayer damagePlayer;
    private static MediaPlayer musicPlayer;
    private static Class<?> appClass;

    // Tum sound effect ve muzik dosyalarini load eder
    public static void load(Class<?> ctx) {
        appClass = ctx;

        String[] names = { "health", "range", "eye", "speed", "time",
                           "ghost_death", "ripper_death", "wisp_death", "game_over", "gg", "select", "cheat", "concerta", "token_spawn" };
        clipNames = names;
        clipValues = new AudioClip[names.length];
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            try {
                var url = ctx.getResource("/sounds/" + name + ".mp3");
                if (url != null) clipValues[i] = new AudioClip(url.toExternalForm());
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

    // Background muzigi degistirir ve calar, varsa once eskisini durdurur
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

    // Verilen isimle AudioClip'i calar
    public static void play(String name) {
        if (clipNames == null || clipValues == null) return;
        for (int i = 0; i < clipNames.length; i++) {
            if (clipNames[i].equals(name)) {
                AudioClip clip = clipValues[i];
                if (clip != null) clip.play();
                return;
            }
        }
    }

    // Vacuum ses efektini baslatir
    public static void startVacuum() {
        if (vacuumPlayer != null && vacuumPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            vacuumPlayer.play();
        }
    }

    // Vacuum ses efektini durdurur
    public static void stopVacuum() {
        if (vacuumPlayer != null) vacuumPlayer.stop();
    }

    // Damage ses efektini baslatir
    public static void startDamage() {
        if (damagePlayer != null && damagePlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            damagePlayer.play();
        }
    }

    // Damage ses efektini durdurur
    public static void stopDamage() {
        if (damagePlayer != null) {
            damagePlayer.stop();
            damagePlayer.seek(javafx.util.Duration.ZERO);
        }
    }
}
