// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Enemy_Ripper - hızlı dikenli Ripper enemy, 20 puan.

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class Enemy_Ripper extends Enemy {

    private final Polygon star;

    // Ripper enemy'sini oluşturur.
    public Enemy_Ripper(
            Group view,
            Circle body,
            ArrayList<Circle> details,
            Color normalBodyColor,
            Polygon star,
            double x,
            double y,
            double vx,
            double vy,
            double baseRadius
    ) {
        super(RIPPER, view, body, details, normalBodyColor, x, y, vx, vy, baseRadius);
        this.star = star;
    }

    // Scanner içindeyken Ripper'ı soluk kırmızıya boyar.
    @Override
    public void inZone() {
        applyZoneColor(Color.web("#CD5C5C"));
        for (Circle detail : getDetails()) {
            detail.setFill(Color.rgb(170, 170, 170, 0.92));
        }
    }

    // Aynı rengi hem body'ye hem star polygon'a uygular.
    private void applyZoneColor(Color c) {
        getBody().setFill(c);
        star.setFill(c);
    }

    // Ripper'ı normal rengine döndürür.
    @Override
    public void outOfZone() {
        getBody().setFill(getNormalBodyColor());
        star.setFill(getNormalBodyColor());
        for (Circle detail : getDetails()) {
            detail.setFill(Color.BLACK);
        }
    }

    // Ripper için 20 puan döner.
    @Override
    public int getScoreValue() {
        return 20;
    }
}
