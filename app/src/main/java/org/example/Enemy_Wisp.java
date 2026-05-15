// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Enemy_Wisp - büyük Wisp enemy, etrafında dönen triangle'lar, 30 puan.

package org.example;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Enemy_Wisp extends Enemy {

    private final Group orbitGroup;
    private final Timeline rotateAnim;
    private double degreesPerFrame = 9.0;

    // Wisp enemy'sini ve orbit rotation Timeline'ını oluşturur.
    public Enemy_Wisp(
            Group view,
            Circle body,
            ArrayList<Circle> details,
            Color normalBodyColor,
            Group orbitGroup,
            double x,
            double y,
            double vx,
            double vy,
            double baseRadius
    ) {
        super(WISP, view, body, details, normalBodyColor, x, y, vx, vy, baseRadius);
        this.orbitGroup = orbitGroup;

        // bunu kusursuz yapamadım ama bu hali de hoş gözüktü
        rotateAnim = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            orbitGroup.setRotate(orbitGroup.getRotate() + degreesPerFrame);
        }));
        rotateAnim.setCycleCount(Timeline.INDEFINITE);
        rotateAnim.play();
    }

    // Wisp'i normal rengine döndürür.
    @Override
    public void outOfZone() {
        getBody().setFill(getNormalBodyColor());
        for (Circle detail : getDetails()) {
            detail.setFill(Color.WHITE);
        }
    }

    // Orbit rotation hızını set eder.
    public void setOrbitSpeed(double deg) {
        degreesPerFrame = deg;
    }

    // Rotation Timeline'ını durdurur.
    @Override
    public void stopAnimation() {
        rotateAnim.stop();
    }

    // Wisp için 30 puan döner.
    @Override
    public int getScoreValue() {
        return 30;
    }
}
