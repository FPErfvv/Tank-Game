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

public class Sprite {
    private Image m_image;
	
    private GameMap m_currentMap;
    
    private int m_mass; // tons
    
    private Hitbox m_hitbox;
    
    private Point2D.Double m_coor;
    
    // in pixels per tick
    private Point2D.Double m_velocity;
    private double m_speed;
    
    private double m_angle; // radian's
    private double m_rotationalVel; // radian's per tick
    private double m_rotationalSpeed;
    
    private double m_inputMovingSpeed = 0.0d;
    
    private boolean m_movingForward = false;
    private boolean m_movingBackward = false;
    
    private int m_inputTurningDirection;
    private double m_inputTurningSpeed; // input speed
    
    private boolean m_turningRight = false;
    private boolean m_turningLeft = false;
    
    private int cooldown = 30;
    
    private int timeLeft = 0;
    
    public Sprite(String imagePath, Point2D.Double coor, double angle, GameMap map) {
    	m_image = new ImageIcon(imagePath).getImage();
    	
    	m_currentMap = map;
    	
    	// create a new Point2D object to avoid overwriting the passed in position
    	m_coor = new Point2D.Double(coor.x, coor.y);
        
        m_velocity = new Point2D.Double(0.0d, 0.0d);
        m_speed = 0.0d;
        
        m_angle = angle;
        m_rotationalVel = 0.0d;
        
        m_inputTurningDirection = Constants.TURNING_STOP;
    }
    
    public Image getImage() {
        return m_image;
    }
    
    public void setImage(String imagePath) {
        m_image = new ImageIcon(imagePath).getImage();
    }
    
    public int getWidth() {
        return m_image.getWidth(null);
    }

    public int getHeight() {
        return m_image.getHeight(null);
    }
    
    public GameMap getCurrentMap() {
        return m_currentMap;
    }
    
    public void setCurrentMap(GameMap map) {
        m_currentMap = map;
    }
    
    public Hitbox getHitbox() {
        return m_hitbox;
    }
    
    public void setHitbox(Hitbox hitbox) {
        m_hitbox = hitbox;
        hitbox.computeCollisionPoints(m_coor, m_angle);
    }
    
    public void setMass(int mass) {
        m_mass = mass;
    }

    public int getMass() {
        return m_mass;
    }

    /**
     * Returns a copy of this sprite's coordinate relative to the game map
     * @return map coordinate
     */
    public Point2D.Double getMapCoodinate() {
        return new Point2D.Double(m_coor.x, m_coor.y);
    }

    /**
     * Places this sprite in the specified coordinate
     * @param newCoor
     */
    public void moveTo(Point2D.Double newCoor) {
        m_coor.setLocation(newCoor.x, newCoor.y);
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
    
    /**
     * Computes and returns the translational speed of this sprite
     * @return
     */
    public double getSpeed() {
    	m_speed = Utility.getVectorMagnitude(m_velocity);
        return m_speed;
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
    
    /**
     * Computes and returns the rotational speed of this sprite
     * @return
     */
    public double getRotationalSpeed() {
    	m_rotationalSpeed = Math.abs(m_rotationalVel);
    	return m_rotationalSpeed;
    }
    
    
    public void setInputMovingSpeed(double speed) {
        m_inputMovingSpeed = speed;
    }
    
    public int getInputMovingDirection() {
    	if (m_movingForward && !m_movingBackward) {
    		return Constants.DIRECTION_FORWARDS;
    	}
    	else if (!m_movingForward && m_movingBackward) {
    		return Constants.DIRECTION_BACKWARDS;
    	}
    	return Constants.DIRECTION_STOP;
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
    
    
    public double getInputTurningSpeed() {
        return m_inputTurningSpeed;
    }
    
    public void setInputTurningSpeed(double turningSpeed) {
    	m_inputTurningSpeed = turningSpeed;
    }
    
    public int getInputTurningDirection() {
    	if (m_turningLeft == true && m_turningRight == false) {
    		m_inputTurningDirection = Constants.TURNING_LEFT;
    	}
    	else if (m_turningLeft == false && m_turningRight == true) {
    		m_inputTurningDirection = Constants.TURNING_RIGHT;
    	}
    	else {
    		m_inputTurningDirection = Constants.TURNING_STOP;
    	}
    	
        return m_inputTurningDirection;
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
    
    /**
     * Determines how to change the rotational velocity based off of an inputted direction.
     * 
     * @param direction the inputed direction
     */
    public void turn(int direction) {
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
        m_turningLeft = false;
        m_turningRight = false;
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
    	
    	double linearVel = (forward - backward) * m_inputMovingSpeed;
    	
        computeVelocity(m_velocity, m_angle, linearVel);
        translate(m_velocity);
        rotate();
        
        if (m_hitbox != null)
        	m_hitbox.computeCollisionPoints(m_coor, m_angle);
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
        		Projectile p = new Projectile(m_coor, m_angle, m_currentMap);
                p.setInputMovingSpeed(20);
                p.moveForward();
                m_currentMap.addProjectile(p);
                
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
    	
    	m_rotationalVel = (right - left) * m_inputTurningSpeed;
    	
        m_angle += m_rotationalVel;
        
        if (m_angle > 0) {
            m_angle -= 2 * Math.PI;
        }
        if (m_angle <= -2 * Math.PI) {
            m_angle += 2 * Math.PI;
        }
    }
    
    /**
     * This is called by {@link MainPanel#actionPerformed(ActionEvent arg0)} every timer cycle in the MainPanel. It creates
     * the hitbox and then calls the {@link Sprite#move()} and {@link Sprite#rotate()} methods to move and rotate the Sprite.
     */
    public void periodic() {
        move();
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
    	
    	// transform tr back to its original transformation
    	tr.translate(w * 0.5d, h * 0.5d);
    	tr.rotate(-rotateAngle);
    	tr.translate(-dx, -dy);
    	
    	if (drawHitbox) {
    		if (m_hitbox != null)
    			m_hitbox.drawHitbox(g2d, scroll);
    		
    		drawFacingLine(g2d, m_coor, rotateAngle, scroll);
    	}
    }
    
    public void drawFacingLine(Graphics2D g2d, Point2D.Double coor, double angle, Point2D.Double scroll) {
    	double lineLen = 100.0d; // in pixels
    	
    	double drawAngle = angle - Math.PI / 2.0f;
    	
    	double xLen = lineLen * Math.cos(drawAngle);
    	double yLen = lineLen * Math.sin(drawAngle);
    	
    	int x0 = (int) (coor.x - scroll.x);
    	int x1 = (int) (coor.x + xLen - scroll.x);
    	int y0 = (int) (coor.y - scroll.y);
    	int y1 = (int) (coor.y + yLen - scroll.y);
    	
    	g2d.setPaint(Color.BLUE);
    	
    	g2d.drawLine(x0, y0, x1, y1);
    	
    	double radius = 2.0d;
    	
    	int x = (int) (coor.x - radius - scroll.x);
    	int y = (int) (coor.y - radius - scroll.y);
    	
    	g2d.setPaint(Color.RED);
    	
    	g2d.fillOval(x, y, (int) (radius * 2.0d), (int) (radius * 2.0d));
    }
}
