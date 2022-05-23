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
        m_axes = new Point[6];
        m_angle = 0;
        //location = m_prop.getTrueCoordinates();
        m_collisionPoints = new Point[4];
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
        m_axes = new Point[6];
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

    // reference: https://www.sevenson.com.au/programming/sat/ 
    // https://dyn4j.org/2010/01/sat/#sat-proj 
    // https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169 
    public Point SAT(Point[] targetsCollisionPoints, Point targetsCoor, Point propsFutureCoor) {
        Point mtv = new Point(0,0);
        int loopLength = 3; // will be one less than half the # of sides
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
            m_axes[i+2] = new Point(x, y);
        }

        double p1min = 0;
        double p1max = p1min;
        double p2min = 0;
        double p2max = p2min;   
        //System.out.println(m_axes[0].getX() + ", " + m_axes[0].getY() + ") (" + m_axes[1].getX() + ", " + m_axes[1].getY());
        int counter = 0;
        for (Point point: m_collisionPoints) {
            point.setLocation(propsFutureCoor.getX() - point.getX(), propsFutureCoor.getY() - point.getY());
        }
        for (Point point: targetsCollisionPoints) {
            point.setLocation(targetsCoor.getX() - point.getX(), targetsCoor.getY() - point.getY());
        }
        for (Point axis: m_axes) {
            counter++;
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
            
            Point vOffset = new Point(propsFutureCoor.getX() - targetsCoor.getX(), propsFutureCoor.getY() - targetsCoor.getY());
            // project that onto the same axis as just used
            double sOffset = vectorDotProduct(axis, vOffset);
            // that will give you a scaler value that you can add to the min/max of one of the polygons from earlier
            // TODO: error in SAT stems from whether offset is added to p2 or p1
            p2min += sOffset;
            p2max += sOffset;
            if (counter == 3) {
                //System.out.print(" Counter 3: " + Math.round(p1min - p2max));
            } if (counter == 4) {
                //System.out.print(" Counter 4: " + Math.round(p1min - p2max));
            }
            if (counter == 2) {
                mtv = new Point(axis.getX() * (int) Math.round(p1min - p2max),axis.getX() * (int) Math.round(p1min - p2max));
                //System.out.println(" Counter 2: " + Math.round(p1min - p2max));
                pts[0] = p1min;
                pts[1] = p1max;
                pts[2] = p2min;
                pts[3] = p2max;
            } else {
                //System.out.println(" Counter 1: " + Math.round(p1min - p2max));
                pts[4] = p1min;
                pts[5] = p1max;
                pts[6] = p2min;
                pts[7] = p2max;
            }
            
            if ( (p1min - p2max > 0) || (p2min - p1max > 0)) {
                return new Point(0,0);
            }
            
        }
        System.out.print("it collided");
        return mtv;
    }

    public void setTurnAngle() {
        m_angle = m_prop.getTurnAngle() - (Math.PI/2);
    }

    public void createRectangle(Point propCoor, double futureAngle) {    
        m_width = m_prop.getWidth();
        m_height = m_prop.getHeight();
        futureAngle -= (Math.PI/2);
        // top right point *
        int x = (int)(propCoor.getX() + (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
        int y = (int) (propCoor.getY() - (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
        m_collisionPoints[0] = new Point(x, y);

        // bottom right point
        x = (int) (propCoor.getX() + (m_width / 2 * Math.cos(futureAngle) + m_height /2 * Math.sin(futureAngle)));
        y = (int) (propCoor.getY() - (m_width / 2 * Math.sin(futureAngle) - m_height /2 * Math.cos(futureAngle)));
        m_collisionPoints[1] = new Point(x, y);

        // bottom left *
        x = (int) (propCoor.getX() - (m_width / 2 * Math.cos(futureAngle) - m_height / 2 * Math.sin(futureAngle)));
        y = (int) (propCoor.getY() + (m_width / 2 * Math.sin(futureAngle) + m_height / 2 * Math.cos(futureAngle)));
        m_collisionPoints[2] = new Point(x, y);

        // top left
        x = (int) (propCoor.getX() - (m_width / 2 * Math.cos(futureAngle) + m_height / 2 * Math.sin(futureAngle)));
        y = (int) (propCoor.getY() + (m_width / 2 * Math.sin(futureAngle) - m_height / 2 * Math.cos(futureAngle)));
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

        g2d.drawString("p1MIN", 100, 100 + Math.round(pts[0]/3));
        g2d.drawString("p1MAX", 100, 100 + Math.round(pts[1]/3));
        g2d.drawString("p2MIN", 100, 100 + Math.round(pts[2]/3));
        g2d.drawString("p2MAX", 100, 100 + Math.round(pts[3]/3));
    }

    public double vectorDotProduct(Point pt1, Point pt2)
    {
       return (pt1.x * pt2.x) + (pt1.y * pt2.y);
    }

    public Point[] getCollisionPoints() {
        return m_collisionPoints;
    }
}
