// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// Class: GamePane - ana game scene, game loop ve tüm mekanikler burada.

package org.example;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
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
    final Config config;
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
    private final Rectangle backpack = new Rectangle(20, 30);

    private final Hud_Lvl lvlHud;
    private Hud_Pause_Menu pauseMenu;

    private final Timeline frameLoop;
    private final Timeline secondLoop;
    private final Timeline tokenSpawnLoop;

    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Token> tokens = new ArrayList<>();

    final double maximumHealth;
    private final double maximumVacuum;
    private final double entityDamage;
    private final double vacuumDecreaseRate;
    private final double vacuumIncreaseRate;

    double currentHealth;
    private double currentVacuum;
    int remainingSeconds;
    private int score;

    private boolean upPressed;
    private boolean leftPressed;
    private boolean downPressed;
    private boolean rightPressed;
    private boolean scannerPressed;
    private boolean cheatKeyHeld;
    private boolean levelEnded;
    private boolean isPaused;

    double eyeRevealRemaining;
    double speedBoostRemaining;
    double healthFlashRemaining;
    private boolean isOverlapping;
    double concertaSpeedRemaining;
    int concertaHealRemaining;
    private boolean concertaSpawned = false;
    boolean concertaMusicPlaying = false;
    private double scannerRange = 140;
    double scannerHalfWidth = 45;
    private double aimX = 1;
    private double aimY = 0;

    // Constructor, tüm game object'lerini initialiye eder.
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
        playableBoundary.setStroke(Color.web("#C71585"));
        playableBoundary.setStrokeWidth(2.5);
        playableBoundary.setVisible(false);

        backpack.setFill(Color.web("#232A29"));
        backpack.setArcWidth(6);
        backpack.setArcHeight(6);

        hunterBody.setRadius(PLAYER_RADIUS);
        hunterBody.setCenterX(levelAreaX + levelAreaWidth / 2.0);
        hunterBody.setCenterY(levelAreaY + levelAreaHeight / 2.0);
        hunterBody.setStroke(Color.BLACK);
        hunterBody.setFill(Color.web("#FF8C00"));

        scanner.setStroke(Color.web("#6F00FF"));
        scanner.setFill(Color.web("#4B0082", 0.35));
        scanner.setVisible(false);

        lvlHud = new Hud_Lvl(SCENE_WIDTH, SCENE_HEIGHT);

        getChildren().addAll(
                playableTint,
                playableBoundary,
                scanner,
                tokenLayer,
                entityLayer,
                hunterBody,
                backpack,
                lvlHud
        );

        spawnEnemies(Enemy.GHOST, levelGhostCount);
        spawnEnemies(Enemy.RIPPER, levelRipperCount);
        spawnEnemies(Enemy.WISP, levelWispCount);

        pauseMenu = new Hud_Pause_Menu(SCENE_WIDTH, SCENE_HEIGHT,
                this::resumeGame,
                () -> { stopGameLoop(); mainApp.startLevel(levelNumber, initialScore); },
                () -> {
                    stopGameLoop();
                    mainApp.showMainMenu();
                });
        getChildren().add(pauseMenu);

        setupInputHandlers();
        aimMethod();
        updateHud();

        String levelMusic;
        if (levelNumber == 1) levelMusic = "level1_music";
        else if (levelNumber == 2) levelMusic = "level2_music";
        else if (levelNumber == 3) levelMusic = "level3_music";
        else levelMusic = "boss_music";
        GameAudio.playMusic(levelMusic);

        frameLoop = new Timeline(new KeyFrame(Duration.seconds(STEP_SECONDS), e -> updateFrame()));
        frameLoop.setCycleCount(Timeline.INDEFINITE);

        secondLoop = new Timeline(new KeyFrame(Duration.seconds(1), e -> timerMethod()));
        secondLoop.setCycleCount(Timeline.INDEFINITE);

        tokenSpawnLoop = new Timeline(new KeyFrame(Duration.seconds(5), e -> spawnToken()));
        tokenSpawnLoop.setCycleCount(Timeline.INDEFINITE);
    }

    // Game loop Timeline'larını başlatır.
    public void startGameLoop() {
        if (levelEnded == true) {
            return;
        }
        System.out.println("basladı");
        frameLoop.play();
        secondLoop.play();
        tokenSpawnLoop.play();
    }

    // Tüm Timeline'ları durdurur.
    public void stopGameLoop() {
        frameLoop.stop();
        secondLoop.stop();
        tokenSpawnLoop.stop();
        GameAudio.stopVacuum();
        GameAudio.stopDamage();
    }

    // Pause menu'yü gösterir.
    private void pauseGame() {
        isPaused = true;
        stopGameLoop();
        pauseMenu.setVisible(true);
        pauseMenu.toFront();
    }

    // Pause'dan çıkar, oyuna devam eder.
    void resumeGame() {
        isPaused = false;
        pauseMenu.setVisible(false);
        startGameLoop();
        requestFocus();
    }

    // Keyboard ve mouse input handler'larını set up eder.
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
                        GameAudio.play("cheat");
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

    // Mouse pozisyonundan aim direction'ı hesaplar.
    private void updateAimFromMouse(double mouseX, double mouseY) {
        double dx = mouseX - hunterBody.getCenterX();
        double dy = mouseY - hunterBody.getCenterY();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0.0001) {
            aimX = dx / length;
            aimY = dy / length;
        }
    }

    // Her frame'de çağrılır (~60fps), tüm update methodlarını sırayla çalıştırır.
    private void updateFrame() {
        if (levelEnded == true) {
            return;
        }

        // boolean temp = levelEnded;


        movePlayer();

        aimMethod();
        mustafa_Suckerberg();
        moveEnemies();
        playerDamage();
        if (!concertaSpawned && currentHealth < maximumHealth * 0.35 && (levelNumber == 3 || levelNumber == 4)) {
            double cx = randomInRange(levelAreaX + Token.RADIUS, levelAreaX + levelAreaWidth - Token.RADIUS);
            double cy = randomInRange(levelAreaY + Token.RADIUS, levelAreaY + levelAreaHeight - Token.RADIUS);
            Token concerta = new Token_Concerta(cx, cy);
            tokens.add(concerta);
            tokenLayer.getChildren().add(concerta.getView());
            concertaSpawned = true;
            GameAudio.play("token_spawn");
        }
        collectTokens();
        fadeEyeReveal();
        updatePlayerColor();
        updateHud();

        if (currentHealth <= 0) {
            showLosePanel("NOOOOOOOOOO, CAESARRRRR!!!!! *young_joseph_crying.gif*");
            return;
        }

        if (enemies.size() == 0 && levelNumber != 4) {
            showWinPanel();
        }
    }

    // Her saniye bir çağrılır, countdown'ı bir azaltır.
    private void timerMethod() {
        if (levelEnded == true) {
            return;
        }

        remainingSeconds--;
        if (remainingSeconds < 0) remainingSeconds = 0;
        if (concertaHealRemaining > 0) {
            currentHealth = Math.min(currentHealth + 1, maximumHealth);
            concertaHealRemaining--;
        }
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

    // WASD ile player'ı hareket ettirir, playable area boundary'sini geçemez.
    private void movePlayer() {
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


        double speedMultiplier = concertaSpeedRemaining > 0 ? 3.4 : (speedBoostRemaining > 0 ? 1.7 : 1.0);
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

    // Vacuum barı tüketir, değilken tekrar doldurur.
    private void aimMethod() {
        double cx = hunterBody.getCenterX();
        double cy = hunterBody.getCenterY();

        double scanX = aimX;
        double scanY = aimY;

        double nearOffset = hunterBody.getRadius() + 2;
        double nearX = cx + scanX * nearOffset;
        double nearY = cy + scanY * nearOffset;

        double farCenterX = nearX + scanX * scannerRange;
        double farCenterY = nearY + scanY * scannerRange;



        // önce Math.atan2 ile yapmayı denedim ama triangle yanlış dönüyordu, ai'a yaptırdım
        //in here because we rotate the scanner by 90 degrees we can use basic trigonometry, we need to swap X and Y and make X negative
        double perpX = -scanY;
        double perpY = scanX;

        double leftX = farCenterX + perpX * scannerHalfWidth;
        double leftY = farCenterY + perpY * scannerHalfWidth;
        double rightX = farCenterX - perpX * scannerHalfWidth;
        double rightY = farCenterY - perpY * scannerHalfWidth;

        scanner.getPoints().setAll(nearX, nearY, leftX, leftY, rightX, rightY);

        double backCX = cx - scanX * 12;
        double backCY = cy - scanY * 12;
        backpack.setX(backCX - backpack.getWidth() / 2);
        backpack.setY(backCY - backpack.getHeight() / 2);
        backpack.setRotate(Math.toDegrees(Math.atan2(scanY, scanX)));
    }

    // Scanner triangle'ın köşelerini mouse yönüne göre hesaplar.
    private void mustafa_Suckerberg() {
        boolean wasActive = scanner.isVisible();
        boolean scannerActive = scannerPressed && currentVacuum > 0;

        if (concertaSpeedRemaining > 0) {
            currentVacuum = maximumVacuum;
        } else if (scannerActive) {
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

        if (scannerActive && !wasActive) GameAudio.startVacuum();
        else if (!scannerActive && wasActive) GameAudio.stopVacuum();
    }

    // Enemz'leri hareket ettirir, wall bounce zapar, scanner içindezken küçültür ve capture eder.
    private void moveEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            enemy.setX(enemy.getX() + enemy.getVx() * STEP_SECONDS);
            enemy.setY(enemy.getY() + enemy.getVy() * STEP_SECONDS);

            double minX = levelAreaX + enemy.getRadius();
            double maxX = levelAreaX + levelAreaWidth - enemy.getRadius();
            double minY = levelAreaY + enemy.getRadius();
            double maxY = levelAreaY + levelAreaHeight - enemy.getRadius();

            // enemy.setVx(0); // bunu kaldırdım çalışmıyordu
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
                enemy.setRadius(enemy.getRadius() - 24.0 * STEP_SECONDS);
            } else {
                enemy.outOfZone();
            }

            if (enemy.getRadius() <= 6.0) {
                if (enemy.getType() == Enemy.GHOST) GameAudio.play("ghost_death");
                else if (enemy.getType() == Enemy.RIPPER) GameAudio.play("ripper_death");
                else GameAudio.play("wisp_death");
                score += enemy.getScoreValue() * levelScoreMultiplier;
                System.out.println("yakalandı");
                enemy.stopAnimation();
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

    // Plazer bir enemz'e çarparsa health'ini düşürür.
    private void playerDamage() {
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

        if (concertaSpeedRemaining > 0) {
            currentHealth = maximumHealth;
        } else if (currentHealth < 0) {
            currentHealth = 0;
        }

        isOverlapping = overlapping;
        if (overlapping == true) GameAudio.startDamage();
        else GameAudio.stopDamage();
    }

    // Player bir token'a değince efektini uygular.
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


    // Player'ın rengini mevcut state'ine göre değiştirir.
    private void updatePlayerColor() {
        // hunterBody.setFill(Color.RED); // test
        if (isOverlapping) {
            hunterBody.setFill(Color.web("#B22222"));
        } else if (concertaSpeedRemaining > 0) {
            hunterBody.setFill(Color.web("#8A5F41"));
        } else if (speedBoostRemaining > 0) {
            hunterBody.setFill(Color.web("#A8DADC"));
        } else if (healthFlashRemaining > 0) {
            hunterBody.setFill(Color.web("#4A8C2B"));
        } else {
            hunterBody.setFill(Color.web("#FF8C00"));
        }
    }

    // HUD'u her frame'de günceller.
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
        if (concertaSpeedRemaining > 0) {
            concertaSpeedRemaining -= STEP_SECONDS;
            if (concertaSpeedRemaining <= 0) {
                concertaSpeedRemaining = 0;
                if (concertaMusicPlaying) {
                    concertaMusicPlaying = false;
                    String levelMusic;
                    if (levelNumber == 1) levelMusic = "level1_music";
                    else if (levelNumber == 2) levelMusic = "level2_music";
                    else if (levelNumber == 3) levelMusic = "level3_music";
                    else levelMusic = "boss_music";
                    GameAudio.playMusic(levelMusic);
                }
            }
        }
    }

    // Her 5 saniyede random token spawn eder, aynı anda max 2 token olabilir.
    private void spawnToken() {
        if (levelEnded || tokens.size() >= 2) {
            return;
        }

        int tokenCount = tokens.size();
        boolean speedAllowed = (levelTimeLimitSeconds - remainingSeconds) >= 10;
        double x = randomInRange(levelAreaX + Token.RADIUS, levelAreaX + levelAreaWidth - Token.RADIUS);
        double y = randomInRange(levelAreaY + Token.RADIUS, levelAreaY + levelAreaHeight - Token.RADIUS);

        Token token;
        if (levelNumber == 4) {
            int type = random.nextInt(10);
            if (type < 6)       token = new Token_Health(x, y);
            else if (type == 6) token = new Token_Range(x, y);
            else                token = speedAllowed ? new Token_Speed(x, y) : new Token_Health(x, y);
        } else if (levelNumber == 3) {
            int type = random.nextInt(5);
            switch (type) {
                case 0:
                case 1: token = new Token_Health(x, y); break;
                case 2: token = new Token_Range(x, y); break;
                case 3: token = new Token_Eye(x, y); break;
                default: token = speedAllowed ? new Token_Speed(x, y) : new Token_Health(x, y); break;
            }
        } else {
            int type = random.nextInt(5);
            switch (type) {
                case 0:
                case 1: token = new Token_Health(x, y); break;
                case 2: token = new Token_Range(x, y); break;
                case 3: token = new Token_Eye(x, y); break;
                default: token = speedAllowed ? new Token_Speed(x, y) : new Token_Health(x, y); break;
            }
        }
        tokens.add(token);
        tokenLayer.getChildren().add(token.getView());
        GameAudio.play("token_spawn");
    }

    // Her kill'de milestone check zapar, gerekirse yeni enemy veya token spawn eder.
    // TODO: bunu duzelt simdi cok fazla enemy spawn ediyor gibi
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

    // Time token spawn eder.
    private void spawnTimeToken() {
        double x = randomInRange(levelAreaX + Token.RADIUS, levelAreaX + levelAreaWidth - Token.RADIUS);
        double y = randomInRange(levelAreaY + Token.RADIUS, levelAreaY + levelAreaHeight - Token.RADIUS);
        Token token = new Token_Time(x, y);
        tokens.add(token);
        tokenLayer.getChildren().add(token.getView());
        GameAudio.play("token_spawn");
    }

    // Level'a uzgun random bir enemz spawn eder.
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
        if (levelNumber == 4) {
            enemy.setRadius(enemy.getRadius() * 1.5);
            if (enemy instanceof Enemy_Wisp) ((Enemy_Wisp) enemy).setOrbitSpeed(5.0);
        }
        enemies.add(enemy);
        entityLayer.getChildren().add(enemy.getView());
    }

    // Verilen type ve count kadar enemy spawn eder.


    private void spawnEnemies(int type, int count) {
        for (int i = 0; i < count; i++) {
            Enemy enemy = Enemy.spawn(type, levelAreaX, levelAreaY, levelAreaWidth, levelAreaHeight, random);
            if (levelNumber == 4) {
                enemy.setRadius(enemy.getRadius() * 1.5);
                if (enemy instanceof Enemy_Wisp) ((Enemy_Wisp) enemy).setOrbitSpeed(5.0);
            }
            enemies.add(enemy);
            entityLayer.getChildren().add(enemy.getView());
        }
    }

    // Enemy'nin scanner triangle içinde olup olmadığını JavaFX intersect ile kontrol eder.
    private boolean enemyInScanner(Enemy enemy) {
        if (!scanner.isVisible()) {
            return false;
        }

        //just used the built-in javafx intersects method to check it
        return scanner.getBoundsInParent().intersects(enemy.getView().getBoundsInParent());
    }

    // HUD'u güncel health, vacuum ve score değerleriyle update eder.
    private void updateHud() {
        lvlHud.update(currentHealth, maximumHealth, currentVacuum, maximumVacuum, score, remainingSeconds);
    }

    // Win panelini gösterir, game loop'u durdurur.

    private void showWinPanel() {
        if (levelEnded) return;
        levelEnded = true;
        stopGameLoop();
        lvlHud.setVisible(false);
        GameAudio.playMusic("message_screen_music");

        Hud_Lvl_Complete overlay = new Hud_Lvl_Complete(SCENE_WIDTH, SCENE_HEIGHT, score, levelNumber, mainApp);
        getChildren().add(overlay);
        overlay.toFront();
    }

    // Lose panelini gösterir, game loop'u durdurur.
    private void showLosePanel(String reason) {
        if (levelEnded) return;
        levelEnded = true;
        stopGameLoop();
        lvlHud.setVisible(false);
        GameAudio.playMusic("message_screen_music");
        GameAudio.play("game_over");

        Hud_Game_Over overlay = new Hud_Game_Over(SCENE_WIDTH, SCENE_HEIGHT, reason, score, levelNumber, mainApp, initialScore);
        getChildren().add(overlay);
        overlay.toFront();
    }

    // Level numarasına göre background image için CSS string'i döner.
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

    // Min ile max arasında random double döner.
    private double randomInRange(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    // Değeri min-max arasında tutar, yani clamp işlemi yapar.
    private double stayBetween(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // İki nokta arasındaki distance'ı hesaplar.
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    // Level numarasına göre config field'larını yükler.
    // TODO: level 4 icin ayri config eklenecek
    private void loadLevelConfig(int levelNumber) {
        System.out.println("lvl " + levelNumber);
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
