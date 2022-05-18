package app;

import java.util.ArrayList;
import java.util.Vector;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class HitBox {
    // Equation for rotated elipse: https://math.stackexchange.com/questions/426150/what-is-the-general-equation-of-the-ellipse-that-is-not-in-the-origin-and-rotate
    /**
     * ((x−h)cos(A)+(y−k)sin(A))^2/a^2+((x−h)sin(A)−(y−k)cos(A))^2/b^2=1
     * A--> the rotation in radians (positive turns it counter-clockwise)
     * a--> stretch along the x axis w/ A of 0
     * b--> stretch along the y axis w/ A of 0
     * h--> translation along x axis
     * k--> translation along y axis
     * 
     * if a point is inside the circle, the equations outputs a number less than 1
     * if a point is outside the circle, the equation outputs a number greater than 1
     * TODO: have predetermined points (i.e. north, south, east, west, northeast, southeast, northwest, southwest, etc)
     * when turning rotate those around and use those to check.
     *  */ 
    public static final int CIRCLE = 0;
    public static final int RECTANGLE = 1;
    private int type;
    private Point location;
    private int m_width, m_height;
    private Prop m_prop;
    private Point[] m_collisionPoints;
    private double m_angle;
    private Point[] m_axes;
    private Double[] pts;

    public HitBox(Prop m_prop, int m_width, int m_height, int type) {
        this.m_prop = m_prop;
        this.m_width = m_width;
        this.m_height = m_height;
        this.type = type;
        m_axes = new Point[4];
        m_angle = 0;
        //location = m_prop.getTrueCoordinates();
        m_collisionPoints = new Point[4];
        createRectangle();
        pts = new Double[8];
        pts[0] = 1.0;
        pts[1] = 1.0;
        pts[2] = 1.0;
        pts[3] = 1.0;
    }

    // creates a bounding box with the m_height and m_width the size of the m_prop's image
    public HitBox(Prop m_prop, int type) {
        this.m_prop = m_prop;
        this.type = type;
        m_axes = new Point[4];
        m_angle = 0;
        m_width = m_prop.getWidth();
        m_height = m_prop.getHeight();
        //location = m_prop.getTrueCoordinates();
        m_collisionPoints = new Point[4];
        pts = new Double[8];
        pts[0] = 1.0;
        pts[1] = 1.0;
        pts[2] = 1.0;
        pts[3] = 1.0;
    }


    public boolean checkCollision(Point[] points) {
        // TODO: check if points are under or above lines
        // TODO: if they satisfy all requirements, then they are in the boundingbox
        Point topR = m_collisionPoints[0];
        Point botR = m_collisionPoints[1];
        Point topL = m_collisionPoints[2];
        Point botL = m_collisionPoints[3];
        double lenM = (topR.getY() - botR.getY())/(topR.getX() - botR.getX()); // Slope of the two sides
        double widthM = 1/-lenM; // slope of the top and bottom lines
        Point test = new Point(50,50);
        double angleInDegrees = Math.toDegrees(m_angle);
        if (angleInDegrees > 0 && angleInDegrees < 90) {
            if (test.getY() - topR.getY() > lenM * (test.getX() - topR.getX())){
                //TODO: finish testing collision detection
            }
        }
        
        return true;
    }

    // reference: https://www.sevenson.com.au/programming/sat/ 
    // https://dyn4j.org/2010/01/sat/#sat-proj 
    // https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169 
    public boolean SAT(Point[] targetsCollisionPoints, Point targetsCoor) {
        int loopLength = 2; // will be one less than half the # of sides
        for (int i = 1; i <= loopLength; i++) {
            
            // finds the x and y of a vector perpendicular to three of the sides
            double x = -(m_collisionPoints[i].getY() - m_collisionPoints[i-1].getY());
            double y = (m_collisionPoints[i].getX() - m_collisionPoints[i-1].getX());
            // the vector needs to be normalized to make it's length 1
            double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            if (magnitude != 0) {
                x *= 1/magnitude;
                y *= 1/magnitude;
            }
            m_axes[i-1] = new Point(x, y);
        }

        for (int i = 1; i <= loopLength; i++) {
            
            // finds the x and y of a vector perpendicular to three of the sides
            double x = -(targetsCollisionPoints[i].getY() - targetsCollisionPoints[i-1].getY());
            double y = (targetsCollisionPoints[i].getX() - targetsCollisionPoints[i-1].getX());
            // the vector needs to be normalized to make it's length 1
            double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            if (magnitude != 0) {
                x *= 1/magnitude;
                y *= 1/magnitude;
            }
            m_axes[i+1] = new Point(x, y);
        }

        double p1min = 0;
        double p1max = p1min;
        double p2min = 0;
        double p2max = p2min;   
        for (Point point: m_collisionPoints) {
            point.setLocation(m_prop.getRelativeCoordinates().getX() - point.getX(), m_prop.getRelativeCoordinates().getY() - point.getY());
        }
        for (Point point: targetsCollisionPoints) {
            point.setLocation(targetsCoor.getX() - point.getX(), targetsCoor.getY() - point.getY());
        }
        for (Point axis: m_axes) {
            // get an initial min/max value for this hitbox
            p1min = vectorDotProduct(axis, m_collisionPoints[0]);
            p1max = p1min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < m_collisionPoints.length; i++)
            { 
                double dot = vectorDotProduct(axis, m_collisionPoints[i]);
                p1min = Math.min(p1min , dot);
                p1max = Math.max(p1max , dot);
            }
            // get an initial min/max value for targets hitbox
            p2min = vectorDotProduct(axis, targetsCollisionPoints[0]);
            p2max = p2min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < targetsCollisionPoints.length; i++)
            { 
                double dot = vectorDotProduct(axis, targetsCollisionPoints[i]);
                p2min = Math.min(p2min, dot);
                p2max = Math.max(p2max, dot);
            }
            // vector offset between the two shapes
            
            Point vOffset = new Point(m_prop.getRelativeCoordinates().getX() - targetsCoor.getX(), m_prop.getRelativeCoordinates().getY() - targetsCoor.getY());
            // project that onto the same axis as just used
            double sOffset = vectorDotProduct(axis, vOffset);
            // that will give you a scaler value that you can add to the min/max of one of the polygons from earlier
            // TODO: error in SAT stems from whether offset is added to p2 or p1
            p2min += sOffset;
            p2max += sOffset;

            if ( (p1min - p2max > 0) || (p2min - p1max > 0)) {
                return false;
            }
            
        }
        
        System.out.println("IT COLLIDED!!!!!!!!!!!!!!!!!!!!");
        return true;
    }

    public void createRectangle() {
        
        m_width = m_prop.getWidth();
        m_height = m_prop.getHeight();
        m_angle = m_prop.getTurnAngle() - (Math.PI/2);
        // top right point *
        int x = (int)(m_prop.getTrueCoordinates().getX() + (m_width / 2 * Math.cos(m_angle) - m_height / 2 * Math.sin(m_angle)));
        int y = (int) (m_prop.getTrueCoordinates().getY() - (m_width / 2 * Math.sin(m_angle) + m_height / 2 * Math.cos(m_angle)));
        m_collisionPoints[0] = new Point(x, y);

        // bottom right point
        x = (int) (m_prop.getTrueCoordinates().getX() + (m_width / 2 * Math.cos(m_angle) + m_height /2 * Math.sin(m_angle)));
        y = (int) (m_prop.getTrueCoordinates().getY() - (m_width / 2 * Math.sin(m_angle) - m_height /2 * Math.cos(m_angle)));
        m_collisionPoints[1] = new Point(x, y);

        // bottom left *
        x = (int) (m_prop.getTrueCoordinates().getX() - (m_width / 2 * Math.cos(m_angle) - m_height / 2 * Math.sin(m_angle)));
        y = (int) (m_prop.getTrueCoordinates().getY() + (m_width / 2 * Math.sin(m_angle) + m_height / 2 * Math.cos(m_angle)));
        m_collisionPoints[2] = new Point(x, y);

        // top left
        x = (int) (m_prop.getTrueCoordinates().getX() - (m_width / 2 * Math.cos(m_angle) + m_height / 2 * Math.sin(m_angle)));
        y = (int) (m_prop.getTrueCoordinates().getY() + (m_width / 2 * Math.sin(m_angle) - m_height / 2 * Math.cos(m_angle)));
        m_collisionPoints[3] = new Point(x,y);


    }

    public void drawLines(Graphics2D g2d) {
        // right side
        g2d.drawLine((int)m_collisionPoints[0].getX(), (int)m_collisionPoints[0].getY(), (int)m_collisionPoints[1].getX(), (int)m_collisionPoints[1].getY());
        // bottom
        g2d.drawLine((int)m_collisionPoints[1].getX(), (int)m_collisionPoints[1].getY(), (int)m_collisionPoints[2].getX(), (int)m_collisionPoints[2].getY());
        // left side
        g2d.drawLine((int)m_collisionPoints[2].getX(), (int)m_collisionPoints[2].getY(), (int)m_collisionPoints[3].getX(), (int)m_collisionPoints[3].getY());
        // top
        g2d.drawLine((int)m_collisionPoints[0].getX(), (int)m_collisionPoints[0].getY(), (int)m_collisionPoints[3].getX(), (int)m_collisionPoints[3].getY());
    }

    public double vectorDotProduct(Point pt1, Point pt2)
    {
       return (pt1.x * pt2.x) + (pt1.y * pt2.y);
    }

    public Point[] getCollisionPoints() {
        return m_collisionPoints;
    }
}
