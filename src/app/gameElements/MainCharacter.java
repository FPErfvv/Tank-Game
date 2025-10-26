
package app.gameElements;

import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import app.GameRenderer;
import app.SoundEngine;
import app.Utility;

import app.gameElements.hitbox.Hitbox;
import app.gameElements.hitbox.hitboxSubClasses.CowHitbox;
import app.gameElements.hitbox.hitboxSubClasses.RectangleHitbox;

public class MainCharacter extends Sprite {
	private static final double DEFAULT_INPUT_MOVING_SPEED = 5;
    private static final double DEFAULT_INPUT_TURNING_SPEED = Math.toRadians(5);
    
    private boolean weaponized = true;
    private boolean isShooting = false;
    
    private int shootCooldown = 3;
    private int timeUntilShot = 0;

    private Sprite weaponSprite;
    
    private boolean isDisguised = false;
    private Hitbox defaultHitbox;
    private Hitbox cowDisguiseHitbox;
    
    public MainCharacter() {
        super("src/images/MainCharacter.png");
        
        defaultHitbox = new RectangleHitbox(44.0d, 50.0d);
        cowDisguiseHitbox = new CowHitbox(50.0d, 113.0d);
        
        setHitbox(defaultHitbox);
        
        setInputMovingSpeed(DEFAULT_INPUT_MOVING_SPEED);
        setInputTurningSpeed(DEFAULT_INPUT_TURNING_SPEED);
        
        setMass(2);

        weaponSprite = new Sprite("src/images/50Cal.png");
    }

    @Override
    public void move() {
    	super.move();
    	Point2D.Double futureCoor = getMapCoodinate();
    	
        // minimum translation vector
        Point2D.Double mtvec = new Point2D.Double(0.0d, 0.0d);
        
        for (Sprite s : getCurrentMap().getSpriteList()) {
        	Hitbox thisBox = this.getHitbox();
        	Hitbox boxOther = s.getHitbox();
        	
        	if (boxOther == null)
        		continue;
        	
        	// The position of target closest to player
        	Point2D.Double targetsCoor = s.getMapCoodinate();
        	
        	//Point2D.Double tempMtv = thisBox.sat(boxOther, futureCoor, s.getMapCoodinate());
        	Point2D.Double tempMtv = thisBox.SAT(boxOther.getVertices(), targetsCoor, futureCoor);
        	
            if (Utility.getVectorMagnitude(tempMtv) != 0.0d) {
                mtvec.setLocation(tempMtv.x, tempMtv.y);
                break;
            }
        }
   
        // If there is no collision, the character is moved like normal
        double mt = Utility.getVectorMagnitude(mtvec);

        // This hitbox is then used to detect if the main character will collide with anything at that future position
        if (mt > 0.0d) { // If there is a collision, the m_velocity is adjusted to move the character right up next to the object
            // This finds the angle between the m_velocity vector and the mtv vector. 
            // Using that, it finds how far the character needs to move in the direction of the m_velocity vector to move the magnitude of the mtv vector
            // cos(α) = a · b / (|a| * |b|)
            // h = adj / cos(α)
            // h = adj / a · b / (|a| * |b|)
            
            Point2D.Double newCoor = new Point2D.Double(futureCoor.x + mtvec.x, futureCoor.y + mtvec.y);
            
            moveTo(newCoor);
            
            getHitbox().computeCollisionPoints(newCoor, getAngle());
        }
    }
    
    /**
     * This is called by {@link MainPanel#actionPerformed(ActionEvent arg0)} 
     * every timer cycle in the MainPanel. 
     * 
     * <p> It calls the {@link MainCharacter#move()} method to both turn and 
     * calculate the velocity of the MainCharacter. Then, it moves both the map  
     * and the MainCharacter with the {@link GameRenderer#moveMap(Point2D.Double mapVel)} 
     * method and the {@link MainCharacter#translate(Point2D.Double vel)} method. 
     */
    @Override
    public void periodic(SoundEngine soundEngine) {
    	super.periodic(soundEngine);
    	
        weaponSprite.moveTo(getMapCoodinate());
        weaponSprite.setAngle(getAngle());
        
        if(isShooting && weaponized) {
        	if (timeUntilShot-- == 0) {
        		Projectile p = new Projectile();
        		p.moveTo(getMapCoodinate());
        		p.setAngle(getAngle());
        		p.setHitbox(new RectangleHitbox(p.getWidth(), p.getHeight()));
        		p.setCurrentMap(getCurrentMap());
                p.setInputMovingSpeed(20);
                p.moveForward();
                //p.setInputTurningSpeed(Math.toRadians(5));
                //p.turnRight();
                
                getCurrentMap().addProjectile(p);
                
                soundEngine.playShootSoundClip();
                
                timeUntilShot = shootCooldown;
        	}
        }
    }


    @Override
    public void draw(Graphics2D g2d, AffineTransform tr, Point2D.Double scroll, boolean drawHitbox) {
    	super.draw(g2d, tr, scroll, drawHitbox);
    	
    	if (weaponized) {
    		weaponSprite.draw(g2d, tr, scroll, drawHitbox);
    	}
    }
    
    public void startShooting() {
    	isShooting = true;
    }
    public void stopShooting() {
    	isShooting = false;
    }
    
    public void toggleDisguise() {
    	if (isDisguised) {
    		weaponized = true;
    		setHitbox(defaultHitbox);
    		setImage("src/images/MainCharacter.png");
    	}
    	else {
    		weaponized = false;
    		setHitbox(cowDisguiseHitbox);
    		setImage("src/images/MainCowPic.png");
    	}
    	isDisguised = !isDisguised;
    }
}