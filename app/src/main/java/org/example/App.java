// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: App - uygulamanin entry point'i, font/sound/config yukler, scene transition'lari yönetir

package org.example;

import java.io.File;

import javafx.application.Application;

import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


import javafx.stage.Stage;

public class App extends Application {
    private static final int SCENE_WIDTH  = 1280;
    private static final int SCENE_HEIGHT = 800;

    private Stage primaryStage;
    private Config config;

    // JavaFX uygulamasi basladiginda calisir, font ve sound'lari load eder
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        System.out.println("uygulama baslatildi");
        Font.loadFont(getClass().getResourceAsStream("/fonts/Christmare.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Ghostz.ttf"), 14);
        Game_Audio.load(getClass());


        config = Config.load(new File("config.txt"));

        primaryStage.setTitle("GHOST HUNTER INC.(ALIBABA EDITION)");
        primaryStage.setResizable(false);
        showMainMenu();
        primaryStage.show();
    }



    // Main menu'yu gösterir
    void showMainMenu() {
        primaryStage.setScene(new Scene(new Hud_Main_Menu(this, SCENE_WIDTH, SCENE_HEIGHT), SCENE_WIDTH, SCENE_HEIGHT));
        Game_Audio.playMusic("main_menu_music");
    }

    // Level select ekranina gecer
    void showLevelSelect() {
        primaryStage.setScene(new Scene(new Hud_Lvl_Select(this, SCENE_WIDTH, SCENE_HEIGHT), SCENE_WIDTH, SCENE_HEIGHT));
        Game_Audio.playMusic("level_select_music");
    }

    // Verilen level numarasiyla yeni bir GamePane olusturur ve baslatir
    void startLevel(int levelNumber, int initialScore) {
        GamePane gamePane = new GamePane(this, config, levelNumber, initialScore);
        Scene scene = new Scene(gamePane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("GHOST HUNTER INC.(ALIBABA EDITION) - LEVEL " + levelNumber);
        primaryStage.setScene(scene);
        gamePane.requestFocus();
        gamePane.startGameLoop();
    }



    // Oyun bitince final screen'i gösterir
    void showVictoryScreen(int finalScore) {
        ImageView bgImg = Hud.loadBg("/images/final.jpg", SCENE_WIDTH, SCENE_HEIGHT);

        Rectangle overlay = new Rectangle(SCENE_WIDTH, SCENE_HEIGHT);
        overlay.setFill(Color.rgb(0, 0, 0, 0.55));

        Label title = new Label("HOLY MOLY YOU BEAT THE GAME!\nHERES YOUR PAYCHECK *hands you a " + finalScore + "TL BIM present card*");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 48px; -fx-font-family: 'Ghostz'; -fx-font-weight: bold;");
        title.setWrapText(true);
        title.setMaxWidth(900);
        title.setTextAlignment(TextAlignment.CENTER);

        Button btMenu = Hud.makeMenu_Button("Main Menu", () -> showMainMenu());

        VBox content = new VBox(24, title, btMenu);
        content.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(bgImg, overlay, content);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        Game_Audio.playMusic("winscreen");
    }

    // Main method, uygulamayi launch eder
    public static void main(String[] args) {
        launch(args);
    }
}
