
package app;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class MainCharacter extends Sprite {

    public static final Point2D.Double TRUE_COOR = new Point2D.Double(0,0); 
    private boolean m_turning;

    
    public MainCharacter(GameMap map) {
        super(new Point2D.Double(0, 0),"src/images/MainCharacter.png", 0, map, Constants.RECTANGLE);
        m_turning = false;
        setSpeed(0);
    }

    @Override
    public void move() {
        setVelocity(computeMovement(getTurnAngle(), getSpeed()));
         
        double futureAngle = getTurnAngle();
        if (m_turning) {
            futureAngle += Math.toRadians(getRotationalSpeed());
            if (futureAngle >= 2 * Math.PI) {
                futureAngle -= 2 * Math.PI;
            }
            if (futureAngle < 0) {
                futureAngle += 2*Math.PI;
            }
        }
        Point2D.Double futureCoor = Utility.addPoints(getRelativeCoordinates(), new Point2D.Double(getVelocity().x, -getVelocity().y)); 
        // A hitbox is created where the main character will be in the future, given the current m_velocity and angle
        getHitBox().createHitbox(futureCoor, futureAngle);
        // This hitbox is then used to detect if the main character will collide with anything at that future position
        Point2D.Double mtv = new Point2D.Double(0,0);

        Point2D.Double closestTargetsCoor = new Point2D.Double(0,0);
        for (Sprite t: getGameMap().getSpriteList()) {
            Point2D.Double tempMtv = this.getHitBox().SAT(t.getHitBox().getCollisionPoints(), t.getRelativeCoordinates(), futureCoor);
            if (Utility.getVectorMagnitude(tempMtv) != 0) {
                mtv = new Point2D.Double(tempMtv.getX(), tempMtv.getY());
                closestTargetsCoor = t.getRelativeCoordinates();
            }
        }

        if (m_turning) {
            setTurnAngle(futureAngle);
        } 
        // If there is no collision, the character is moved like normal

        if (Utility.getVectorMagnitude(mtv) > 2 ) { // If there is a collision, the m_velocity is adjusted to move the character right up next to the object
                // This is the default value for the distance used to move right up against the target
                double sidleUpDistance = Utility.getVectorMagnitude(mtv) -1 ;
                // This finds the angle between the m_velocity vector and the mtv vector. 
                // Using that, it finds how far the character needs to move in the direction of the m_velocity vector to move the magnitude of the mtv vector
                // cos(α) = a · b / (|a| * |b|)
                // h = adj / cos(α)
                // h = adj / a · b / (|a| * |b|)
                Point2D.Double vel = getVelocity();
                if (Utility.getVectorMagnitude(vel) != 0) {
                    sidleUpDistance = Math.abs(Utility.getVectorMagnitude(mtv)/(Utility.vectorDotProduct(mtv,vel) / (Utility.getVectorMagnitude(mtv) * Utility.getVectorMagnitude(getVelocity()))))-1;
                }
                // This limits the translation to five pixels so that large jumps are not experienced.
                if (sidleUpDistance > Utility.getVectorMagnitude(vel) + 2) {
                    sidleUpDistance = Utility.getVectorMagnitude(vel) + 2;
                }

                int closestPoint = this.getHitBox().getClosestSide(closestTargetsCoor);
                // If the front of the MainCharacter is closest to the center of the target, the sidleUpDistance is subtracted from the m_velocity
                if (closestPoint == Constants.FRONT) {
                    setVelocity(new Point2D.Double(vel.x - Math.cos(futureAngle) * sidleUpDistance, vel.y - Math.sin(futureAngle) * sidleUpDistance));
                } else { // If the back of the MainCharacter is closest to the center of the target, the sidleUpDistance is added to the m_velocity
                    setVelocity(new Point2D.Double(vel.x - Math.cos(futureAngle) * -sidleUpDistance, vel.y - Math.sin(futureAngle) * -sidleUpDistance));
                }
        }

    }

    public void startTurning(boolean turning, int direction) {
        // This boolean determines if the direction stated by "dirction" is the same as the current direction
        // This prevents the character from spinning for ever with out stopping. There are no limits
        // on the setting of the values of the method is the same direction as current direction.
        boolean sameDirection = false;
        if (Math.abs(getRotationalSpeed())/getRotationalSpeed() == direction) {
            sameDirection = true;
        }

        
        if (sameDirection) {
            m_turning = turning;
            setRotationState(direction);
        } else if (m_turning && !turning) {
            // If the character is currently turning, and it is being asked to stop turning, then the command is ignored
        } else {
            m_turning = turning;
            setRotationState(direction);
        }
        
    }

    @Override
    public void translate(Point2D.Double vel) {
        getRelativeCoordinates().setLocation(getRelativeCoordinates().getX() + vel.x, getRelativeCoordinates().getY() + vel.y);
    }
    
    /**
     * This method is called every timer cycle in the MainPanel. It creates
     * the hitbox and then calls the move() and rotate() method.
     */
    @Override
    public void periodic() {
        move();
        Point2D.Double vel = getVelocity();
        getGameMap().moveMap(new Point2D.Double(-vel.getX(), vel.getY()));
        translate(new Point2D.Double(vel.getX(), -vel.getY())); 
    }



    @Override
    public void draw(Graphics2D g2d) {

        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the image
        tr.translate(TRUE_COOR.getX() - getWidth()/2, TRUE_COOR.getY() - getHeight()/2);
        tr.rotate(
                -(getTurnAngle() - (Math.PI/2)),
                getImage().getWidth(null) / 2,
                getImage().getHeight(null) / 2
        );
        g2d.drawImage(getImage(), tr, null);
    }

    public int getWidth() {
        return getImage().getWidth(null);
    }

    public int getHeight() {
        return getImage().getHeight(null);
    }

    @Override
    public Point2D.Double getTrueCoordinates() {
        return TRUE_COOR;
    }

}