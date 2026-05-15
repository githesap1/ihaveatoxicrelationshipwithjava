// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Hud_Lvl_Complete - level complete ekranı.

package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hud_Lvl_Complete extends Hud {

    // Level complete ekranını oluşturur.
    public Hud_Lvl_Complete(double width, double height, int score, int levelNumber, App app) {
        // use the level's own wallpaper as background
        String bgPath = levelNumber == 4 ? "/images/bossfight.jpg" : "/images/level" + levelNumber + ".jpg";
        ImageView wallpaper = loadBg(bgPath, width, height);

        // dark semi-transparent overlay to darken the wallpaper
        Rectangle shade = new Rectangle(0, 0, width, height);
        shade.setFill(Color.web("#1F2937", 0.80));

        String titleText = levelNumber == 4 ? "YOU SURVIVED THE BOSS FIGHT!"
                         : levelNumber < 3  ? "YOU WON HOORAYYY!"
                         : "WORK COMPLETE!";

        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 46px; -fx-font-family: 'Christmare';");

        Label message = new Label("YOU ARE THE STRONGEST HUNTER IN THE WORLD with the sole exception of satoru gojo, but hes not in this game so who cares?");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Ghostz';");
        message.setWrapText(true);
        message.setMaxWidth(480);

        Label scoreText = new Label("Score: " + score);
        scoreText.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");

        Button btNext;
        if (levelNumber < 3) {
            btNext = makePanel_Button("Next Level", () -> app.startLevel(levelNumber + 1, score));
        } else if (levelNumber == 3) {
            btNext = makePanel_Button("Next Level", () -> app.startLevel(4, score));
        } else {
            btNext = makePanel_Button("View Ending(pls do)", () -> app.showVictoryScreen(score));
        }

        Button btRestart = makePanel_Button("Restart",   () -> app.startLevel(levelNumber, 0));
        Button btMenu    = makePanel_Button("Main Menu", () -> app.showMainMenu());

        VBox panel = new VBox(14, title, message, scoreText, btNext, btRestart, btMenu);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(420);

        getChildren().addAll(wallpaper, shade, panel);
        setPrefSize(width, height);
    }
}
