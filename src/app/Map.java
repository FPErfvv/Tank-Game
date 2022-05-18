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
        for (int i = 0; i < 1; i++) {
            //Point coor = new Point((int) (Math.random() * 1000), (int) (Math.random() * 1000));
            Point coor = new Point(0,0);
            Prop prop = new Prop(coor, "src/images/MainCowPic.png", Math.toRadians(0), this, m_mainCharacter, false);        
            prop.initialize();
            propList.add(prop);
        }
                
        
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        m_g2d = (Graphics2D) g;
        m_g2d.translate(getWidth()/2, getHeight()/2);
        m_g2d.fillRect(10, -10, 20, 20);
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
        System.out.println(getWidth() + " " + getHeight());
    }

    public void checkCollisions() {
        m_mainCharacter.getHitBox().SAT(propList.get(0).getHitBox().getCollisionPoints(), propList.get(0).getRelativeCoordinates());
    }

    public double[] getOffset() {
        return new double[] {xOffset, yOffset};
    }

}
