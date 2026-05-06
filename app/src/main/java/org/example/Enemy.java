// CSE 1242 - Introduction to Programming II - Term Project
// Author(s): [Ad Soyad] - [Ogrenci No]
//
// Enemy.java
// Abstract base class for all paranormal entities. Stores shared state (position,
// velocity, radius, visual group) and delegates type-specific behavior to subclasses.

package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract class Enemy {
    public static final int GHOST = 0;
    public static final int RIPPER = 1;
    public static final int WISP = 2;

    private int type;
    private Group view;
    private Circle body;
    private ArrayList<Circle> details;
    private Color normalBodyColor;

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double baseRadius;
    private double radius;

    protected Enemy(
            int type,
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
        this.type = type;
        this.view = view;
        this.body = body;
        this.details = details;
        this.normalBodyColor = normalBodyColor;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.baseRadius = baseRadius;
        this.radius = baseRadius;
    }

    // updates the JavaFX group position and scale to match the logical state
    public void updatePosition() {
        double scale = radius / baseRadius;
        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    // changes the enemy color to gray/white to indicate it is being vacuumed
    public void inZone() {
        body.setFill(Color.rgb(230, 230, 230, 0.90));
        for (Circle detail : details) {
            detail.setFill(Color.rgb(170, 170, 170, 0.92));
        }
    }

    // restores the enemy's original colors when it is not in the scanner
    public abstract void outOfZone();

    // returns how many points the player gets for capturing this enemy
    public abstract int getScoreValue();

    public int getType() {
        return type;
    }

    public Group getView() {
        return view;
    }

    public void setView(Group view) {
        this.view = view;
    }

    public Circle getBody() {
        return body;
    }

    public ArrayList<Circle> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Circle> details) {
        this.details = details;
    }

    public Color getNormalBodyColor() {
        return normalBodyColor;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getBaseRadius() {
        return baseRadius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
