
package app;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class MainCharacter extends Prop {
    private boolean facingDown;
    private double mouseOpp;
    private double mouseAdjacent;
    private Map currentMap;
    public static final Point TRUE_COOR = new Point(MainPanel.frameWidth/2,MainPanel.frameHeight/2);

    public MainCharacter() {

        super(new Point(200,200),"src/images/MainCowPic.png");
        //image = new ImageIcon("src/images/MainCowPic.png").getImage();

        facingDown = false;

    }


    public void moveBackForth(int distance, int direction) {
        if (direction < 0) {
            distance /= 2;
        }
        double mouseHyp = Math.sqrt(Math.pow(mouseOpp, 2)+Math.pow(mouseAdjacent, 2));
        //uses the ratio btw the move distance and the hyp of the triangle created by the mouse and the character
        //to determine how far the character should be moved along the x and y axis
        double sideRatio = distance/mouseHyp; 
        double xTransform = mouseOpp * sideRatio;
        double yTransform = mouseAdjacent * sideRatio;
        double deltax = xTransform * direction;
        double deltay = yTransform * direction;
        currentMap.moveMap(-deltax, -deltay);
        move(deltax, deltay);
        
    }

    public void moveSideways(int distance, int direction) {
        distance /= 3;
        double mouseHyp = Math.sqrt(Math.pow(mouseOpp, 2)+Math.pow(mouseAdjacent, 2));
        //uses the ratio btw the move distance and the hyp of the triangle created by the mouse and the character
        //to determine how far the character should be moved along the x and y axis
        double sideRatio = distance/mouseHyp; 
        double yTransform = mouseOpp * sideRatio *-1;
        double xTransform = mouseAdjacent * sideRatio;
        double deltax = xTransform * direction;
        double deltay = yTransform * direction;
        currentMap.moveMap(deltax, deltay);
        move(deltax, deltay);
    }


    @Override
    public void draw(Graphics2D g2d) {

        
        AffineTransform tr = new AffineTransform();
        // X and Y are the coordinates of the image
        tr.translate(TRUE_COOR.getX() - getWidth()/2, TRUE_COOR.getY() - getHeight()/2);


        tr.rotate(
                -turnAngle,
                image.getWidth(null) / 2,
                image.getHeight(null) / 2
        );
        g2d.drawImage(image, tr, null);
        hitBox.createRectangle();
        hitBox.drawLines(g2d);
        incrementTurnAngle();
        hitBox.getContactPoints();
    }

    public int getWidth() {
        return image.getWidth(null);
    }

    public int getHeight() {
        return image.getHeight(null);
    }

    public void updateInfo(double updatedTurnAngle, boolean updatedFacingDown, double mouseOpp, double mouseAdjacent) {
        turnAngle = updatedTurnAngle;
        facingDown = updatedFacingDown;
        if (facingDown) {
            turnAngle = 3.1415926 + turnAngle;
        }
        this.mouseAdjacent = mouseAdjacent;
        this.mouseOpp = mouseOpp;
        TRUE_COOR.setLocation(MainPanel.frameWidth/2, MainPanel.frameHeight/2);
    }

    public void setCurrentMap(Map map) {
        currentMap = map;
    }

    public Point getCoordinates() {
        return TRUE_COOR;
    }

}