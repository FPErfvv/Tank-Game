package app;

import java.awt.geom.Point2D;

public class Utility {
    public static double vectorDotProduct(Point2D.Double pt1, Point2D.Double pt2)
    {
       return (pt1.x * pt2.x) + (pt1.y * pt2.y);
    }

    public static double getDistance(Point2D.Double pt1, Point2D.Double pt2) {
      return (Math.sqrt(Math.pow(pt1.getX() - pt2.getX(),2) + Math.pow(pt1.getY() - pt2.getY(),2)));
    }

    public static double getVectorMagnitude(Point2D.Double pt1) {
      return Math.sqrt(Math.pow(pt1.x, 2) + Math.pow(pt1.y, 2));
    }

    public static Point2D.Double addPoints(Point2D.Double pt1, Point2D.Double pt2) {
      return new Point2D.Double(pt1.x + pt2.x, pt1.y + pt2.y);
    }

    public static Point2D.Double normalize(Point2D.Double pt) {
        double magnitude = Math.sqrt(Math.pow(pt.getX(), 2) + Math.pow(pt.getY(), 2));
        if (magnitude != 0) {
            pt.x *= 1/magnitude;
            pt.y *= 1/magnitude;
        }
        return pt;
    }
}
