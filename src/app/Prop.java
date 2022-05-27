package app;


import java.awt.Rectangle;

import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

public class Prop {
    protected Point m_coor;
    protected Point m_trueCoor;
    protected HitBox m_hitBox;
    protected double m_turnAngle; // radians
    protected Image m_image;
    protected MainCharacter m_mainCharacter;
    protected boolean m_moving;
    protected boolean m_turning;
    protected boolean m_changingDirection;
    protected Map m_currentMap;
    protected double m_speed;
    protected double m_angularVelocity;
    protected static final double DEFAULT_ANGULAR_VELOCITY = 5;
    public boolean m_isMainCharacter;


    public Prop(Point coor, String imagePath, Map map, MainCharacter mainCharacter,boolean isMainCharacter) {
        m_isMainCharacter = isMainCharacter;
        m_changingDirection = false;
        m_turning = false;
        m_angularVelocity = DEFAULT_ANGULAR_VELOCITY;
        m_coor = coor;
        m_trueCoor = coor;
        m_image = new ImageIcon(imagePath).getImage();
        m_moving = false;
        m_currentMap = map;
        m_mainCharacter = mainCharacter;
        m_hitBox = new HitBox(this, 1);
        m_hitBox.createRectangle(m_trueCoor, m_turnAngle);
    }

    public Prop(Point coor, String imagePath, double m_turnAngle, Map map, MainCharacter mainCharacter, boolean isMainCharacter) {
        m_isMainCharacter = isMainCharacter;
        m_changingDirection = false;
        m_turning = false;
        m_angularVelocity = DEFAULT_ANGULAR_VELOCITY;
        m_coor = coor;
        m_trueCoor = coor;
        this.m_turnAngle = m_turnAngle;
        m_image = new ImageIcon(imagePath).getImage();
        m_moving = false;
        m_currentMap = map;
        m_mainCharacter = mainCharacter;
        m_hitBox = new HitBox(this, 1);
        m_hitBox.createRectangle(m_trueCoor, m_turnAngle);
    }

    public void initialize() {
        m_trueCoor = Constants.addPoints(m_mainCharacter.getTrueCoordinates(), m_trueCoor);
    }

    public void draw(Graphics2D g2d) {

        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the m_image
        // the main character is used as the origin-(0,0)
        // this means that when the page is resized, all the props remain the same distance from the character
        
        tr.translate(m_mainCharacter.getTrueCoordinates().getX()+ m_trueCoor.getX()- getWidth()/2, m_mainCharacter.getTrueCoordinates().getY()+m_trueCoor.getY()- getHeight()/2);

        tr.rotate(
                -(m_turnAngle - (Math.PI/2)),
                m_image.getWidth(null) / 2,
                m_image.getHeight(null) / 2
        );
        g2d.drawImage(m_image, tr, null);
        
        m_hitBox.createRectangle(m_coor, m_turnAngle);
        m_hitBox.drawLines(g2d);
    }

    public void translate(double deltax, double deltay) {
        m_coor.setLocation(m_coor.getX() + deltax, m_coor.getY() + deltay);
        m_trueCoor.setLocation(m_trueCoor.getX() + deltax, m_trueCoor.getY() + deltay);
    }

    public void move(int distance) {
        double deltax = distance * Math.cos(m_turnAngle % (Math.PI/2));
        double deltay = distance * Math.sin(m_turnAngle % (Math.PI/2));
        if (m_turnAngle >= (Math.PI/2) && m_turnAngle < Math.PI) { // quadrant II
            deltax = distance * Math.cos((Math.PI /2) - (m_turnAngle % (Math.PI/2)));
            deltay = distance * Math.sin((Math.PI/2) - (m_turnAngle % (Math.PI/2)));
            deltax = -deltax;
        } else if (m_turnAngle >= (Math.PI) && m_turnAngle < (3 * Math.PI / 2)) { // quadrant III
            deltax = -deltax;
            deltay = -deltay;
        } else if (m_turnAngle >= (3 * Math.PI / 2) && m_turnAngle < (2 *Math.PI)) { // quadrant IV
            deltax = distance * Math.cos((Math.PI/2)- (m_turnAngle % (Math.PI/2)));
            deltay = distance * Math.sin((Math.PI/2)- (m_turnAngle % (Math.PI/2)));
            deltay = -deltay;
        }
        
        translate(deltax, deltay);    
    }

    public void moveWithMap(double deltax, double deltay) {
        m_trueCoor.setLocation(m_trueCoor.getX() + deltax, m_trueCoor.getY() + deltay);
    }

    public void setMoving(boolean moving) {
        m_moving = moving;
    }
    
    // returns the location of the object
    public Point getTrueCoordinates() {
        return m_trueCoor;
    }

    public Point getRelativeCoordinates() {
        return m_coor;
    }

    public void rotate() {
        if (m_turning) {
            m_turnAngle += Math.toRadians(m_angularVelocity);
            if (m_turnAngle >= 2 * Math.PI) {
                m_turnAngle -= 2 * Math.PI;
            }
            if (m_turnAngle < 0) {
                m_turnAngle += 2*Math.PI;
            }
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

    public void setAngularVelocity(double angularVelocity) {
        m_angularVelocity = angularVelocity;
    }

    public void startTurning(boolean turning, int direction) {
        // This boolean determines if the direction stated by "dirction" is the same as the current direction
        // This prevents the character from spinning for ever with out stopping. There are no limits
        // on the setting of the values of the method is the same direction as current direction.
        boolean sameDirection = false;
        if (Math.abs(m_angularVelocity)/m_angularVelocity == direction) {
            sameDirection = true;
        }
        
        if (sameDirection) {
            m_turning = turning;
            m_angularVelocity = Math.abs(m_angularVelocity) * direction;
        } else if (m_turning && !turning) {
            // If the character is currently turning, and it is being asked to stop turning, then the command is ignored
        } else {
            m_turning = turning;
            m_angularVelocity = Math.abs(m_angularVelocity) * direction;
        }
    }

    public double getSpeed() {
        return m_speed;
    }

    public void setSpeed(double speed) {
        m_speed = speed;
    }

    public int getWidth() {
        return m_image.getWidth(null);
    }

    public int getHeight() {
        return m_image.getHeight(null);
    }

    public HitBox getHitBox() {
        return m_hitBox;
    }


}
