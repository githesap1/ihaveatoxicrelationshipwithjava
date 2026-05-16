// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Enemy_Ghost - zavas ghost enemz, 10 puan.

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Enemy_Ghost extends Enemy {

    private final ArrayList<Circle> bumps;

    // Ghost enemy'sini olusturur.
    public Enemy_Ghost(
            Group view,
            Circle body,
            ArrayList<Circle> details,
            ArrayList<Circle> bumps,
            Color normalBodyColor,
            double x,
            double y,
            double vx,
            double vy,
            double baseRadius
    ) {
        super(GHOST, view, body, details, normalBodyColor, x, y, vx, vy, baseRadius);
        this.bumps = bumps;
    }

    // Scanner icindeyken ghost body, bump ve eye'larini griye boyar.
    @Override
    public void inZone() {
        super.inZone(); // gray out body and eyes
        for (Circle bump : bumps) {
            bump.setFill(Color.rgb(170, 170, 170, 0.92));
        }
    }

    // Ghost'u normal rengine döndurur, eye'lari kirmizi yapar.
    @Override
    public void outOfZone() {
        getBody().setFill(getNormalBodyColor());
        for (Circle detail : getDetails()) {
            detail.setFill(Color.web("#C70039"));
        }
        for (Circle bump : bumps) {
            bump.setFill(getNormalBodyColor());
        }
    }

    // Ghost icin 10 puan döner.
    @Override
    public int getScoreValue() {
        return 10;
    }
}
