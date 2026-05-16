// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: Token_Speed - player'a gecici speed boost veren token

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class Token_Speed extends Token {

    // Speed token'in görselini olusturur
    public Token_Speed(double x, double y) {
        super(x, y);
        Rectangle bg = new Rectangle(-12, -12, 24, 24);
        bg.setFill(Color.web("#A8DADC"));

        // lower triangle (pointing up)
        Polygon lower = new Polygon(-7.0, 3.0, 7.0, 3.0, 0.0, -3.0);
        lower.setFill(Color.web("#1C1C1C"));

        // upper triangle (pointing up, just above the lower one)
        Polygon upper = new Polygon(-7.0, -3.0, 7.0, -3.0, 0.0, -9.0);
        upper.setFill(Color.web("#1C1C1C"));

        view.getChildren().addAll(bg, lower, upper);
    }

    // Speed token efektini player'a uygular
    @Override
    public void apply(GamePane game) {
        double duration = game.config.speedTokenDuration > 0 ? game.config.speedTokenDuration : 6;
        game.speedBoostRemaining = Math.max(game.speedBoostRemaining, duration);
        Game_Audio.play("speed");
    }
}
