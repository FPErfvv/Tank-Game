package app;

import java.awt.Point;
import java.util.ArrayList;
import java.awt.Graphics2D;

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
    private int width, height;
    private Prop prop;
    private Point[] collisionPoints;
    private double angle;

    public HitBox(Prop prop, int width, int height, int type) {
        this.prop = prop;
        this.width = width;
        this.height = height;
        this.type = type;
        angle = 0;
        //location = prop.getCoordinates();
        collisionPoints = new Point[4];
        createRectangle();
    }

    // creates a bounding box with the height and width the size of the prop's image
    public HitBox(Prop prop, int type) {
        this.prop = prop;
        this.type = type;
        angle = 0;
        width = prop.getWidth();
        height = prop.getHeight();
        //location = prop.getCoordinates();
        collisionPoints = new Point[4];
    }


    public boolean checkCollision(Point[] points) {
        // TODO: check if points are under or above lines
        // TODO: if they satisfy all requirements, then they are in the boundingbox
        Point topR = collisionPoints[0];
        Point botR = collisionPoints[1];
        Point topL = collisionPoints[2];
        Point botL = collisionPoints[3];
        double lenM = (topR.getY() - botR.getY())/(topR.getX() - botR.getX()); // Slope of the two sides
        double widthM = 1/-lenM; // slope of the top and bottom lines
        Point test = new Point(50,50);
        double angleInDegrees = Math.toDegrees(angle);
        if (angleInDegrees > 0 && angleInDegrees < 90) {
            if (test.getY() - topR.getY() > lenM * (test.getX() - topR.getX())){
                //TODO: finish testing collision detection
            }
        }
        for (Point p: points) {
            
        }
        return true;
    }

    public void createRectangle() {
        width = prop.getWidth();
        height = prop.getHeight();
        angle = prop.getTurnAngle() - (Math.PI/2);
        // top right point *
        int x = (int)(prop.getCoordinates().getX() + (width / 2 * Math.cos(angle) - height / 2 * Math.sin(angle)));
        int y = (int) (prop.getCoordinates().getY() - (width / 2 * Math.sin(angle) + height / 2 * Math.cos(angle)));
        collisionPoints[0] = new Point(x, y);

        // bottom right point
        x = (int) (prop.getCoordinates().getX() + (width / 2 * Math.cos(angle) + height /2 * Math.sin(angle)));
        y = (int) (prop.getCoordinates().getY() - (width / 2 * Math.sin(angle) - height /2 * Math.cos(angle)));
        collisionPoints[1] = new Point(x, y);

        // bottom left *
        x = (int) (prop.getCoordinates().getX() - (width / 2 * Math.cos(angle) - height / 2 * Math.sin(angle)));
        y = (int) (prop.getCoordinates().getY() + (width / 2 * Math.sin(angle) + height / 2 * Math.cos(angle)));
        collisionPoints[3] = new Point(x, y);

        // top left
        x = (int) (prop.getCoordinates().getX() - (width / 2 * Math.cos(angle) + height / 2 * Math.sin(angle)));
        y = (int) (prop.getCoordinates().getY() + (width / 2 * Math.sin(angle) - height / 2 * Math.cos(angle)));
        collisionPoints[2] = new Point(x,y);
    }

    public void drawLines(Graphics2D g2d) {
        // right side
        g2d.drawLine((int)collisionPoints[0].getX(), (int)collisionPoints[0].getY(), (int)collisionPoints[1].getX(), (int)collisionPoints[1].getY());
        // bottom
        g2d.drawLine((int)collisionPoints[1].getX(), (int)collisionPoints[1].getY(), (int)collisionPoints[3].getX(), (int)collisionPoints[3].getY());
        // left side
        g2d.drawLine((int)collisionPoints[2].getX(), (int)collisionPoints[2].getY(), (int)collisionPoints[3].getX(), (int)collisionPoints[3].getY());
        // top
        g2d.drawLine((int)collisionPoints[0].getX(), (int)collisionPoints[0].getY(), (int)collisionPoints[2].getX(), (int)collisionPoints[2].getY());
        Point[] point = getContactPoints();
        for (Point p: point) {
            g2d.fillOval((int)p.getX(), (int)p.getY(), 5, 5);
        }
        g2d.fillOval(200, 200, 10, 10);
    }

    public Point[] getContactPoints() {
        // slope of right side
        ArrayList<Point> points = new ArrayList<Point>();

        for (int i = -height /2; i < height/2; i+=20) {
            int x = (int) (prop.getCoordinates().getX() + (width / 2 * Math.cos(angle) - i * Math.sin(angle)));
            int y = (int) (prop.getCoordinates().getY() - (width / 2 * Math.sin(angle) + i * Math.cos(angle)));
            points.add(new Point(x, y));
            x = (int) (prop.getCoordinates().getX() - (width / 2 * Math.cos(angle) - i * Math.sin(angle)));
            y = (int) (prop.getCoordinates().getY() + (width / 2 * Math.sin(angle) + i * Math.cos(angle)));
            points.add(new Point(x,y));
        }
        for (int i = -width /2; i < width/2; i+=20) {
            int x = (int) (prop.getCoordinates().getX() - (i * Math.cos(angle) - height / 2 * Math.sin(angle)));
            int y = (int) (prop.getCoordinates().getY() + (i * Math.sin(angle) + height / 2 * Math.cos(angle)));
            points.add(new Point(x, y));
            x = (int) (prop.getCoordinates().getX() - (i * Math.cos(angle) + height / 2 * Math.sin(angle)));
            y = (int) (prop.getCoordinates().getY() + (i * Math.sin(angle) - height / 2 * Math.cos(angle)));
            points.add(new Point(x,y));
        }

        Point[] contactPoints = new Point[points.size()];
        for (int i = 0; i < contactPoints.length;i++) {
            contactPoints[i] = points.get(i);
        }
        return contactPoints;
    }
}
