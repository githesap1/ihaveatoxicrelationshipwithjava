// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Token_Range.java - scanner menzilini genisleten token

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Token_Range extends Token {

    public Token_Range(double x, double y) {
        super(x, y);
        Polygon triangle = new Polygon(
                0.0, -14.0,
                13.0, 12.0,
                -13.0, 12.0
        );
        triangle.setFill(Color.PURPLE);
        triangle.setStroke(Color.WHITE);
        view.getChildren().add(triangle);
    }

    @Override
    public void apply(GamePane game) {
        game.applyRangeToken();
    }
}
