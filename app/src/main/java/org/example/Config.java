// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Config.java - config.txt'i okur ve oyun ayarlarini saklar

package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

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

    private Config() {}

    public static Config load(File file) throws IOException {
        Config config = new Config();

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                int value = Integer.parseInt(parts[1].trim());

                switch (key) {
                    case "maximum_health":           config.maximumHealth = value; break;
                    case "maximum_vacuum":           config.maximumVacuum = value; break;
                    case "entity_damage":            config.entityDamage = value; break;
                    case "vacuum_decrease":          config.vacuumDecrease = value; break;
                    case "vacuum_increase":          config.vacuumIncrease = value; break;
                    case "level_1_playable_area_x":  config.level1PlayableAreaX = value; break;
                    case "level_1_playable_area_y":  config.level1PlayableAreaY = value; break;
                    case "level_1_playable_area_width": config.level1PlayableAreaWidth = value; break;
                    case "level_1_playable_area_height": config.level1PlayableAreaHeight = value; break;
                    case "level_1_time":             config.level1Time = value; break;
                    case "level_1_ghosts":           config.level1Ghosts = value; break;
                    case "level_1_rippers":          config.level1Rippers = value; break;
                    case "level_1_wisps":            config.level1Wisps = value; break;
                    case "level_2_playable_area_x":  config.level2PlayableAreaX = value; break;
                    case "level_2_playable_area_y":  config.level2PlayableAreaY = value; break;
                    case "level_2_playable_area_width": config.level2PlayableAreaWidth = value; break;
                    case "level_2_playable_area_height": config.level2PlayableAreaHeight = value; break;
                    case "level_2_time":             config.level2Time = value; break;
                    case "level_2_ghosts":           config.level2Ghosts = value; break;
                    case "level_2_rippers":          config.level2Rippers = value; break;
                    case "level_2_wisps":            config.level2Wisps = value; break;
                    case "level_3_playable_area_x":  config.level3PlayableAreaX = value; break;
                    case "level_3_playable_area_y":  config.level3PlayableAreaY = value; break;
                    case "level_3_playable_area_width": config.level3PlayableAreaWidth = value; break;
                    case "level_3_playable_area_height": config.level3PlayableAreaHeight = value; break;
                    case "level_3_time":             config.level3Time = value; break;
                    case "level_3_ghosts":           config.level3Ghosts = value; break;
                    case "level_3_rippers":          config.level3Rippers = value; break;
                    case "level_3_wisps":            config.level3Wisps = value; break;
                    case "health_token_increase":    config.healthTokenIncrease = value; break;
                    case "vacuum_token_increase":    config.vacuumTokenIncrease = value; break;
                    case "eye_token_duration":       config.eyeTokenDuration = value; break;
                    case "speed_token_duration":     config.speedTokenDuration = value; break;
                    default: break;
                }
            }
        }

        return config;
    }
}
