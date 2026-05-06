// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Token.java - toplanabilir powerup tokenlar icin soyut taban sinifi

package org.example;

import javafx.scene.Group;

public abstract class Token {

    public static final double RADIUS = 14.0;

    protected final Group view;
    private final double x;
    private final double y;

    protected Token(double x, double y) {
        this.x = x;
        this.y = y;
        this.view = new Group();
        this.view.setLayoutX(x);
        this.view.setLayoutY(y);
    }

    public abstract void apply(GamePane game);

    public Group getView() { return view; }
    public double getX()   { return x; }
    public double getY()   { return y; }
    public double getRadius() { return RADIUS; }
}
