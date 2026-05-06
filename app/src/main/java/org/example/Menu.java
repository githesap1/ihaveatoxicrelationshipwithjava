// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Menu.java - tum menu ekranlari burada, her biri ayri bir ic sinif

package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Menu {

    // --- ANA MENU ---
    public static class Main extends StackPane {

        public Main(App app, double width, double height) {
            ImageView bg = loadBg("/images/menu.jpg", width, height);

            Rectangle overlay = new Rectangle(width, height);
            overlay.setFill(Color.rgb(0, 0, 0, 0.52));

            Label title = new Label("GHOST HUNTER INC.(ALIBABA EDITION)");
            title.setStyle("-fx-text-fill: white; -fx-font-size: 54px; -fx-font-family: 'Blood Crow';");

            Label subtitle = new Label("SIR WE NEED MORE BACKUP!");
            subtitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'I Still Know';");

            Button btStart  = makeMenuBtn("Start Game",   () -> app.startLevel(1, 0));
            Button btSelect = makeMenuBtn("Select Level", () -> app.showLevelSelect());
            Button btExit   = makeMenuBtn("Exit Game",    () -> javafx.application.Platform.exit());

            VBox content = new VBox(20, title, subtitle, btStart, btSelect, btExit);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));

            getChildren().addAll(bg, overlay, content);
            setPrefSize(width, height);
        }
    }

    // --- LEVEL SECIM MENUSU ---
    public static class LevelSelect extends StackPane {

        public LevelSelect(App app, double width, double height) {
            ImageView bg = loadBg("/images/menu2.jpg", width, height);

            Rectangle overlay = new Rectangle(width, height);
            overlay.setFill(Color.rgb(0, 0, 0, 0.45));

            Label title = new Label("Select Level");
            title.setStyle("-fx-text-fill: white; -fx-font-size: 44px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");

            HBox cards = new HBox(28);
            cards.setAlignment(Pos.CENTER);
            cards.getChildren().addAll(
                buildLevelCard(1, app),
                buildLevelCard(2, app),
                buildLevelCard(3, app)
            );

            Button btBack = makeMenuBtn("Back", () -> app.showMainMenu());

            VBox content = new VBox(25, title, cards, btBack);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));

            getChildren().addAll(bg, overlay, content);
            setPrefSize(width, height);
        }

        private StackPane buildLevelCard(int levelNumber, App app) {
            ImageView preview = new ImageView(
                new Image(getClass().getResourceAsStream("/images/level" + levelNumber + ".jpg"))
            );
            preview.setFitWidth(290);
            preview.setFitHeight(200);
            preview.setPreserveRatio(false);

            Rectangle clip = new Rectangle(290, 200);
            clip.setArcWidth(26);
            clip.setArcHeight(26);
            preview.setClip(clip);

            Rectangle border = new Rectangle(290, 200);
            border.setArcWidth(26);
            border.setArcHeight(26);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.WHITE);
            border.setStrokeWidth(3);

            Rectangle textBg = new Rectangle(290, 48);
            textBg.setFill(Color.rgb(0, 0, 0, 0.68));
            StackPane.setAlignment(textBg, Pos.BOTTOM_CENTER);

            Label text = new Label("Level " + levelNumber);
            text.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");
            StackPane.setAlignment(text, Pos.BOTTOM_CENTER);
            text.setTranslateY(-10);

            StackPane card = new StackPane(preview, border, textBg, text);
            card.setPrefSize(290, 200);

            card.setOnMouseEntered(e -> { if (!card.isPressed()) border.setStroke(Color.YELLOW); });
            card.setOnMouseExited(e -> {
                border.setStroke(Color.WHITE);
                text.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");
            });
            card.setOnMousePressed(e -> {
                border.setStroke(Color.RED);
                text.setStyle("-fx-text-fill: red; -fx-font-size: 26px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");
            });
            card.setOnMouseReleased(e -> {
                border.setStroke(Color.WHITE);
                text.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");
                if (card.isHover()) { SoundManager.play("select"); app.startLevel(levelNumber, 0); }
            });

            return card;
        }
    }

    // --- PAUSE MENUSU ---
    public static class Pause extends StackPane {

        public Pause(double width, double height, Runnable onResume, Runnable onMainMenu) {
            Rectangle bg = new Rectangle(0, 0, width, height);
            bg.setFill(Color.rgb(0, 0, 0, 0.72));

            Label title = new Label("PAUSED");
            title.setStyle("-fx-text-fill: white; -fx-font-size: 52px; -fx-font-family: 'Blood Crow';");

            Label hint = new Label("press ESC to continue");
            hint.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'I Still Know';");

            Button btResume = makePanelBtn("Resume",    onResume);
            Button btMenu   = makePanelBtn("Main Menu", onMainMenu);

            VBox panel = new VBox(14, title, hint, btResume, btMenu);
            panel.setAlignment(Pos.CENTER);

            getChildren().addAll(bg, panel);
            setPrefSize(width, height);
            setVisible(false);
        }
    }

    // --- OLUM MENUSU ---
    public static class GameOver extends StackPane {

        public GameOver(double width, double height, String reason, int score,
                        int levelNumber, App app, int initialScore) {
            Rectangle bg = new Rectangle(0, 0, width, height);
            bg.setFill(Color.rgb(255, 255, 255, 0.95));

            Label title = new Label("GAME OVER");
            title.setStyle("-fx-text-fill: red; -fx-font-size: 52px; -fx-font-family: 'Blood Crow';");

            Label reasonText = new Label(reason);
            reasonText.setStyle("-fx-text-fill: black; -fx-font-size: 22px; -fx-font-family: 'I Still Know';");
            reasonText.setWrapText(true);
            reasonText.setMaxWidth(480);

            Label scoreText = new Label("Final Score: " + score);
            scoreText.setStyle("-fx-text-fill: black; -fx-font-size: 24px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");

            Button btRetry = makePanelBtn("Retry Level", () -> app.startLevel(levelNumber, initialScore));
            Button btMenu  = makePanelBtn("Main Menu",   () -> app.showMainMenu());

            VBox panel = new VBox(14, title, reasonText, scoreText, btRetry, btMenu);
            panel.setAlignment(Pos.CENTER);
            panel.setMaxWidth(480);

            getChildren().addAll(bg, panel);
            setPrefSize(width, height);
        }
    }

    // --- KAZANMA MENUSU ---
    public static class Win extends StackPane {

        public Win(double width, double height, int score, int levelNumber, App app) {
            Rectangle shade = new Rectangle(0, 0, width, height);
            shade.setFill(Color.rgb(0, 0, 0, 0.70));

            String titleText = levelNumber == 4 ? "YOU SURVIVED THE BOSS FIGHT!"
                             : levelNumber < 3  ? "YOU WON HOORAYYY!"
                             : "WORK COMPLETE!";

            Label title = new Label(titleText);
            title.setStyle("-fx-text-fill: white; -fx-font-size: 46px; -fx-font-family: 'Blood Crow';");

            Label subtitle = new Label("YOU ARE THE STRONGEST HUNTER IN THE WORLD with the sole exception of satoru gojo, but hes not in this game so who cares?");
            subtitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'I Still Know';");
            subtitle.setWrapText(true);
            subtitle.setMaxWidth(480);

            Label scoreText = new Label("Score: " + score);
            scoreText.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");

            Button btContinue;
            if (levelNumber < 3) {
                btContinue = makePanelBtn("Next Level", () -> app.startLevel(levelNumber + 1, score));
            } else if (levelNumber == 3) {
                btContinue = makePanelBtn("Continue", () -> app.startLevel(4, score));
            } else {
                btContinue = makePanelBtn("View Ending(pls do)", () -> app.showVictoryScreen(score));
            }

            Button btMenu = makePanelBtn("Main Menu", () -> app.showMainMenu());

            VBox panel = new VBox(14, title, subtitle, scoreText, btContinue, btMenu);
            panel.setAlignment(Pos.CENTER);
            panel.setMaxWidth(420);

            getChildren().addAll(shade, panel);
            setPrefSize(width, height);
        }
    }

    // --- ORTAK YARDIMCI METODLAR ---

    static Button makeMenuBtn(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(280);
        button.setPrefHeight(52);
        styleMenuDefault(button);

        button.setOnMouseEntered(e -> { if (!button.isPressed()) styleMenuHover(button); });
        button.setOnMousePressed(e -> styleMenuPressed(button));
        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                styleMenuHover(button);
                SoundManager.play("select");
                action.run();
            } else {
                styleMenuDefault(button);
            }
        });
        button.setOnMouseExited(e -> styleMenuDefault(button));
        return button;
    }

    static Button makePanelBtn(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setPrefHeight(46);
        stylePanelDefault(button);

        button.setOnMousePressed(e -> stylePanelPressed(button));
        button.setOnMouseReleased(e -> {
            stylePanelDefault(button);
            if (button.isHover()) {
                SoundManager.play("select");
                action.run();
            }
        });
        button.setOnMouseExited(e -> stylePanelDefault(button));
        return button;
    }

    private static void styleMenuDefault(Button b) {
        b.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-font-size: 20px;"
                + "-fx-font-family: 'I Still Know'; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    private static void styleMenuHover(Button b) {
        b.setStyle("-fx-background-color: #a040ff; -fx-text-fill: white; -fx-font-size: 20px;"
                + "-fx-font-family: 'I Still Know'; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    private static void styleMenuPressed(Button b) {
        b.setStyle("-fx-background-color: white; -fx-text-fill: red; -fx-font-size: 20px;"
                + "-fx-font-family: 'I Still Know'; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    private static void stylePanelDefault(Button b) {
        b.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-font-size: 18px;"
                + "-fx-font-family: 'I Still Know'; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    private static void stylePanelPressed(Button b) {
        b.setStyle("-fx-background-color: white; -fx-text-fill: red; -fx-font-size: 18px;"
                + "-fx-font-family: 'I Still Know'; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    static ImageView loadBg(String path, double width, double height) {
        ImageView iv = new ImageView(new Image(Menu.class.getResourceAsStream(path)));
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        iv.setPreserveRatio(false);
        return iv;
    }
}
