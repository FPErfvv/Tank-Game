package app;

import java.awt.Graphics2D;

public class HitBox {

    public static final int CIRCLE = 0;
    public static final int RECTANGLE = 1;
    private int m_type;
    private Point location;
    private int m_width, m_height;
    private Prop m_prop;
    // An array of points used to create the shape of the bounding box.
    private Point[] m_vert;
    private double m_angle;
    private Double[] pts;

    public HitBox(Prop m_prop, int m_width, int m_height, int type) {
        this.m_prop = m_prop;
        this.m_width = m_width;
        this.m_height = m_height;
        m_type = type;
        m_angle = 0;
        if (type == Constants.RECTANGLE) {
            m_vert = new Point[4];
        } else if (type == Constants.COW) {
            m_vert = new Point[6];
        }
    }

    // creates a bounding box with the m_height and m_width the size of the m_prop's image
    public HitBox(Prop m_prop, int type) {
        this.m_prop = m_prop;
        m_type = type;
        m_angle = 0;
        m_width = m_prop.getWidth();
        m_height = m_prop.getHeight();
        
        if (type == Constants.RECTANGLE) {
            m_vert = new Point[4];
        } else if (type == Constants.COW) {
            m_vert = new Point[6];
        }

    }

    // reference: https://www.sevenson.com.au/programming/sat/ 
    // https://dyn4j.org/2010/01/sat/#sat-proj 
    // https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169 
    public Point SAT(Point[] targetsVert, Point targetsCoor, Point propsFutureCoor) {
        Point mtv = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        int loopLength = m_vert.length; // will be one less than half the # of sides
        double x = 0;
        double y = 0;
        Point[] axes = new Point[m_vert.length + targetsVert.length];
        for (int i = 1; i < loopLength; i++) {

            // finds the x and y of a vector perpendicular to three of the sides
            x = -(m_vert[i].getY() - m_vert[i-1].getY());
            y = (m_vert[i].getX() - m_vert[i-1].getX());
            // the vector needs to be normalized to make it's length 1
            axes[i-1] = Constants.normalize(new Point(x,y));
        }
        // finds the x and y of a vector perpendicular to three of the sides
        x = -(m_vert[loopLength-1].getY() - m_vert[0].getY());
        y = (m_vert[loopLength-1].getX() - m_vert[0].getX());
        // the vector needs to be normalized to make it's length 1
        axes[loopLength-1] = Constants.normalize(new Point(x,y));
        axes[1] = new Point(-axes[1].getX(), -axes[1].getY());
        
        loopLength = targetsVert.length;
        for (int i = 1; i < loopLength; i++) {
            
            // finds the x and y of a vector perpendicular to three of the sides
            x = -(targetsVert[i].getY() - targetsVert[i-1].getY());
            y = (targetsVert[i].getX() - targetsVert[i-1].getX());
            // the vector needs to be normalized to make it's length 1
            axes[m_vert.length + i - 1] = Constants.normalize(new Point(x,y));
        }
                // finds the x and y of a vector perpendicular to three of the sides
        x = -(targetsVert[loopLength - 1].getY() - m_vert[0].getY());
        y = (targetsVert[loopLength- 1].getX() - m_vert[0].getX());
        // the vector needs to be normalized to make it's length 1
        axes[loopLength + m_vert.length -1] = Constants.normalize(new Point(x,y));

        double p1min = 0;
        double p1max = p1min;
        double p2min = 0;
        double p2max = p2min;   
        Point[] collisionPoints = new Point[m_vert.length];
        for (int i = 0; i < collisionPoints.length; i++) {
            collisionPoints[i] = new Point(m_vert[i].getX(),m_vert[i].getY());
        }
        Point[] targetsCollisionPoints = new Point[targetsVert.length];
        for (int i = 0; i < targetsCollisionPoints.length; i++) {
            targetsCollisionPoints[i] = new Point(targetsVert[i].getX(),targetsVert[i].getY());
        }
        for (Point point: collisionPoints) {
            point.setLocation(propsFutureCoor.getX() - point.getX(), propsFutureCoor.getY() - point.getY());
        }
        for (Point point: targetsCollisionPoints) {
            point.setLocation(targetsCoor.getX() - point.getX(), targetsCoor.getY() - point.getY());
        }
        for (Point axis: axes) {
            // get an initial min/max value for this hitbox
            p1min = axis.vectorDotProduct(collisionPoints[0]);
            p1max = p1min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < collisionPoints.length; i++)
            { 
                double dot = axis.vectorDotProduct(collisionPoints[i]);
                p1min = Math.min(p1min , dot);
                p1max = Math.max(p1max , dot);
            }
            // get an initial min/max value for targets hitbox
            p2min = axis.vectorDotProduct(targetsCollisionPoints[0]);
            p2max = p2min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < targetsCollisionPoints.length; i++)
            { 
                double dot = axis.vectorDotProduct(targetsCollisionPoints[i]);
                p2min = Math.min(p2min, dot);
                p2max = Math.max(p2max, dot);
            }

            // vector offset between the two shapes
            Point vOffset = new Point(propsFutureCoor.getX() - targetsCoor.getX(), propsFutureCoor.getY() - targetsCoor.getY());
            // project that onto the same axis as just used
            double sOffset = axis.vectorDotProduct(vOffset);
            // that will give you a scaler value that you can add to the min/max of one of the polygons from earlier
            p2min += sOffset;
            p2max += sOffset;
            Point tempMTV = new Point(Math.abs(axis.getX() * (p1min - p2max)),Math.abs(axis.getY() * (p1min - p2max))) ;

            if (tempMTV.getMagnitude() < mtv.getMagnitude() && tempMTV.getMagnitude() != 0) {
                mtv = tempMTV;
            }
            
            // if a gap is found between the projections, there is no collision detected
            if ( (p1min - p2max > 0) || (p2min - p1max > 0)) {
                return new Point(0,0);
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
    public int getClosestSide(Point targetsCenter) {
        int indexOfClosestPoint = 0;
        double smallestDistance = Double.MAX_VALUE;
        for (int i = 0; i < m_vert.length; i++) {
            if (Point.getDistance(targetsCenter, m_vert[i]) < smallestDistance) {
                smallestDistance = Point.getDistance(targetsCenter, m_vert[i]);
                indexOfClosestPoint = i;
            }
        }
        // TODO: adjust this method to allow for different hitboxes other than rect
        return indexOfClosestPoint < 2 ? Constants.FRONT : Constants.BACK;
    }

    public void setTurnAngle() {
        m_angle = m_prop.getTurnAngle() - (Math.PI/2);
    }

    /**
     * This method takes an angle and a coordinate of the prop and 
     * updates the m_vert array with new values. The m_vert array
     * is a list of coordinates that determine the corner points
     * of a rectangular hitbox.
     * @param futurePropCoor the future coordinate of the prop
     * @param futureAngle the future angle of the prop
     */
    public void createRectangle(Point futurePropCoor, double futureAngle) {    
        m_width = m_prop.getWidth();
        m_height = m_prop.getHeight();
        futureAngle -= (Math.PI/2);
        if (m_type == Constants.RECTANGLE) {
            // top left
            int x = (int) (futurePropCoor.getX() - (m_width / 2 * Math.cos(futureAngle) + m_height / 2 * Math.sin(futureAngle)));
            int y = (int) (futurePropCoor.getY() + (m_width / 2 * Math.sin(futureAngle) - m_height / 2 * Math.cos(futureAngle)));
            m_vert[0] = new Point(x,y); 

            //top right
            x = (int)(futurePropCoor.getX() + (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() - (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[1] = new Point(x, y);    

            // bottom right
            x = (int) (futurePropCoor.getX() + (m_width / 2 * Math.cos(futureAngle) + m_height /2 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() - (m_width / 2 * Math.sin(futureAngle) - m_height /2 * Math.cos(futureAngle)));
            m_vert[2] = new Point(x, y);
  
            //bottom left
            x = (int) (futurePropCoor.getX() - (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() + (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[3] = new Point(x, y); 

        } else if (m_type == Constants.COW) {
            // top left
            int x = (int) (futurePropCoor.getX() - (m_width / 4 * Math.cos(futureAngle) + m_height / 2 * Math.sin(futureAngle)));
            int y = (int) (futurePropCoor.getY() + (m_width / 4 * Math.sin(futureAngle) - m_height / 2 * Math.cos(futureAngle)));
            m_vert[0] = new Point(x,y); 

            // top right
            x = (int)(futurePropCoor.getX() + (m_width / 4 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() - (m_width / 4 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[1] = new Point(x, y);  
            
            // middle right
            x = (int) (futurePropCoor.getX() + (m_width / 2 * Math.cos(futureAngle) + m_height / 4 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() - (m_width / 2 * Math.sin(futureAngle) - m_height / 4 * Math.cos(futureAngle)));
            m_vert[2] = new Point(x, y);            

            // bottom right
            x = (int) (futurePropCoor.getX() + (m_width / 2 * Math.cos(futureAngle) + m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() - (m_width / 2 * Math.sin(futureAngle) - m_height / 2 * Math.cos(futureAngle)));
            m_vert[3] = new Point(x, y);
            
            // bottom left
            x = (int) (futurePropCoor.getX() - (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() + (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
            m_vert[4] = new Point(x, y);

            // middle left
            x = (int) (futurePropCoor.getX() - (m_width / 2 * Math.cos(futureAngle) - m_height / 4 * Math.sin(futureAngle)));
            y = (int) (futurePropCoor.getY() + (m_width / 2 * Math.sin(futureAngle) + m_height / 4 * Math.cos(futureAngle)));
            m_vert[5] = new Point(x, y);
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

    public Point[] getCollisionPoints() {
        return m_vert;
    }

    public void setType(int type) {
        m_type = type;
        if (type == Constants.RECTANGLE) {
            m_vert = new Point[4];
        } else if (type == Constants.COW) {
            m_vert = new Point[6];
        }
    }
}
