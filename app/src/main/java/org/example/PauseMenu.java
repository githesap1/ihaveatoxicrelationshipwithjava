// CSE 1242 - Introduction to Programming II - Term Project
// Author(s): [Ad Soyad] - [Ogrenci No]
//
// PauseMenu.java
// Overlay panel shown when the player presses ESC during a level.
// Has Resume and Main Menu buttons. Rendered on top of the game scene.

package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PauseMenu extends StackPane {

    // creates the pause overlay with the given screen dimensions and button callbacks
    public PauseMenu(double width, double height, Runnable onResume, Runnable onMainMenu) {
        Rectangle bg = new Rectangle(0, 0, width, height);
        bg.setFill(Color.rgb(0, 0, 0, 0.72));

        Label title = new Label("PAUSED");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 52px; -fx-font-family: 'Blood Crow';");

        Label hint = new Label("press ESC to continue");
        hint.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'I Still Know';");

        Button btResume = createButton("Resume", onResume);
        Button btMenu = createButton("Main Menu", onMainMenu);

        VBox panel = new VBox(14, title, hint, btResume, btMenu);
        panel.setAlignment(Pos.CENTER);

        getChildren().addAll(bg, panel);
        setPrefSize(width, height);
        setVisible(false);
    }

    // creates a styled button with hover, press, and release color effects
    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setPrefHeight(46);
        styleDefault(button);

        button.setOnMouseEntered(e -> {
            if (!button.isPressed()) {
                styleHover(button);
            }
        });
        button.setOnMousePressed(e -> stylePressed(button));
        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                styleHover(button);
                action.run();
            } else {
                styleDefault(button);
            }
        });
        button.setOnMouseExited(e -> styleDefault(button));
        return button;
    }

    private void styleDefault(Button button) {
        button.setStyle("-fx-background-color: purple;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-family: 'I Still Know';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

    private void styleHover(Button button) {
        button.setStyle("-fx-background-color: #a040ff;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-family: 'I Still Know';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

    private void stylePressed(Button button) {
        button.setStyle("-fx-background-color: white;"
                + "-fx-text-fill: red;"
                + "-fx-font-size: 18px;"
                + "-fx-font-family: 'I Still Know';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }
}
