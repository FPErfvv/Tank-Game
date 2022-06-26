package app.hitbox;

import app.HitBox;
import app.Sprite;
import java.awt.geom.Point2D;

public class CowHitbox extends HitBox {

    public CowHitbox(Sprite sprite) {
        super(sprite);
        initializeVert(new Point2D.Double[6]);
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
        int x = (int) (futureSpriteCoor.getX() - (dimensions.getX() / 4 * Math.cos(futureAngle) + dimensions.getY() / 2 * Math.sin(futureAngle)));
        int y = (int) (futureSpriteCoor.getY() + (dimensions.getX() / 4 * Math.sin(futureAngle) - dimensions.getY() / 2 * Math.cos(futureAngle)));
        getVertices()[0] = new Point2D.Double(x,y); 

        // top right
        x = (int)(futureSpriteCoor.getX() + (dimensions.getX() / 4 * Math.cos(futureAngle) - dimensions.getY() / 2 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() - (dimensions.getX() / 4 * Math.sin(futureAngle) + dimensions.getY() / 2 * Math.cos(futureAngle)));
        getVertices()[1] = new Point2D.Double(x, y);  
        
        // middle right
        x = (int) (futureSpriteCoor.getX() + (dimensions.getX() / 2 * Math.cos(futureAngle) + dimensions.getY() / 4 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() - (dimensions.getX() / 2 * Math.sin(futureAngle) - dimensions.getY() / 4 * Math.cos(futureAngle)));
        getVertices()[2] = new Point2D.Double(x, y);            

        // bottom right
        x = (int) (futureSpriteCoor.getX() + (dimensions.getX() / 2 * Math.cos(futureAngle) + dimensions.getY() / 2 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() - (dimensions.getX() / 2 * Math.sin(futureAngle) - dimensions.getY() / 2 * Math.cos(futureAngle)));
        getVertices()[3] = new Point2D.Double(x, y);
        
        // bottom left
        x = (int) (futureSpriteCoor.getX() - (dimensions.getX() / 2 * Math.cos(futureAngle) - dimensions.getY() / 2 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() + (dimensions.getX() / 2 * Math.sin(futureAngle) + dimensions.getY() / 2 * Math.cos(futureAngle)));
        getVertices()[4] = new Point2D.Double(x, y);

        // middle left
        x = (int) (futureSpriteCoor.getX() - (dimensions.getX() / 2 * Math.cos(futureAngle) - dimensions.getY() / 4 * Math.sin(futureAngle)));
        y = (int) (futureSpriteCoor.getY() + (dimensions.getX() / 2 * Math.sin(futureAngle) + dimensions.getY() / 4 * Math.cos(futureAngle)));
        getVertices()[5] = new Point2D.Double(x, y);

    }
    
}
