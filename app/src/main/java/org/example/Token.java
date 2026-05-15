// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Token - tüm collectible token'lar için abstract base class.

package org.example;

import javafx.scene.Group;

public abstract class Token {

    public static final double RADIUS = 14.0;

    protected final Group view;
    private final double x;
    private final double y;

    // Token'ın pozisyonunu ve JavaFX Group'unu set eder.
    protected Token(double x, double y) {
        this.x = x;
        this.y = y;
        this.view = new Group();
        this.view.setLayoutX(x);
        this.view.setLayoutY(y);
    }

    // Token toplandığında efektini GamePane üzerinde uzgular.
    public abstract void apply(GamePane game);

    public Group getView() { return view; }

    public double getX()   { return x; }

    public double getY()   { return y; }

    public double getRadius() { return RADIUS; }
}
