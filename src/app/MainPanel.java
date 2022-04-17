package app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.MouseInfo;
import java.awt.Point;


public class MainPanel extends JPanel implements ActionListener, KeyListener  {
    private Timer timer;
    private JFrame frame;
    private MainCharacter mainCharacter;
    private boolean facingDown;
    private boolean isApple;
    private final int MAIN_CHARACTER_SPEED = 3;
    private Map currentMap;
    public static int frameWidth;
    public static int frameHeight;

    
    MainPanel(JFrame f) {
        frame = f;
        frameWidth = 500;
        frameHeight = 500;
        //this.setPreferredSize(new Dimension(frameWidth, frameHeight));
        setVisible(true);
        facingDown = false;
        timer = new Timer(30, (ActionListener)this);
        
        
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        isApple = false;
        currentMap = new Map();
        mainCharacter = new MainCharacter(currentMap, this);
        currentMap.setMainCharacter(mainCharacter);
        currentMap.populateList();
        currentMap.setBackground(new Color(0,154,23));
        setMap();
        add(currentMap);
        timer.start();

    }


    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        currentMap.revalidate();
        currentMap.repaint();
        currentMap.setPreferredSize(new Dimension(frameWidth, frameHeight));
        mainCharacter.updateTurnAngle();
        frameWidth = frame.getWidth();
        frameHeight = frame.getHeight();


    }
    public void setMap() {
        mainCharacter.setCurrentMap(currentMap);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_A) {
            mainCharacter.moveSideways(MAIN_CHARACTER_SPEED,Constants.DIRECTION_LEFT);
        }
        if (arg0.getKeyCode() == KeyEvent.VK_D) {
            mainCharacter.moveSideways(MAIN_CHARACTER_SPEED,Constants.DIRECTION_RIGHT);
        }
        if (arg0.getKeyCode() == KeyEvent.VK_W) {
            mainCharacter.moveBackForth(MAIN_CHARACTER_SPEED,Constants.DIRECTION_FORWARDS);
        }
        if (arg0.getKeyCode() == KeyEvent.VK_S) {
            mainCharacter.moveBackForth(MAIN_CHARACTER_SPEED,Constants.DIRECTION_BACKWARDS);
        }
        if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
            if (isApple) {
                mainCharacter.setImage("src/images/MainCowPic.png");
                isApple = false;
            } else {
                mainCharacter.setImage("src/images/snake-apple.png");
                isApple = true;
            }
            
        }
    }
}
