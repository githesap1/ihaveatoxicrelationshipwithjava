// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Enemy_Ghost.java - temel hayalet dusmani, yavas, 10 puan

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Enemy_Ghost extends Enemy {

    public Enemy_Ghost(
            Group view,
            Circle body,
            ArrayList<Circle> details,
            Color normalBodyColor,
            double x,
            double y,
            double vx,
            double vy,
            double baseRadius
    ) {
        super(GHOST, view, body, details, normalBodyColor, x, y, vx, vy, baseRadius);
    }

    @Override
    public void outOfZone() {
        getBody().setFill(getNormalBodyColor());
        for (Circle detail : getDetails()) {
            detail.setFill(Color.BLACK);
        }
    }

    @Override
    public int getScoreValue() {
        return 10;
    }
}
