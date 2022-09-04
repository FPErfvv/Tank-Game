package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.JPanel;



public class GameMap extends JPanel {

    private final List<Sprite> spriteList;
    private Point2D.Double offset;
    private MainCharacter m_mainCharacter;
    private Graphics2D m_g2d;

    public GameMap() {
        offset = new Point2D.Double(0,0);
        setBackground(new Color(0,154,23));
        setVisible(true);
        setFocusable(true);
        spriteList = new ArrayList<Sprite>();
        populateList();
    }
    


    public void populateList() {
        
        for (int i = 0; i < 1; i++) {
            //Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
            Point2D.Double coor = new Point2D.Double(100,0);
            Sprite sprite = new Sprite(coor, "src/images/MainCowPic.png", Math.toRadians(0), this);        
            spriteList.add(sprite);

            Sprite g = new Sprite(coor, "src/images/Cow50Cal.png", Math.toRadians(0), this);
            spriteList.add(g);

        }
                
        
    }

    public void addSprite() {
        //Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
        Point2D.Double coor = new Point2D.Double(0,50);
        Sprite sprite = new Sprite(coor, "src/images/MainCowPic.png", Math.toRadians(0), this);        
        sprite.setSpeed(5);
        sprite.turn(Constants.TURNING_LEFT);
        spriteList.add(sprite);

        Sprite g = new Sprite(coor, "src/images/Cow50Cal.png", Math.toRadians(0), this);
        g.setSpeed(5);
        g.turn(Constants.TURNING_LEFT);
        spriteList.add(g);
        System.out.println(spriteList.size());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        m_g2d = (Graphics2D) g;
        m_g2d.translate(getWidth()/2, getHeight()/2);
        for (Sprite p: spriteList) {
            p.draw(m_g2d);
        }
        m_mainCharacter.draw(m_g2d);
        
    }

    public void setMainCharacter(MainCharacter mainCharacter) {
        m_mainCharacter = mainCharacter;
    }

    public void moveMap(Point2D.Double mapVel) {
        offset = Utility.addPoints(mapVel, offset);
        for (Sprite p: spriteList) {
            p.moveWithMap(mapVel);
        }
    }

    public void translateOrigin() {
        m_g2d.translate(getWidth()/2, -getHeight()/2);
    }

    public Point2D.Double getOffset() {
        return new Point2D.Double(offset.getX(),offset.getY());
    }

    public List<Sprite> getSpriteList() {
        return Collections.unmodifiableList(spriteList);
    }

    /**
     * Links to Terrain Generation
     * https://www.procjam.com/tutorials/en/ooze/
     * https://gamedev.stackexchange.com/questions/186194/how-to-randomly-generate-biome-with-perlin-noise
     * https://www.redblobgames.com/x/1721-voronoi-alternative/
     * 
     * Link to hexagon class + hexagon grid
     * https://github.com/javagl/Hexagon
     **/ 
}
