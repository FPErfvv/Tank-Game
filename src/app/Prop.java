package app;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

public class Prop {
    protected Point m_coor;
    protected HitBox m_hitBox;
    protected double m_turnAngle; // radians
    protected Image m_image;
    protected MainCharacter m_mainCharacter;
    protected boolean m_moving;
    protected Map m_currentMap;
    protected double m_velocity;
    protected double m_angularVelocity;


    public Prop(Point coor, String imagePath, Map map, MainCharacter mainCharacter) {
        m_coor = coor;
        m_image = new ImageIcon(imagePath).getImage();
        m_moving = false;
        m_currentMap = map;
        m_mainCharacter = mainCharacter;
        m_hitBox = new HitBox(this, 1);
    }

    public Prop(Point coor, String imagePath, double m_turnAngle, Map map, MainCharacter mainCharacter) {
        m_coor = coor;
        this.m_turnAngle = m_turnAngle;
        m_image = new ImageIcon(imagePath).getImage();
        m_moving = false;
        m_currentMap = map;
        m_mainCharacter = mainCharacter;
        m_hitBox = new HitBox(this, 1);
    }

    public void draw(Graphics2D g2d) {

        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the m_image
        // the main character is used as the origin-(0,0)
        // this means that when the page is resized, all the props remain the same distance from the character
        tr.translate(m_mainCharacter.getCoordinates().getX() + m_coor.getX()- getWidth()/2, m_mainCharacter.getCoordinates().getY() + m_coor.getY()- getHeight()/2);

        tr.rotate(
                -(m_turnAngle - (Math.PI/2)),
                m_image.getWidth(null) / 2,
                m_image.getHeight(null) / 2
        );
        g2d.drawImage(m_image, tr, null);
        
        m_hitBox.createRectangle();
        m_hitBox.drawLines(g2d);
    }

    public void move(double deltax, double deltay) {
        m_coor.setLocation(m_coor.getX() + deltax, m_coor.getY() + deltay);
    }

    public void setMoving(boolean moving) {
        m_moving = moving;
    }

    // returns the location of the object
    public Point getCoordinates() {
        return new Point((int)(m_coor.getX() + m_mainCharacter.getCoordinates().getX()), (int)(m_coor.getY() + m_mainCharacter.getCoordinates().getY()));
    }

    public Point getRelativeCoordinates() {
        return new Point((int)(m_coor.getX()), (int)(m_coor.getY()));
    }

    public void rotate() {
        m_turnAngle += Math.toRadians(m_angularVelocity);
        if (m_turnAngle >= 2 * Math.PI) {
            m_turnAngle -= 2 * Math.PI;
        }
        if (m_turnAngle < 0) {
            m_turnAngle += 2*Math.PI;
        }
    }

    // sets the coordinates of the object
    public void moveTo(Point newCoor) {
        m_coor = newCoor;
    }
    // sets the props character to a certain m_image
    public void setImage(String imagePath) {
        m_image = new ImageIcon(imagePath).getImage();
    }

    public void setMainCharacter(MainCharacter m_mainCharacter) {
        this.m_mainCharacter = m_mainCharacter;
    }

    public void setTurnAngle(double m_turnAngle) {
        this.m_turnAngle = m_turnAngle;
    }

    public double getTurnAngle() {
        return m_turnAngle;
    }

    public double getAngularVelocity() {
        return m_angularVelocity;
    }

    public void setAngularVelocity(double rotationSpeed) {
        m_angularVelocity = rotationSpeed;
    }

    public double getVelocity() {
        return m_velocity;
    }

    public void setVelocity(double velocity) {
        m_velocity = velocity;
    }

    public int getWidth() {
        return m_image.getWidth(null);
    }

    public int getHeight() {
        return m_image.getHeight(null);
    }


}
