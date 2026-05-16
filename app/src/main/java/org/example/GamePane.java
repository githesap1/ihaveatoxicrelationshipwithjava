// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084
// Class: GamePane - ana game scene, game loop ve tum mekanikler burada

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
    private Enemy_Spawner spawner;

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

    // Constructor, tum game object'lerini initialiye eder
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
        hunterBody.setStroke(Color.web("#1C1C1C"));
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

        spawner = new Enemy_Spawner(levelNumber, levelAreaX, levelAreaY, levelAreaWidth, levelAreaHeight,
                enemies, entityLayer, random);
        spawner.spawnGroup(Enemy.GHOST, levelGhostCount);
        spawner.spawnGroup(Enemy.RIPPER, levelRipperCount);
        spawner.spawnGroup(Enemy.WISP, levelWispCount);

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
        Game_Audio.playMusic(levelMusic);

        frameLoop = new Timeline(new KeyFrame(Duration.seconds(STEP_SECONDS), e -> updateFrame()));
        frameLoop.setCycleCount(Timeline.INDEFINITE);

        secondLoop = new Timeline(new KeyFrame(Duration.seconds(1), e -> timerMethod()));
        secondLoop.setCycleCount(Timeline.INDEFINITE);

        tokenSpawnLoop = new Timeline(new KeyFrame(Duration.seconds(5), e -> spawnToken()));
        tokenSpawnLoop.setCycleCount(Timeline.INDEFINITE);
    }

    // Game loop Timeline'larini baslatir
    public void startGameLoop() {
        if (levelEnded == true) {
            return;
        }
        System.out.println("basladi");
        frameLoop.play();
        secondLoop.play();
        tokenSpawnLoop.play();
    }

    // Tum Timeline'lari durdurur
    public void stopGameLoop() {
        frameLoop.stop();
        secondLoop.stop();
        tokenSpawnLoop.stop();
        Game_Audio.stopVacuum();
        Game_Audio.stopDamage();
    }

    // Pause menu'yu gösterir
    private void pauseGame() {
        isPaused = true;
        stopGameLoop();
        pauseMenu.setVisible(true);
        pauseMenu.toFront();
    }

    // Pause'dan cikar, oyuna devam eder
    void resumeGame() {
        isPaused = false;
        pauseMenu.setVisible(false);
        startGameLoop();
        requestFocus();
    }

    // Keyboard ve mouse input handler'larini set up eder
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
                        Game_Audio.play("cheat");
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

    // Mouse pozisyonundan aim direction'i hesaplar
    private void updateAimFromMouse(double mouseX, double mouseY) {
        double dx = mouseX - hunterBody.getCenterX();
        double dy = mouseY - hunterBody.getCenterY();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0.0001) {
            aimX = dx / length;
            aimY = dy / length;
        }
    }

    // Her frame'de cagrilir (~60fps), tum update methodlarini sirayla calistirir
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
            Game_Audio.play("token_spawn");
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

    // Her saniye bir cagrilir, countdown'i bir azaltir
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

    // WASD ile player'i hareket ettirir, playable area boundary'sini gecemez
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

    // Vacuum bari tuketir, degilken tekrar doldurur
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



        // önce Math.atan2 ile yapmayi denedim ama triangle yanlis dönuyordu, ai'a ve internette araştıdım böyle olduu
        // burda scanner'ı 90 derece döndürdüğümüz için temel trigonometri kullanarak X ve Y'yi değiştiriyoruz ve X'i negatif yapıyoruz
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

    // Scanner triangle'in köselerini mouse yönune göre hesaplar
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

        if (scannerActive && !wasActive) Game_Audio.startVacuum();
        else if (!scannerActive && wasActive) Game_Audio.stopVacuum();
    }

    // Enemy'leri hareket ettirir, wall bounce yapar, scanner icindeyken kucultur ve yakalar
    private void moveEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            enemy.setX(enemy.getX() + enemy.getVx() * STEP_SECONDS);
            enemy.setY(enemy.getY() + enemy.getVy() * STEP_SECONDS);

            double minX = levelAreaX + enemy.getRadius();
            double maxX = levelAreaX + levelAreaWidth - enemy.getRadius();
            double minY = levelAreaY + enemy.getRadius();
            double maxY = levelAreaY + levelAreaHeight - enemy.getRadius();

            // enemy.setVx(0); // bunu kaldirdim calismiyordu
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
                if (enemy.getType() == Enemy.GHOST) Game_Audio.play("ghost_death");
                else if (enemy.getType() == Enemy.RIPPER) Game_Audio.play("ripper_death");
                else Game_Audio.play("wisp_death");
                score += enemy.getScoreValue() * levelScoreMultiplier;
                enemy.stopAnimation();
                entityLayer.getChildren().remove(enemy.getView());
                enemies.remove(i);
                killCount++;
                onEnemyCaptured();
                continue;
            }

            enemy.updatePosition();
            enemy.getView().setVisible(visibleByPower || insideScanner);
        }
    }

    // Plazer bir enemz'e carparsa health'ini dusurur
    private void playerDamage() {
        boolean overlapping = false;

        for (Enemy enemy : enemies) {
            if (Collisions.circleOverlap(
                    hunterBody.getCenterX(), hunterBody.getCenterY(), hunterBody.getRadius(),
                    enemy.getX(), enemy.getY(), enemy.getRadius())) {
                overlapping = true;

                // bu önemli çünkü eğer STEP_SECONDS ile çarpmassak sağlığımız her frame'de azalırdı ki bu çok fazla olurdu, bu yüzden sadece çarparak hasarın yönetilebilir olmasını sağlıyoruz
                currentHealth -= entityDamage * HEALTH_DRAIN_MULTIPLIER * levelDamageMultiplier * STEP_SECONDS;
            }
        }

        if (concertaSpeedRemaining > 0) {
            currentHealth = maximumHealth;
        } else if (currentHealth < 0) {
            currentHealth = 0;
        }

        isOverlapping = overlapping;
        if (overlapping == true) Game_Audio.startDamage();
        else Game_Audio.stopDamage();
    }

    // Player bir token'a degince efektini uygular
    private void collectTokens() {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            Token token = tokens.get(i);

            if (Collisions.circleOverlap(
                    hunterBody.getCenterX(), hunterBody.getCenterY(), hunterBody.getRadius(),
                    token.getX(), token.getY(), token.getRadius())) {
                token.apply(this);
                tokenLayer.getChildren().remove(token.getView());
                tokens.remove(i);
            }
        }
    }


    // Player'in rengini mevcut state'ine göre degistirir
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

    // HUD'u her frame'de gunceller
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
                    Game_Audio.playMusic(levelMusic);
                }
            }
        }
    }

    // Her 5 saniyede random token spawn eder, ayni anda max 2 token olabilir
    private void spawnToken() {
        if (levelEnded || tokens.size() >= 2) {
            return;
        }

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
        Game_Audio.play("token_spawn");
    }

    private void onEnemyCaptured() {
        if (levelNumber == 4) {
            spawner.spawnRandom();
            spawner.spawnRandom();
            return;
        }
        if (killCount % 5 == 0) {
            spawnTimeToken();
        }
        if (killCount % respawnKillThreshold == 0) {
            spawner.spawnRandom();
        }
    }

    // Time token spawn eder
    private void spawnTimeToken() {
        double x = randomInRange(levelAreaX + Token.RADIUS, levelAreaX + levelAreaWidth - Token.RADIUS);
        double y = randomInRange(levelAreaY + Token.RADIUS, levelAreaY + levelAreaHeight - Token.RADIUS);
        Token token = new Token_Time(x, y);
        tokens.add(token);
        tokenLayer.getChildren().add(token.getView());
        Game_Audio.play("token_spawn");
    }


    // Enemy'nin scanner triangle icinde olup olmadigini JavaFX intersect ile kontrol eder
    private boolean enemyInScanner(Enemy enemy) {
        if (!scanner.isVisible()) {
            return false;
        }

        //javafx'in içindeki intersect methodunu kullanarak kontrol ettim
        return scanner.getBoundsInParent().intersects(enemy.getView().getBoundsInParent());
    }

    // HUD'u guncel health, vacuum ve score degerleriyle update eder
    private void updateHud() {
        lvlHud.update(currentHealth, maximumHealth, currentVacuum, maximumVacuum, score, remainingSeconds);
    }

    // Win panelini gösterir, game loop'u durdurur

    private void showWinPanel() {
        if (levelEnded) return;
        levelEnded = true;
        stopGameLoop();
        lvlHud.setVisible(false);
        Game_Audio.playMusic("message_screen_music");

        Hud_Lvl_Complete overlay = new Hud_Lvl_Complete(SCENE_WIDTH, SCENE_HEIGHT, score, levelNumber, mainApp);
        getChildren().add(overlay);
        overlay.toFront();
    }

    // Lose panelini gösterir, game loop'u durdurur
    private void showLosePanel(String reason) {
        if (levelEnded) return;
        levelEnded = true;
        stopGameLoop();
        lvlHud.setVisible(false);
        Game_Audio.playMusic("message_screen_music");
        Game_Audio.play("game_over");

        Hud_Game_Over overlay = new Hud_Game_Over(SCENE_WIDTH, SCENE_HEIGHT, reason, score, levelNumber, mainApp, initialScore);
        getChildren().add(overlay);
        overlay.toFront();
    }

    // Level numarasina göre background image icin CSS string'i döner
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

    // Min ile max arasinda random double döner
    private double randomInRange(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    // Degeri min-max arasinda tutar, yani clamp islemi yapar
    private double stayBetween(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // Level numarasina göre config field'larini yukler
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
