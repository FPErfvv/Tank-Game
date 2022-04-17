
package app;

import javax.management.RuntimeOperationsException;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class MainCharacter extends Prop {
    private boolean facingDown;
    private double m_mouseOpp;
    private double m_mouseAdjacent;
    private MainPanel m_mainPanel;
    public static final Point TRUE_COOR = new Point(MainPanel.frameWidth/2,MainPanel.frameHeight/2);
    
    public MainCharacter(Map map, MainPanel mainPanel) {

        super(new Point(200,200),"src/images/MainCowPic.png", map, null);
        //image = new ImageIcon("src/images/MainCowPic.png").getImage();
        m_mainPanel = mainPanel; 
        setMoving(true);
        setMainCharacter(this);
        m_velocity = 0;
        m_angularVelocity = 0;
    }


    public void moveBackForth() {
        double deltax = m_velocity * Math.cos(m_turnAngle % (Math.PI/2));
        double deltay = m_velocity * Math.sin(m_turnAngle % (Math.PI/2));
        System.out.println(Math.toDegrees(m_turnAngle % (Math.PI/2)));
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
        
        m_currentMap.moveMap(-deltax, deltay);
        move(deltax, deltay);    
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
        m_hitBox.createRectangle();
        m_hitBox.drawLines(g2d);
        m_hitBox.getContactPoints();
        m_hitBox.checkCollision(new Point[] {new Point()});
    }

    public int getWidth() {
        return m_image.getWidth(null);
    }

    public int getHeight() {
        return m_image.getHeight(null);
    }

    public void updateTurnAngle() {
        TRUE_COOR.setLocation(MainPanel.frameWidth/2, MainPanel.frameHeight/2);
    }

    public void setCurrentMap(Map map) {
        m_currentMap = map;
    }

    public Point getCoordinates() {
        return TRUE_COOR;
    }

}