// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Token_Concerta - CONCERTA :)

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Token_Concerta extends Token {

    // Concerta token'ının görselini oluşturur (hap şeklinde).
    public Token_Concerta(double x, double y) {
        super(x, y);
        Rectangle pill = new Rectangle(-14, -5, 28, 10);
        pill.setFill(Color.web("#8A5F41"));
        pill.setArcWidth(10);
        pill.setArcHeight(10);
        pill.setStroke(Color.web("#5C3D2E"));
        Circle dot = new Circle(0, 0, 3, Color.WHITE);
        view.getChildren().addAll(pill, dot);
    }

    // Concerta token efektini player'a uygular.
    @Override
    public void apply(GamePane game) {
        game.concertaSpeedRemaining = 27;
        game.concertaHealRemaining = 15;
        game.concertaMusicPlaying = true;
        GameAudio.play("concerta");
        GameAudio.playMusic("concerta_music");
    }
}
