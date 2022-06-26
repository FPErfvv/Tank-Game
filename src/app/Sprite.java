package app;

import javax.swing.ImageIcon;

import app.hitbox.CowHitbox;

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
    private GameMap m_currentMap;
    private double m_speed;
    private Point2D.Double m_velocity;
    // The velocity at which the sprite is rotating at
    private double m_rotationalVel;
    // The speed at which the sprite turns at
    private double m_turningSpeed;
    private static final double DEFAULT_TURNING_VEL = 5;
    private int m_turningDirection;




    public Sprite(Point2D.Double coor, String imagePath, double m_turnAngle, GameMap map) {
        m_turningDirection = Constants.TURNING_STOP;
        m_turningSpeed = DEFAULT_TURNING_VEL;
        m_rotationalVel = 0;
        m_coor = coor;
        m_trueCoor = new Point2D.Double(m_coor.x + map.getOffset().getX(), m_coor.y + map.getOffset().getY());
        this.m_turnAngle = m_turnAngle;
        m_image = new ImageIcon(imagePath).getImage();
        m_currentMap = map;
        m_velocity = new Point2D.Double(0,0);
        m_hitBox = new CowHitbox(this);
        m_hitBox.computeCollisionPoints(m_coor, m_turnAngle);
    }

    /**
     * This method translates the sprite to it's actual coordinate and
     * then rotates it to it's turnAngle. Then, it draws the image of
     * the sprite.
     * @param g2d
     */
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
        getHitBox().drawHitBox(g2d);
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

    /**
     * This method changes the current angle of the character by 
     * m_rotationalVel every timer cycle. It also wraps the
     * angle around to keep it in the range of 0 to 2π radians
     */
    public void rotate() {
        m_turnAngle += Math.toRadians(m_rotationalVel);
        if (m_turnAngle >= 2 * Math.PI) {
            m_turnAngle -= 2 * Math.PI;
        }
        if (m_turnAngle < 0) {
            m_turnAngle += 2*Math.PI;
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

    protected double getRotationalVel() {
        return m_rotationalVel;
    }

    protected double getTurningSpeed() {
        return m_turningSpeed;
    }

    
    /**
     * Takes the center of a sprite and returns which coordinate
     * from the list of coordinates is closest.
     * 
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
    // TODO: Change use of constant to use of a new variable 
    public void changeRotationalVel(double deltaV) {
        m_rotationalVel += deltaV;
    }

    /**
     * Determines how to change the rotational velocity based off of an inputted direction.
     * 
     * @param direction the inputted direction
     */
    public void turn(int direction) {
        
        boolean sameDirection = false;
        boolean alreadyMoving = false;
        if (m_rotationalVel != 0) {
            if (Math.abs(m_rotationalVel)/m_rotationalVel == direction) {
                sameDirection = true;
            }
            alreadyMoving = true;
        }

        /**
         * If the Sprite is already moving, and it is being told to change direction, and the
         * inputted direction states that the Sprite should stop, the velocity is changed by
         * the speed that the Sprite turns at, in the opposite direction that the Sprite
         * is currently turning. 
         * 
         * If the Sprite is already moving, and it is being told to change direction, and the inputted 
         * direction states that the Sprite should completely change direction, the velocity is changed 
         * by 2x the speed that the Sprite turns at, in the opposite direction that the Sprite
         * is currently turning.
         * 
         * If the Sprite is not already moving, the velocity is simply changed by the turning speed
         * in the direction that the Sprite is being told to turn.
         */
        if (alreadyMoving) {
            if (!sameDirection) {
                if (direction == 0) {
                    m_rotationalVel += m_turningSpeed * -m_turningDirection;
                } else {
                    m_rotationalVel += m_turningSpeed * direction * 2;
                }
            }         
        } else {
            m_rotationalVel += m_turningSpeed * direction;
        }
        m_turningDirection = direction;
        
    }

    protected void setRotationalVel(double rotationalSpeed) {
        m_rotationalVel = rotationalSpeed;
    }

    /**
     * This is called by {@link MainPanel#actionPerformed(ActionEvent arg0)} every timer cycle in the MainPanel. It creates
     * the hitbox and then calls the {@link Sprite#move()} and {@link Sprite#rotate()} methods to move and rotate the Sprite.
     */
    public void periodic() {
        m_hitBox.computeCollisionPoints(m_coor, m_turnAngle);
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

    public int getTurningDirection() {
        return m_turningDirection;
    }

    public void setHitbox(HitBox hitbox) {
        m_hitBox = hitbox;
    }



}
