
package app.gameElements;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import app.Constants;
import app.GameMap;
import app.Projectile;
import app.SoundFx;
import app.Utility;
import app.gameElements.hitbox.hitboxSubClasses.RectangleHitbox;
import app.PlayerControls;
public class MainCharacter extends Sprite {
    public boolean weaponized = true;
    public static final Point2D.Double TRUE_COOR = new Point2D.Double(0,0); 
    private Point2D.Double previousVel;

    private ArrayList<Projectile> projectileList;
    private Sprite weaponSprite;
    private SoundFx fx;
    
    public MainCharacter(GameMap map) {
        super(new Point2D.Double(0, 0),"src/images/MainCharacter.png", 0, map);
        setHitbox(new RectangleHitbox(getWidth(),getHeight()));
        getHitbox().computeCollisionPoints(getMapCoodinate(), getAngle());
        setSpeed(0);
        setMass(2);
        previousVel = new Point2D.Double(0,0);

        weaponSprite = new Sprite(getMapCoodinate(),"src/images/50Cal.png", 0, map);
        weaponSprite.setHitbox(new RectangleHitbox(weaponSprite.getWidth(),weaponSprite.getHeight()));
        weaponSprite.getHitbox().computeCollisionPoints(weaponSprite.getMapCoodinate(), weaponSprite.getAngle());
        fx = new SoundFx();
        projectileList = new ArrayList<Projectile>();
    
    }

    @Override
    public void move() {
    	super.move();
    	Point2D.Double futureCoor = getMapCoodinate();
        // minimum translation vector
        Point2D.Double mtvec = new Point2D.Double(0,0);

        // The position of target closest to player
        Point2D.Double targetsCoor = new Point2D.Double(0,0);
        for (Sprite s: getGameMap().getSpriteList()) {
            Point2D.Double tempMtv = this.getHitbox().SAT(s.getHitbox().getVertices(), s.getMapCoodinate(), futureCoor);
            if (Utility.getVectorMagnitude(tempMtv) != 0.0d) {
                mtvec.setLocation(tempMtv.getX(),tempMtv.getY());
                targetsCoor = s.getMapCoodinate();
            }
        }
   
        // If there is no collision, the character is moved like normal
        
        double mt=Utility.getVectorMagnitude(mtvec);

        // This hitbox is then used to detect if the main character will collide with anything at that future position
        if (mt > 0.0d) { // If there is a collision, the m_velocity is adjusted to move the character right up next to the object
            // This finds the angle between the m_velocity vector and the mtv vector. 
            // Using that, it finds how far the character needs to move in the direction of the m_velocity vector to move the magnitude of the mtv vector
            // cos(α) = a · b / (|a| * |b|)
            // h = adj / cos(α)
            // h = adj / a · b / (|a| * |b|)
            
            Point2D.Double d = new Point2D.Double(futureCoor.getX()-targetsCoor.getX(), futureCoor.getY()-targetsCoor.getY());
            
            double s=Utility.getVectorMagnitude(d);
            
            double dx=mt*d.getX()/s;
            double dy=mt*d.getY()/s;
            
            futureCoor.setLocation(dx+futureCoor.getX(), dy+futureCoor.getY());
            moveTo(futureCoor);
            getHitbox().computeCollisionPoints(futureCoor, getAngle());
        }
    }
    
    /**
     * This is called by {@link MainPanel#actionPerformed(ActionEvent arg0)} 
     * every timer cycle in the MainPanel. 
     * 
     * <p> It calls the {@link MainCharacter#move()} method to both turn and 
     * calculate the velocity of the MainCharacter. Then, it moves both the map  
     * and the MainCharacter with the {@link GameMap#moveMap(Point2D.Double mapVel)} 
     * method and the {@link MainCharacter#translate(Point2D.Double vel)} method. 
     */
    @Override
    public void periodic() {
        move();
        weaponSprite.moveTo(getMapCoodinate());
        weaponSprite.setAngle(getAngle());
        weaponSprite.getHitbox().computeCollisionPoints(getMapCoodinate(), getAngle());

        if(PlayerControls.fireTime == true && weaponized == true) {
            fx.repeat50Cal();
            if(fx.timeToRepeat >= 3) {
                Projectile p = new Projectile(getMapCoodinate(), getAngle(), getGameMap());
                p.setSpeed(20);
                projectileList.add(p);
            }
            
        }
        else {
            fx.resetFireTime();
        }
        
        for(Projectile p: projectileList) {
            p.periodic(); 
        }
    }


    @Override
    public void draw(Graphics2D g2d, AffineTransform tr, Point2D.Double scroll) {
    	super.draw(g2d, tr, scroll);
    	
    	if (weaponized) {
    		weaponSprite.draw(g2d, tr, scroll);
    	}
        
        for(Projectile p: projectileList) {
            p.draw(g2d, tr, scroll);
        }
    }

}