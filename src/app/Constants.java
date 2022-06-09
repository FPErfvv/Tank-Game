package app;

public class Constants {
    
    public static final int DIRECTION_LEFT = -1;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_FORWARDS = 1;
    public static final int DIRECTION_BACKWARDS = -1;

    public static final int QUADRANT_I = 1;
    public static final int QUADRANT_II = 2;
    public static final int QUADRANT_III = 3;
    public static final int QUADRANT_IV = 4;

    public static final int TURNING_LEFT = 1;
    public static final int TURNING_RIGHT = -1;

    // Hitbox
    public static final int TOP_RIGHT_VERT = 0;
    public static final int BOTTOM_RIGHT_VERT = 1;
    public static final int BOTTOM_LEFT_VERT = 2;
    public static final int TOP_LEFT_VERT = 3;

    // Hitbox Type
    public static final int RECTANGLE = 0;
    public static final int CIRCLE = 1;
    public static final int COW = 2;

    // Hitbox direction of collision
    public static final int FRONT = 0;
    public static final int BACK = 1;

    public static Point addPoints(Point pt1, Point pt2) {
        return new Point(pt1.getX() + pt2.getX(), pt1.getY() + pt2.getY());
    }

    public static Point normalize(Point pt) {
        double magnitude = Math.sqrt(Math.pow(pt.getX(), 2) + Math.pow(pt.getY(), 2));
        if (magnitude != 0) {
            pt.x *= 1/magnitude;
            pt.y *= 1/magnitude;
        }
        return pt;
    }
}
