package app;

import java.awt.geom.Point2D;

import app.hitbox.RectangleHitbox;

public class Projectile extends Sprite {
    
    private int projectileSpeed = 5;

    public Projectile(Point2D.Double p, double angle, GameMap map) {
        super(p,"src/images/Bullet.png", angle, map);
        setHitbox(new RectangleHitbox(getWidth(),getHeight()));
        //setSpeed(projectileSpeed);
    }

}
