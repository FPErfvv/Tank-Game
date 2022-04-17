
package app;

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
    }


    public void moveBackForth(int distance, int direction) {
        if (direction < 0) {
            distance /= 2;
        }
        double mouseHyp = Math.sqrt(Math.pow(m_mouseOpp, 2)+Math.pow(m_mouseAdjacent, 2));
        //uses the ratio btw the move distance and the hyp of the triangle created by the mouse and the character
        //to determine how far the character should be moved along the x and y axis
        double sideRatio = distance/mouseHyp; 
        double yTransform = m_mouseOpp * sideRatio  *-1;
        double xTransform = m_mouseAdjacent * sideRatio;
        double deltax = xTransform * direction;
        double deltay = yTransform * direction;
        m_currentMap.moveMap(-deltax, -deltay);
        move(-deltax, -deltay);    
    }

    public void moveSideways(int distance, int direction) {
        distance /= 3;

        double mouseHyp = Math.sqrt(Math.pow(m_mouseOpp, 2)+Math.pow(m_mouseAdjacent, 2));
        //uses the ratio btw the move distance and the hyp of the triangle created by the mouse and the character
        //to determine how far the character should be moved along the x and y axis
        double sideRatio = distance/mouseHyp; 
        double yTransform = m_mouseOpp * sideRatio;
        double xTransform = m_mouseAdjacent * sideRatio;
        double deltax = xTransform * direction;
        double deltay = yTransform * direction;
        m_currentMap.moveMap(deltax, deltay);
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
        Point pointOnScreen = MouseInfo.getPointerInfo().getLocation();
        Point framesPoint = m_mainPanel.getLocationOnScreen();

        double mouseX = pointOnScreen.getX() - framesPoint.getX();
        double mouseY = pointOnScreen.getY() - framesPoint.getY();

        m_mouseAdjacent = mouseX - getCoordinates().getX();
        m_mouseOpp = getCoordinates().getY() - mouseY;

        m_turnAngle = Math.atan(m_mouseOpp/m_mouseAdjacent);
        if (m_mouseAdjacent <= 0 && m_mouseOpp > 0) { // if in quadrant II
            m_turnAngle = Math.PI + m_turnAngle;
        } else if (m_mouseAdjacent < 0 && m_mouseOpp <= 0) { // if in quadrant III
            m_turnAngle = Math.PI + m_turnAngle;
        } else if (m_mouseAdjacent >= 0 && m_mouseOpp < 0) { // if in quadrant IV
            m_turnAngle = 2 * Math.PI + m_turnAngle;
        }
        TRUE_COOR.setLocation(MainPanel.frameWidth/2, MainPanel.frameHeight/2);
    }

    public void setCurrentMap(Map map) {
        m_currentMap = map;
    }

    public Point getCoordinates() {
        return TRUE_COOR;
    }

}