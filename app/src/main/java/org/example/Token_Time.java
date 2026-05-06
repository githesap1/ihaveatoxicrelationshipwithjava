// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Token_Time.java - 10 saniye ekleyen token, her 5 kill'de spawn olur

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Token_Time extends Token {

    public Token_Time(double x, double y) {
        super(x, y);
        Circle clockFace = new Circle(0, 0, 14, Color.WHITE);
        clockFace.setStroke(Color.BLACK);
        clockFace.setStrokeWidth(2.5);
        // yelkovan: 12'yi gösterir, düz yukarı
        Rectangle minuteHand = new Rectangle(-1, -10, 2, 10);
        minuteHand.setFill(Color.BLACK);
        // akrep: 3'ü gösterir, düz sağa
        Rectangle hourHand = new Rectangle(0, -1.5, 6, 3);
        hourHand.setFill(Color.BLACK);
        Circle clockCenter = new Circle(0, 0, 1.5, Color.BLACK);
        view.getChildren().addAll(clockFace, minuteHand, hourHand, clockCenter);
    }

    @Override
    public void apply(GamePane game) {
        game.applyTimeToken();
    }
}
