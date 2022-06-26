package app.hitbox;

import app.Constants;
import app.Sprite;
import app.Utility;

import app.Sprite;
import java.awt.geom.Point2D;

public class RectangleHitbox extends Hitbox {

    public RectangleHitbox(Sprite sprite) {
        super(sprite);
        initializeVert(new Point2D.Double[4]);
    }

    
    /**
     * This method takes an angle and a coordinate of the sprite and 
     * updates the m_vert array with new values. The m_vert array
     * is a list of coordinates that determine the corner points
     * of a rectangular hitbox.
     * @param futureSpriteCoor the future coordinate of the sprite
     * @param futureAngle the future angle of the sprite
     */
    @Override
    public void computeCollisionPoints(Point2D.Double futureSpriteCoor, double futureAngle) {    
        Point2D.Double dimensions = getDimensions();
        futureAngle -= (Math.PI/2);
            // top left
        int x = (int) (futureSpriteCoor.getX() - (dimensions.getX() / 2 * Math.cos(futureAngle) + dimensions.getY() / 2 * Math.sin(futureAngle)));
        int y = (int) (futureSpriteCoor.getY() + (dimensions.getX() / 2 * Math.sin(futureAngle) - dimensions.getY() / 2 * Math.cos(futureAngle)));
        getVertices()[0] = new Point2D.Double(x,y); 

        //top right
        x = (int)(futureSpriteCoor.getX() + (dimensions.getX() / 2 * Math.cos(futureAngle) - dimensions.getY() / 2 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() - (dimensions.getX() / 2 * Math.sin(futureAngle) + dimensions.getY() / 2 * Math.cos(futureAngle)));
        getVertices()[1] = new Point2D.Double(x, y);    

        // bottom right
        x = (int) (futureSpriteCoor.getX() + (dimensions.getX() / 2 * Math.cos(futureAngle) + dimensions.getY() /2 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() - (dimensions.getX() / 2 * Math.sin(futureAngle) - dimensions.getY() /2 * Math.cos(futureAngle)));
        getVertices()[2] = new Point2D.Double(x, y);

        //bottom left
        x = (int) (futureSpriteCoor.getX() - (dimensions.getX() / 2 * Math.cos(futureAngle) - dimensions.getY() / 2 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() + (dimensions.getX() / 2 * Math.sin(futureAngle) + dimensions.getY() / 2 * Math.cos(futureAngle)));
        getVertices()[3] = new Point2D.Double(x, y); 

    }


	@Override
	public int getClosestSide(Point2D.Double targetsCenter) {
        int indexOfClosestPoint = 0;
        double smallestDistance = Double.MAX_VALUE;
        for (int i = 0; i < getVertices().length; i++) {
            if (Utility.getDistance(targetsCenter, getVertices()[i]) < smallestDistance) {
                smallestDistance = Utility.getDistance(targetsCenter, getVertices()[i]);
                indexOfClosestPoint = i;
            }
        }
        // TODO: adjust this method to allow for different hitboxes other than rect
        return indexOfClosestPoint < 2 ? Constants.FRONT : Constants.BACK;
	}
    
}
