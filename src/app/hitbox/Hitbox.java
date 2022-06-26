package app.hitbox;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import app.Sprite;
import app.Utility;

public abstract class Hitbox {

    public static final int CIRCLE = 0;
    public static final int RECTANGLE = 1;
    private Sprite m_sprite;
    // An array of points used to create the shape of the bounding box.
    private Point2D.Double[] m_vert;

    // creates a bounding box with the m_dimensions.getY() and m_dimensions.getX() the size of the m_sprite's image
    public Hitbox(Sprite sprite) {
        m_sprite = sprite;
    }

    // reference: https://www.sevenson.com.au/programming/sat/ 
    // https://dyn4j.org/2010/01/sat/#sat-proj 
    // https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169 
    public Point2D.Double SAT(Point2D.Double[] targetsVert, Point2D.Double targetsCoor, Point2D.Double spritesFutureCoor) {
        Point2D.Double mtv = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        int loopLength = m_vert.length; // will be one less than half the # of sides
        double x = 0;
        double y = 0;
        Point2D.Double[] axes = new Point2D.Double[m_vert.length + targetsVert.length];
        for (int i = 1; i < loopLength; i++) {

            // finds the x and y of a vector perpendicular to three of the sides
            x = -(m_vert[i].getY() - m_vert[i-1].getY());
            y = (m_vert[i].getX() - m_vert[i-1].getX());
            // the vector needs to be normalized to make it's length 1
            axes[i-1] = Utility.normalize(new Point2D.Double(x,y));
        }
        // finds the x and y of a vector perpendicular to three of the sides
        x = -(m_vert[loopLength-1].getY() - m_vert[0].getY());
        y = (m_vert[loopLength-1].getX() - m_vert[0].getX());
        // the vector needs to be normalized to make it's length 1
        axes[loopLength-1] = Utility.normalize(new Point2D.Double(x,y));
        axes[1] = new Point2D.Double(-axes[1].getX(), -axes[1].getY());
        
        loopLength = targetsVert.length;
        for (int i = 1; i < loopLength; i++) {
            
            // finds the x and y of a vector perpendicular to three of the sides
            x = -(targetsVert[i].getY() - targetsVert[i-1].getY());
            y = (targetsVert[i].getX() - targetsVert[i-1].getX());
            // the vector needs to be normalized to make it's length 1
            axes[m_vert.length + i - 1] = Utility.normalize(new Point2D.Double(x,y));
        }
                // finds the x and y of a vector perpendicular to three of the sides
        x = -(targetsVert[loopLength - 1].getY() - m_vert[0].getY());
        y = (targetsVert[loopLength- 1].getX() - m_vert[0].getX());
        // the vector needs to be normalized to make it's length 1
        axes[loopLength + m_vert.length -1] = Utility.normalize(new Point2D.Double(x,y));

        double p1min = 0;
        double p1max = p1min;
        double p2min = 0;
        double p2max = p2min;   
        Point2D.Double[] collisionPoints = new Point2D.Double[m_vert.length];
        for (int i = 0; i < collisionPoints.length; i++) {
            collisionPoints[i] = new Point2D.Double(m_vert[i].getX(),m_vert[i].getY());
        }
        Point2D.Double[] targetsCollisionPoints = new Point2D.Double[targetsVert.length];
        for (int i = 0; i < targetsCollisionPoints.length; i++) {
            targetsCollisionPoints[i] = new Point2D.Double(targetsVert[i].getX(),targetsVert[i].getY());
        }
        for (Point2D.Double point: collisionPoints) {
            point.setLocation(spritesFutureCoor.getX() - point.getX(), spritesFutureCoor.getY() - point.getY());
        }
        for (Point2D.Double point: targetsCollisionPoints) {
            point.setLocation(targetsCoor.getX() - point.getX(), targetsCoor.getY() - point.getY());
        }
        for (Point2D.Double axis: axes) {
            // get an initial min/max value for this hitbox
            p1min = Utility.vectorDotProduct(axis, collisionPoints[0]);
            p1max = p1min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < collisionPoints.length; i++)
            { 
                double dot = Utility.vectorDotProduct(axis, collisionPoints[i]);
                p1min = Math.min(p1min , dot);
                p1max = Math.max(p1max , dot);
            }
            // get an initial min/max value for targets hitbox
            p2min = Utility.vectorDotProduct(axis, targetsCollisionPoints[0]);
            p2max = p2min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < targetsCollisionPoints.length; i++)
            { 
                double dot = Utility.vectorDotProduct(axis, targetsCollisionPoints[i]);
                p2min = Math.min(p2min, dot);
                p2max = Math.max(p2max, dot);
            }

            // vector offset between the two shapes
            Point2D.Double vOffset = new Point2D.Double(spritesFutureCoor.getX() - targetsCoor.getX(), spritesFutureCoor.getY() - targetsCoor.getY());
            // project that onto the same axis as just used
            double sOffset = Utility.vectorDotProduct(axis, vOffset);
            // that will give you a scaler value that you can add to the min/max of one of the polygons from earlier
            p2min += sOffset;
            p2max += sOffset;
            Point2D.Double tempMTV = new Point2D.Double(Math.abs(axis.getX() * (p1min - p2max)),Math.abs(axis.getY() * (p1min - p2max))) ;

            if (Utility.getVectorMagnitude(tempMTV) < Utility.getVectorMagnitude(mtv) && Utility.getVectorMagnitude(tempMTV) != 0) {
                mtv = tempMTV;
            }
            
            // if a gap is found between the projections, there is no collision detected
            if ( (p1min - p2max > 0) || (p2min - p1max > 0)) {
                return new Point2D.Double(0,0);
            }
            
        }
        return mtv;
    }

    /**
     * This method takes the center of a target game element and returns which side
     * of this hitbox is closest, either front or back.
     * @param targetsCenter
     * @return closest side (front or back) to the targetsCenter
     */
    public abstract int getClosestSide(Point2D.Double targetsCenter);

    /**
     * This method takes an angle and a coordinate of the sprite and 
     * updates the m_vert array with new values. The m_vert array
     * is a list of coordinates that determine the corner points
     * of a rectangular hitbox.
     * @param futureSpriteCoor the future coordinate of the sprite
     * @param futureAngle the future angle of the sprite
     */
    public abstract void computeCollisionPoints(Point2D.Double futureSpriteCoor, double futureAngle);

    /**
     * Draws the hitbox
     * @param g2d 
     */
    public void drawHitbox(Graphics2D g2d) {
        int loopLength = m_vert.length;
        for (int i = 1; i < loopLength; i++) {
            g2d.drawLine((int)m_vert[i].getX(), (int)m_vert[i].getY(), (int)m_vert[i-1].getX(), (int)m_vert[i-1].getY());
        }
        g2d.drawLine((int)m_vert[m_vert.length-1].getX(), (int)m_vert[m_vert.length-1].getY(), (int)m_vert[0].getX(), (int)m_vert[0].getY());
    }

    public Point2D.Double[] getVertices() {
        return m_vert;
    }

    protected void initializeVert(Point2D.Double[] vert) {
        m_vert = vert;
    }

    protected Sprite getSprite() {
        return m_sprite;
    }

    protected Point2D.Double getDimensions() {
        return new Point2D.Double(m_sprite.getWidth(), m_sprite.getHeight());
    }
}
