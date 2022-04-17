package app;

import java.util.ArrayList;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;



public class Map extends JPanel {

    private ArrayList<Prop> propList;
    private double xOffset;
    private double yOffset;
    private MainCharacter m_mainCharacter;

    public Map() {
        xOffset = 0;
        yOffset = 0;
        setVisible(true);
        setFocusable(true);
        setPreferredSize(new Dimension(MainPanel.frameWidth -20,MainPanel.frameHeight-20));
    }


    public void populateList() {
        propList = new ArrayList<Prop>();
        for (int i = 0; i < 10; i++) {
            Point coor = new Point((int) (Math.random() * 1000), (int) (Math.random() * 1000));
            Prop prop = new Prop(coor, "src/images/MainCowPic.png", Math.toRadians(0), this, m_mainCharacter);
            propList.add(prop);
        }
                
        
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        for (Prop p: propList) {
            p.draw(g2d);
        }
        m_mainCharacter.draw(g2d);
        
    }

    public void setMainCharacter(MainCharacter mainCharacter) {
        m_mainCharacter = mainCharacter;
    }

    public void moveMap(double deltax, double deltay) {
        xOffset += deltax;
        yOffset += deltay;
        for (Prop p: propList) {
            p.move(deltax, deltay);
        }
    }

    public double[] getOffset() {
        return new double[] {xOffset, yOffset};
    }

}
