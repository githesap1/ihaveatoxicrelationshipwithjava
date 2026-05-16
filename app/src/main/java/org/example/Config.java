// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Config - config.txt'den oyun ayarlarini okur ve depolar.

package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Config {

    public int maximumHealth;
    public int maximumVacuum;
    public int entityDamage;
    public int vacuumDecrease;
    public int vacuumIncrease;

    public int level1PlayableAreaX;
    public int level1PlayableAreaY;
    public int level1PlayableAreaWidth;
    public int level1PlayableAreaHeight;
    public int level1Time;
    public int level1Ghosts;
    public int level1Rippers;
    public int level1Wisps;

    public int level2PlayableAreaX;
    public int level2PlayableAreaY;
    public int level2PlayableAreaWidth;
    public int level2PlayableAreaHeight;
    public int level2Time;
    public int level2Ghosts;
    public int level2Rippers;
    public int level2Wisps;

    public int level3PlayableAreaX;
    public int level3PlayableAreaY;
    public int level3PlayableAreaWidth;
    public int level3PlayableAreaHeight;
    public int level3Time;
    public int level3Ghosts;
    public int level3Rippers;
    public int level3Wisps;

    public int healthTokenIncrease;
    public int vacuumTokenIncrease;
    public int eyeTokenDuration;
    public int speedTokenDuration;

    // Private constructor, disaridan new Config() yapilamaz.
    private Config() {}

    // config.txt'zi okuzup compile eder, field'lari doldurur.
    public static Config load(File file) throws IOException {
        Config config = new Config();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(":");
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                int value = Integer.parseInt(parts[1].trim());

                // player stats
                if (key.equals("maximum_health")) {
                    config.maximumHealth = value;
                } else if (key.equals("maximum_vacuum")) {
                    config.maximumVacuum = value;
                } else if (key.equals("entity_damage")) {
                    config.entityDamage = value;
                } else if (key.equals("vacuum_decrease")) {
                    config.vacuumDecrease = value;
                } else if (key.equals("vacuum_increase")) {
                    config.vacuumIncrease = value;

                // level 1
                } else if (key.equals("level_1_playable_area_x")) {
                    config.level1PlayableAreaX = value;
                } else if (key.equals("level_1_playable_area_y")) {
                    config.level1PlayableAreaY = value;
                } else if (key.equals("level_1_playable_area_width")) {
                    config.level1PlayableAreaWidth = value;
                } else if (key.equals("level_1_playable_area_height")) {
                    config.level1PlayableAreaHeight = value;
                } else if (key.equals("level_1_time")) {
                    config.level1Time = value;
                } else if (key.equals("level_1_ghosts")) {
                    config.level1Ghosts = value;
                } else if (key.equals("level_1_rippers")) {
                    config.level1Rippers = value;
                } else if (key.equals("level_1_wisps")) {
                    config.level1Wisps = value;

                // level 2
                } else if (key.equals("level_2_playable_area_x")) {
                    config.level2PlayableAreaX = value;
                } else if (key.equals("level_2_playable_area_y")) {
                    config.level2PlayableAreaY = value;
                } else if (key.equals("level_2_playable_area_width")) {
                    config.level2PlayableAreaWidth = value;
                } else if (key.equals("level_2_playable_area_height")) {
                    config.level2PlayableAreaHeight = value;
                } else if (key.equals("level_2_time")) {
                    config.level2Time = value;
                } else if (key.equals("level_2_ghosts")) {
                    config.level2Ghosts = value;
                } else if (key.equals("level_2_rippers")) {
                    config.level2Rippers = value;
                } else if (key.equals("level_2_wisps")) {
                    config.level2Wisps = value;

                // level 3
                } else if (key.equals("level_3_playable_area_x")) {
                    config.level3PlayableAreaX = value;
                } else if (key.equals("level_3_playable_area_y")) {
                    config.level3PlayableAreaY = value;
                } else if (key.equals("level_3_playable_area_width")) {
                    config.level3PlayableAreaWidth = value;
                } else if (key.equals("level_3_playable_area_height")) {
                    config.level3PlayableAreaHeight = value;
                } else if (key.equals("level_3_time")) {
                    config.level3Time = value;
                } else if (key.equals("level_3_ghosts")) {
                    config.level3Ghosts = value;
                } else if (key.equals("level_3_rippers")) {
                    config.level3Rippers = value;
                } else if (key.equals("level_3_wisps")) {
                    config.level3Wisps = value;

                // tokens
                } else if (key.equals("health_token_increase")) {
                    config.healthTokenIncrease = value;
                } else if (key.equals("vacuum_token_increase")) {
                    config.vacuumTokenIncrease = value;
                } else if (key.equals("eye_token_duration")) {
                    config.eyeTokenDuration = value;
                } else if (key.equals("speed_token_duration")) {
                    config.speedTokenDuration = value;
                }
            }

        return config;
    }
}
