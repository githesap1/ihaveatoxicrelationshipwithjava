// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Token_Health.java - can yenileyen token

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Token_Health extends Token {

    public Token_Health(double x, double y) {
        super(x, y);
        Rectangle vertical = new Rectangle(-4, -12, 8, 24);
        vertical.setFill(Color.LIMEGREEN);
        Rectangle horizontal = new Rectangle(-12, -4, 24, 8);
        horizontal.setFill(Color.LIMEGREEN);
        view.getChildren().addAll(vertical, horizontal);
    }

    @Override
    public void apply(GamePane game) {
        game.applyHealthToken();
    }
}
