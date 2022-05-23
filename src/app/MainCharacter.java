
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
        m_velocity = 0;
    }


    public void moveBackForth() {
        
        double deltax = m_velocity * Math.cos(m_turnAngle % (Math.PI/2));
        double deltay = m_velocity * Math.sin(m_turnAngle % (Math.PI/2));
        if (m_turnAngle >= (Math.PI/2) && m_turnAngle < Math.PI) { // quadrant II
            deltax = m_velocity * Math.cos((Math.PI /2) - (m_turnAngle % (Math.PI/2)));
            deltay = m_velocity * Math.sin((Math.PI/2) - (m_turnAngle % (Math.PI/2)));
            deltax = -deltax;
        } else if (m_turnAngle >= (Math.PI) && m_turnAngle < (3 * Math.PI / 2)) { // quadrant III
            deltax = -deltax;
            deltay = -deltay;
        } else if (m_turnAngle >= (3 * Math.PI / 2) && m_turnAngle < (2 *Math.PI)) { // quadrant IV
            deltax = m_velocity * Math.cos((Math.PI/2)- (m_turnAngle % (Math.PI/2)));
            deltay = m_velocity * Math.sin((Math.PI/2)- (m_turnAngle % (Math.PI/2)));
            deltay = -deltay;
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

        m_hitBox.createRectangle(Constants.addPoints(m_coor, new Point(deltax, -deltay)), futureAngle);
        Point mtv = m_mainCharacter.getHitBox().SAT(m_currentMap.getPropList().get(0).getHitBox().getCollisionPoints(), m_currentMap.getPropList().get(0).getRelativeCoordinates(), Constants.addPoints(m_coor, new Point(deltax, -deltay)));
        if (mtv.getX() == 0) {
            m_currentMap.moveMap(-deltax, deltay);
            translate(deltax, -deltay); 
            if (m_turning) {
                m_turnAngle = futureAngle;
                m_hitBox.setTurnAngle();
            }
        }
           
    }

    @Override
    public void translate(double deltax, double deltay) {
        m_coor.setLocation(m_coor.getX() + deltax, m_coor.getY() + deltay);
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
    @Override
    public void rotate() {
        
        // if (m_turning) {
            
        //     //Point mtv = new Point(0,0);
        //     double futureAngle = m_turnAngle;
        //     futureAngle += Math.toRadians(m_angularVelocity);
        //     if (futureAngle >= 2 * Math.PI) {
        //         futureAngle -= 2 * Math.PI;
        //     }
        //     if (futureAngle < 0) {
        //         futureAngle += 2*Math.PI;
        //     }
        //     m_hitBox.createRectangle(m_coor, futureAngle);
        //     Point mtv = getHitBox().SAT(m_currentMap.getPropList().get(0).getHitBox().getCollisionPoints(), m_currentMap.getPropList().get(0).getRelativeCoordinates(), m_coor);
        //     if (mtv.getX() == 0) {
        //         m_turnAngle = futureAngle;
        //         m_hitBox.setTurnAngle();
        //     }
        // }
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