// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: Enemy_Spawner - oyun basladiginda ve her level'da yeni enemy'ler spawn eder

package org.example;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.layout.Pane;

public class Enemy_Spawner {

    private final int levelNumber;
    private final double areaX;
    private final double areaY;
    private final double areaW;
    private final double areaH;
    private final ArrayList<Enemy> enemies;
    private final Pane entityLayer;
    private final Random random;

    public Enemy_Spawner(int levelNumber, double areaX, double areaY, double areaW, double areaH, ArrayList<Enemy> enemies, Pane entityLayer, Random random) {
        this.levelNumber = levelNumber;
        this.areaX       = areaX;
        this.areaY       = areaY;
        this.areaW       = areaW;
        this.areaH       = areaH;
        this.enemies     = enemies;
        this.entityLayer = entityLayer;
        this.random      = random;
    }

    public void spawnGroup(int type, int count) {
        for (int i = 0; i < count; i++) {
            Enemy e = Enemy.spawn(type, areaX, areaY, areaW, areaH, random);
            applyBossModifiers(e);
            enemies.add(e);
            entityLayer.getChildren().add(e.getView());
        }
    }

    public void spawnRandom() {
        int type;
        if (levelNumber == 1) {
            type = Enemy.GHOST;
        } else if (levelNumber == 2) {
            type = random.nextBoolean() ? Enemy.GHOST : Enemy.RIPPER;
        } else {
            type = random.nextInt(3);
        }
        spawnGroup(type, 1);
    }

    private void applyBossModifiers(Enemy e) {
        if (levelNumber != 4) return;
        e.setRadius(e.getRadius() * 1.5);
        if (e instanceof Enemy_Wisp) ((Enemy_Wisp) e).setOrbitSpeed(5.0);
    }
}
