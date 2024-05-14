package app.gameElements.hitbox;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Point2D;

import app.Constants;
import app.Utility;
import app.gameElements.Sprite;

public abstract class Hitbox {
    public static final int CIRCLE = 0;
    public static final int RECTANGLE = 1;
    
    private double m_width, m_height;
    
    /**
     * Position of each point relative to the rotating position of the figure,
     * which is at the center of its image bounding box
     */
    private Point2D.Double[] m_model;
    /**
     * Points relative to the world
     */
    private Point2D.Double[] m_vert;

    // creates a bounding box with the m_dimensions.getY() and m_dimensions.getX() the size of the m_sprite's image
    public Hitbox(Point2D.Double[] model, double imageWidth, double imageHeight) {
    	// creates a copy of the model rather than taking the memory address of the parameter
        m_model = new Point2D.Double[model.length];
        m_vert = new Point2D.Double[model.length];
        m_width = imageWidth;
        m_height = imageHeight;
        for(int i = 0; i < model.length; i++) {
        	// scale each point of the model to the bounds of the image
        	double x = model[i].getX() * m_height;
        	double y = model[i].getY() * m_width;
        	m_model[i] = new Point2D.Double(x, y);
        	m_vert[i] = new Point2D.Double(x, y);
        }
    }

    /**
     * Separated Axis Theorem: a concave polygon collision detection algorithm.
     * <p>
     * Sources Used:
     * <p>
     * reference: https://www.sevenson.com.au/programming/sat/ 
     * <p>
     * https://dyn4j.org/2010/01/sat/#sat-proj 
     * <p>
     * https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169
     * <p>
     * https://github.com/OneLoneCoder/Javidx9/blob/master/PixelGameEngine/SmallerProjects/OneLoneCoder_PGE_PolygonCollisions1.cpp
     * @param targetsVert
     * @param targetsCoor
     * @param spritesFutureCoor
     * @return mtv
     */
    public Point2D.Double SAT(Point2D.Double[] targetsVert, Point2D.Double targetsCoor, Point2D.Double spritesFutureCoor) {
    	// Minimum Translation Vector
    	Point2D.Double mtv = new Point2D.Double(0.0d, 0.0d);
        
        // finds the x and y of a vector perpendicular to three of the sides
    	// Normal to line = -1/m = -dx/dy
        // the vector is then normalized to make its magnitude 1
        Point2D.Double[] axes = new Point2D.Double[m_vert.length + targetsVert.length];
        // list of normalized perpendicular vectors to the sides of both hitboxes
        
        int size = m_vert.length;
        for (int i = 0; i < size; i++) {
        	// the point that comes after index i
        	int b = (i + 1) % size;
            double dx = m_vert[b].x - m_vert[i].x;
            double dy = m_vert[b].y - m_vert[i].y;
            axes[i] = new Point2D.Double(-dy, dx);
            Utility.normalize(axes[i]);
        }
        
        size = targetsVert.length;
        for (int i = 0; i < size; i++) {
        	// the point that comes after index i
        	int b = (i + 1) % size;
            double dx = targetsVert[b].x - targetsVert[i].x;
            double dy = targetsVert[b].y - targetsVert[i].y;
            axes[m_vert.length+i] = new Point2D.Double(-dy, dx);
            Utility.normalize(axes[m_vert.length+i]);
        }
        
        Point2D.Double[] collisionPoints = m_vert;
        Point2D.Double[] targetsCollisionPoints = targetsVert;
        
        // magnitude of mtv
        double mtvLen = Double.MAX_VALUE;
        
        // for each axis, project each point of the polygons to use aabb logic
        for (Point2D.Double axis : axes) {
            // get an initial min/max value for this hitbox
            double p1min = Double.POSITIVE_INFINITY;
            double p1max = Double.NEGATIVE_INFINITY;

            // loop over all the other verts to complete the range
            for (int i = 0; i < collisionPoints.length; i++)
            { 
                double dot = Utility.vectorDotProduct(axis, collisionPoints[i]);
                p1min = Math.min(p1min, dot);
                p1max = Math.max(p1max, dot);
            }
            
            // get an initial min/max value for targets hitbox
            double p2min = Double.POSITIVE_INFINITY;
            double p2max = Double.NEGATIVE_INFINITY;

            // loop over all the other verts to complete the range
            for (int i = 0; i < targetsCollisionPoints.length; i++) {
                double dot = Utility.vectorDotProduct(axis, targetsCollisionPoints[i]);
                p2min = Math.min(p2min, dot);
                p2max = Math.max(p2max, dot);
            }
         	
         	// if a gap is found between the projections, there is no collision detected
            if (p1min-p2max >= 0.0d || p2min-p1max >= 0.0d) {
                return mtv;
            }
            
            // if thread reaches this point, the projected points overlap
            
            // Calculate actual overlap along projected axis, and store the minimum
            double tempMTVlen = Math.min(p1max, p2max) - Math.max(p1min, p2min);
            
            mtvLen = Math.min(mtvLen, tempMTVlen);
        }
        
        // vector offset between the two shapes
        Point2D.Double vOffset = new Point2D.Double(targetsCoor.x - spritesFutureCoor.x, targetsCoor.y - spritesFutureCoor.y);
        
        double sOffset = Utility.getVectorMagnitude(vOffset);
        
        mtv.setLocation(-mtvLen * vOffset.x / sOffset, -mtvLen * vOffset.y / sOffset);
        
        return mtv;
    }

    /**
     * This method takes an angle and a coordinate of the sprite and 
     * updates the m_vert array with new values. The m_vert array
     * is a list of coordinates that determine the corner points
     * of a rectangular hitbox.
     * @param futureSpriteCoor the future coordinate of the sprite
     * @param futureAngle the future angle of the sprite
     */
    public void computeCollisionPoints(Point2D.Double futureSpriteCoor, double futureAngle) {
    	double x = futureSpriteCoor.x;
    	double y = futureSpriteCoor.y;
    	
    	int size = m_vert.length;
    	
    	for(int i = 0; i < size; i++) {
    		double mx = m_model[i].x;
    		double my = m_model[i].y;
    		
    		double cosine = Math.cos(futureAngle);
    		double sine = Math.sin(futureAngle);
    		
    		// the 2d rotation transformation
    		double ptx = mx * cosine - my * sine + x;
    		double pty = my * cosine + mx * sine + y;
    		
    		m_vert[i].setLocation(ptx, pty);
    	}
    }

    /**
     * Draws the hitbox in order of point index
     * @param g2d 
     */
    public void drawHitbox(Graphics2D g2d, Point2D.Double scroll) {
    	g2d.setPaint(Color.WHITE);
    	
    	int size = m_vert.length;
    	
        for (int i = 0; i < size; i++) {
        	int b = (i + 1) % size;
        	
        	double x0 = m_vert[i].x - scroll.x;
        	double x1 = m_vert[b].x - scroll.x;
        	double y0 = m_vert[i].y - scroll.y;
        	double y1 = m_vert[b].y - scroll.y;
        	
            g2d.drawLine((int)x0, (int)y0, (int)x1, (int)y1);
        }
    }
    
    /**
     * Returns the points that form the hitbox model. Each point of the model is the x and y 
     * distance each point is from the axis of rotation if the angle of rotation is 0.
     * @return
     */
    public Point2D.Double[] getModel() {
    	return m_model;
    }

    /**
     * Returns the map coordinates of each point of this hitbox.
     * @return
     */
    public Point2D.Double[] getVertices() {
        return m_vert;
    }

    public Point2D.Double getDimensions() {
        return new Point2D.Double(m_width, m_height);
    }
}
