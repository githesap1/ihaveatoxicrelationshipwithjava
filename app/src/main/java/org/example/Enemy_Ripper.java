// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Enemy_Ripper.java - hizli, dikenli dusman, 20 puan

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class Enemy_Ripper extends Enemy {

    private final Polygon star;

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

    @Override
    public void inZone() {
        super.inZone();
        star.setFill(Color.rgb(210, 210, 210, 0.90));
    }

    @Override
    public void outOfZone() {
        getBody().setFill(getNormalBodyColor());
        star.setFill(getNormalBodyColor());
        for (Circle detail : getDetails()) {
            detail.setFill(Color.BLACK);
        }
    }

    @Override
    public int getScoreValue() {
        return 20;
    }
}
