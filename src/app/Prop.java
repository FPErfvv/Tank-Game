package app;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

public class Prop {
    protected Point coor;
    protected HitBox hitBox;
    protected double turnAngle; // radians
    protected Image image;
    protected MainCharacter mainCharacter;


    public Prop(Point coor, String imagePath) {
        this.coor = coor;
        image = new ImageIcon(imagePath).getImage();
        hitBox = new HitBox(this, 1);
    }

    public Prop(Point coor, String imagePath, double turnAngle) {
        this.coor = coor;
        this.turnAngle = turnAngle;
        image = new ImageIcon(imagePath).getImage();
        hitBox = new HitBox(this, 1);

    }

    public void draw(Graphics2D g2d) {

        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the image
        // the main character is used as the origin-(0,0)
        // this means that when the page is resized, all the props remain the same distance from the character
        tr.translate(mainCharacter.getCoordinates().getX() + coor.getX()- getWidth()/2, mainCharacter.getCoordinates().getY() + coor.getY()- getHeight()/2);


        tr.rotate(
                -turnAngle,
                image.getWidth(null) / 2,
                image.getHeight(null) / 2
        );
        g2d.drawImage(image, tr, null);
        hitBox.createRectangle();
        hitBox.drawLines(g2d);
        incrementTurnAngle();
    }

    public void move(double deltax, double deltay) {
        coor.setLocation(coor.getX() + deltax, coor.getY() + deltay);
    }

    // returns the location of the object
    public Point getCoordinates() {
        return new Point((int)(coor.getX() + mainCharacter.getCoordinates().getX()), (int)(coor.getY() + mainCharacter.getCoordinates().getY()));
    }

    public Point getRelativeCoordinates() {
        return new Point((int)(coor.getX()), (int)(coor.getY()));
    }

    public void incrementTurnAngle() {
        turnAngle += Math.toRadians(1);
    }

    // sets the coordinates of the object
    public void moveTo(Point newCoor) {
        coor = newCoor;
    }
    // sets the props character to a certain image
    public void setImage(String imagePath) {
        image = new ImageIcon(imagePath).getImage();
    }

    public void setMainCharacter(MainCharacter mainCharacter) {
        this.mainCharacter = mainCharacter;
    }

    public void setTurnAngle(double turnAngle) {
        this.turnAngle = turnAngle;
    }

    public double getTurnAngle() {
        return turnAngle;
    }

    public int getWidth() {
        return image.getWidth(null);
    }

    public int getHeight() {
        return image.getHeight(null);
    }


}
