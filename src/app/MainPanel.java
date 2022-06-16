package app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;


public class MainPanel extends JPanel implements ActionListener {
    private Timer timer;
    private JFrame frame;
    private MainCharacter mainCharacter;
    private boolean facingDown;
    private boolean isApple;
    private final int MAIN_CHARACTER_SPEED = 3;
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
        facingDown = false;
        timer = new Timer(30, (ActionListener) this);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        isApple = false;
        currentMap = new GameMap();
        mainCharacter = new MainCharacter(currentMap);
        repaintRectangle = new Rectangle((int) mainCharacter.getTrueCoordinates().x - (frameWidth / 2),
                (int) mainCharacter.getTrueCoordinates().y - (frameHeight / 2), frameWidth, frameHeight);
        currentMap.setMainCharacter(mainCharacter);
        currentMap.initialize();
        setBackground(new Color(0, 154, 23));
        add(currentMap);
        addKeyListener(new PlayerControls(mainCharacter, timer));
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
        repaintRectangle.setBounds( 0, 0,frameWidth,frameHeight);
        frameHeight = frame.getHeight();
        mainCharacter.move();
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
