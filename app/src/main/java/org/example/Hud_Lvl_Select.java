// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084
// Class: Hud_Lvl_Select - level select ekrani

package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hud_Lvl_Select extends Hud {

    // Level select ekranini olusturur
    public Hud_Lvl_Select(App app, double width, double height) {
        ImageView bg = loadBg("/images/menu2.jpg", width, height);

        Rectangle overlay = new Rectangle(width, height);
        overlay.setFill(Color.rgb(0, 0, 0, 0.45));

        Label title = new Label("Select Level");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 44px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");

        HBox cards = new HBox(28);
        cards.setAlignment(Pos.CENTER);
        cards.getChildren().addAll(
            buildLevelCard(1, app),
            buildLevelCard(2, app),
            buildLevelCard(3, app)
        );

        Button btMainMenu = makeMenu_Button("Main Menu", () -> app.showMainMenu());

        VBox content = new VBox(25, title, cards, btMainMenu);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        getChildren().addAll(bg, overlay, content);
        setPrefSize(width, height);
    }

    // Bir level icin tiklanabilir preview karti olusturur
    private VBox buildLevelCard(int levelNumber, App app) {
        ImageView preview = new ImageView(
            new Image(getClass().getResourceAsStream("/images/level" + levelNumber + ".jpg"))
        );
        preview.setFitWidth(290);
        preview.setFitHeight(200);
        preview.setPreserveRatio(false);

        // keskin köşeler, yuvarlama yok
        Rectangle border = new Rectangle(290, 200);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web("#2C2C2C"));
        border.setStrokeWidth(5);

        StackPane imgPane = new StackPane(preview, border);
        imgPane.setPrefSize(290, 200);

        // level name goes below the image
        Label text = new Label("Level " + levelNumber);
        text.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");

        VBox card = new VBox(6, imgPane, text);
        card.setAlignment(Pos.CENTER);

        imgPane.setOnMouseEntered(e -> { if (!imgPane.isPressed()) border.setStroke(Color.web("#FFD700")); });
        imgPane.setOnMouseExited(e -> {
            border.setStroke(Color.web("#2C2C2C"));
            text.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");
        });
        imgPane.setOnMousePressed(e -> {
            border.setStroke(Color.web("#EF233C"));
            text.setStyle("-fx-text-fill: red; -fx-font-size: 22px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");
        });
        imgPane.setOnMouseReleased(e -> {
            border.setStroke(Color.web("#2C2C2C"));
            text.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");
            if (imgPane.isHover()) {
                Game_Audio.play("select");
                app.startLevel(levelNumber, 0);
            }
        });

        return card;
    }
}
