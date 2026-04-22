package org.example;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
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
    private static final double BAR_WIDTH = 34.0;
    private static final double BAR_HEIGHT = 260.0;
    private static final double BAR_TOP = 86.0;
    private static final double BAR_PADDING = 4.0;
    private static final double TOKEN_RADIUS = 14.0;

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

    private final Rectangle vacuumContainer;
    private final Rectangle vacuumFill;
    private final Rectangle healthContainer;
    private final Rectangle healthFill;

    private final Label vacuumLabel = new Label("VACUUM");
    private final Label healthLabel = new Label("HEALTH");
    private final Label scoreLabel = new Label();
    private final Label timeLabel = new Label();

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
        readLevelSettings(levelNumber);

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

        vacuumContainer = createBarContainer(26.0);
        vacuumFill = createBarFill(vacuumContainer.getX(), Color.MEDIUMPURPLE);
        healthContainer = createBarContainer(SCENE_WIDTH - 26.0 - BAR_WIDTH);
        healthFill = createBarFill(healthContainer.getX(), Color.CRIMSON);

        styleStatusLabels();

        getChildren().addAll(
                playableTint,
                playableBoundary,
                scanner,
                tokenLayer,
                entityLayer,
                hunterBody,
                vacuumFill,
                vacuumContainer,
                healthFill,
                healthContainer,
                vacuumLabel,
                healthLabel,
                scoreLabel,
                timeLabel
        );

        spawnEnemies(Enemy.GHOST, levelGhostCount);
        spawnEnemies(Enemy.RIPPER, levelRipperCount);
        spawnEnemies(Enemy.WISP, levelWispCount);

        setupInputHandlers();
        updateScannerShape();
        updateHud();

        frameLoop = new Timeline(new KeyFrame(Duration.seconds(STEP_SECONDS), e -> updateFrame()));
        frameLoop.setCycleCount(Timeline.INDEFINITE);

        secondLoop = new Timeline(new KeyFrame(Duration.seconds(1), e -> tickSecond()));
        secondLoop.setCycleCount(Timeline.INDEFINITE);

        tokenSpawnLoop = new Timeline(new KeyFrame(Duration.seconds(5), e -> spawnToken()));
        tokenSpawnLoop.setCycleCount(Timeline.INDEFINITE);
    }

    public void startGameLoop() {
        if (levelEnded) {
            return;
        }
        frameLoop.play();
        secondLoop.play();
        tokenSpawnLoop.play();
    }

    public void stopGameLoop() {
        frameLoop.stop();
        secondLoop.stop();
        tokenSpawnLoop.stop();
    }

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

    private void updateAimFromMouse(double mouseX, double mouseY) {
        double dx = mouseX - hunterBody.getCenterX();
        double dy = mouseY - hunterBody.getCenterY();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0.0001) {
            aimX = dx / length;
            aimY = dy / length;
        }
    }

    private void updateFrame() {
        if (levelEnded) {
            return;
        }

        moveHunter();
        updateScannerShape();
        updateScannerEnergy();
        updateEnemies();
        applyHunterDamage();
        collectTokens();
        updateEyeReveal();
        updateHud();

        if (currentHealth <= 0) {
            showLosePanel("NOOOOOOOOOO, CAESARRRRR!!!!! *young_joseph_crying.gif*");
            return;
        }

        if (enemies.isEmpty()) {
            showWinPanel();
        }
    }

    private void tickSecond() {
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

            hunterBody.setCenterX(clamp(nextX, minX, maxX));
            hunterBody.setCenterY(clamp(nextY, minY, maxY));
        }
    }

    private void updateScannerShape() {
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

    private void updateScannerEnergy() {
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

    private void updateEnemies() {
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
                enemy.setX(clamp(enemy.getX(), minX, maxX));
            }
            if (enemy.getY() <= minY || enemy.getY() >= maxY) {
                enemy.setVy(enemy.getVy() * -1);
                enemy.setY(clamp(enemy.getY(), minY, maxY));
            }

            boolean insideScanner = isInsideScanner(enemy);
            boolean visibleByPower = playableBoundary.isVisible() || eyeRevealRemaining > 0;

            if (insideScanner) {
                enemy.setDetectedStyle();
                enemy.setRadius(enemy.getRadius() - VACUUM_SHRINK_RATE * STEP_SECONDS);
            } else {
                enemy.setNormalStyle();
            }

            if (enemy.getRadius() <= CAPTURE_THRESHOLD) {
                score += enemy.getScoreValue();
                entityLayer.getChildren().remove(enemy.getView());
                enemies.remove(i);
                continue;
            }

            enemy.syncView();
            enemy.getView().setVisible(visibleByPower || insideScanner);
        }
    }

    private void applyHunterDamage() {
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
                applyTokenEffect(token.getType());
                tokenLayer.getChildren().remove(token.getView());
                tokens.remove(i);
            }
        }
    }

    private void applyTokenEffect(int tokenType) {
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

    private void updateEyeReveal() {
        if (eyeRevealRemaining > 0) {
            eyeRevealRemaining -= STEP_SECONDS;
            if (eyeRevealRemaining < 0) {
                eyeRevealRemaining = 0;
            }
        }
    }

    private void spawnToken() {
        if (levelEnded || tokens.size() >= 2) {
            return;
        }

        int type = random.nextInt(3);
        double x = randomInRange(levelAreaX + TOKEN_RADIUS, levelAreaX + levelAreaWidth - TOKEN_RADIUS);
        double y = randomInRange(levelAreaY + TOKEN_RADIUS, levelAreaY + levelAreaHeight - TOKEN_RADIUS);

        Token token = createToken(type, x, y);
        tokens.add(token);
        tokenLayer.getChildren().add(token.getView());
    }

    private Token createToken(int type, double x, double y) {
        Group view = new Group();

        switch (type) {
            case Token.HEALTH:
                Rectangle vertical = new Rectangle(-4, -12, 8, 24);
                vertical.setFill(Color.RED);
                Rectangle horizontal = new Rectangle(-12, -4, 24, 8);
                horizontal.setFill(Color.RED);
                view.getChildren().addAll(vertical, horizontal);
                break;
            case Token.RANGE:
                Polygon triangle = new Polygon(
                        0.0, -14.0,
                        13.0, 12.0,
                        -13.0, 12.0
                );
                triangle.setFill(Color.PURPLE);
                triangle.setStroke(Color.WHITE);
                view.getChildren().add(triangle);
                break;
            case Token.EYE:
                Ellipse eye = new Ellipse(0, 0, 14, 9);
                eye.setFill(Color.WHITE);
                eye.setStroke(Color.BLACK);
                Circle pupil = new Circle(0, 0, 4, Color.BLACK);
                view.getChildren().addAll(eye, pupil);
                break;
            default:
                break;
        }

        view.setLayoutX(x);
        view.setLayoutY(y);

        return new Token(type, view, x, y, TOKEN_RADIUS);
    }

    private void spawnEnemies(int type, int count) {
        for (int i = 0; i < count; i++) {
            Enemy enemy = createEnemy(type);
            enemies.add(enemy);
            entityLayer.getChildren().add(enemy.getView());
        }
    }

    private Enemy createEnemy(int type) {
        double baseRadius;
        double minSpeed;
        double maxSpeed;
        Color bodyColor;

        switch (type) {
            case Enemy.GHOST:
                baseRadius = 18;
                minSpeed = 70;
                maxSpeed = 130;
                bodyColor = Color.rgb(255, 255, 255, 0.80);
                break;
            case Enemy.RIPPER:
                baseRadius = 16;
                minSpeed = 110;
                maxSpeed = 170;
                bodyColor = Color.rgb(176, 85, 255, 0.88);
                break;
            case Enemy.WISP:
                baseRadius = 24;
                minSpeed = 65;
                maxSpeed = 120;
                bodyColor = Color.rgb(130, 240, 255, 0.75);
                break;
            default:
                throw new IllegalStateException("Unexpected enemy type");
        }

        double x = randomInRange(levelAreaX + baseRadius, levelAreaX + levelAreaWidth - baseRadius);
        double y = randomInRange(levelAreaY + baseRadius, levelAreaY + levelAreaHeight - baseRadius);
        double speed = randomInRange(minSpeed, maxSpeed);
        double angle = randomInRange(0, Math.PI * 2);

        Group view = new Group();
        Circle body = new Circle(0, 0, baseRadius);
        body.setFill(bodyColor);
        body.setStroke(Color.BLACK);

        ArrayList<Circle> details = new ArrayList<>();
        if (type == Enemy.GHOST) {
            Circle eye1 = new Circle(-5, -4, 2.5, Color.BLACK);
            Circle eye2 = new Circle(5, -4, 2.5, Color.BLACK);
            details.add(eye1);
            details.add(eye2);
        } else if (type == Enemy.RIPPER) {
            Circle core = new Circle(0, 0, baseRadius * 0.45, Color.PURPLE);
            details.add(core);
        } else {
            Circle core = new Circle(0, 0, baseRadius * 0.5, Color.rgb(180, 250, 255, 0.9));
            details.add(core);
        }

        view.getChildren().add(body);
        view.getChildren().addAll(details);
        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setVisible(false);

        return new Enemy(
                type,
                view,
                body,
                details,
                bodyColor,
                x,
                y,
                Math.cos(angle) * speed,
                Math.sin(angle) * speed,
                baseRadius
        );
    }

    private boolean isInsideScanner(Enemy enemy) {
        if (!scanner.isVisible()) {
            return false;
        }

        //just used the built-in javafx intersects method to check it
        return scanner.getBoundsInParent().intersects(enemy.getView().getBoundsInParent());
    }

    private void updateHud() {
        double healthRatio = currentHealth / maximumHealth;
        double vacuumRatio = currentVacuum / maximumVacuum;

        setBarLevel(healthFill, healthContainer.getX(), healthRatio);
        setBarLevel(vacuumFill, vacuumContainer.getX(), vacuumRatio);

        scoreLabel.setText("SCORE: " + score);
        timeLabel.setText(formatTime(remainingSeconds));

        scoreLabel.autosize();
        timeLabel.autosize();

        scoreLabel.setLayoutX((SCENE_WIDTH - scoreLabel.getWidth()) / 2.0);
        scoreLabel.setLayoutY(18);
        timeLabel.setLayoutX((SCENE_WIDTH - timeLabel.getWidth()) / 2.0);
        timeLabel.setLayoutY(48);
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

    private void setBarLevel(Rectangle fill, double containerX, double ratio) {
        double clamped = clamp(ratio, 0, 1);
        double maxFillHeight = BAR_HEIGHT - BAR_PADDING * 2;
        double currentFillHeight = maxFillHeight * clamped;

        fill.setX(containerX + BAR_PADDING);
        fill.setY(BAR_TOP + BAR_HEIGHT - BAR_PADDING - currentFillHeight);
        fill.setWidth(BAR_WIDTH - BAR_PADDING * 2);
        fill.setHeight(currentFillHeight);
    }

    private void styleStatusLabels() {
        vacuumLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        healthLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        vacuumLabel.setLayoutX(20);
        vacuumLabel.setLayoutY(BAR_TOP - 34);
        healthLabel.setLayoutX(SCENE_WIDTH - 136);
        healthLabel.setLayoutY(BAR_TOP - 34);
    }

    private void showWinPanel() {
        if (levelEnded) {
            return;
        }
        levelEnded = true;
        stopGameLoop();

        Rectangle shade = new Rectangle(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
        shade.setFill(Color.rgb(0, 0, 0, 0.70));

        Label title = new Label(levelNumber < 3 ? "YOU WON HOORAYYY!" : "WORK COMPLETE!");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 46px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        Label subtitle = new Label("YOU ARE THE STRONGEST HUNTER IN THE WORLD with the sole exception of satoru gojo, but hes not in this game so who cares?");
        subtitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Verdana';");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(480);

        Label scoreText = new Label("Score: " + score);
        scoreText.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        Button btContinue;
        if (levelNumber < 3) {
            btContinue = createPanelButton("Next Level", () -> mainApp.startLevel(levelNumber + 1, score));
        } else {
            btContinue = createPanelButton("View Ending(pls do)", () -> mainApp.showCampaignWin(score));
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

    private void showLosePanel(String reason) {
        if (levelEnded) {
            return;
        }
        levelEnded = true;
        stopGameLoop();

        Rectangle whiteBackground = new Rectangle(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
        whiteBackground.setFill(Color.rgb(255, 255, 255, 0.95));

        Label title = new Label("GAME OVER");
        title.setStyle("-fx-text-fill: red; -fx-font-size: 52px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        Label reasonText = new Label(reason);
        reasonText.setStyle("-fx-text-fill: black; -fx-font-size: 22px; -fx-font-family: 'Verdana';");
        reasonText.setWrapText(true);
        reasonText.setMaxWidth(480);

        Label scoreText = new Label("Final Score: " + score);
        scoreText.setStyle("-fx-text-fill: black; -fx-font-size: 24px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

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
                + "-fx-font-family: 'Verdana';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

    private void stylePanelButtonPressed(Button button) {
        button.setStyle("-fx-background-color: white;"
                + "-fx-text-fill: red;"
                + "-fx-font-size: 18px;"
                + "-fx-font-family: 'Verdana';"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");
    }

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

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private double randomInRange(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void readLevelSettings(int levelNumber) {
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