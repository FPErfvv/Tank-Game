
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
    //private static MainCharacter m_mainCharacter;
    
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

    // public static MainCharacter getInstance() {
    //     if (m_mainCharacter == null) {
    //         m_mainCharacter = new MainCharacter(GameMap.getInstance());

    //     }
    //     return m_mainCharacter;
    // }
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


    //@Override
    // public void move() {
    //     //setVelocity(new Point2D.Double(0,0));
    //     // Remove the previous addition to the velocity before adding again.
    //     //adjustVelocity(new Point2D.Double(-previousVel.x, -previousVel.y));
    //     //System.out.println(previousVel.toString());
    //     Point2D.Double unimpededVel = computeMovement(getTurnAngle(), getSpeed());
    //     setVelocity(unimpededVel);
    //     previousVel = unimpededVel;
    //     double futureAngle = getTurnAngle();
    //     // If the character isn't rotating, there is no need to calculate a future angle
    //     if (getRotationalVel() != 0) {
    //         futureAngle += Math.toRadians(getRotationalVel());
    //         if (futureAngle >= 2 * Math.PI) {
    //             futureAngle -= 2 * Math.PI;
    //         }
    //         if (futureAngle < 0) {
    //             futureAngle += 2*Math.PI;
    //         }            
    //     }

    //     boolean hasCollided = false;
    //     Point2D.Double futureCoor = Utility.addPoints(getRelativeCoordinates(), new Point2D.Double(unimpededVel.x, -unimpededVel.y)); 
    //     // A hitbox is created where the main character will be in the future, given the current m_velocity and angle
    //     getHitbox().computeCollisionPoints(futureCoor, futureAngle);
    //     // This hitbox is then used to detect if the main character will collide with anything at that future position
    //     ArrayList<Point2D.Double> velocities = new ArrayList<Point2D.Double>();
    //     for (Sprite t: getGameMap().getSpriteList()) {
    //         Point2D.Double mtv = this.getHitbox().SAT(t.getHitbox().getVertices(), t.getRelativeCoordinates(), futureCoor);
    //          if (Utility.getVectorMagnitude(mtv) != 0) {
    //             // if the mass of the target is greater than the mass of the sprite, the target doesn't move.
    //             System.out.println(mtv);
    //             if (t.getMass() > getMass()) {
    //                 hasCollided = true;
    //                 // This is the default value for the distance used to move right up against the target
    //                 double sidleUpDistance = Utility.getVectorMagnitude(mtv);
    //                 // This finds the angle between the m_velocity vector and the mtv vector. 
    //                 // Using that, it finds how far the character needs to move in the direction of the m_velocity vector to move the magnitude of the mtv vector
    //                 // cos(α) = a · b / (|a| * |b|)
    //                 // h = adj / cos(α)
    //                 // h = adj / a · b / (|a| * |b|)
    //                 if (Utility.getVectorMagnitude(unimpededVel) != 0) {
    //                     sidleUpDistance = Math.abs(Utility.getVectorMagnitude(mtv)/(Utility.vectorDotProduct(mtv,unimpededVel) / (Utility.getVectorMagnitude(mtv) * Utility.getVectorMagnitude(getVelocity()))));
    //                 }
    //                 // This limits the translation to five pixels so that large jumps are not experienced.
    //                 int adjustmentFactor = 0;
    //                 if (getRotationalVel() != 0 ) {
    //                     adjustmentFactor = 2;
    //                 }
    //                 if (sidleUpDistance > Utility.getVectorMagnitude(unimpededVel) + adjustmentFactor) {
    //                     sidleUpDistance = Utility.getVectorMagnitude(unimpededVel) + adjustmentFactor;
    //                 }

    //                 int closestPoint = this.getHitbox().getClosestSide(t.getHitbox().getVertices());
    //                 // If the front of the MainCharacter is closest to the center of the target, the sidleUpDistance is subtracted from the m_velocity
    //                 if (closestPoint == Constants.FRONT) {
    //                     previousVel = new Point2D.Double(unimpededVel.x - (Math.cos(futureAngle) * (sidleUpDistance)), unimpededVel.y - (Math.sin(futureAngle) * (sidleUpDistance)));
    //                     velocities.add(previousVel);
    //                     //setVelocity(previousVel);
    //                 } else { // If the back of the MainCharacter is closest to the center of the target, the sidleUpDistance is added to the m_velocity
    //                     previousVel = new Point2D.Double(unimpededVel.x - (Math.cos(futureAngle) * -(sidleUpDistance)), unimpededVel.y - (Math.sin(futureAngle) * -(sidleUpDistance)));
    //                     velocities.add(previousVel);
    //                     //setVelocity(previousVel);
    //                 }
    //             }
    //         // If there is no collision, the character is moved like normal
    //         }
    //     } 
    //     if (hasCollided) {
    //         setVelocity(Utility.average(velocities));
    //     }
    //     //System.out.println(numTargets);
    //     if (getRotationalVel() != 0) { 
    //     setTurnAngle(futureAngle);            
    //     }
    // }
    
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