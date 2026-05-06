// CSE 1242 - Introduction to Programming II - Term Project
// Author(s): [Ad Soyad] - [Ogrenci No]
//
// HudDisplay.java
// Manages all HUD elements drawn on screen: the vacuum bar (left), health bar (right),
// score label and timer label (top center). GamePane calls update() every frame.

package org.example;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HudDisplay {

    private static final double BAR_WIDTH = 34.0;
    private static final double BAR_HEIGHT = 260.0;
    private static final double BAR_TOP = 86.0;
    private static final double BAR_PADDING = 4.0;

    private final double sceneWidth;

    private final Rectangle vacuumContainer;
    private final Rectangle vacuumFill;
    private final Rectangle healthContainer;
    private final Rectangle healthFill;

    private final Label vacuumLabel = new Label("VACUUM");
    private final Label healthLabel = new Label("HEALTH");
    private final Label scoreLabel = new Label();
    private final Label timeLabel = new Label();

    // sets up all bar and label elements based on the scene width
    public HudDisplay(double sceneWidth) {
        this.sceneWidth = sceneWidth;

        vacuumContainer = createBarContainer(26.0);
        vacuumFill = createBarFill(vacuumContainer.getX(), Color.MEDIUMPURPLE);
        healthContainer = createBarContainer(sceneWidth - 26.0 - BAR_WIDTH);
        healthFill = createBarFill(healthContainer.getX(), Color.CRIMSON);

        configureLabels();
    }

    // updates the bar fill heights and label texts each frame
    public void update(double healthRatio, double vacuumRatio, int score, int remainingSeconds) {
        setBarLevel(healthFill, healthContainer.getX(), healthRatio);
        setBarLevel(vacuumFill, vacuumContainer.getX(), vacuumRatio);

        scoreLabel.setText("SCORE: " + score);
        timeLabel.setText(formatTime(remainingSeconds));

        // autosize so we can center the labels properly
        scoreLabel.autosize();
        timeLabel.autosize();

        scoreLabel.setLayoutX((sceneWidth - scoreLabel.getWidth()) / 2.0);
        scoreLabel.setLayoutY(18);
        timeLabel.setLayoutX((sceneWidth - timeLabel.getWidth()) / 2.0);
        timeLabel.setLayoutY(48);
    }

    // returns all JavaFX nodes so GamePane can add them to its children list
    public List<Node> getNodes() {
        return List.of(
                vacuumFill, vacuumContainer,
                healthFill, healthContainer,
                vacuumLabel, healthLabel,
                scoreLabel, timeLabel
        );
    }

    private Rectangle createBarContainer(double x) {
        Rectangle bar = new Rectangle(x, BAR_TOP, BAR_WIDTH, BAR_HEIGHT);
        bar.setFill(Color.rgb(0, 0, 0, 0.20));
        bar.setStroke(Color.BLACK);
        bar.setStrokeWidth(4);
        return bar;
    }

    private Rectangle createBarFill(double containerX, Color color) {
        Rectangle fill = new Rectangle(
                containerX + BAR_PADDING,
                BAR_TOP + BAR_PADDING,
                BAR_WIDTH - BAR_PADDING * 2,
                BAR_HEIGHT - BAR_PADDING * 2
        );
        fill.setFill(color);
        return fill;
    }

    // adjusts the fill rectangle height based on the 0-1 ratio
    private void setBarLevel(Rectangle fill, double containerX, double ratio) {
        double clamped = Math.max(0, Math.min(1, ratio));
        double maxFillHeight = BAR_HEIGHT - BAR_PADDING * 2;
        double currentFillHeight = maxFillHeight * clamped;

        fill.setX(containerX + BAR_PADDING);
        fill.setY(BAR_TOP + BAR_HEIGHT - BAR_PADDING - currentFillHeight);
        fill.setWidth(BAR_WIDTH - BAR_PADDING * 2);
        fill.setHeight(currentFillHeight);
    }

    private void configureLabels() {
        vacuumLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Blood Crow';");
        healthLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Blood Crow';");
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: 'Blood Crow';");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'Blood Crow';");

        vacuumLabel.setLayoutX(20);
        vacuumLabel.setLayoutY(BAR_TOP - 34);
        healthLabel.setLayoutX(sceneWidth - 136);
        healthLabel.setLayoutY(BAR_TOP - 34);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
