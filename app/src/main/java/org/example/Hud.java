// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: Hud - tum HUD ekranlari icin parent class, shared button factory ve helper methodlar burada

package org.example;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Hud extends StackPane {

    // Menu button olusturur, hover ve click event'lerini set eder
    static Button makeMenu_Button(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(280);
        button.setPrefHeight(52);
        styleMenuDefault(button);

        button.setOnMouseEntered(e -> { if (!button.isPressed()) styleMenuHover(button); });
        button.setOnMousePressed(e -> styleMenuPressed(button));
        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                styleMenuHover(button);
                Game_Audio.play("select");
                action.run();
            } else {
                styleMenuDefault(button);
            }
        });
        button.setOnMouseExited(e -> styleMenuDefault(button));
        return button;
    }

    // Panel button olusturur, click event'ini set eder
    static Button makePanel_Button(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setPrefHeight(46);
        stylePanelDefault(button);

        button.setOnMousePressed(e -> stylePanelPressed(button));
        button.setOnMouseReleased(e -> {
            stylePanelDefault(button);
            if (button.isHover()) {
                Game_Audio.play("select");
                action.run();
            }
        });
        button.setOnMouseExited(e -> stylePanelDefault(button));
        return button;
    }

    // Button'a default menu style'ini uygular
    static void styleMenuDefault(Button b) {
        b.setStyle("-fx-background-color: #C3073F; -fx-text-fill: #F5F5F5; -fx-font-size: 20px;"
                + "-fx-font-family: 'Ghostz'; -fx-font-weight: bold; -fx-background-radius: 8;"
                + "");
    }

    // Button'a hover style'ini uygular
    static void styleMenuHover(Button b) {
        b.setStyle("-fx-background-color: #C72C3E; -fx-text-fill: #F5F5F5; -fx-font-size: 20px;"
                + "-fx-font-family: 'Ghostz'; -fx-font-weight: bold; -fx-background-radius: 8;"
                + "");
    }

    // Button'a pressed style'ini uygular
    static void styleMenuPressed(Button b) {
        b.setStyle("-fx-background-color: #D6D0C4; -fx-text-fill: #F5F5F5; -fx-font-size: 20px;"
                + "-fx-font-family: 'Ghostz'; -fx-font-weight: bold; -fx-background-radius: 8;"
                + "");
    }

    // Button'a panel default style'ini uygular
    static void stylePanelDefault(Button b) {
        b.setStyle("-fx-background-color: #C3073F; -fx-text-fill: #F5F5F5; -fx-font-size: 18px;"
                + "-fx-font-family: 'Ghostz'; -fx-font-weight: bold; -fx-background-radius: 8;"
                + "");
    }

    // Button'a panel pressed style'ini uygular
    static void stylePanelPressed(Button b) {
        b.setStyle("-fx-background-color: #D6D0C4; -fx-text-fill: #F5F5F5; -fx-font-size: 18px;"
                + "-fx-font-family: 'Ghostz'; -fx-font-weight: bold; -fx-background-radius: 8;"
                + "");
    }

    // Background image'i load edip ekrana fit eden ImageView döner
    static ImageView loadBg(String path, double width, double height) {
        ImageView iv = new ImageView(new Image(Hud.class.getResourceAsStream(path)));
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        iv.setPreserveRatio(false);
        return iv;
    }
}
