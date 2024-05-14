package app;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Utility {
    public static double vectorDotProduct(Point2D.Double pt1, Point2D.Double pt2)
    {
       return pt1.x * pt2.x + pt1.y * pt2.y;
    }

    public static double getDistance(Point2D.Double pt1, Point2D.Double pt2) {
      return Math.sqrt(Math.pow(pt1.getX() - pt2.getX(),2) + Math.pow(pt1.getY() - pt2.getY(),2));
    }

    public static double getVectorMagnitude(Point2D.Double pt1) {
      return Math.sqrt(Math.pow(pt1.getX(), 2) + Math.pow(pt1.getY(), 2));
    }

    public static Point2D.Double addPoints(Point2D.Double pt1, Point2D.Double pt2) {
    	Point2D.Double newPt = new Point2D.Double(pt1.getX()+pt2.getX(), pt1.getY()+pt2.getY());
    	return newPt;
    }

    /**
     * Takes in a Point2D object and normalizes its x and y components as long as its magnitude is not 0
     * @param pt
     */
    public static void normalize(Point2D.Double pt) {
    	double magnitude = getVectorMagnitude(pt);
        if (magnitude != 0) {
            pt.x *= 1 / magnitude;
            pt.y *= 1 / magnitude;
        }
    }

    public static Point2D.Double average(ArrayList<Point2D.Double> array) {
    	if (array.size() != 0) {
    		int x = 0, y = 0;
    		for (int i = 0; i < array.size(); i++) {
    			x += array.get(i).getX();
    		  	y += array.get(i).getY();
    		}
    	  
    		return new Point2D.Double(x / array.size(), y / array.size());
    	}
    	else {
    		return new Point2D.Double(0, 0);
    	}
    }
    
    /**
     * Takes the center of a sprite and returns which coordinate
     * from the list of coordinates is closest.
     * 
     * @param coors
     * @return closest coordinate to the center of the sprite   
     */
    public Point2D.Double getClosestCoor(Point2D.Double coor, Point2D.Double[] coors) {
        Double smallestDistance = Double.MAX_VALUE;
        Point2D.Double closestCoor = new Point2D.Double(0, 0);
        for (Point2D.Double pt : coors) {
            double distance = Utility.getDistance(pt, coor);
            if (distance < smallestDistance) {
                closestCoor = new Point2D.Double(pt.getX(),pt.getY());
                smallestDistance = distance;
            }
        }
        return closestCoor;
    }
}
