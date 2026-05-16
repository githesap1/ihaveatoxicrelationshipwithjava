// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Token_Time - sureye 10 saniye ekleyen token, her 5 kill'de spawn olur.

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Token_Time extends Token {

    // Time token'in görselini olusturur (saat seklinde).
    public Token_Time(double x, double y) {
        super(x, y);
        Circle clockFace = new Circle(0, 0, 14, Color.web("#F7F7F7"));
        clockFace.setStroke(Color.web("#1C1C1C"));
        clockFace.setStrokeWidth(2.5);
        // minute hand: pointing to 12, straight up
        Rectangle minuteHand = new Rectangle(-1, -10, 2, 10);
        minuteHand.setFill(Color.web("#1C1C1C"));
        // hour hand: pointing to 3, straight right
        Rectangle hourHand = new Rectangle(0, -1.5, 6, 3);
        hourHand.setFill(Color.web("#1C1C1C"));
        Circle clockCenter = new Circle(0, 0, 1.5, Color.web("#1C1C1C"));
        view.getChildren().addAll(clockFace, minuteHand, hourHand, clockCenter);
    }

    // Time token efektini player'a uygular.
    @Override
    public void apply(GamePane game) {
        game.remainingSeconds += 10;
        Game_Audio.play("time");
    }
}
