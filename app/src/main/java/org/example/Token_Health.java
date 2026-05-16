// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Token_Health - player health'ini artiran token.

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Token_Health extends Token {

    // Health token'in görselini olusturur (yesil arti isareti).
    public Token_Health(double x, double y) {
        super(x, y);
        Rectangle vertical = new Rectangle(-4, -12, 8, 24);
        vertical.setFill(Color.web("#4A8C2B"));
        Rectangle horizontal = new Rectangle(-12, -4, 24, 8);
        horizontal.setFill(Color.web("#4A8C2B"));
        view.getChildren().addAll(vertical, horizontal);
    }

    // Health token efektini player'a uygular.
    @Override
    public void apply(GamePane game) {
        double amount = game.config.healthTokenIncrease > 0 ? game.config.healthTokenIncrease : 20;
        // currentHealth = maximumHealth; // bunu degistirdim
        game.currentHealth = Math.min(game.currentHealth + amount, game.maximumHealth);
        game.healthFlashRemaining = 1.5;
        Game_Audio.play("health");
    }
}
