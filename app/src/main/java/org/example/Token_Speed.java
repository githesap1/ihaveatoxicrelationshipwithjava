// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Token_Speed.java - gecici hiz artisi saglayan token

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class Token_Speed extends Token {

    public Token_Speed(double x, double y) {
        super(x, y);
        Rectangle bg = new Rectangle(-12, -12, 24, 24);
        bg.setFill(Color.DODGERBLUE);

        // alt ucgen (yukari bakiyor)
        Polygon lower = new Polygon(-7.0, 3.0, 7.0, 3.0, 0.0, -3.0);
        lower.setFill(Color.BLACK);

        // ust ucgen (yukari bakiyor, altin hemen ustunde)
        Polygon upper = new Polygon(-7.0, -3.0, 7.0, -3.0, 0.0, -9.0);
        upper.setFill(Color.BLACK);

        view.getChildren().addAll(bg, lower, upper);
    }

    @Override
    public void apply(GamePane game) {
        game.applySpeedToken();
    }
}
