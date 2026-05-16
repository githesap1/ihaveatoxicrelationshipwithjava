// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: Hud_Pause_Menu - pause menu ekrani.

package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hud_Pause_Menu extends Hud {

    // Pause menu ekranini olusturur.
    public Hud_Pause_Menu(double width, double height, Runnable onResume, Runnable onRestart, Runnable onMainMenu) {
        Rectangle bg = new Rectangle(0, 0, width, height);
        bg.setFill(Color.web("#1F2937", 0.80));

        Label title = new Label("PAUSED");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 52px; -fx-font-family: 'Christmare';");

        Label message = new Label("press ESC to continue");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Ghostz';");

        Button btResume  = makePanel_Button("Resume",    onResume);
        Button btRestart = makePanel_Button("Restart",   onRestart);
        Button btMenu    = makePanel_Button("Main Menu", onMainMenu);

        VBox panel = new VBox(14, title, message, btResume, btRestart, btMenu);
        panel.setAlignment(Pos.CENTER);

        getChildren().addAll(bg, panel);
        setPrefSize(width, height);
        setVisible(false);
    }
}
