package app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D.Double;
import java.awt.Rectangle;

import app.gameElements.MainCharacter;
import app.gameElements.Sprite;



public class MainPanel extends JPanel implements ActionListener {
	
    private Timer timer;
    private JFrame frame;
    private MainCharacter mainCharacter;
    private GameMap currentMap;
    private int frameWidth;
    private int frameHeight;
    private Rectangle repaintRectangle;

    private int debugCounter;

    MainPanel(JFrame f) {
        debugCounter = 0;

        int delay = 1;
        
        frame = f;
        setVisible(true);
        timer = new Timer(delay, this);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        currentMap = new GameMap();
        mainCharacter = new MainCharacter(currentMap);
        //repaintRectangle = new Rectangle((int) mainCharacter.getScreenCoodinate().x - (frameWidth / 2),
                //(int) mainCharacter.getScreenCoodinate().y - (frameHeight / 2), frameWidth, frameHeight);
        repaintRectangle = new Rectangle(0, 0, frameWidth, frameHeight);
        currentMap.setMainCharacter(mainCharacter);
        setBackground(new Color(0, 154, 23));
        add(currentMap);
        addKeyListener(new PlayerControls(mainCharacter, timer, currentMap));
        addMouseListener(new PlayerControls(mainCharacter, timer, currentMap));
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
        debugCounter++;
        
        
        if (debugCounter > 200) {
            for (Sprite s: currentMap.getSpriteList()) {
                s.turn(-s.getTurningDirection());
            }
            debugCounter = 0;
        }
        
        for (Sprite s: currentMap.getSpriteList()) {
            s.periodic();
        }
        
        mainCharacter.periodic();
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
