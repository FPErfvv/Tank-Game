package app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Rectangle;


public class MainPanel extends JPanel implements ActionListener {
    private Timer timer;
    private JFrame frame;
    private MainCharacter mainCharacter;
    private GameMap currentMap;
    private int frameWidth;
    public int frameHeight;
    private static Rectangle repaintRectangle;

    MainPanel(JFrame f) {

        frame = f;
        frameWidth = 500;
        frameHeight = 500;
        // this.setPreferredSize(new Dimension(frameWidth, frameHeight));
        setVisible(true);
        timer = new Timer(30, (ActionListener) this);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        currentMap = new GameMap();
        mainCharacter = new MainCharacter(currentMap);
        repaintRectangle = new Rectangle((int) mainCharacter.getTrueCoordinates().x - (frameWidth / 2),
                (int) mainCharacter.getTrueCoordinates().y - (frameHeight / 2), frameWidth, frameHeight);
        currentMap.setMainCharacter(mainCharacter);
        setBackground(new Color(0, 154, 23));
        add(currentMap);
        addKeyListener(new PlayerControls(mainCharacter, timer, currentMap));
        timer.start();
    }

    public void initialize() {
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        currentMap.revalidate();
        currentMap.repaint(repaintRectangle);
        currentMap.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frameWidth = frame.getWidth();
        repaintRectangle.setBounds(0, 0, frameWidth, frameHeight);
        frameHeight = frame.getHeight();
        mainCharacter.periodic();
        System.out.println(currentMap.getSpriteList().get(0).getRelativeCoordinates() + ", " + mainCharacter.getRelativeCoordinates());
        for (Sprite s: currentMap.getSpriteList()) {
            s.periodic();
        }
    }
    
    public void setMap() {
        mainCharacter.setCurrentMap(currentMap);
    }


    public int getFrameHeight() {
        return frameHeight;
    }
    public int getFrameWidth() {
        return frameWidth;
    }

    public Timer getTimer() {
        return timer;
        
    }
}
