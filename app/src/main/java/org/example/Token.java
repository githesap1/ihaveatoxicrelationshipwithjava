package org.example;

import javafx.scene.Group;

public class Token {
    public static final int HEALTH = 0;
    public static final int RANGE = 1;
    public static final int EYE = 2;

    private int type;
    private Group view;
    private double x;
    private double y;
    private double radius;

    public Token(int type, Group view, double x, double y, double radius) {
        this.type = type;
        this.view = view;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public int getType() {
        return type;
    }

    public Group getView() {
        return view;
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

    public double getRadius() {
        return radius;
    }
}
