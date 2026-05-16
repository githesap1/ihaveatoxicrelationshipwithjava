// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084
// Class: Enemy_Ripper - hizli dikenli Ripper enemy, 20 puan

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class Enemy_Ripper extends Enemy {

    private final Polygon star;

    // Ripper enemy'sini olusturur
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

    // Scanner icindeyken Ripper'i soluk kirmiziya boyar
    @Override
    public void inZone() {
        applyZoneColor(Color.web("#CD5C5C"));
        for (Circle detail : getDetails()) {
            detail.setFill(Color.rgb(170, 170, 170, 0.92));
        }
    }

    // Ayni rengi hem body'ye hem star polygon'a uygular
    private void applyZoneColor(Color c) {
        getBody().setFill(c);
        star.setFill(c);
    }

    // Ripper'i normal rengine döndurur
    @Override
    public void outOfZone() {
        getBody().setFill(getNormalBodyColor());
        star.setFill(getNormalBodyColor());
        for (Circle detail : getDetails()) {
            detail.setFill(Color.web("#1C1C1C"));
        }
    }

    // Ripper icin 20 puan döner
    @Override
    public int getScoreValue() {
        return 20;
    }
}
