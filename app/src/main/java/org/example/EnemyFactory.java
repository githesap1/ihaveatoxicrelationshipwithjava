// CSE 1242 - Introduction to Programming II - Term Project
// Author(s): [Ad Soyad] - [Ogrenci No]
//
// EnemyFactory.java
// Static factory class for creating Enemy and Token objects with their JavaFX shapes.
// Keeps all shape-building code out of GamePane to keep it readable.

package org.example;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class EnemyFactory {

    public static final double TOKEN_RADIUS = 14.0;

    // creates an enemy of the given type at a random position within the level area
    public static Enemy spawnEnemy(int type, double levelAreaX, double levelAreaY,
                                    double levelAreaWidth, double levelAreaHeight, Random random) {
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

        double x = randomInRange(levelAreaX + baseRadius, levelAreaX + levelAreaWidth - baseRadius, random);
        double y = randomInRange(levelAreaY + baseRadius, levelAreaY + levelAreaHeight - baseRadius, random);
        double speed = randomInRange(minSpeed, maxSpeed, random);
        double angle = randomInRange(0, Math.PI * 2, random);

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

            view.getChildren().add(body);
            view.getChildren().addAll(details);

        } else if (type == Enemy.RIPPER) {
            // ripper's body circle acts as the inner part, make it smaller
            body.setRadius(baseRadius * 0.5);

            // build the spiky star shape with 8 outer points
            Polygon star = new Polygon();
            for (int j = 0; j < 16; j++) {
                double ang = (Math.PI / 8.0) * j - Math.PI / 2.0;
                double r = (j % 2 == 0) ? baseRadius : baseRadius * 0.5;
                star.getPoints().addAll(Math.cos(ang) * r, Math.sin(ang) * r);
            }
            star.setFill(bodyColor);
            star.setStroke(Color.BLACK);

            // two eyes on the inner circle
            Circle eye1 = new Circle(-4, -3, 3, Color.BLACK);
            Circle eye2 = new Circle(4, -3, 3, Color.BLACK);
            details.add(eye1);
            details.add(eye2);

            // star goes behind, body (inner circle) and eyes on top
            view.getChildren().add(star);
            view.getChildren().add(body);
            view.getChildren().addAll(details);

        } else { // WISP
            // two inner concentric circles for the glow effect
            Circle inner1 = new Circle(0, 0, baseRadius * 0.65, Color.rgb(180, 250, 255, 0.9));
            Circle inner2 = new Circle(0, 0, baseRadius * 0.35, Color.rgb(220, 255, 255, 1.0));
            details.add(inner1);
            details.add(inner2);

            // four small rectangles that spin around the wisp body
            Group revolvers = new Group();
            double rLen = baseRadius * 0.55;
            double rThick = 5;
            double dist = baseRadius * 1.3;
            Rectangle rect1 = new Rectangle(-dist - rLen / 2, -rThick / 2, rLen, rThick);
            rect1.setFill(Color.rgb(130, 240, 255, 0.75));
            Rectangle rect2 = new Rectangle(dist - rLen / 2, -rThick / 2, rLen, rThick);
            rect2.setFill(Color.rgb(130, 240, 255, 0.75));
            Rectangle rect3 = new Rectangle(-rThick / 2, -dist - rLen / 2, rThick, rLen);
            rect3.setFill(Color.rgb(130, 240, 255, 0.75));
            Rectangle rect4 = new Rectangle(-rThick / 2, dist - rLen / 2, rThick, rLen);
            rect4.setFill(Color.rgb(130, 240, 255, 0.75));
            revolvers.getChildren().addAll(rect1, rect2, rect3, rect4);

            RotateTransition rot = new RotateTransition(Duration.seconds(2.5), revolvers);
            rot.setByAngle(360);
            rot.setCycleCount(Animation.INDEFINITE);
            rot.play();

            view.getChildren().add(revolvers);
            view.getChildren().add(body);
            view.getChildren().addAll(details);
        }

        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setVisible(false);

        double vx = Math.cos(angle) * speed;
        double vy = Math.sin(angle) * speed;

        switch (type) {
            case Enemy.GHOST:
                return new Ghost(view, body, details, bodyColor, x, y, vx, vy, baseRadius);
            case Enemy.RIPPER:
                return new Ripper(view, body, details, bodyColor, x, y, vx, vy, baseRadius);
            case Enemy.WISP:
                return new Wisp(view, body, details, bodyColor, x, y, vx, vy, baseRadius);
            default:
                throw new IllegalStateException("Unexpected enemy type");
        }
    }

    // creates a token with a JavaFX shape based on its type and places it at (x, y)
    public static Token spawnToken(int type, double x, double y) {
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

    private static double randomInRange(double min, double max, Random random) {
        return min + random.nextDouble() * (max - min);
    }
}
