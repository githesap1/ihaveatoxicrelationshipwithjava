// CSE 1242 - Introduction to Programming II - Term Project
// Author(s): [Ad Soyad] - [Ogrenci No]
//
// GamePane.java
// Main game screen. Acts as the coordinator between all game systems.
// Handles the game loop, player input, enemy movement, collision detection,
// the scanner mechanic, token spawning, and win/lose panels.
// HUD drawing is delegated to HudDisplay, entity creation to EnemyFactory.

package org.example;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import org.example.config.GameConfig;

public class GamePane extends Pane {

    private static final double SCENE_WIDTH = 1280;
    private static final double SCENE_HEIGHT = 800;

    private static final double FPS = 60.0;
    private static final double STEP_SECONDS = 1.0 / FPS;
    private static final double PLAYER_SPEED = 280.0;
    private static final double PLAYER_RADIUS = 18.0;
    private static final double CAPTURE_THRESHOLD = 6.0;
    private static final double VACUUM_SHRINK_RATE = 24.0;
    private static final double VACUUM_DRAIN_MULTIPLIER = 4.0;
    private static final double HEALTH_DRAIN_MULTIPLIER = 3.0;

    private final App mainApp;
    private final GameConfig config;
    private final int levelNumber;
    private final int initialScore;

    private double levelAreaX;
    private double levelAreaY;
    private double levelAreaWidth;
    private double levelAreaHeight;
    private int levelTimeLimitSeconds;
    private int levelGhostCount;
    private int levelRipperCount;
    private int levelWispCount;

    private final Random random = new Random();
    private final Pane entityLayer = new Pane();
    private final Pane tokenLayer = new Pane();
    private final Circle hunterBody = new Circle();
    private final Polygon scanner = new Polygon();
    private final Rectangle playableBoundary;

    private final HudDisplay hud;

    private final Timeline frameLoop;
    private final Timeline secondLoop;
    private final Timeline tokenSpawnLoop;

    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Token> tokens = new ArrayList<>();

    private final double maximumHealth;
    private final double maximumVacuum;
    private final double entityDamage;
    private final double vacuumDecreaseRate;
    private final double vacuumIncreaseRate;

    private double currentHealth;
    private double currentVacuum;
    private int remainingSeconds;
    private int score;

    private boolean upPressed;
    private boolean leftPressed;
    private boolean downPressed;
    private boolean rightPressed;
    private boolean scannerPressed;
    private boolean cheatKeyHeld;
    private boolean levelEnded;
    private boolean isPaused;

    private PauseMenu pauseMenu;

    private double eyeRevealRemaining;
    private double scannerRange = 140;
    private double scannerHalfWidth = 45;
    private double aimX = 1;
    private double aimY = 0;

    public GamePane(
            App mainApp,
            GameConfig config,
            int levelNumber,
            int initialScore
    ) {
        this.mainApp = mainApp;
        this.config = config;
        this.levelNumber = levelNumber;
        this.initialScore = initialScore;
        loadLevelConfig(levelNumber);

        setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        setFocusTraversable(true);
        setStyle(backgroundStyle(levelNumber));

        maximumHealth = config.maximumHealth > 0 ? config.maximumHealth : 100;
        maximumVacuum = config.maximumVacuum > 0 ? config.maximumVacuum : 100;
        entityDamage = config.entityDamage > 0 ? config.entityDamage : 2;
        vacuumDecreaseRate = config.vacuumDecrease > 0 ? config.vacuumDecrease : 2;
        vacuumIncreaseRate = config.vacuumIncrease > 0 ? config.vacuumIncrease : 5;

        currentHealth = maximumHealth;
        currentVacuum = maximumVacuum;
        remainingSeconds = Math.max(1, levelTimeLimitSeconds);
        score = initialScore;

        Rectangle playableTint = new Rectangle(levelAreaX, levelAreaY, levelAreaWidth, levelAreaHeight);
        playableTint.setFill(Color.rgb(255, 255, 255, 0.06));

        playableBoundary = new Rectangle(levelAreaX, levelAreaY, levelAreaWidth, levelAreaHeight);
        playableBoundary.setFill(Color.TRANSPARENT);
        playableBoundary.setStroke(Color.YELLOW);
        playableBoundary.setStrokeWidth(2.5);
        playableBoundary.setVisible(false);

        hunterBody.setRadius(PLAYER_RADIUS);
        hunterBody.setCenterX(levelAreaX + levelAreaWidth / 2.0);
        hunterBody.setCenterY(levelAreaY + levelAreaHeight / 2.0);
        hunterBody.setStroke(Color.BLACK);
        hunterBody.setFill(Color.ORANGE);

        scanner.setStroke(Color.rgb(255, 110, 110, 0.70));
        scanner.setFill(Color.rgb(255, 70, 70, 0.24));
        scanner.setVisible(false);

        hud = new HudDisplay(SCENE_WIDTH);

        getChildren().addAll(
                playableTint,
                playableBoundary,
                scanner,
                tokenLayer,
                entityLayer,
                hunterBody
        );
        getChildren().addAll(hud.getNodes());

        spawnEnemies(Enemy.GHOST, levelGhostCount);
        spawnEnemies(Enemy.RIPPER, levelRipperCount);
        spawnEnemies(Enemy.WISP, levelWispCount);

        pauseMenu = new PauseMenu(SCENE_WIDTH, SCENE_HEIGHT,
                this::resumeGame,
                () -> {
                    stopGameLoop();
                    mainApp.showMainMenu();
                });
        getChildren().add(pauseMenu);

        setupInputHandlers();
        aimMethod();
        updateHud();

        frameLoop = new Timeline(new KeyFrame(Duration.seconds(STEP_SECONDS), e -> updateFrame()));
        frameLoop.setCycleCount(Timeline.INDEFINITE);

        secondLoop = new Timeline(new KeyFrame(Duration.seconds(1), e -> timerMethod()));
        secondLoop.setCycleCount(Timeline.INDEFINITE);

        tokenSpawnLoop = new Timeline(new KeyFrame(Duration.seconds(5), e -> spawnToken()));
        tokenSpawnLoop.setCycleCount(Timeline.INDEFINITE);
    }

    // starts the frame loop, second timer, and token spawn timer
    public void startGameLoop() {
        if (levelEnded) {
            return;
        }
        frameLoop.play();
        secondLoop.play();
        tokenSpawnLoop.play();
    }

    // stops all timers (called when level ends)
    public void stopGameLoop() {
        frameLoop.stop();
        secondLoop.stop();
        tokenSpawnLoop.stop();
    }

    // pauses the game and shows the pause menu overlay
    private void pauseGame() {
        isPaused = true;
        stopGameLoop();
        pauseMenu.setVisible(true);
        pauseMenu.toFront();
    }

    // hides the pause menu and resumes the game
    void resumeGame() {
        isPaused = false;
        pauseMenu.setVisible(false);
        startGameLoop();
        requestFocus();
    }

    // registers keyboard and mouse input handlers
    private void setupInputHandlers() {
        setOnMouseMoved(e -> updateAimFromMouse(e.getX(), e.getY()));
        setOnMouseDragged(e -> updateAimFromMouse(e.getX(), e.getY()));

        setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                    upPressed = true;
                    break;
                case A:
                    leftPressed = true;
                    break;
                case S:
                    downPressed = true;
                    break;
                case D:
                    rightPressed = true;
                    break;
                case SPACE:
                    scannerPressed = true;
                    break;
                case M:
                    if (!cheatKeyHeld) {
                        playableBoundary.setVisible(!playableBoundary.isVisible());
                    }
                    cheatKeyHeld = true;
                    break;
                case ESCAPE:
                    if (!levelEnded) {
                        if (isPaused) {
                            resumeGame();
                        } else {
                            pauseGame();
                        }
                    }
                    break;
                default:
                    break;
            }
        });

        setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W:
                    upPressed = false;
                    break;
                case A:
                    leftPressed = false;
                    break;
                case S:
                    downPressed = false;
                    break;
                case D:
                    rightPressed = false;
                    break;
                case SPACE:
                    scannerPressed = false;
                    break;
                case M:
                    cheatKeyHeld = false;
                    break;
                default:
                    break;
            }
        });
    }

    // calculates the aim direction vector from the hunter's center to the mouse position
    private void updateAimFromMouse(double mouseX, double mouseY) {
        double dx = mouseX - hunterBody.getCenterX();
        double dy = mouseY - hunterBody.getCenterY();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0.0001) {
            aimX = dx / length;
            aimY = dy / length;
        }
    }

    // called every frame (~60 times per second), runs the full game update
    private void updateFrame() {
        if (levelEnded) {
            return;
        }

        moveHunter();
        aimMethod();
        mustafa_Suckerberg();
        moveEnemies();
        hunterDamage();
        collectTokens();
        fadeEyeReveal();
        updateHud();

        if (currentHealth <= 0) {
            showLosePanel("NOOOOOOOOOO, CAESARRRRR!!!!! *young_joseph_crying.gif*");
            return;
        }

        if (enemies.isEmpty()) {
            showWinPanel();
        }
    }

    // called every second to decrement the countdown timer
    private void timerMethod() {
        if (levelEnded) {
            return;
        }

        remainingSeconds--;
        if (remainingSeconds <= 0) {
            remainingSeconds = 0;
            updateHud();
            showLosePanel("Time is up");
        }
    }

    // moves the hunter based on WASD key states, stayBetweened to the playable area
    private void moveHunter() {
        double dx = 0;
        double dy = 0;

        if (upPressed) {
            dy -= 1;
        }
        if (downPressed) {
            dy += 1;
        }
        if (leftPressed) {
            dx -= 1;
        }
        if (rightPressed) {
            dx += 1;
        }

        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;

            double distance = PLAYER_SPEED * STEP_SECONDS;
            double nextX = hunterBody.getCenterX() + dx * distance;
            double nextY = hunterBody.getCenterY() + dy * distance;

            double minX = levelAreaX + hunterBody.getRadius();
            double maxX = levelAreaX + levelAreaWidth - hunterBody.getRadius();
            double minY = levelAreaY + hunterBody.getRadius();
            double maxY = levelAreaY + levelAreaHeight - hunterBody.getRadius();

            hunterBody.setCenterX(stayBetween(nextX, minX, maxX));
            hunterBody.setCenterY(stayBetween(nextY, minY, maxY));
        }
    }

    // recalculates the scanner triangle's vertex positions based on aim direction
    private void aimMethod() {
        double cx = hunterBody.getCenterX();
        double cy = hunterBody.getCenterY();

        double scanX = aimX;
        double scanY = aimY;

        // Keep the scanner attached to the hunter, then spread it outward toward the aim direction.
        double nearOffset = hunterBody.getRadius() + 2;
        double nearX = cx + scanX * nearOffset;
        double nearY = cy + scanY * nearOffset;

        double farCenterX = nearX + scanX * scannerRange;
        double farCenterY = nearY + scanY * scannerRange;

        //in here because we rotate the scanner by 90 degrees we can use basic trigonometry, we need to swap X and Y and make X negative
        double perpX = -scanY;
        double perpY = scanX;

        double leftX = farCenterX + perpX * scannerHalfWidth;
        double leftY = farCenterY + perpY * scannerHalfWidth;
        double rightX = farCenterX - perpX * scannerHalfWidth;
        double rightY = farCenterY - perpY * scannerHalfWidth;

        scanner.getPoints().setAll(nearX, nearY, leftX, leftY, rightX, rightY);
    }

    // drains vacuum when scanning, recharges when not scanning
    private void mustafa_Suckerberg() {
        boolean scannerActive = scannerPressed && currentVacuum > 0;

        if (scannerActive) {
            currentVacuum -= vacuumDecreaseRate * VACUUM_DRAIN_MULTIPLIER * STEP_SECONDS;
        } else {
            currentVacuum += vacuumIncreaseRate * STEP_SECONDS;
        }

        if (currentVacuum <= 0) {
            currentVacuum = 0;
            scannerActive = false;
        }

        if (currentVacuum > maximumVacuum) {
            currentVacuum = maximumVacuum;
        }

        scanner.setVisible(scannerActive);
    }

    // moves all enemies, bounces them off walls, shrinks them in scanner, removes captured ones
    private void moveEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            enemy.setX(enemy.getX() + enemy.getVx() * STEP_SECONDS);
            enemy.setY(enemy.getY() + enemy.getVy() * STEP_SECONDS);

            double minX = levelAreaX + enemy.getRadius();
            double maxX = levelAreaX + levelAreaWidth - enemy.getRadius();
            double minY = levelAreaY + enemy.getRadius();
            double maxY = levelAreaY + levelAreaHeight - enemy.getRadius();

            if (enemy.getX() <= minX || enemy.getX() >= maxX) {
                enemy.setVx(enemy.getVx() * -1);
                enemy.setX(stayBetween(enemy.getX(), minX, maxX));
            }
            if (enemy.getY() <= minY || enemy.getY() >= maxY) {
                enemy.setVy(enemy.getVy() * -1);
                enemy.setY(stayBetween(enemy.getY(), minY, maxY));
            }

            boolean insideScanner = enemyInScanner(enemy);
            boolean visibleByPower = playableBoundary.isVisible() || eyeRevealRemaining > 0;

            if (insideScanner) {
                enemy.inZone();
                enemy.setRadius(enemy.getRadius() - VACUUM_SHRINK_RATE * STEP_SECONDS);
            } else {
                enemy.outOfZone();
            }

            if (enemy.getRadius() <= CAPTURE_THRESHOLD) {
                score += enemy.getScoreValue();
                entityLayer.getChildren().remove(enemy.getView());
                enemies.remove(i);
                continue;
            }

            enemy.updatePosition();
            enemy.getView().setVisible(visibleByPower || insideScanner);
        }
    }

    // checks for enemy-hunter overlap and drains health, also turns the hunter red
    private void hunterDamage() {
        boolean overlapping = false;

        for (Enemy enemy : enemies) {
            double distance = distance(
                    hunterBody.getCenterX(),
                    hunterBody.getCenterY(),
                    enemy.getX(),
                    enemy.getY()
            );

                if (distance < hunterBody.getRadius() + enemy.getRadius()) {
                overlapping = true;

                //now this is important because if we didnt multiply it by STEP_SECONDS our health would decrease per frame which is too much so we just multiply it so the damage is manageable
                currentHealth -= entityDamage * HEALTH_DRAIN_MULTIPLIER * STEP_SECONDS;
            }
        }

        if (currentHealth < 0) {
            currentHealth = 0;
        }

        if (overlapping) {
            hunterBody.setFill(Color.RED);
        } else {
            hunterBody.setFill(Color.ORANGE);
        }
    }

    // checks if the hunter has walked over any token and applies its effect
    private void collectTokens() {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            Token token = tokens.get(i);

            double distance = distance(
                    hunterBody.getCenterX(),
                    hunterBody.getCenterY(),
                    token.getX(),
                    token.getY()
            );

            if (distance <= hunterBody.getRadius() + token.getRadius()) {
                activateToken(token.getType());
                tokenLayer.getChildren().remove(token.getView());
                tokens.remove(i);
            }
        }
    }

    // applies the effect of the collected token based on its type
    private void activateToken(int tokenType) {
        switch (tokenType) {
            case Token.HEALTH:
                currentHealth += config.healthTokenIncrease > 0 ? config.healthTokenIncrease : 20;
                if (currentHealth > maximumHealth) {
                    currentHealth = maximumHealth;
                }
                break;
            case Token.RANGE:
                scannerHalfWidth += config.vacuumTokenIncrease > 0 ? config.vacuumTokenIncrease : 20;
                break;
            case Token.EYE:
                eyeRevealRemaining = Math.max(
                        eyeRevealRemaining,
                        config.eyeTokenDuration > 0 ? config.eyeTokenDuration : 5
                );
                break;
            default:
                break;
        }
    }

    // counts down the eye token timer each frame
    private void fadeEyeReveal() {
        if (eyeRevealRemaining > 0) {
            eyeRevealRemaining -= STEP_SECONDS;
            if (eyeRevealRemaining < 0) {
                eyeRevealRemaining = 0;
            }
        }
    }

    // spawns a random token at a random location in the playable area (max 2 at a time)
    private void spawnToken() {
        if (levelEnded || tokens.size() >= 2) {
            return;
        }

        int type = random.nextInt(3);
        double x = randomInRange(levelAreaX + EnemyFactory.TOKEN_RADIUS, levelAreaX + levelAreaWidth - EnemyFactory.TOKEN_RADIUS);
        double y = randomInRange(levelAreaY + EnemyFactory.TOKEN_RADIUS, levelAreaY + levelAreaHeight - EnemyFactory.TOKEN_RADIUS);

        Token token = EnemyFactory.spawnToken(type, x, y);
        tokens.add(token);
        tokenLayer.getChildren().add(token.getView());
    }

    // spawns a given number of enemies of the specified type into the entity layer
    private void spawnEnemies(int type, int count) {
        for (int i = 0; i < count; i++) {
            Enemy enemy = EnemyFactory.spawnEnemy(type, levelAreaX, levelAreaY, levelAreaWidth, levelAreaHeight, random);
            enemies.add(enemy);
            entityLayer.getChildren().add(enemy.getView());
        }
    }

    // returns true if the enemy overlaps with the scanner triangle
    private boolean enemyInScanner(Enemy enemy) {
        if (!scanner.isVisible()) {
            return false;
        }

        //just used the built-in javafx intersects method to check it
        return scanner.getBoundsInParent().intersects(enemy.getView().getBoundsInParent());
    }

    // updates the health/vacuum bar fills and score/time labels on screen
    private void updateHud() {
        hud.update(currentHealth / maximumHealth, currentVacuum / maximumVacuum, score, remainingSeconds);
    }

    // displays the win overlay with next level or ending button
    private void showWinPanel() {
        if (levelEnded) {
            return;
        }
        levelEnded = true;
        stopGameLoop();

        Rectangle shade = new Rectangle(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
        shade.setFill(Color.rgb(0, 0, 0, 0.70));

        Label title = new Label(levelNumber < 3 ? "YOU WON HOORAYYY!" : "WORK COMPLETE!");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 46px; -fx-font-family: 'Blood Crow';");

        Label subtitle = new Label("YOU ARE THE STRONGEST HUNTER IN THE WORLD with the sole exception of satoru gojo, but hes not in this game so who cares?");
        subtitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'I Still Know';");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(480);

        Label scoreText = new Label("Score: " + score);
        scoreText.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");

        Button btContinue;
        if (levelNumber < 3) {
            btContinue = createPanelButton("Next Level", () -> mainApp.startLevel(levelNumber + 1, score));
        } else {
            btContinue = createPanelButton("View Ending(pls do)", () -> mainApp.showVictoryScreen(score));
        }

        Button btMenu = createPanelButton("Main Menu", () -> mainApp.showMainMenu());

        VBox panel = new VBox(14, title, subtitle, scoreText, btContinue, btMenu);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(420);

        StackPane overlay = new StackPane(shade, panel);
        overlay.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);

        getChildren().add(overlay);
        overlay.toFront();
    }

    // displays the game over overlay with the reason, score, retry and menu buttons
    private void showLosePanel(String reason) {
        if (levelEnded) {
            return;
        }
        levelEnded = true;
        stopGameLoop();

        Rectangle whiteBackground = new Rectangle(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
        whiteBackground.setFill(Color.rgb(255, 255, 255, 0.95));

        Label title = new Label("GAME OVER");
        title.setStyle("-fx-text-fill: red; -fx-font-size: 52px; -fx-font-family: 'Blood Crow';");

        Label reasonText = new Label(reason);
        reasonText.setStyle("-fx-text-fill: black; -fx-font-size: 22px; -fx-font-family: 'I Still Know';");
        reasonText.setWrapText(true);
        reasonText.setMaxWidth(480);

        Label scoreText = new Label("Final Score: " + score);
        scoreText.setStyle("-fx-text-fill: black; -fx-font-size: 24px; -fx-font-family: 'I Still Know'; -fx-font-weight: bold;");

        Button btRetry = createPanelButton("Retry Level", () -> mainApp.startLevel(levelNumber, initialScore));
        Button btMenu = createPanelButton("Main Menu", () -> mainApp.showMainMenu());

        VBox panel = new VBox(14, title, reasonText, scoreText, btRetry, btMenu);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(480);
        StackPane overlay = new StackPane(whiteBackground, panel);
        overlay.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);

        getChildren().add(overlay);
        overlay.toFront();
    }

    private Button createPanelButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setPrefHeight(46);
        stylePanelButtonDefault(button);

        button.setOnMousePressed(e -> stylePanelButtonPressed(button));
        button.setOnMouseReleased(e -> {
            stylePanelButtonDefault(button);
            if (button.isHover()) {
                action.run();
            }
        });
        button.setOnMouseExited(e -> stylePanelButtonDefault(button));
        return button;
    }

    private void stylePanelButtonDefault(Button button) {
        button.setStyle("-fx-background-color: purple;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-family: 'I Still Know';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

    private void stylePanelButtonPressed(Button button) {
        button.setStyle("-fx-background-color: white;"
                + "-fx-text-fill: red;"
                + "-fx-font-size: 18px;"
                + "-fx-font-family: 'I Still Know';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

    // returns the background color style string for a given level number
    private String backgroundStyle(int levelNumber) {
        if (levelNumber == 1) {
            return "-fx-background-color: purple;";
        } else if (levelNumber == 2) {
            return "-fx-background-color: darkslategray;";
        } else if (levelNumber == 3) {
            return "-fx-background-color: saddlebrown;";
        }

        return "-fx-background-color: black;";
    }

    private double randomInRange(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private double stayBetween(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // reads the level-specific config values (area, time, entity counts) from the config object
    private void loadLevelConfig(int levelNumber) {
        if (levelNumber == 1) {
            levelAreaX = config.level1PlayableAreaX;
            levelAreaY = config.level1PlayableAreaY;
            levelAreaWidth = config.level1PlayableAreaWidth;
            levelAreaHeight = config.level1PlayableAreaHeight;
            levelTimeLimitSeconds = config.level1Time;
            levelGhostCount = config.level1Ghosts;
            levelRipperCount = config.level1Rippers;
            levelWispCount = config.level1Wisps;
        } else if (levelNumber == 2) {
            levelAreaX = config.level2PlayableAreaX;
            levelAreaY = config.level2PlayableAreaY;
            levelAreaWidth = config.level2PlayableAreaWidth;
            levelAreaHeight = config.level2PlayableAreaHeight;
            levelTimeLimitSeconds = config.level2Time;
            levelGhostCount = config.level2Ghosts;
            levelRipperCount = config.level2Rippers;
            levelWispCount = config.level2Wisps;
        } else if (levelNumber == 3) {
            levelAreaX = config.level3PlayableAreaX;
            levelAreaY = config.level3PlayableAreaY;
            levelAreaWidth = config.level3PlayableAreaWidth;
            levelAreaHeight = config.level3PlayableAreaHeight;
            levelTimeLimitSeconds = config.level3Time;
            levelGhostCount = config.level3Ghosts;
            levelRipperCount = config.level3Rippers;
            levelWispCount = config.level3Wisps;
        } else {
            levelAreaX = 200;
            levelAreaY = 120;
            levelAreaWidth = 900;
            levelAreaHeight = 560;
            levelTimeLimitSeconds = 60;
            levelGhostCount = 5;
            levelRipperCount = 0;
            levelWispCount = 0;
        }
    }
}
