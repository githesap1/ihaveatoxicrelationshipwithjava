// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Token_Time - süreye 10 saniye ekleyen token, her 5 kill'de spawn olur.

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Token_Time extends Token {

    // Time token'ın görselini oluşturur (saat şeklinde).
    public Token_Time(double x, double y) {
        super(x, y);
        Circle clockFace = new Circle(0, 0, 14, Color.WHITE);
        clockFace.setStroke(Color.BLACK);
        clockFace.setStrokeWidth(2.5);
        // minute hand: pointing to 12, straight up
        Rectangle minuteHand = new Rectangle(-1, -10, 2, 10);
        minuteHand.setFill(Color.BLACK);
        // hour hand: pointing to 3, straight right
        Rectangle hourHand = new Rectangle(0, -1.5, 6, 3);
        hourHand.setFill(Color.BLACK);
        Circle clockCenter = new Circle(0, 0, 1.5, Color.BLACK);
        view.getChildren().addAll(clockFace, minuteHand, hourHand, clockCenter);
    }

    // Time token efektini player'a uygular.
    @Override
    public void apply(GamePane game) {
        game.remainingSeconds += 10;
        GameAudio.play("time");
    }
}
