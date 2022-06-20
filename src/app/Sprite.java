package app;

import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


public class Sprite {
    private Point2D.Double m_coor;
    private Point2D.Double m_trueCoor;
    private HitBox m_hitBox;
    private double m_turnAngle; // radians
    private Image m_image;
    private boolean m_turning;
    private GameMap m_currentMap;
    private double m_speed;
    private Point2D.Double m_velocity;
    private double m_rotationalSpeed;
    private static final double DEFAULT_ROTATIONAL_SPEED = 5;


    public Sprite(Point2D.Double coor, String imagePath, GameMap map, int hitboxType) {
        m_turning = false;
        m_rotationalSpeed = DEFAULT_ROTATIONAL_SPEED;
        m_coor = coor;
        m_trueCoor = coor;
        m_image = new ImageIcon(imagePath).getImage();
        m_currentMap = map;
        m_velocity = new Point2D.Double(0,0);
        m_hitBox = new HitBox(this, hitboxType);
        m_hitBox.createHitbox(m_coor, m_turnAngle);
    }

    public Sprite(Point2D.Double coor, String imagePath, double m_turnAngle, GameMap map, int hitboxType) {
        m_turning = false;
        m_rotationalSpeed = DEFAULT_ROTATIONAL_SPEED;
        m_coor = coor;
        m_trueCoor = new Point2D.Double(m_coor.x + map.getOffset().getX(), m_coor.y + map.getOffset().getY());
        this.m_turnAngle = m_turnAngle;
        m_image = new ImageIcon(imagePath).getImage();
        m_currentMap = map;
        m_velocity = new Point2D.Double(0,0);
        m_hitBox = new HitBox(this, hitboxType);
        m_hitBox.createHitbox(m_coor, m_turnAngle);
    }

    public void draw(Graphics2D g2d) {

        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the m_image
        // the main character is used as the origin-(0,0)
        // this means that when the page is resized, all the sprites remain the same m_speed from the character
        
        tr.translate(m_trueCoor.getX() - getWidth()/2,m_trueCoor.getY() - getHeight()/2);

        tr.rotate(
                -(m_turnAngle - (Math.PI/2)),
                m_image.getWidth(null) / 2,
                m_image.getHeight(null) / 2
        );
        g2d.drawImage(m_image, tr, null);
        
        m_hitBox.createHitbox(m_coor, m_turnAngle);
        //m_hitBox.drawHitBox(g2d);
    }

    public void translate(Point2D.Double vel) {
        m_coor = Utility.addPoints(m_coor, vel);
        m_trueCoor = Utility.addPoints(m_trueCoor, vel);
    }

    public static Point2D.Double computeMovement(double angleRad, double speed)
    {
    	double x = Math.cos(angleRad);
    	double y = Math.sin(angleRad);
    	return new Point2D.Double(x * speed, y * speed);
    }

    public void move() {
        m_velocity = computeMovement(m_turnAngle, m_speed);
        translate(new Point2D.Double(m_velocity.getX(), -m_velocity.getY())); 
    }

    public void moveWithMap(Point2D.Double mapVel) {
        m_trueCoor.setLocation(m_trueCoor.getX() + mapVel.x, m_trueCoor.getY() + mapVel.y);
    }
    
    // returns the location of the object
    public Point2D.Double getTrueCoordinates() {
        return m_trueCoor;
    }

    public Point2D.Double getRelativeCoordinates() {
        return m_coor;
    }

    public void rotate() {
        if (m_turning) {
            m_turnAngle += Math.toRadians(m_rotationalSpeed);
            if (m_turnAngle >= 2 * Math.PI) {
                m_turnAngle -= 2 * Math.PI;
            }
            if (m_turnAngle < 0) {
                m_turnAngle += 2*Math.PI;
            }
        }
    }

    // sets the coordinates of the object
    public void moveTo(Point2D.Double newCoor) {
        m_coor = newCoor;
    }
    // sets the sprites character to a certain m_image
    public void setImage(String imagePath) {
        m_image = new ImageIcon(imagePath).getImage();
    }


    protected void setTurnAngle(double turnAngle) {
        m_turnAngle = turnAngle;
    }

    protected double getTurnAngle() {
        return m_turnAngle;
    }

    public double getRotationalSpeed() {
        return m_rotationalSpeed;
    }

    public void setRotationalSpeed(double rotationalSpeed) {
        m_rotationalSpeed = rotationalSpeed;
    }

    
    /**
     * This method takes the center of a sprite and returns which coordinate
     * from the list of coordinates is closest.
     * @param coors
     * @return closest coordinate to the center of the sprite   
     */
    public Point2D.Double getClosestCoor(Point2D.Double[] coors) {
        Double smallestDistance = Double.MAX_VALUE;
        Point2D.Double closestCoor = new Point2D.Double(0,0);
        for (Point2D.Double pt: coors) {
            double distance = Utility.getDistance(pt, m_coor);
            if (distance < smallestDistance) {
                closestCoor = new Point2D.Double(pt.getX(),pt.getY());
                smallestDistance = distance;
            }
        }
        return closestCoor;
    }

    public void startTurning(boolean turning, int direction) {
        // This boolean determines if the direction stated by "dirction" is the same as the current direction
        // This prevents the character from spinning for ever with out stopping. There are no limits
        // on the setting of the values of the method is the same direction as current direction.
        boolean sameDirection = false;
        if (Math.abs(m_rotationalSpeed)/m_rotationalSpeed == direction) {
            sameDirection = true;
        }
        
        if (sameDirection) {
            m_turning = turning;
            m_rotationalSpeed = Math.abs(m_rotationalSpeed) * direction;
        } else if (m_turning && !turning) {
            // If the character is currently turning, and it is being asked to stop turning, then the command is ignored
        } else {
            m_turning = turning;
            m_rotationalSpeed = Math.abs(m_rotationalSpeed) * direction;
        }
    }

    public void periodic() {
        move();
        rotate();  
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

    protected GameMap getGameMap() {
        return m_currentMap;
    }

    protected boolean getTurningStatus() {
        return m_turning;
    }

    protected Point2D.Double getVelocity() {
        return new Point2D.Double(m_velocity.x, m_velocity.y);
    }

    protected void setVelocity(Point2D.Double velocity) {
        m_velocity = new Point2D.Double(velocity.x, velocity.y);;
    }
    
    protected Image getImage() {
        return m_image;
    }

    public void setCurrentMap(GameMap map) {
        m_currentMap = map;
    }



}
