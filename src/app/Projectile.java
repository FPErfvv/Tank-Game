package app;

import java.awt.geom.Point2D;

import app.gameElements.Sprite;
import app.gameElements.hitbox.hitboxSubClasses.RectangleHitbox;

public class Projectile extends Sprite {
    private int projectileSpeed = 5;

    public Projectile(Point2D.Double p, double angle, GameMap map) {
        super("src/images/Bullet.png", p, angle, map);
        setHitbox(new RectangleHitbox(getWidth(), getHeight()));
    }
}
