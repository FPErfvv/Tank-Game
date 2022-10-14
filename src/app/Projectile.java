package app;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import app.hitbox.RectangleHitbox;

public class Projectile extends Sprite {
    
    private int projectileSpeed = 5;

    private GameMap map;

    public Projectile(GameMap map, Point2D p) {
        super(new Point2D.Double(p.getX(), p.getY()),"src/images/Bullet.png", 0, map);
        setHitbox(new RectangleHitbox(this));
        getHitbox().computeCollisionPoints(getRelativeCoordinates(), getTurnAngle());
        setSpeed(projectileSpeed);

        this.map = map;
    }
    /*
    @Override
    public void draw(Graphics2D g2d) {

        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the image

        tr.translate(0, 0);
        tr.rotate(
                -(getTurnAngle() - (Math.PI/2)),
                getImage().getWidth(null) / 2,
                getImage().getHeight(null) / 2
        );
        
        g2d.drawImage(getImage(), tr, null);

    }
*/
}
