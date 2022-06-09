package app;

import java.util.ArrayList;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;



public class Map extends JPanel {

    private ArrayList<Prop> propList;
    private double xOffset;
    private double yOffset;
    private MainCharacter m_mainCharacter;
    private Graphics2D m_g2d;

    public Map() {
        xOffset = 0;
        yOffset = 0;
        setVisible(true);
        setFocusable(true);
        setPreferredSize(new Dimension(MainPanel.frameWidth -20,MainPanel.frameHeight-20));
    }
    

    public void initialize() {
        populateList();
        setBackground(new Color(0,154,23));
    }


    public void populateList() {
        propList = new ArrayList<Prop>();
        for (int i = 0; i < 2; i++) {
            //Point coor = new Point((int) (Math.random() * 1000), (int) (Math.random() * 1000));
            Point coor = new Point(100,-100);

            Prop prop = new Prop(coor, "src/images/MainCowPic.png", Math.toRadians(0), this, m_mainCharacter, false, Constants.COW);        

            prop.initialize();
            propList.add(prop);

            Prop g = new Prop(coor, "src/images/Cow50Cal.png", Math.toRadians(0), this, m_mainCharacter, false, Constants.COW);
            g.initialize();
            propList.add(g);

        }
                
        
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        m_g2d = (Graphics2D) g;
        m_g2d.translate(getWidth()/2, getHeight()/2);
        for (Prop p: propList) {
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
        for (Prop p: propList) {
            p.moveWithMap(deltax, deltay);
        }
    }

    public void translateOrigin() {
        m_g2d.translate(getWidth()/2, -getHeight()/2);
    }

    public double[] getOffset() {
        return new double[] {xOffset, yOffset};
    }

    public ArrayList<Prop> getPropList() {
        return propList;
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
