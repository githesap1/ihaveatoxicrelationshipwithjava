// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Enemy_Wisp.java - buyuk, parlayan dusman, etrafinda donen halkalar, 30 puan

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Enemy_Wisp extends Enemy {

    public Enemy_Wisp(
            Group view,
            Circle body,
            ArrayList<Circle> details,
            Color normalBodyColor,
            Group revolvers,
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
            detail.setFill(Color.WHITE);
        }
    }

    @Override
    public int getScoreValue() {
        return 30;
    }
}
