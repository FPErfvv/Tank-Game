package app.gameElements;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import app.Constants;
import app.GameMap;
import app.MainPanel;
import app.Projectile;
import app.Utility;
import app.gameElements.hitbox.Hitbox;
import app.gameElements.hitbox.hitboxSubClasses.CowHitbox;

public class Sprite {
    private Image m_image;
	
    private GameMap m_currentMap;
    
    private int m_mass; // tons
    
    private Point2D.Double m_coor;
    private Point2D.Double m_prevCoor;
    
    private Hitbox m_hitbox;
    
    // in pixels per tick
    private Point2D.Double m_velocity;
    private double m_speed;
    
    private double m_angle; // radian's
    private double m_rotationalVel; // radian's / tick
    private double m_rotationalSpeed;
    
    private double m_maxInputMovingSpeed = 0.0d;
    
    private boolean m_movingForward = false;
    private boolean m_movingBackward = false;
    
    private int m_inputTurningDirection;
    private double m_maxInputTurningSpeed; // input speed
    
    private boolean m_turningRight = false;
    private boolean m_turningLeft = false;
    
    private int cooldown = 30;
    
    private int timeLeft = 0;
    
    public Sprite(Point2D.Double coor, String imagePath, double angle, GameMap map) {
    	m_image = new ImageIcon(imagePath).getImage();
    	
    	m_currentMap = map;
    	
    	// create a new Point2D object to avoid overwriting the passed in position
    	m_coor = new Point2D.Double(coor.x, coor.y);
    	m_prevCoor = new Point2D.Double(m_coor.x, m_coor.y);
        
        m_velocity = new Point2D.Double(0.0d, 0.0d);
        m_speed = 0.0d;
        
        m_hitbox = new CowHitbox(m_image.getWidth(null), m_image.getHeight(null));
        m_hitbox.computeCollisionPoints(m_coor, angle);
        
        m_angle = angle;
        m_rotationalVel = 0.0d;
        
        m_inputTurningDirection = Constants.TURNING_STOP;
    }

    /**
     * This method translates the sprite to it's actual coordinate and
     * then rotates it to it's turnAngle. Then, it draws the image of
     * the sprite.
     * @param g2d
     * <p>
     * PRECONDITION: tr must be translated to the center of the screen
     */
    public void draw(Graphics2D g2d, AffineTransform tr, Point2D.Double scroll, boolean drawHitbox) {
        // X and Y are the coordinates of the m_image
        // the main character is used as the origin-(0,0)
        // this means that when the page is resized, all the sprites remain the same m_speed from the character
    	
    	// distance this sprite is from the scroll point (likely where the main character is)
    	double dx = m_coor.getX() - scroll.getX();
    	double dy = m_coor.getY() - scroll.getY();
    	
    	double w = getWidth(), h = getHeight();
    	
    	// image is faced upwards if 0 degrees, so add 90 degrees
    	// remember: upwards is negative
    	double rotateAngle = m_angle + Math.PI * 0.5d;
    	
    	tr.translate(dx, dy);
    	tr.rotate(rotateAngle);
    	tr.translate(-w * 0.5d, -h * 0.5d);
    	
    	g2d.drawImage(m_image, tr, null);
    	
    	if (drawHitbox) {
    		m_hitbox.drawHitbox(g2d, scroll);
    	
    		m_hitbox.drawFacingLine(g2d, m_coor, rotateAngle, scroll);
    	}
    	
    	// transform tr back to its original transformation
    	tr.translate(w * 0.5d, h * 0.5d);
    	tr.rotate(-rotateAngle);
    	tr.translate(-dx, -dy);
    }
    
    /**
     * Takes in a velocity vector (or point) and modifys the x and y components based on the 
     * speed and angle.
     * @param vel
     * @param angleRad
     * @param speed
     */
    public static void computeVelocity(Point2D.Double vel, double angleRad, double speed) {
    	double x = speed * Math.cos(angleRad);
    	double y = speed * Math.sin(angleRad);
    	vel.setLocation(x, y);
    }
    
    /**
     * Translate and then rotates this sprite
     */
    
    public void move() {
    	double forward = m_movingForward ? 1.0d : 0.0d;
    	double backward = m_movingBackward ? 1.0d : 0.0d;
    	
    	double linearVel = (forward - backward) * m_maxInputMovingSpeed;
    	
        computeVelocity(m_velocity, m_angle, linearVel);
        translate(m_velocity);
        rotate();
        m_hitbox.computeCollisionPoints(m_coor, m_angle);
        
        m_speed = Utility.getVectorMagnitude(m_velocity);
        m_rotationalSpeed = Math.abs(m_rotationalVel);
    }
    
    public void moveAI(Point2D.Double playerCoor) {
    	Point2D.Double diff = new Point2D.Double(playerCoor.x - m_coor.x, playerCoor.y - m_coor.y);
    	
    	double len = Utility.getVectorMagnitude(diff);
    	
    	double angle = Math.atan(diff.y / diff.x);
    	
    	if (diff.x < 0.0d) {
    		angle += Math.PI;
    	}
    	
    	m_angle = angle;
    	
    	if (len > 300) {
    		moveForward();
    	}
    	else {
    		stopMovingForward();
    		
    		if (timeLeft == 0) {
        		Projectile p = new Projectile(getMapCoodinate(), getAngle(), getGameMap());
                p.setMovingSpeed(20);
                p.moveForward();
                getGameMap().addProjectile(p);
                
                timeLeft = cooldown;
        	}
    	}
    	
    	if (timeLeft > 0)
    		timeLeft--;
    	
    	move();
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
    	double left = m_turningLeft ? 1.0d : 0.0d;
    	double right = m_turningRight ? 1.0d : 0.0d;
    	
    	m_rotationalVel = (right - left) * m_maxInputTurningSpeed;
    	
        m_angle += m_rotationalVel;
        
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
    	m_prevCoor.setLocation(m_coor.x, m_coor.y);
    }

    /**
     * Places this sprite in the specified coordinate
     * @param newCoor
     */
    public void moveTo(Point2D.Double newCoor) {
        m_coor.setLocation(newCoor.x, newCoor.y);
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
    
    public void setRotationalVel(double newRotationVel) {
        m_rotationalVel = newRotationVel;
    }
    
    public double getRotationalSpeed() {
    	return m_rotationalSpeed;
    }

    public double getInputTurningSpeed() {
        return m_maxInputTurningSpeed;
    }
    
    public void setInputTurningSpeed(double turningSpeed) {
    	m_maxInputTurningSpeed = turningSpeed;
    }
    
    public void turnLeft() {
    	m_turningLeft = true;
    }
    
    public void turnRight() {
    	m_turningRight = true;
    }
    
    public void stopTurningLeft() {
    	m_turningLeft = false;
    }
    
    public void stopTurningRight() {
    	m_turningRight = false;
    }
    
    public void moveForward() {
    	m_movingForward = true;
    }
    
    public void moveBackward() {
    	m_movingBackward = true;
    }
    
    public void stopMovingForward() {
    	m_movingForward = false;
    }
    
    public void stopMovingBackward() {
    	m_movingBackward = false;
    }
    
    /*
    public void changeRotationalVel(double deltaV) {
        m_rotationalVel += deltaV;
    }*/

    public int getTurningDirection() {
        return m_inputTurningDirection;
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
        Point2D.Double closestCoor = new Point2D.Double(0, 0);
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
            if (m_inputTurningDirection == direction) {
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
                    m_rotationalVel += m_maxInputTurningSpeed * -m_inputTurningDirection;
                } else {
                    m_rotationalVel += m_maxInputTurningSpeed * direction * 2;
                }
            }         
        } else {
            m_rotationalVel += m_maxInputTurningSpeed * direction;
        }
        m_inputTurningDirection = direction;
        
        if (m_inputTurningDirection == Constants.TURNING_LEFT) {
        	m_turningLeft = true;
        	m_turningRight = false;
        }
        if (m_inputTurningDirection == Constants.TURNING_RIGHT) {
        	m_turningLeft = false;
        	m_turningRight = true;
        }
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
        	m_rotationalVel += m_maxInputTurningSpeed*-direction;
            //changeRotationalVel(getTurningSpeed() * -direction);
        }
        
        m_turningLeft = false;
        m_turningRight = false;
    }

    /**
     * This is called by {@link MainPanel#actionPerformed(ActionEvent arg0)} every timer cycle in the MainPanel. It creates
     * the hitbox and then calls the {@link Sprite#move()} and {@link Sprite#rotate()} methods to move and rotate the Sprite.
     */
    public void periodic() {
        move();
    }

    public double getSpeed() {
        return m_speed;
    }

    public void setMovingSpeed(double speed) {
        m_maxInputMovingSpeed = speed;
    }
    
    /**
     * Returns a copy of the velocity vector in which this sprite moves
     * @return velocity
     */
    public Point2D.Double getVelocity() {
        return new Point2D.Double(m_velocity.x, m_velocity.y);
    }

    public void setVelocity(Point2D.Double newVel) {
        m_velocity.setLocation(newVel.x, newVel.y);;
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
    
    public void setHitbox(Hitbox hitbox) {
        m_hitbox = hitbox;
    }

    public void adjustVelocity(Point2D.Double deltaV) {
        m_velocity = Utility.addPoints(m_velocity, deltaV);
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
