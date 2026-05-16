// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084
// Class: Token_Range - scanner menzilini artiran token

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Token_Range extends Token {

    // Range token'in görselini olusturur
    public Token_Range(double x, double y) {
        super(x, y);
        Polygon triangle = new Polygon(
                0.0, -14.0,
                13.0, 12.0,
                -13.0, 12.0
        );
        triangle.setFill(Color.web("#4B0082"));
        triangle.setStroke(Color.web("#DDA0DD"));
        triangle.setStrokeWidth(2.5);
        view.getChildren().add(triangle);
    }

    // Range token efektini player'a uygular
    @Override
    public void apply(GamePane game) {
        double amount = game.config.vacuumTokenIncrease > 0 ? game.config.vacuumTokenIncrease : 20;
        game.scannerHalfWidth += amount;
        Game_Audio.play("range");
    }
}
