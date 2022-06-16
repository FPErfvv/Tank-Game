
package app;

import javax.management.RuntimeOperationsException;
import javax.swing.*;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import java.util.ArrayList;

public class MainCharacter extends Sprite {

    public static final Point2D.Double TRUE_COOR = new Point2D.Double(0,0); 
    
    public MainCharacter(GameMap map) {
        super(new Point2D.Double(0, 0),"src/images/MainCharacter.png", map, null, true, Constants.RECTANGLE);
        setMoving(true);
        setMainCharacter(this);
        m_speed = 0;
    }

    @Override
    public void move() {
        super.move();

        double futureAngle = m_turnAngle;
        if (m_turning) {
            futureAngle += Math.toRadians(m_rotationalSpeed);
            if (futureAngle >= 2 * Math.PI) {
                futureAngle -= 2 * Math.PI;
            }
            if (futureAngle < 0) {
                futureAngle += 2*Math.PI;
            }
        }
        Point2D.Double futureCoor = Utility.addPoints(m_coor, new Point2D.Double(m_velocity.x, -m_velocity.y)); 
        // A hitbox is created where the main character will be in the future, given the current m_velocity and angle
        m_hitBox.createHitbox(futureCoor, futureAngle);
        // This hitbox is then used to detect if the main character will collide with anything at that future position
        Point2D.Double mtv = new Point2D.Double(0,0);

        Point2D.Double closestTargetsCoor = new Point2D.Double(0,0);
        for (Sprite t: m_currentMap.getSpriteList()) {
            Point2D.Double tempMtv = this.getHitBox().SAT(t.getHitBox().getCollisionPoints(), t.getRelativeCoordinates(), futureCoor);
            if (Utility.getVectorMagnitude(tempMtv) != 0) {
                mtv = new Point2D.Double(tempMtv.getX(), tempMtv.getY());
                closestTargetsCoor = t.getRelativeCoordinates();
            }
        }
    
        if (m_turning) {
            m_turnAngle = futureAngle;
        } 

        // If there is no collision, the character is moved like normal

        if (Utility.getVectorMagnitude(mtv) > 2 ) { // If there is a collision, the m_velocity is adjusted to move the character right up next to the object
                // This is the default value for the distance used to move right up against the target
                double sidleUpDistance = Utility.getVectorMagnitude(mtv) -1 ;
                // This finds the angle between the m_velocity vector and the mtv vector. 
                // Using that, it finds how far the character needs to move in the direction of the m_velocity vector to move the magnitude of the mtv vector
                // cos(α) = a · b / (|a| * |b|)
                // h = adj / cos(α)
                // h = adj / a · b / (|a| * |b|)
                if (Utility.getVectorMagnitude(m_velocity) != 0) {
                    sidleUpDistance = Math.abs(Utility.getVectorMagnitude(mtv)/(Utility.vectorDotProduct(mtv,m_velocity) / (Utility.getVectorMagnitude(mtv) * Utility.getVectorMagnitude(m_velocity))))-1;
                }
                // This limits the translation to five pixels so that large jumps are not experienced.
                if (sidleUpDistance > Utility.getVectorMagnitude(m_velocity) + 2) {
                    sidleUpDistance = Utility.getVectorMagnitude(m_velocity) + 2;
                }

                int closestPoint = this.getHitBox().getClosestSide(closestTargetsCoor);
                // If the front of the MainCharacter is closest to the center of the target, the sidleUpDistance is subtracted from the m_velocity
                if (closestPoint == Constants.FRONT) {
                    m_velocity = new Point2D.Double(m_velocity.x - Math.cos(m_turnAngle) * sidleUpDistance, m_velocity.y - Math.sin(m_turnAngle) * sidleUpDistance);
                } else { // If the back of the MainCharacter is closest to the center of the target, the sidleUpDistance is added to the m_velocity
                    m_velocity = new Point2D.Double(m_velocity.x - Math.cos(m_turnAngle) * -sidleUpDistance, m_velocity.y - Math.sin(m_turnAngle) * -sidleUpDistance);
                }
        }

    }

    @Override
    public void translate(Point2D.Double vel) {
        m_coor.setLocation(m_coor.getX() + vel.x, m_coor.getY() + vel.y);
    }
    
    @Override
    public void periodic() {
        m_currentMap.moveMap(-m_velocity.getX(), m_velocity.getY());
        translate(new Point2D.Double(m_velocity.getX(), -m_velocity.getY())); 
    }



    @Override
    public void draw(Graphics2D g2d) {

        
        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the image
        tr.translate(TRUE_COOR.getX() - getWidth()/2, TRUE_COOR.getY() - getHeight()/2);
        tr.rotate(
                -(m_turnAngle - (Math.PI/2)),
                m_image.getWidth(null) / 2,
                m_image.getHeight(null) / 2
        );
        g2d.drawImage(m_image, tr, null);
    }

    public int getWidth() {
        return m_image.getWidth(null);
    }

    public int getHeight() {
        return m_image.getHeight(null);
    }

    public void setCurrentMap(GameMap map) {
        m_currentMap = map;
    }

    @Override
    public Point2D.Double getTrueCoordinates() {
        return TRUE_COOR;
    }

}