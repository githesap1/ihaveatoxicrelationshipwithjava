// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: Hud_Main_Menu - main menu ekrani

package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hud_Main_Menu extends Hud {

    // Main menu ekranini olusturur
    public Hud_Main_Menu(App app, double width, double height) {
        ImageView wallpaper = loadBg("/images/menu.jpg", width, height);

        Rectangle overlay = new Rectangle(width, height);
        overlay.setFill(Color.rgb(0, 0, 0, 0.52));

        Label title = new Label("GHOST HUNTER INC.(ALIBABA EDITION)");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 54px; -fx-font-family: 'Christmare';");

        Label message = new Label("SIR WE NEED MORE BACKUP!");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Ghostz';");

        Button btStart  = makeMenu_Button("Start Game",   () -> app.startLevel(1, 0));
        Button btSelect = makeMenu_Button("Lvl Select",   () -> app.showLevelSelect());
        Button btExit   = makeMenu_Button("Exit Game",    () -> System.exit(0));

        VBox content = new VBox(20, title, message, btStart, btSelect, btExit);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        getChildren().addAll(wallpaper, overlay, content);
        setPrefSize(width, height);
    }
}
