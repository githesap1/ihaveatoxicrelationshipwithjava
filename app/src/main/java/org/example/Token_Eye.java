// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Token_Eye - tüm enemy'leri geçici görünür yapan token.

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

public class Token_Eye extends Token {

    // Eye token'ın görselini oluşturur.
    public Token_Eye(double x, double y) {
        super(x, y);
        Ellipse eye = new Ellipse(0, 0, 14, 9);
        eye.setFill(Color.WHITE);
        eye.setStroke(Color.BLACK);
        Circle pupil = new Circle(0, 0, 4, Color.BLACK);
        view.getChildren().addAll(eye, pupil);
    }

    // Eye token efektini player'a uygular.
    @Override
    public void apply(GamePane game) {
        double duration = game.config.eyeTokenDuration > 0 ? game.config.eyeTokenDuration : 5;
        game.eyeRevealRemaining = Math.max(game.eyeRevealRemaining, duration);
        GameAudio.play("eye");
    }
}
