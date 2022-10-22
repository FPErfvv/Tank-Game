package app;

import java.awt.geom.Point2D;

import app.hitbox.RectangleHitbox;

public class Projectile extends Sprite {
    
    private int projectileSpeed = 5;

    public Projectile(GameMap map, Point2D.Double p, double angle) {
        super(new Point2D.Double(p.getX(), p.getY()),"src/images/Bullet.png", angle, map);
        setHitbox(new RectangleHitbox(getWidth(),getHeight()));
        //setSpeed(projectileSpeed);
    }

}
