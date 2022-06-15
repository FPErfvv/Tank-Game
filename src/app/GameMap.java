package app;

import java.util.ArrayList;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.JPanel;



public class GameMap extends JPanel {

    private ArrayList<Sprite> spriteList;
    private double xOffset;
    private double yOffset;
    private MainCharacter m_mainCharacter;
    private Graphics2D m_g2d;

    public GameMap() {
        xOffset = 0;
        yOffset = 0;
        setVisible(true);
        setFocusable(true);
    }
    

    public void initialize() {
        populateList();
        setBackground(new Color(0,154,23));
    }


    public void populateList() {
        spriteList = new ArrayList<Sprite>();
        for (int i = 0; i < 5; i++) {
            Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));

            Sprite Sprite = new Sprite(coor, "src/images/MainCowPic.png", Math.toRadians(0), this, m_mainCharacter, false, Constants.COW);        

            Sprite.initialize();
            spriteList.add(Sprite);

            Sprite g = new Sprite(coor, "src/images/Cow50Cal.png", Math.toRadians(0), this, m_mainCharacter, false, Constants.COW);
            g.initialize();
            spriteList.add(g);

        }
                
        
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

    public void moveMap(double deltax, double deltay) {
        xOffset += deltax;
        yOffset += deltay;
        for (Sprite p: spriteList) {
            p.moveWithMap(deltax, deltay);
        }
    }

    public void translateOrigin() {
        m_g2d.translate(getWidth()/2, -getHeight()/2);
    }

    public double[] getOffset() {
        return new double[] {xOffset, yOffset};
    }

    public ArrayList<Sprite> getSpriteList() {
        return spriteList;
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
