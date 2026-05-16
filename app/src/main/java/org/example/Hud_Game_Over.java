// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: Hud_Game_Over - game over ekrani

package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hud_Game_Over extends Hud {

    // Game over ekranini olusturur
    public Hud_Game_Over(double width, double height, String reason, int score,
                         int levelNumber, App app, int initialScore) {
        String bgPath = levelNumber == 4 ? "/images/bossfight.jpg" : "/images/level" + levelNumber + ".jpg";
        ImageView wallpaper = loadBg(bgPath, width, height);

        Rectangle shade = new Rectangle(0, 0, width, height);
        shade.setFill(Color.web("#1F2937", 0.80));

        Label title = new Label("GAME OVER");
        title.setStyle("-fx-text-fill: #F5F5F5; -fx-font-size: 52px; -fx-font-family: 'Christmare';");

        Label reasonText = new Label(reason);
        reasonText.setStyle("-fx-text-fill: #F5F5F5; -fx-font-size: 22px; -fx-font-family: 'Ghostz';");
        reasonText.setWrapText(true);
        reasonText.setMaxWidth(480);

        Label scoreText = new Label("Final Score: " + score);
        scoreText.setStyle("-fx-text-fill: #F5F5F5; -fx-font-size: 24px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");

        Button btRetry = makePanel_Button("Retry Level", () -> app.startLevel(levelNumber, initialScore));
        Button btMenu  = makePanel_Button("Main Menu",   () -> app.showMainMenu());

        VBox panel = new VBox(14, title, reasonText, scoreText, btRetry, btMenu);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(480);

        getChildren().addAll(wallpaper, shade, panel);
        setPrefSize(width, height);
    }
}
