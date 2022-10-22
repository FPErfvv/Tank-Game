package app.hitbox;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import app.Constants;
import app.Utility;

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
        m_model=new Point2D.Double[model.length];
        m_vert=new Point2D.Double[model.length];
        m_width=imageWidth;
        m_height=imageHeight;
        for(int i=0;i<model.length;i++) {
        	double x=model[i].getX()*m_height;
        	double y=model[i].getY()*m_width;
        	m_model[i]=new Point2D.Double(x,y);
        }
    }

    /**
     * Separated Axis Theorem: a concave polygon collision detection algorithm.
     * <p>
     * Sources Used:
     * reference: https://www.sevenson.com.au/programming/sat/ 
     * https://dyn4j.org/2010/01/sat/#sat-proj 
     * https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169
     * @param targetsVert
     * @param targetsCoor
     * @param spritesFutureCoor
     * @return mtv
     */
    public Point2D.Double SAT(Point2D.Double[] targetsVert, Point2D.Double targetsCoor, Point2D.Double spritesFutureCoor) {
    	// Minimum Translation Vector
        Point2D.Double mtv = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        double dx = 0;
        double dy = 0;
        
        // finds the x and y of a vector perpendicular to three of the sides
    	// -1/m = dx/-dy
        // the vector is then normalized to make its magnitude 1
        Point2D.Double[] axes = new Point2D.Double[m_vert.length+targetsVert.length];
        // list of normalized perpendicular vectors to the sides of both hitboxes
        
        int size = m_vert.length;
        for (int i = 0; i < size; i++) {
            dx = -(m_vert[(i+1)%size].getY()-m_vert[i].getY());
            dy = (m_vert[(i+1)%size].getX()-m_vert[i].getX());
            axes[i] = Utility.normalize(new Point2D.Double(dx,dy));
        }
        
        size = targetsVert.length;
        for (int i = 0; i < size; i++) {
            dx = -(targetsVert[(i+1)%size].getY()-targetsVert[i].getY());
            dy = (targetsVert[(i+1)%size].getX()-targetsVert[i].getX());
            axes[m_vert.length+i] = Utility.normalize(new Point2D.Double(dx,dy));
        }

        Point2D.Double[] collisionPoints = new Point2D.Double[m_vert.length];
        for (int i = 0; i < collisionPoints.length; i++) {
            collisionPoints[i] = new Point2D.Double(spritesFutureCoor.getX()-m_vert[i].getX(),spritesFutureCoor.getY()-m_vert[i].getY());
        }
        Point2D.Double[] targetsCollisionPoints = new Point2D.Double[targetsVert.length];
        for (int i = 0; i < targetsCollisionPoints.length; i++) {
            targetsCollisionPoints[i] = new Point2D.Double(targetsCoor.getX()-targetsVert[i].getX(),targetsCoor.getY()-targetsVert[i].getY());
        }
        
        // magnitude of tempMTV
        double mtvLen=Double.MAX_VALUE;
        
        double p1min = Double.MAX_VALUE;
        double p1max = -Double.MAX_VALUE;
        double p2min = Double.MAX_VALUE;
        double p2max = -Double.MAX_VALUE;   
        for (Point2D.Double axis : axes) {
            // get an initial min/max value for this hitbox
            p1min = Utility.vectorDotProduct(axis, collisionPoints[0]);
            p1max = p1min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < collisionPoints.length; i++)
            { 
                double dot = Utility.vectorDotProduct(axis, collisionPoints[i]);
                p1min = Math.min(p1min, dot);
                p1max = Math.max(p1max, dot);
            }
            // get an initial min/max value for targets hitbox
            p2min = Utility.vectorDotProduct(axis, targetsCollisionPoints[0]);
            p2max = p2min;

            // loop over all the other verts to complete the range
            for (int i = 1; i < targetsCollisionPoints.length; i++) {
                double dot = Utility.vectorDotProduct(axis, targetsCollisionPoints[i]);
                p2min = Math.min(p2min, dot);
                p2max = Math.max(p2max, dot);
            }
            
            // vector offset between the two shapes
            Point2D.Double vOffset = new Point2D.Double(spritesFutureCoor.getX()-targetsCoor.getX(), spritesFutureCoor.getY()-targetsCoor.getY());
            // project that onto the same axis as just used
            double sOffset = Utility.vectorDotProduct(axis, vOffset);
            
            p2min += sOffset;
            p2max += sOffset;
            
            // if a gap is found between the projections, there is no collision detected
            if (p1min-p2max >= 0.0d || p2min-p1max >= 0.0d) {
                return new Point2D.Double(0.0d,0.0d);
            }
            
            // if thread reaches this point, the projected points overlap
            // that will give you a scaler value that you can add to the min/max of one of the polygons from earlier
            Point2D.Double tempMTV = new Point2D.Double(Math.abs(axis.getX() * (p1min-p2max)), Math.abs(axis.getY() * (p1min-p2max)));
            
            double tempMTVlen=Utility.getVectorMagnitude(tempMTV);
            
            if (tempMTVlen < mtvLen) {
                mtv=tempMTV;
                mtvLen=tempMTVlen;
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
    	double x=targetsCenter.getX();
    	double y=targetsCenter.getY();
    	int size=m_vert.length;
    	Point2D.Double closePt=m_model[0];
    	double mindsquared=Double.MAX_VALUE;
    	for(int i=0;i<size;i++) {
    		double ptx=m_vert[i].getX();
    		double pty=m_vert[i].getY();
    		double dsquared=(x-ptx)*(x-ptx)+(y-pty)*(y-pty);
    		if(dsquared<mindsquared) {
    			closePt=m_model[i];
    			mindsquared=dsquared;
    		}
    	}
    	
    	return (closePt.getX()>=0) ? Constants.FRONT : Constants.BACK;
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
    	double x=futureSpriteCoor.getX();
    	double y=futureSpriteCoor.getY();
    	Point2D.Double[] pts=getVertices();
    	int size=pts.length;
    	for(int i=0;i<size;i++) {
    		double mx=m_model[i].getX();
    		double my=m_model[i].getY();
    		double ptx=mx*Math.cos(-futureAngle)-my*Math.sin(-futureAngle)+x;
    		double pty=my*Math.cos(-futureAngle)+mx*Math.sin(-futureAngle)+y;
    		pts[i]=new Point2D.Double(ptx,pty);
    	}
    }

    /**
     * Draws the hitbox in order of point index
     * @param g2d 
     */
    public void drawHitbox(Graphics2D g2d) {
        int size=m_vert.length;
        for (int i=0;i<size;i++) {
        	double x0=m_vert[i].getX();
        	double x1=m_vert[(i+1)%size].getX();
        	double y0=m_vert[i].getY();
        	double y1=m_vert[(i+1)%size].getY();
            g2d.drawLine((int)x0, (int)y0, (int)x1, (int)y1);
        }
    }
    
    public Point2D.Double[] getModel() {
    	return m_model;
    }

    public Point2D.Double[] getVertices() {
        return m_vert;
    }

    protected Point2D.Double getDimensions() {
        return new Point2D.Double(m_width, m_height);
    }
}
