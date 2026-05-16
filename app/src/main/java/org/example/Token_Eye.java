// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: Token_Eye - tum enemy'leri gecici görunur yapan token

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

public class Token_Eye extends Token {

    // Eye token'in görselini olusturur
    public Token_Eye(double x, double y) {
        super(x, y);
        Ellipse eye = new Ellipse(0, 0, 14, 9);
        eye.setFill(Color.web("#F7F7F7"));
        eye.setStroke(Color.web("#1C1C1C"));
        Circle pupil = new Circle(0, 0, 4, Color.web("#1C1C1C"));
        view.getChildren().addAll(eye, pupil);
    }

    // Eye token efektini player'a uygular
    @Override
    public void apply(GamePane game) {
        double duration = game.config.eyeTokenDuration > 0 ? game.config.eyeTokenDuration : 5;
        game.eyeRevealRemaining = Math.max(game.eyeRevealRemaining, duration);
        Game_Audio.play("eye");
    }
}
