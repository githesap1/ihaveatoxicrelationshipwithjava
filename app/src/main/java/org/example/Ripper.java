// CSE 1242 - Introduction to Programming II - Term Project
// Author(s): [Ad Soyad] - [Ogrenci No]
//
// Ripper.java
// A fast, spiky paranormal entity. Worth 20 points.

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ripper extends Enemy {

    public Ripper(
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
        super(RIPPER, view, body, details, normalBodyColor, x, y, vx, vy, baseRadius);
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
        return 20;
    }
}
