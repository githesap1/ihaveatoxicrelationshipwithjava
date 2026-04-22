package org.example;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Enemy {
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

    public Enemy(
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

    public void syncView() {
        double scale = radius / baseRadius;
        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    public void setNormalStyle() {
        body.setFill(normalBodyColor);
        for (Circle detail : details) {
            if (type == GHOST) {
                detail.setFill(Color.BLACK);
            } else if (type == RIPPER) {
                detail.setFill(Color.PURPLE);
            } else {
                detail.setFill(Color.rgb(180, 250, 255, 0.9));
            }
        }
    }

    public void setDetectedStyle() {
        body.setFill(Color.rgb(230, 230, 230, 0.90));
        for (Circle detail : details) {
            detail.setFill(Color.rgb(170, 170, 170, 0.92));
        }
    }

    public int getScoreValue() {
        if (type == GHOST) {
            return 10;
        }
        if (type == RIPPER) {
            return 20;
        }
        if (type == WISP) {
            return 30;
        }
        throw new IllegalStateException("Unknown enemy type: " + type);
    }

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
