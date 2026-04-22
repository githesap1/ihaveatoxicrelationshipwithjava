package org.example;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.example.config.GameConfig;

public class App extends Application {
    private static final int SCENE_WIDTH = 1280;
    private static final int SCENE_HEIGHT = 800;

    private Stage primaryStage;
    private GameConfig config;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        try {
            config = GameConfig.load(new File("config.txt"));
        } catch (Exception ex) {
            showConfigErrorScene("Could not load config.txt.\n" + ex.getMessage());
            return;
        }

        primaryStage.setTitle("GHOST HUNTER INC.(ALIBABA EDITION)");
        showMainMenu();
        primaryStage.show();
    }

    void showMainMenu() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: black;");

        Label title = new Label("GHOST HUNTER INC.(ALIBABA EDITION)");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 54px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        Label subtitle = new Label("SIR WE NEED MORE BACKUP!");
        subtitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Verdana';");

        Button btStart = createMenuButton("Start Game", () -> startLevel(1, 0));
        Button btSelect = createMenuButton("Select Level", () -> showLevelSelect());
        Button btExit = createMenuButton("Exit Game", () -> Platform.exit());

        root.getChildren().addAll(title, subtitle, btStart, btSelect, btExit);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
    }

    private void showLevelSelect() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: darkslateblue;");

        Label title = new Label("Select Level");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 44px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        HBox cards = new HBox(24);
        cards.setAlignment(Pos.CENTER);
        cards.getChildren().addAll(
            createLevelCard(1, Color.MEDIUMPURPLE),
            createLevelCard(2, Color.DARKCYAN),
            createLevelCard(3, Color.DARKRED)
        );

        Button btBack = createMenuButton("Back", () -> showMainMenu());

        root.getChildren().addAll(title, cards, btBack);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
    }

    private StackPane createLevelCard(int levelNumber, Color cardColor) {
        Rectangle background = new Rectangle(260, 180);
        background.setArcWidth(26);
        background.setArcHeight(26);
        background.setFill(cardColor);
        background.setStroke(Color.WHITE);
        background.setStrokeWidth(3);

        Label text = new Label("Level " + levelNumber);
        text.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        StackPane card = new StackPane(background, text);

        card.setOnMouseEntered(e -> {
            if (!card.isPressed()) {
                background.setFill(cardColor.brighter());
            }
        });

        card.setOnMouseExited(e -> {
            text.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
            background.setFill(cardColor);
        });

        card.setOnMousePressed(e -> {
            background.setFill(Color.WHITE);
            text.setStyle("-fx-text-fill: red; -fx-font-size: 30px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        });

        card.setOnMouseReleased(e -> {
            text.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
            background.setFill(cardColor);
            if (card.isHover()) {
                startLevel(levelNumber, 0);
            }
        });

        return card;
    }

    void startLevel(int levelNumber, int initialScore) {
        GamePane gamePane = new GamePane(
                this,
                config,
                levelNumber,
                initialScore
        );

        Scene scene = new Scene(gamePane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("GHOST HUNTER INC.(ALIBABA EDITION) - LEVEL " + levelNumber);
        primaryStage.setScene(scene);
        gamePane.requestFocus();
        gamePane.startGameLoop();
    }

    void showCampaignWin(int finalScore) {
        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: darkgreen;");

        Label title = new Label("HOLY MOLY YOU BEAT THE GAME! HERES YOUR PAYCHECK *hands you a " + finalScore + "TL BIM present card*");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 48px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        title.setWrapText(true);
        title.setMaxWidth(900);

        Button btMenu = createMenuButton("Main Menu", () -> showMainMenu());

        root.getChildren().addAll(title, btMenu);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
    }

    private void showConfigErrorScene(String message) {
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: darkred;");

        Label title = new Label("Config Error");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        Label body = new Label(message);
        body.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Verdana';");
        body.setWrapText(true);
        body.setMaxWidth(900);

        Button btExit = createMenuButton("Exit", () -> Platform.exit());

        root.getChildren().addAll(title, body, btExit);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(280);
        button.setPrefHeight(52);
        setButtonDefaultStyle(button);

        button.setOnMousePressed(e -> setButtonPressedStyle(button));
        button.setOnMouseReleased(e -> {
            setButtonDefaultStyle(button);
            if (button.isHover()) {
                action.run();
            }
        });
        button.setOnMouseExited(e -> setButtonDefaultStyle(button));
        return button;
    }

    private void setButtonDefaultStyle(Button button) {
        button.setStyle("-fx-background-color: purple;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 20px;"
                + "-fx-font-family: 'Verdana';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

    private void setButtonPressedStyle(Button button) {
        button.setStyle("-fx-background-color: white;"
                + "-fx-text-fill: red;"
                + "-fx-font-size: 20px;"
                + "-fx-font-family: 'Verdana';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
