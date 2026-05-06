// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// GamePane.java - oyunun ana sahnesi, game loop, input ve tum mekanikler burada

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

    private double levelDamageMultiplier;
    private int levelScoreMultiplier;
    private int respawnKillThreshold;
    private int killCount;

    private final App mainApp;
    private final Config config;
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

    private static final double BAR_WIDTH   = 34.0;
    private static final double BAR_HEIGHT  = 260.0;
    private static final double BAR_TOP     = 86.0;
    private static final double BAR_PADDING = 4.0;

    private final Rectangle vacuumContainer;
    private final Rectangle vacuumFill;
    private final Rectangle healthContainer;
    private final Rectangle healthFill;
    private final Label vacuumLabel = new Label("VACUUM:");
    private final Label healthLabel = new Label("HEALTH:");
    private final Label scoreLabel  = new Label();
    private final Label timeLabel   = new Label();

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

    private Menu.Pause pauseMenu;

    private double eyeRevealRemaining;
    private double speedBoostRemaining;
    private double healthFlashRemaining;
    private boolean isOverlapping;
    private double scannerRange = 140;
    private double scannerHalfWidth = 45;
    private double aimX = 1;
    private double aimY = 0;

    public GamePane(
            App mainApp,
            Config config,
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

        vacuumContainer = createBarContainer(26.0);
        vacuumFill      = createBarFill(vacuumContainer.getX(), Color.MEDIUMPURPLE);
        healthContainer = createBarContainer(SCENE_WIDTH - 26.0 - BAR_WIDTH);
        healthFill      = createBarFill(healthContainer.getX(), Color.CRIMSON);

        vacuumLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Blood Crow';");
        healthLabel.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-family: 'Blood Crow';");
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: 'Blood Crow';");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'Blood Crow';");
        vacuumLabel.setLayoutX(20);
        vacuumLabel.setLayoutY(BAR_TOP - 34);
        healthLabel.setLayoutX(SCENE_WIDTH - 136);
        healthLabel.setLayoutY(BAR_TOP - 34);

        getChildren().addAll(
                playableTint,
                playableBoundary,
                scanner,
                tokenLayer,
                entityLayer,
                hunterBody
        );
        getChildren().addAll(vacuumFill, vacuumContainer, healthFill, healthContainer,
                             vacuumLabel, healthLabel, scoreLabel, timeLabel);

        spawnEnemies(Enemy.GHOST, levelGhostCount);
        spawnEnemies(Enemy.RIPPER, levelRipperCount);
        spawnEnemies(Enemy.WISP, levelWispCount);

        pauseMenu = new Menu.Pause(SCENE_WIDTH, SCENE_HEIGHT,
                this::resumeGame,
                () -> {
                    stopGameLoop();
                    mainApp.showMainMenu();
                });
        getChildren().add(pauseMenu);

        setupInputHandlers();
        aimMethod();
        updateHud();

        String levelMusic = levelNumber == 4 ? "boss_music" : "level" + levelNumber + "_music";
        SoundManager.playMusic(levelMusic);

        frameLoop = new Timeline(new KeyFrame(Duration.seconds(STEP_SECONDS), e -> updateFrame()));
        frameLoop.setCycleCount(Timeline.INDEFINITE);

        secondLoop = new Timeline(new KeyFrame(Duration.seconds(1), e -> timerMethod()));
        secondLoop.setCycleCount(Timeline.INDEFINITE);

        tokenSpawnLoop = new Timeline(new KeyFrame(Duration.seconds(5), e -> spawnToken()));
        tokenSpawnLoop.setCycleCount(Timeline.INDEFINITE);
    }

    // looplari baslatir
    public void startGameLoop() {
        if (levelEnded) {
            return;
        }
        frameLoop.play();
        secondLoop.play();
        tokenSpawnLoop.play();
    }

    // level bitince looplari durdurur
    public void stopGameLoop() {
        frameLoop.stop();
        secondLoop.stop();
        tokenSpawnLoop.stop();
        SoundManager.stopVacuum();
        SoundManager.stopDamage();
    }

    private void pauseGame() {
        isPaused = true;
        stopGameLoop();
        pauseMenu.setVisible(true);
        pauseMenu.toFront();
    }

    void resumeGame() {
        isPaused = false;
        pauseMenu.setVisible(false);
        startGameLoop();
        requestFocus();
    }

    // klavye ve mouse input'lari
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
                        SoundManager.play("gg");
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

    // mouse pozisyonundan aim yonu hesaplar
    private void updateAimFromMouse(double mouseX, double mouseY) {
        double dx = mouseX - hunterBody.getCenterX();
        double dy = mouseY - hunterBody.getCenterY();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0.0001) {
            aimX = dx / length;
            aimY = dy / length;
        }
    }

    // her frame'de cagrilir (~60fps)
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
        updateHunterColor();
        updateHud();

        if (currentHealth <= 0) {
            showLosePanel("NOOOOOOOOOO, CAESARRRRR!!!!! *young_joseph_crying.gif*");
            return;
        }

        if (enemies.isEmpty() && levelNumber != 4) {
            showWinPanel();
        }
    }

    // her saniye geri sayim azaltir
    private void timerMethod() {
        if (levelEnded) {
            return;
        }

        remainingSeconds--;
        if (remainingSeconds <= 0) {
            remainingSeconds = 0;
            updateHud();
            if (levelNumber == 4) {
                showWinPanel();
            } else {
                showLosePanel("Time is up");
            }
        }
    }

    // WASD ile hunter hareketi, sinir kontrollü
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

            double speedMultiplier = speedBoostRemaining > 0 ? 1.7 : 1.0;
            double distance = PLAYER_SPEED * speedMultiplier * STEP_SECONDS;
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

    // scanner ucgeninin koselerini aim yonune gore hesaplar
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
        boolean wasActive = scanner.isVisible();
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

        if (scannerActive && !wasActive) SoundManager.startVacuum();
        else if (!scannerActive && wasActive) SoundManager.stopVacuum();
    }

    // enemy hareketi, duvar sekme, scanner kucultme, yakalama
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
                if (enemy.getType() == Enemy.GHOST) SoundManager.play("ghost_death");
                else if (enemy.getType() == Enemy.RIPPER) SoundManager.play("ripper_death");
                else SoundManager.play("wisp_death");
                score += enemy.getScoreValue() * levelScoreMultiplier;
                entityLayer.getChildren().remove(enemy.getView());
                enemies.remove(i);
                killCount++;
                checkKillMilestones();
                continue;
            }

            enemy.updatePosition();
            enemy.getView().setVisible(visibleByPower || insideScanner);
        }
    }

    // enemy cakismasi varsa can azaltir, hunter kirmizi yanar
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
                currentHealth -= entityDamage * HEALTH_DRAIN_MULTIPLIER * levelDamageMultiplier * STEP_SECONDS;
            }
        }

        if (currentHealth < 0) {
            currentHealth = 0;
        }

        isOverlapping = overlapping;
        if (overlapping) SoundManager.startDamage();
        else SoundManager.stopDamage();
    }

    // token toplama kontrolu
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
                token.apply(this);
                tokenLayer.getChildren().remove(token.getView());
                tokens.remove(i);
            }
        }
    }

    void applyHealthToken() {
        double amount = config.healthTokenIncrease > 0 ? config.healthTokenIncrease : 20;
        currentHealth = Math.min(currentHealth + amount, maximumHealth);
        healthFlashRemaining = 1.5;
        SoundManager.play("health");
    }

    void applyRangeToken() {
        double amount = config.vacuumTokenIncrease > 0 ? config.vacuumTokenIncrease : 20;
        scannerHalfWidth += amount;
        SoundManager.play("range");
    }

    void applyEyeToken() {
        double duration = config.eyeTokenDuration > 0 ? config.eyeTokenDuration : 5;
        eyeRevealRemaining = Math.max(eyeRevealRemaining, duration);
        SoundManager.play("eye");
    }

    void applyTimeToken() {
        remainingSeconds += 10;
        SoundManager.play("time");
    }

    void applySpeedToken() {
        double duration = config.speedTokenDuration > 0 ? config.speedTokenDuration : 6;
        speedBoostRemaining = Math.max(speedBoostRemaining, duration);
        SoundManager.play("speed");
    }

    private void updateHunterColor() {
        if (isOverlapping) {
            hunterBody.setFill(Color.RED);
        } else if (speedBoostRemaining > 0) {
            hunterBody.setFill(Color.DODGERBLUE);
        } else if (healthFlashRemaining > 0) {
            hunterBody.setFill(Color.LIMEGREEN);
        } else {
            hunterBody.setFill(Color.ORANGE);
        }
    }

    private void fadeEyeReveal() {
        if (eyeRevealRemaining > 0) {
            eyeRevealRemaining -= STEP_SECONDS;
            if (eyeRevealRemaining < 0) {
                eyeRevealRemaining = 0;
            }
        }
        if (speedBoostRemaining > 0) {
            speedBoostRemaining -= STEP_SECONDS;
            if (speedBoostRemaining < 0) speedBoostRemaining = 0;
        }
        if (healthFlashRemaining > 0) {
            healthFlashRemaining -= STEP_SECONDS;
            if (healthFlashRemaining < 0) healthFlashRemaining = 0;
        }
    }

    // rastgele token spawn eder, max 2 ayni anda
    private void spawnToken() {
        if (levelEnded || tokens.size() >= 2) {
            return;
        }

        double x = randomInRange(levelAreaX + Token.RADIUS, levelAreaX + levelAreaWidth - Token.RADIUS);
        double y = randomInRange(levelAreaY + Token.RADIUS, levelAreaY + levelAreaHeight - Token.RADIUS);

        Token token;
        if (levelNumber == 4) {
            int type = random.nextInt(10);
            if (type < 8)      token = new Token_Health(x, y);
            else if (type == 8) token = new Token_Range(x, y);
            else               token = new Token_Speed(x, y);
        } else {
            int type = random.nextInt(5);
            switch (type) {
                case 0:
                case 1: token = new Token_Health(x, y); break;
                case 2: token = new Token_Range(x, y); break;
                case 3: token = new Token_Eye(x, y); break;
                default: token = new Token_Speed(x, y); break;
            }
        }
        tokens.add(token);
        tokenLayer.getChildren().add(token.getView());
    }

    private void checkKillMilestones() {
        if (levelNumber == 4) {
            spawnRandomEnemyForLevel();
            spawnRandomEnemyForLevel();
            return;
        }
        if (killCount % 5 == 0) {
            spawnTimeToken();
        }
        if (killCount % respawnKillThreshold == 0) {
            spawnRandomEnemyForLevel();
        }
    }

    // her 5 kill'de bir zaman tokeni cikar
    private void spawnTimeToken() {
        double x = randomInRange(levelAreaX + Token.RADIUS, levelAreaX + levelAreaWidth - Token.RADIUS);
        double y = randomInRange(levelAreaY + Token.RADIUS, levelAreaY + levelAreaHeight - Token.RADIUS);
        Token token = new Token_Time(x, y);
        tokens.add(token);
        tokenLayer.getChildren().add(token.getView());
    }

    private void spawnRandomEnemyForLevel() {
        int type;
        if (levelNumber == 1) {
            type = Enemy.GHOST;
        } else if (levelNumber == 2) {
            type = random.nextBoolean() ? Enemy.GHOST : Enemy.RIPPER;
        } else {
            type = random.nextInt(3);
        }
        Enemy enemy = Enemy.spawn(type, levelAreaX, levelAreaY, levelAreaWidth, levelAreaHeight, random);
        if (levelNumber == 4) enemy.setRadius(enemy.getRadius() * 1.5);
        enemies.add(enemy);
        entityLayer.getChildren().add(enemy.getView());
    }

    private void spawnEnemies(int type, int count) {
        for (int i = 0; i < count; i++) {
            Enemy enemy = Enemy.spawn(type, levelAreaX, levelAreaY, levelAreaWidth, levelAreaHeight, random);
            if (levelNumber == 4) enemy.setRadius(enemy.getRadius() * 1.5);
            enemies.add(enemy);
            entityLayer.getChildren().add(enemy.getView());
        }
    }

    private boolean enemyInScanner(Enemy enemy) {
        if (!scanner.isVisible()) {
            return false;
        }

        //just used the built-in javafx intersects method to check it
        return scanner.getBoundsInParent().intersects(enemy.getView().getBoundsInParent());
    }

    private void updateHud() {
        updateBar(healthFill, healthContainer.getX(), currentHealth / maximumHealth);
        updateBar(vacuumFill, vacuumContainer.getX(), currentVacuum / maximumVacuum);

        scoreLabel.setText("SCORE: " + score);
        timeLabel.setText(formatTime(remainingSeconds));
        scoreLabel.autosize();
        timeLabel.autosize();
        scoreLabel.setLayoutX((SCENE_WIDTH - scoreLabel.getWidth()) / 2.0);
        scoreLabel.setLayoutY(18);
        timeLabel.setLayoutX((SCENE_WIDTH - timeLabel.getWidth()) / 2.0);
        timeLabel.setLayoutY(48);
    }

    private void updateBar(Rectangle fill, double containerX, double ratio) {
        double clamped = Math.max(0, Math.min(1, ratio));
        double h = (BAR_HEIGHT - BAR_PADDING * 2) * clamped;
        fill.setX(containerX + BAR_PADDING);
        fill.setY(BAR_TOP + BAR_HEIGHT - BAR_PADDING - h);
        fill.setWidth(BAR_WIDTH - BAR_PADDING * 2);
        fill.setHeight(h);
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

    private String formatTime(int totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    // kazanma paneli
    private void showWinPanel() {
        if (levelEnded) return;
        levelEnded = true;
        stopGameLoop();
        SoundManager.playMusic("message_screen_music");

        Menu.Win overlay = new Menu.Win(SCENE_WIDTH, SCENE_HEIGHT, score, levelNumber, mainApp);
        getChildren().add(overlay);
        overlay.toFront();
    }

    // kaybetme paneli
    private void showLosePanel(String reason) {
        if (levelEnded) return;
        levelEnded = true;
        stopGameLoop();
        SoundManager.playMusic("message_screen_music");
        SoundManager.play("game_over");

        Menu.GameOver overlay = new Menu.GameOver(SCENE_WIDTH, SCENE_HEIGHT, reason, score, levelNumber, mainApp, initialScore);
        getChildren().add(overlay);
        overlay.toFront();
    }

    private String backgroundStyle(int levelNumber) {
        String name;
        if (levelNumber == 1) name = "level1.jpg";
        else if (levelNumber == 2) name = "level2.jpg";
        else if (levelNumber == 3) name = "level3.jpg";
        else if (levelNumber == 4) name = "bossfight.jpg";
        else return "-fx-background-color: black;";

        java.net.URL url = getClass().getResource("/images/" + name);
        if (url != null) {
            return "-fx-background-image: url('" + url.toExternalForm() + "');"
                 + "-fx-background-size: cover;"
                 + "-fx-background-position: center;";
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

    // config.txt'den level ayarlarini yukler
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
            levelDamageMultiplier = 1.0;
            levelScoreMultiplier = 1;
            respawnKillThreshold = 3;
        } else if (levelNumber == 2) {
            levelAreaX = config.level2PlayableAreaX;
            levelAreaY = config.level2PlayableAreaY;
            levelAreaWidth = config.level2PlayableAreaWidth;
            levelAreaHeight = config.level2PlayableAreaHeight;
            levelTimeLimitSeconds = config.level2Time;
            levelGhostCount = config.level2Ghosts;
            levelRipperCount = config.level2Rippers;
            levelWispCount = config.level2Wisps;
            levelDamageMultiplier = 1.5;
            levelScoreMultiplier = 2;
            respawnKillThreshold = 3;
        } else if (levelNumber == 3) {
            levelAreaX = config.level3PlayableAreaX;
            levelAreaY = config.level3PlayableAreaY;
            levelAreaWidth = config.level3PlayableAreaWidth;
            levelAreaHeight = config.level3PlayableAreaHeight;
            levelTimeLimitSeconds = config.level3Time;
            levelGhostCount = config.level3Ghosts;
            levelRipperCount = config.level3Rippers;
            levelWispCount = config.level3Wisps;
            levelDamageMultiplier = 2.0;
            levelScoreMultiplier = 4;
            respawnKillThreshold = 2;
        } else if (levelNumber == 4) {
            levelAreaX = config.level3PlayableAreaX;
            levelAreaY = config.level3PlayableAreaY;
            levelAreaWidth = config.level3PlayableAreaWidth;
            levelAreaHeight = config.level3PlayableAreaHeight;
            levelTimeLimitSeconds = 90;
            levelGhostCount = 4;
            levelRipperCount = 3;
            levelWispCount = 3;
            levelDamageMultiplier = 2.5;
            levelScoreMultiplier = 6;
            respawnKillThreshold = 1;
        } else {
            levelAreaX = 200;
            levelAreaY = 120;
            levelAreaWidth = 900;
            levelAreaHeight = 560;
            levelTimeLimitSeconds = 60;
            levelGhostCount = 5;
            levelRipperCount = 0;
            levelWispCount = 0;
            levelDamageMultiplier = 1.0;
            levelScoreMultiplier = 1;
            respawnKillThreshold = 5;
        }
    }
}
