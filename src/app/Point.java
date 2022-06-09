package app;

public class Point {
    public double x;
  
    public double y;

    
    public Point() {
        x = 0;
        y = 0;
    }
    
    public Point(java.awt.Point p) {
        x = p.getX();
        y = p.getY();
    }
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() {
      return x;
    }
    
    public double getY() {
      return y;
    }
    
    public Point getLocation() {
      return this;
    }
    
    public void setLocation(java.awt.Point p) {
        x = p.getX();
        y = p.getY();
    }
    
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void move(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    public boolean equals(java.lang.Object obj) {
      return false;
    }

    public double getMagnitude() {
      return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    public String toString() {
      return "(" + (Math.round(x * 100.00)/100.00) + ", " + Math.round(y * 100.00) / 100.00 + ")"; // rounds it to two decimal places
    }

    public double vectorDotProduct(Point pt2)
    {
       return (x * pt2.x) + (y * pt2.y);
    }

    public static double getDistance(Point pt1, Point pt2) {
      return (Math.sqrt(Math.pow(pt1.getX() - pt2.getX(),2) + Math.pow(pt1.getY() - pt2.getY(),2)));
    }

}
