// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Token_Range - scanner menzilini artıran token.

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Token_Range extends Token {

    // Range token'ın görselini oluşturur.
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

    // Range token efektini player'a uygular.
    @Override
    public void apply(GamePane game) {
        double amount = game.config.vacuumTokenIncrease > 0 ? game.config.vacuumTokenIncrease : 20;
        game.scannerHalfWidth += amount;
        GameAudio.play("range");
    }
}
