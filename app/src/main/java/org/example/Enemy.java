// CSE 1242 - Term Project
// Ogrenci: [Ad Soyad] - [Ogrenci No]
// Enemy.java - tum paranormal varliklar icin soyut taban sinifi

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

    public void updatePosition() {
        double scale = radius / baseRadius;
        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    // scanner icindeyken griye boyar
    public void inZone() {
        body.setFill(Color.rgb(230, 230, 230, 0.90));
        for (Circle detail : details) {
            detail.setFill(Color.rgb(170, 170, 170, 0.92));
        }
    }

    public abstract void outOfZone();

    public abstract int getScoreValue();

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

    // enemy olusturur, rastgele konuma yerlestirir
    public static Enemy spawn(int type, double areaX, double areaY,
                              double areaW, double areaH, Random random) {
        double baseRadius, minSpeed, maxSpeed;
        Color bodyColor;

        switch (type) {
            case GHOST:
                baseRadius = 18; minSpeed = 70;  maxSpeed = 130;
                bodyColor = Color.rgb(255, 255, 255, 0.80); break;
            case RIPPER:
                baseRadius = 16; minSpeed = 110; maxSpeed = 170;
                bodyColor = Color.rgb(176, 85, 255, 0.88); break;
            case WISP:
                baseRadius = 24; minSpeed = 65;  maxSpeed = 120;
                bodyColor = Color.rgb(130, 240, 255, 0.75); break;
            default: throw new IllegalStateException("Unexpected enemy type");
        }

        double x = areaX + baseRadius + random.nextDouble() * (areaW - baseRadius * 2);
        double y = areaY + baseRadius + random.nextDouble() * (areaH - baseRadius * 2);
        double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
        double angle = random.nextDouble() * Math.PI * 2;

        Group view = new Group();
        Circle body = new Circle(0, 0, baseRadius);
        body.setFill(bodyColor);
        body.setStroke(Color.BLACK);

        ArrayList<Circle> details = new ArrayList<>();
        Polygon star = null;
        Group revolvers = null;

        if (type == GHOST) {
            Circle eye1 = new Circle(-5, -4, 2.5, Color.BLACK);
            Circle eye2 = new Circle(5, -4, 2.5, Color.BLACK);
            details.add(eye1);
            details.add(eye2);
            view.getChildren().add(body);
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
            star.setStroke(Color.BLACK);
            Circle eye1 = new Circle(-4, -3, 3, Color.BLACK);
            Circle eye2 = new Circle(4, -3, 3, Color.BLACK);
            details.add(eye1);
            details.add(eye2);
            view.getChildren().add(star);
            view.getChildren().add(body);
            view.getChildren().addAll(details);

        } else { // WISP
            Circle eye1 = new Circle(-6, -5, 4, Color.WHITE);
            Circle eye2 = new Circle(6, -5, 4, Color.WHITE);
            Circle pupil1 = new Circle(-6, -5, 2, Color.BLACK);
            Circle pupil2 = new Circle(6, -5, 2, Color.BLACK);
            details.add(eye1);
            details.add(eye2);
            revolvers = new Group();
            view.getChildren().add(body);
            view.getChildren().addAll(eye1, eye2, pupil1, pupil2);
        }

        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setVisible(false);

        double vx = Math.cos(angle) * speed;
        double vy = Math.sin(angle) * speed;

        switch (type) {
            case GHOST:  return new Enemy_Ghost(view, body, details, bodyColor, x, y, vx, vy, baseRadius);
            case RIPPER: return new Enemy_Ripper(view, body, details, bodyColor, star, x, y, vx, vy, baseRadius);
            case WISP:   return new Enemy_Wisp(view, body, details, bodyColor, revolvers, x, y, vx, vy, baseRadius);
            default: throw new IllegalStateException("Unexpected enemy type");
        }
    }
}
