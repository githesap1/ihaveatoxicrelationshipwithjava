// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Token_Eye.java - tum dusmanlari gecici sure gozle goren token

package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

public class Token_Eye extends Token {

    public Token_Eye(double x, double y) {
        super(x, y);
        Ellipse eye = new Ellipse(0, 0, 14, 9);
        eye.setFill(Color.WHITE);
        eye.setStroke(Color.BLACK);
        Circle pupil = new Circle(0, 0, 4, Color.BLACK);
        view.getChildren().addAll(eye, pupil);
    }

    @Override
    public void apply(GamePane game) {
        game.applyEyeToken();
    }
}
