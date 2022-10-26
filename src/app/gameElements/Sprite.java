package app.gameElements;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import app.Constants;
import app.GameMap;
import app.MainPanel;
import app.Utility;
import app.gameElements.hitbox.Hitbox;
import app.gameElements.hitbox.hitboxSubClasses.CowHitbox;

public class Sprite {
	
    private Image m_image;
	
    private GameMap m_currentMap;
    private int m_mass; // tons
    private Point2D.Double m_coor;
    private Point2D.Double m_prevCoor;
    //private Point2D.Double m_screenCoor;
    private Hitbox m_hitbox;
    
    private double m_speed;
    private Point2D.Double m_velocity;
    private double m_angle; // radian's
    
    private int m_turningDirection; // one of the turning constants
    private double m_turningSpeed; //speed in which this sprite can rotate
    private double m_rotationalVel; // the direction and magnitude this sprite is moving in per frame
    private static final double DEFAULT_TURNING_VEL = 5; // speed in degrees per frame
    
    public Sprite(Point2D.Double coor, String imagePath, double angle, GameMap map) {
    	m_image = new ImageIcon(imagePath).getImage();
    	// create a new Point2D object to avoid overwriting the passed in position
    	m_coor = new Point2D.Double(coor.getX(), coor.getY());
    	m_prevCoor = new Point2D.Double(m_coor.getX(),m_coor.getY());
        //m_screenCoor = new Point2D.Double(m_coor.x + map.getOffset().getX(), m_coor.y + map.getOffset().getY());
        m_hitbox = new CowHitbox(m_image.getWidth(null), m_image.getHeight(null));
        m_hitbox.computeCollisionPoints(m_coor, angle);
        m_velocity = new Point2D.Double(0.0d ,0.0d );
        m_angle = angle;
        m_turningDirection = Constants.TURNING_STOP;
        m_turningSpeed = DEFAULT_TURNING_VEL;
        m_rotationalVel = 0.0d;
        m_currentMap = map;
    }

    /**
     * This method translates the sprite to it's actual coordinate and
     * then rotates it to it's turnAngle. Then, it draws the image of
     * the sprite.
     * @param g2d
     * <p>
     * PRECONDITION: tr must be translated to the center of the screen
     */
    public void draw(Graphics2D g2d, AffineTransform tr, Point2D.Double scroll) {
        // X and Y are the coordinates of the m_image
        // the main character is used as the origin-(0,0)
        // this means that when the page is resized, all the sprites remain the same m_speed from the character
    	
    	// distance this sprite is from the scroll point (likely where the main character is)
    	double dx = m_coor.getX()-scroll.getX();
    	double dy = m_coor.getY()-scroll.getY();
    	
    	double w = getWidth(), h = getHeight();
    	
    	// image is faced upwards if 0 degrees, so add 90 degrees
    	// remember: upwards is negative
    	double rotateAngle=m_angle+Math.PI*0.5d;
    	
    	tr.translate(dx, dy);
    	tr.rotate(rotateAngle);
    	tr.translate(-w*0.5d,-h*0.5d);
    	
    	g2d.drawImage(m_image, tr, null);
    	
    	// transform tr back to its original transformation
    	tr.translate(w*0.5d,h*0.5d);
    	tr.rotate(-rotateAngle);
    	tr.translate(-dx, -dy);
    	
    	drawHitbox(g2d, scroll);
    	
    }
    /**
     * Draws the hitbox in order of point index
     * @param g2d 
     * @param scroll
     */
    public void drawHitbox(Graphics2D g2d, Point2D.Double scroll) {
    	Point2D.Double[] verts = getHitbox().getVertices();
    	int size=verts.length;
        for (int i=0;i<size;i++) {
        	// the point that point i connects to
        	int b=(i+1)%size;
        	double x0=verts[i].getX()-scroll.getX();
        	double x1=verts[b].getX()-scroll.getX();
        	double y0=verts[i].getY()-scroll.getY();
        	double y1=verts[b].getY()-scroll.getY();
            g2d.drawLine((int)x0, (int)y0, (int)x1, (int)y1);
        }
    }
    
    /**
     * Takes in a velocity vector (or point) and modifys the x and y components based on the 
     * speed and angle.
     * @param vel
     * @param angleRad
     * @param speed
     */
    public static void computeVelocity(Point2D.Double vel, double angleRad, double speed) {
    	double x = speed*Math.cos(angleRad);
    	double y = speed*Math.sin(angleRad);
    	vel.setLocation(x, y);
    }
    
    /**
     * Translate and then rotates this sprite
     */
    public void move() {
        computeVelocity(m_velocity, m_angle, m_speed);
        translate(m_velocity);
        rotate();
        getHitbox().computeCollisionPoints(m_coor, m_angle);
    }

    public void translate(Point2D.Double shift) {
        m_coor = Utility.addPoints(m_coor, shift);
    }
    
    /**
     * This method changes the current angle of the character by 
     * m_rotationalVel every timer cycle. It also wraps the
     * angle around to keep it in the range of 0 to -2π radian's
     */
    public void rotate() {
        m_angle += Math.toRadians(m_rotationalVel);
        
        if (m_angle > 0) {
            m_angle -= 2 * Math.PI;
        }
        if (m_angle <= -2*Math.PI) {
            m_angle += 2*Math.PI;
        }
    }

    /**
     * Returns this sprite's coordinate relative to the game map
     * @return map coordinate
     */
    public Point2D.Double getMapCoodinate() {
        return new Point2D.Double(m_coor.getX(), m_coor.getY());
    }
    
    /**
     * Returns this Sprite's coordinate in the previous frame
     * @return
     */
    public Point2D.Double getPreviousCoodinate(){
    	return new Point2D.Double(m_prevCoor.getX(), m_prevCoor.getY());
    }
    
    /**
     * Changes the instance variable that stores this sprite's position in the previous frame
     * to the current coordinate.
     */
    public void updatePrevCoordinate() {
    	m_prevCoor.setLocation(m_coor.getX(), m_coor.getY());
    }

    /**
     * Places this sprite in the specified coordinate
     * @param newCoor
     */
    public void moveTo(Point2D.Double newCoor) {
        m_coor.setLocation(newCoor.getX(), newCoor.getY());
    }

    /**
     * Returns the direction this sprite is facing in radian's in the interval [2, 2pi]
     * @return
     */
    public double getAngle() {
        return m_angle;
    }
    
    /**
     * Sets the facing direction of this sprite
     * @param newAngle
     */
    public void setAngle(double newAngle) {
        m_angle = newAngle;
    }

    public double getRotationalVel() {
        return m_rotationalVel;
    }

    public double getTurningSpeed() {
        return m_turningSpeed;
    }
    
    /*
    public void changeRotationalVel(double deltaV) {
        m_rotationalVel += deltaV;
    }*/

    public int getTurningDirection() {
        return m_turningDirection;
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
        for (Point2D.Double pt : coors) {
            double distance = Utility.getDistance(pt, m_coor);
            if (distance < smallestDistance) {
                closestCoor = new Point2D.Double(pt.getX(),pt.getY());
                smallestDistance = distance;
            }
        }
        return closestCoor;
    }
    
    /**
     * Determines how to change the rotational velocity based off of an inputted direction.
     * 
     * @param direction the inputed direction
     */
    public void turn(int direction) {
    	
        boolean sameDirection = false;
        boolean alreadyMoving = false;
        if (m_rotationalVel != 0) {
            if (m_turningDirection==direction) {
                sameDirection = true;
            }
            alreadyMoving = true;
        }
        

        /*
         * If the Sprite is already moving, and it is being told to change direction, and the
         * inputed direction states that the Sprite should stop, the velocity is changed by
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
    
    /**
     * Changes the rotational velocity in order to negate the velocity added while turning.
     * This is called by the {@link PlayerControls#keyReleased(java.awt.event.KeyEvent)}
     * method when the player releases either the directional ('a' or 'd') keys. It does not 
     * set the rotational velocity to zero in case there is an external factor that is causing
     * the MainCharacter to turn. This is used separatedly from the 
     * {@link Sprite#turn(int direction)} in order to prevent a glitch in the movement
     * when the player releases one of the directional keys while holding down the other key.
     * 
     * @param direction direction at which the MainCharacter needs to stop turning
     */
    public void stopTurn(int direction) {
        // sameDirection determines if the direction stated by "direction" is the same as the current direction
        boolean sameDirection = false;
        if (getRotationalVel() != 0) {
            if (Math.abs(getRotationalVel())/getRotationalVel() == direction) {
                sameDirection = true;
            }
        }

        if (sameDirection) {
        	m_rotationalVel += m_turningSpeed*-direction;
            //changeRotationalVel(getTurningSpeed() * -direction);
        }
        
    }

    public void setRotationalVel(double newRotationVel) {
        m_rotationalVel = newRotationVel;
    }

    /**
     * This is called by {@link MainPanel#actionPerformed(ActionEvent arg0)} every timer cycle in the MainPanel. It creates
     * the hitbox and then calls the {@link Sprite#move()} and {@link Sprite#rotate()} methods to move and rotate the Sprite.
     */
    public void periodic() {
        move();
        m_hitbox.computeCollisionPoints(m_coor, m_angle);
    }

    public double getSpeed() {
        return m_speed;
    }

    public void setSpeed(double speed) {
        m_speed = speed;
    }
    
    /**
     * Returns a copy of the velocity vector in which this sprite moves
     * @return velocity
     */
    public Point2D.Double getVelocity() {
        return new Point2D.Double(m_velocity.getX(), m_velocity.getY());
    }

    public void setVelocity(Point2D.Double newVel) {
        m_velocity.setLocation(newVel.getX(),newVel.getY());;
    }
    
    public int getWidth() {
        return m_image.getWidth(null);
    }

    public int getHeight() {
        return m_image.getHeight(null);
    }

    /**
     * Returns sprite bounding box
     * @return
     */
    public Hitbox getHitbox() {
        return m_hitbox;
    }

    public void adjustVelocity(Point2D.Double deltaV) {
        m_velocity = Utility.addPoints(m_velocity, deltaV);
    }
    
    public void setHitbox(Hitbox hitbox) {
        m_hitbox = hitbox;
    }

    public GameMap getGameMap() {
        return m_currentMap;
    }
    
    public void setCurrentMap(GameMap map) {
        m_currentMap = map;
    }
    
    public Image getImage() {
        return m_image;
    }
    
    /**
     * sets the sprite's character to a certain m_image
     * @param imagePath
     */
    public void setImage(String imagePath) {
        m_image = new ImageIcon(imagePath).getImage();
    }
    public void setMass(int mass) {
        m_mass = mass;
    }

    public int getMass() {
        return m_mass;
        
    }



}
