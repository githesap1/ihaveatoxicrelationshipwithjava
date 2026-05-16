// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084
// Class: Hud_Lvl - in-game HUD, vacuum bar, health bar, score ve timer icerir

package org.example;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hud_Lvl extends Hud {

    private static final double BAR_WIDTH   = 34.0;
    private static final double BAR_HEIGHT  = 260.0;
    private static final double BAR_TOP     = 86.0;
    private static final double BAR_PADDING = 4.0;
    private static final double SCENE_W     = 1280;

    private final Rectangle vacuumContainer;
    private final Rectangle vacuumFill;
    private final Rectangle healthContainer;
    private final Rectangle healthFill;
    private final Label vacuumLabel = new Label("VACUUM:");
    private final Label healthLabel = new Label("HEALTH:");
    private final Label scoreLabel  = new Label();
    private final Label timeLabel   = new Label();

    // Vacuum bar, health bar, score ve timer label'larini olusturur
    public Hud_Lvl(double sceneWidth, double sceneHeight) {
        Pane layer = new Pane();

        vacuumContainer = makeBarContainer(26.0);
        vacuumFill      = makeBarFill(vacuumContainer.getX(), Color.web("#6F00FF"));
        healthContainer = makeBarContainer(SCENE_W - 26.0 - BAR_WIDTH);
        healthFill      = makeBarFill(healthContainer.getX(), Color.web("#4A8C2B"));

        vacuumLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Christmare';");
        healthLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Christmare';");
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: 'Ghostz';");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'Ghostz';");


        vacuumLabel.setLayoutX(20);
        vacuumLabel.setLayoutY(BAR_TOP - 34);
        healthLabel.setLayoutX(SCENE_W - 136);
        healthLabel.setLayoutY(BAR_TOP - 34);

        layer.getChildren().addAll(
            vacuumFill, vacuumContainer,
            healthFill, healthContainer,
            vacuumLabel, healthLabel,
            scoreLabel, timeLabel
        );

        getChildren().add(layer);
        setPrefSize(sceneWidth, sceneHeight);
        setMouseTransparent(true);
    }

    // Health, vacuum, score ve timer'i guncel degerlerle update eder
    public void update(double health, double maxHealth, double vacuum, double maxVacuum, int score, int seconds) {
        refreshBar(healthFill, healthContainer.getX(), health / maxHealth);
        refreshBar(vacuumFill, vacuumContainer.getX(), vacuum / maxVacuum);

        scoreLabel.setText("SCORE: " + score);

        timeLabel.setText(formatTime(seconds));
        scoreLabel.autosize();
        timeLabel.autosize();
        scoreLabel.setLayoutX((SCENE_W - scoreLabel.getWidth()) / 2.0);
        scoreLabel.setLayoutY(18);
        timeLabel.setLayoutX((SCENE_W - timeLabel.getWidth()) / 2.0);
        timeLabel.setLayoutY(48);
    }

    // Fill rectangle'in yuksekligini mevcut orana göre gunceller
    private void refreshBar(Rectangle fill, double containerX, double ratio) {
        double clamped = Math.max(0, Math.min(1, ratio));
        double h = (BAR_HEIGHT - BAR_PADDING * 2) * clamped;
        fill.setX(containerX + BAR_PADDING);
        fill.setY(BAR_TOP + BAR_HEIGHT - BAR_PADDING - h);
        fill.setWidth(BAR_WIDTH - BAR_PADDING * 2);
        fill.setHeight(h);
    }

    // Bar container rectangle'i olusturur
    private Rectangle makeBarContainer(double x) {
        Rectangle bar = new Rectangle(x, BAR_TOP, BAR_WIDTH, BAR_HEIGHT);
        bar.setFill(Color.rgb(0, 0, 0, 0.20));
        bar.setStroke(Color.web("#2C2C2C"));
        bar.setStrokeWidth(4);
        return bar;
    }

    // Bar fill rectangle'i olusturur
    private Rectangle makeBarFill(double containerX, Color color) {
        Rectangle fill = new Rectangle(
            containerX + BAR_PADDING,
            BAR_TOP + BAR_PADDING,
            BAR_WIDTH - BAR_PADDING * 2,
            BAR_HEIGHT - BAR_PADDING * 2
        );
        fill.setFill(color);
        return fill;
    }

    // Saniyeyi M:SS formatina cevirir
    private String formatTime(int totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
