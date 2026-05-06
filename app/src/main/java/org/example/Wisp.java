// CSE 1242 - Introduction to Programming II - Term Project
// Author(s): [Ad Soyad] - [Ogrenci No]
//
// Wisp.java
// A glowing, large paranormal entity with orbiting rings. Worth 30 points.

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Wisp extends Enemy {

    public Wisp(
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
        super(WISP, view, body, details, normalBodyColor, x, y, vx, vy, baseRadius);
    }

    @Override
    public void outOfZone() {
        getBody().setFill(getNormalBodyColor());
        for (Circle detail : getDetails()) {
            detail.setFill(Color.rgb(180, 250, 255, 0.9));
        }
    }

    @Override
    public int getScoreValue() {
        return 30;
    }
}
