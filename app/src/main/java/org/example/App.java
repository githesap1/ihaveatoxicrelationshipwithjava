// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// App.java - main class, ekranlar arasi gecis ve config yukleme

package org.example;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Font.loadFont(getClass().getResourceAsStream("/fonts/bloodcrow.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/I Still Know.ttf"), 14);
        SoundManager.load(getClass());

        try {
            config = Config.load(new File("config.txt"));
        } catch (Exception ex) {
            showConfigError("Could not load config.txt.\n" + ex.getMessage());
            return;
        }

        primaryStage.setTitle("GHOST HUNTER INC.(ALIBABA EDITION)");
        primaryStage.setResizable(false);
        showMainMenu();
        primaryStage.show();
    }

    void showMainMenu() {
        primaryStage.setScene(new Scene(new Menu.Main(this, SCENE_WIDTH, SCENE_HEIGHT), SCENE_WIDTH, SCENE_HEIGHT));
        SoundManager.playMusic("main_menu_music");
    }

    void showLevelSelect() {
        primaryStage.setScene(new Scene(new Menu.LevelSelect(this, SCENE_WIDTH, SCENE_HEIGHT), SCENE_WIDTH, SCENE_HEIGHT));
        SoundManager.playMusic("level_select_music");
    }

    void startLevel(int levelNumber, int initialScore) {
        GamePane gamePane = new GamePane(this, config, levelNumber, initialScore);
        Scene scene = new Scene(gamePane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("GHOST HUNTER INC.(ALIBABA EDITION) - LEVEL " + levelNumber);
        primaryStage.setScene(scene);
        gamePane.requestFocus();
        gamePane.startGameLoop();
    }

    void showVictoryScreen(int finalScore) {
        javafx.scene.image.ImageView bgImg = Menu.loadBg("/images/final.jpg", SCENE_WIDTH, SCENE_HEIGHT);

        Rectangle overlay = new Rectangle(SCENE_WIDTH, SCENE_HEIGHT);
        overlay.setFill(Color.rgb(0, 0, 0, 0.55));

        Label title = new Label("HOLY MOLY YOU BEAT THE GAME!\nHERES YOUR PAYCHECK *hands you a " + finalScore + "TL BIM present card*");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 48px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");
        title.setWrapText(true);
        title.setMaxWidth(900);
        title.setTextAlignment(TextAlignment.CENTER);

        Button btMenu = Menu.makeMenuBtn("Main Menu", () -> showMainMenu());

        VBox content = new VBox(24, title, btMenu);
        content.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(bgImg, overlay, content);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        SoundManager.playMusic("message_screen_music");
    }

    private void showConfigError(String message) {
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: darkred;");

        Label title = new Label("Config Error");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");

        Label body = new Label(message);
        body.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'I Still Know';");
        body.setWrapText(true);
        body.setMaxWidth(900);

        Button btExit = Menu.makeMenuBtn("Exit", () -> Platform.exit());

        root.getChildren().addAll(title, body, btExit);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
