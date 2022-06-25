package app;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class HitBox {

    public static final int CIRCLE = 0;
    public static final int RECTANGLE = 1;
    private int m_type;
    private int m_width, m_height;
    private Sprite m_sprite;
    // An array of points used to create the shape of the bounding box.
    private Point2D.Double[] m_vert;

    // creates a bounding box with the m_height and m_width the size of the m_sprite's image
    public HitBox(Sprite sprite, int type) {
        m_sprite = sprite;
        m_type = type;
        m_width = m_sprite.getWidth();
        m_height = m_sprite.getHeight();
        
        if (type == Constants.RECTANGLE) {
            m_vert = new Point2D.Double[4];
        } else if (type == Constants.COW) {
            m_vert = new Point2D.Double[6];
        }
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
    public int getClosestSide(Point2D.Double targetsCenter) {
        int indexOfClosestPoint = 0;
        double smallestDistance = Double.MAX_VALUE;
        for (int i = 0; i < m_vert.length; i++) {
            if (Utility.getDistance(targetsCenter, m_vert[i]) < smallestDistance) {
                smallestDistance = Utility.getDistance(targetsCenter, m_vert[i]);
                indexOfClosestPoint = i;
            }
        }
        // TODO: adjust this method to allow for different hitboxes other than rect
        return indexOfClosestPoint < 2 ? Constants.FRONT : Constants.BACK;
    }

    /**
     * This method takes an angle and a coordinate of the sprite and 
     * updates the m_vert array with new values. The m_vert array
     * is a list of coordinates that determine the corner points
     * of a rectangular hitbox.
     * @param futureSpriteCoor the future coordinate of the sprite
     * @param futureAngle the future angle of the sprite
     */
    public void createHitbox(Point2D.Double futureSpriteCoor, double futureAngle) {    
        m_width = m_sprite.getWidth();
        m_height = m_sprite.getHeight();
        futureAngle -= (Math.PI/2);
        if (m_type == Constants.RECTANGLE) {
            // top left
            int x = (int) (futureSpriteCoor.getX() - (m_width / 2 * Math.cos(futureAngle) + m_height / 2 * Math.sin(futureAngle)));
            int y = (int) (futureSpriteCoor.getY() + (m_width / 2 * Math.sin(futureAngle) - m_height / 2 * Math.cos(futureAngle)));
            m_vert[0] = new Point2D.Double(x,y); 

            //top right
            x = (int)(futureSpriteCoor.getX() + (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() - (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[1] = new Point2D.Double(x, y);    

            // bottom right
            x = (int) (futureSpriteCoor.getX() + (m_width / 2 * Math.cos(futureAngle) + m_height /2 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() - (m_width / 2 * Math.sin(futureAngle) - m_height /2 * Math.cos(futureAngle)));
            m_vert[2] = new Point2D.Double(x, y);
  
            //bottom left
            x = (int) (futureSpriteCoor.getX() - (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() + (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[3] = new Point2D.Double(x, y); 

        } else if (m_type == Constants.COW) {
            // top left
            int x = (int) (futureSpriteCoor.getX() - (m_width / 4 * Math.cos(futureAngle) + m_height / 2 * Math.sin(futureAngle)));
            int y = (int) (futureSpriteCoor.getY() + (m_width / 4 * Math.sin(futureAngle) - m_height / 2 * Math.cos(futureAngle)));
            m_vert[0] = new Point2D.Double(x,y); 

            // top right
            x = (int)(futureSpriteCoor.getX() + (m_width / 4 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() - (m_width / 4 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[1] = new Point2D.Double(x, y);  
            
            // middle right
            x = (int) (futureSpriteCoor.getX() + (m_width / 2 * Math.cos(futureAngle) + m_height / 4 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() - (m_width / 2 * Math.sin(futureAngle) - m_height / 4 * Math.cos(futureAngle)));
            m_vert[2] = new Point2D.Double(x, y);            

            // bottom right
            x = (int) (futureSpriteCoor.getX() + (m_width / 2 * Math.cos(futureAngle) + m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() - (m_width / 2 * Math.sin(futureAngle) - m_height / 2 * Math.cos(futureAngle)));
            m_vert[3] = new Point2D.Double(x, y);
            
            // bottom left
            x = (int) (futureSpriteCoor.getX() - (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() + (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[4] = new Point2D.Double(x, y);

            // middle left
            x = (int) (futureSpriteCoor.getX() - (m_width / 2 * Math.cos(futureAngle) - m_height / 4 * Math.sin(futureAngle)));
            y = (int) (futureSpriteCoor.getY() + (m_width / 2 * Math.sin(futureAngle) + m_height / 4 * Math.cos(futureAngle)));
            m_vert[5] = new Point2D.Double(x, y);
        }

    }

    /**
     * Draws the hitbox
     * @param g2d 
     */
    public void drawHitBox(Graphics2D g2d) {
        int loopLength = m_vert.length;
        for (int i = 1; i < loopLength; i++) {
            g2d.drawLine((int)m_vert[i].getX(), (int)m_vert[i].getY(), (int)m_vert[i-1].getX(), (int)m_vert[i-1].getY());
        }
        g2d.drawLine((int)m_vert[m_vert.length-1].getX(), (int)m_vert[m_vert.length-1].getY(), (int)m_vert[0].getX(), (int)m_vert[0].getY());
    }

    public Point2D.Double[] getCollisionPoints() {
        return m_vert;
    }

    public void setType(int type) {
        m_type = type;
        if (type == Constants.RECTANGLE) {
            m_vert = new Point2D.Double[4];
        } else if (type == Constants.COW) {
            m_vert = new Point2D.Double[6];
        }
    }
}
