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
    
    public String toString() {
      return "(" + Math.round(x) + ", " + Math.round(y) + ")";
    }

}
