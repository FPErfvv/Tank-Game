
package app;

import javax.management.RuntimeOperationsException;
import javax.swing.*;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MainCharacter extends Prop {
    private boolean facingDown;
    private double m_mouseOpp;
    private double m_mouseAdjacent;
    private MainPanel m_mainPanel;
    public static final Point TRUE_COOR = new Point(0,0);
    private ArrayList<Byte> turningBuffer; 
    
    public MainCharacter(Map map, MainPanel mainPanel) {
        super(new Point(0, 0),"src/images/MainCowPic.png", map, null, true);
        //image = new ImageIcon("src/images/MainCowPic.png").getImage();
        turningBuffer = new ArrayList<Byte>();
        m_mainPanel = mainPanel; 
        setMoving(true);
        setMainCharacter(this);
        m_speed = 0;
    }


    public void moveBackForth() {
        Point velocity = new Point();
        velocity.x = m_speed * Math.cos(m_turnAngle % (Math.PI/2));
        velocity.y = m_speed * Math.sin(m_turnAngle % (Math.PI/2));
        if (m_turnAngle >= (Math.PI/2) && m_turnAngle < Math.PI) { // quadrant II
            velocity.x = m_speed * Math.cos((Math.PI /2) - (m_turnAngle % (Math.PI/2)));
            velocity.y = m_speed * Math.sin((Math.PI/2) - (m_turnAngle % (Math.PI/2)));
            velocity.x = -velocity.x;
        } else if (m_turnAngle >= (Math.PI) && m_turnAngle < (3 * Math.PI / 2)) { // quadrant III
            velocity.x = -velocity.x;
            velocity.y = -velocity.y;
        } else if (m_turnAngle >= (3 * Math.PI / 2) && m_turnAngle < (2 *Math.PI)) { // quadrant IV
            velocity.x = m_speed * Math.cos((Math.PI/2)- (m_turnAngle % (Math.PI/2)));
            velocity.y = m_speed * Math.sin((Math.PI/2)- (m_turnAngle % (Math.PI/2)));
            velocity.y = -velocity.y;
        }

        double futureAngle = m_turnAngle;
        if (m_turning) {
            
            //Point mtv = new Point(0,0);
            futureAngle += Math.toRadians(m_angularVelocity);
            if (futureAngle >= 2 * Math.PI) {
                futureAngle -= 2 * Math.PI;
            }
            if (futureAngle < 0) {
                futureAngle += 2*Math.PI;
            }
        }

        m_hitBox.createRectangle(Constants.addPoints(m_coor, new Point(velocity.x, -velocity.y)), futureAngle);
        Point mtv = m_mainCharacter.getHitBox().SAT(m_currentMap.getPropList().get(0).getHitBox().getCollisionPoints(), m_currentMap.getPropList().get(0).getRelativeCoordinates(), Constants.addPoints(m_coor, new Point(velocity.x, -velocity.y)));
        System.out.println(mtv);
        if (mtv.getMagnitude() <= .1 && mtv.getMagnitude() >= -.1) {
            m_currentMap.moveMap(-velocity.x, velocity.y);
            translate(velocity.x, -velocity.y); 
            if (m_turning) {
                m_turnAngle = futureAngle;
                m_hitBox.setTurnAngle();
            } 
         } 
        else {

            // Point normalized = Constants.normalize(velocity);
            // double sidleUpDistance = -1;
            // if (mtv.getMagnitude() != 0 || velocity.getMagnitude() != 0) {
            //     // This finds the angle between the velocity vector and the mtv vector. 
            //     // Using that, it finds how far the cow needs to move in the direction of the velocity vector to move the magnitude of the mtv vector
            //     // cos(α) = a · b / (|a| * |b|)
            //     // h = adj / cos(α)
            //     // h = adj / a · b / (|a| * |b|)
            //     sidleUpDistance = Math.abs(mtv.getMagnitude()/(mtv.vectorDotProduct(velocity) / (mtv.getMagnitude() * velocity.getMagnitude())));
            // }
            // if (sidleUpDistance >= 0) {
            //     System.out.println(sidleUpDistance + " " + mtv);
            //     Point sidleUpVector = new Point(Math.abs(velocity.x) - Math.abs(normalized.getX() * sidleUpDistance -.1), Math.abs(velocity.y) - Math.abs(normalized.getY() * sidleUpDistance -.1));
            //     m_currentMap.moveMap(-sidleUpVector.getX(), sidleUpVector.getY());
            //     translate(sidleUpVector.getX(), -sidleUpVector.getY()); 

            // }
            if (m_turning) {
                m_turnAngle = futureAngle;
                m_hitBox.setTurnAngle();
            } 

        }
           
    }

    @Override
    public void translate(double deltaX, double deltaY) {
        m_coor.setLocation(m_coor.getX() + deltaX, m_coor.getY() + deltaY);
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
        m_hitBox.drawLines(g2d);
        
    }

    public int getWidth() {
        return m_image.getWidth(null);
    }

    public int getHeight() {
        return m_image.getHeight(null);
    }

    public void setCurrentMap(Map map) {
        m_currentMap = map;
    }

    @Override
    public Point getTrueCoordinates() {
        return TRUE_COOR;
    }

}