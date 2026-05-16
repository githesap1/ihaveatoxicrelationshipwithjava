// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083 Akın_Selçuk_15015084 Mustafa_İshak_Yalçın_150125032
// Class: Enemy - tum enemy'ler icin abstract base class

package org.example;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public abstract class Enemy {
    public static final int GHOST = 0;
    public static final int RIPPER = 1;
    public static final int WISP = 2;

    private int type;
    private Group view;
    private Circle body;
    private ArrayList<Circle> details;
    private Color normalBodyColor;

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double baseRadius;
    private double radius;

    // Enemy'nin tum field'larini set eder
    protected Enemy(
            int type,
            Group view,
            Circle body,
            ArrayList<Circle> details,
            Color normalBodyColor,
            double x,
            double y,
            double vx,
            double vy,
            double baseRadius
    ) {
        this.type = type;
        this.view = view;
        this.body = body;
        this.details = details;
        this.normalBodyColor = normalBodyColor;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.baseRadius = baseRadius;
        this.radius = baseRadius;

    }

    // Group'u enemy pozisyonuna ve mevcut scale'ine göre yerlestirir
    public void updatePosition() {
        double scale = radius / baseRadius;
        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    // Scanner icindeyken enemy body ve detail'lerini griye boyar
    public void inZone() {
        body.setFill(Color.rgb(230, 230, 230, 0.90));
        for (Circle detail : details) {
            detail.setFill(Color.rgb(170, 170, 170, 0.92));
        }
    }

    // Enemy scanner disina cikinca normal rengine döner
    public abstract void outOfZone();

    // Bu enemy type'inin puan degerini döner
    public abstract int getScoreValue();

    // Animation varsa durdurur, yoksa bir sey yapmaz
    public void stopAnimation() {}

    public int getType() {
        return type;
    }

    public Group getView() {
        return view;
    }

    public void setView(Group view) {
        this.view = view;
    }

    public Circle getBody() {
        return body;
    }

    public ArrayList<Circle> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Circle> details) {
        this.details = details;
    }

    public Color getNormalBodyColor() {
        return normalBodyColor;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getBaseRadius() {
        return baseRadius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    // Verilen tzpe'ta random pozisyonda enemz olusturup döner
    public static Enemy spawn(int type, double areaX, double areaY, double areaW, double areaH, Random random) {

        if (type == GHOST) System.out.println("enemy spawned");
        double baseRadius, minSpeed, maxSpeed;
        Color bodyColor;

        switch (type) {
            case GHOST:
                baseRadius = 18; minSpeed = 70;  maxSpeed = 130;
                bodyColor = Color.web("#C0C0C0", 0.80); break;
            case RIPPER:
                baseRadius = 16; minSpeed = 110; maxSpeed = 170;
                bodyColor = Color.web("#FF4500", 0.88); break;
            case WISP:
                baseRadius = 24; minSpeed = 65;  maxSpeed = 120;
                bodyColor = Color.web("#1E3A8A", 0.75); break;
            default: throw new IllegalStateException("Unexpected enemy type");
        }

        double x = areaX + baseRadius + random.nextDouble() * (areaW - baseRadius * 2);
        double y = areaY + baseRadius + random.nextDouble() * (areaH - baseRadius * 2);
        double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
        double angle = random.nextDouble() * Math.PI * 2;

        Group view = new Group();
        Circle body = new Circle(0, 0, baseRadius);
        body.setFill(bodyColor);
        body.setStroke(Color.web("#1C1C1C"));

        ArrayList<Circle> details = new ArrayList<>();
        ArrayList<Circle> ghostBumps = new ArrayList<>();
        Polygon star = null;
        Group orbitGroup = null;

        if (type == GHOST) {
            // 3 tane küçük daire, ghost yuzunu oluşturmak için
            Circle bump1 = new Circle(-9, 15, 6, Color.web("#C0C0C0", 0.80));
            Circle bump2 = new Circle(0,  18, 6, Color.web("#C0C0C0", 0.80));
            Circle bump3 = new Circle(9,  15, 6, Color.web("#C0C0C0", 0.80));
            ghostBumps.add(bump1);
            ghostBumps.add(bump2);
            ghostBumps.add(bump3);

            Circle eye1 = new Circle(-5, -4, 2.5, Color.web("#C70039"));
            Circle eye2 = new Circle(5,  -4, 2.5, Color.web("#C70039"));
            details.add(eye1);
            details.add(eye2);

            view.getChildren().add(body);
            view.getChildren().addAll(ghostBumps);
            view.getChildren().addAll(details);

        } else if (type == RIPPER) {
            body.setRadius(baseRadius * 0.5);
            star = new Polygon();
            for (int j = 0; j < 16; j++) {
                double ang = (Math.PI / 8.0) * j - Math.PI / 2.0;
                double r = (j % 2 == 0) ? baseRadius : baseRadius * 0.5;
                star.getPoints().addAll(Math.cos(ang) * r, Math.sin(ang) * r);
            }
            star.setFill(bodyColor);
            star.setStroke(Color.web("#1C1C1C"));

            Circle eye1 = new Circle(-4, -3, 3, Color.web("#1C1C1C"));
            Circle eye2 = new Circle(4,  -3, 3, Color.web("#1C1C1C"));
            details.add(eye1);
            details.add(eye2);

            view.getChildren().add(star);
            view.getChildren().add(body);
            view.getChildren().addAll(details);

        } else { // WISP
            // 3 tane ucgen, wisp'in etrafinda dönecek sekilde
            orbitGroup = new Group();
            double orbitR   = baseRadius + 12;
            double tipOff   = 10;
            double baseOff  = 5;
            double halfBase = 6;
            for (int k = 0; k < 3; k++) {
                double ang  = Math.toRadians(k * 120);
                double cosA = Math.cos(ang);
                double sinA = Math.sin(ang);
                double tangX = -sinA; // tanjant yonu
                double tangY =  cosA;
                // ucgenleri elle yerlestirmeye calistim ama dönerken bozuluyordu, ai'a yaptirdim
                double cx = cosA * orbitR;
                double cy = sinA * orbitR;
                double tipX = cx + tangX * tipOff;
                double tipY = cy + tangY * tipOff;
                double b1X  = cx - tangX * baseOff + cosA * halfBase;
                double b1Y  = cy - tangY * baseOff + sinA * halfBase;
                double b2X  = cx - tangX * baseOff - cosA * halfBase;
                double b2Y  = cy - tangY * baseOff - sinA * halfBase;
                Polygon tri = new Polygon(tipX, tipY, b1X, b1Y, b2X, b2Y);
                tri.setFill(Color.web("#3B82F6"));
                orbitGroup.getChildren().add(tri);
            }

            Circle eye1 = new Circle(-6, -5, 4, Color.web("#F7F7F7"));
            Circle eye2 = new Circle(6,  -5, 4, Color.web("#F7F7F7"));
            Circle pupil1 = new Circle(-6, -5, 2, Color.web("#1C1C1C"));
            Circle pupil2 = new Circle(6,  -5, 2, Color.web("#1C1C1C"));
            details.add(eye1);
            details.add(eye2);

            view.getChildren().add(orbitGroup);
            view.getChildren().add(body);
            view.getChildren().addAll(eye1, eye2, pupil1, pupil2);
        }

        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setVisible(false);

        double vx = Math.cos(angle) * speed;
        double vy = Math.sin(angle) * speed;

        switch (type) {
            case GHOST:  return new Enemy_Ghost(view, body, details, ghostBumps, bodyColor, x, y, vx, vy, baseRadius);
            case RIPPER: return new Enemy_Ripper(view, body, details, bodyColor, star, x, y, vx, vy, baseRadius);
            case WISP:   return new Enemy_Wisp(view, body, details, bodyColor, orbitGroup, x, y, vx, vy, baseRadius);
            default: throw new IllegalStateException("Unexpected enemy type");
        }
    }
}
