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
    private final int MAIN_CHARACTER_SPEED = 10;
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
        timer = new Timer(50, (ActionListener)this);
        timer.start();
        mainCharacter = new MainCharacter();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        isApple = false;
        currentMap = new Map(mainCharacter);
        currentMap.setBackground(Color.GREEN);
        setMap();
        add(currentMap);

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
        Point pointOnScreen = MouseInfo.getPointerInfo().getLocation();
        Point framesPoint = this.getLocationOnScreen();

        double mouseX = pointOnScreen.getX() - framesPoint.getX();
        double mouseY = pointOnScreen.getY() - framesPoint.getY();

        double opposite = mouseX - mainCharacter.getCoordinates().getX();
        double adjacent = mouseY - mainCharacter.getCoordinates().getY();

        if (adjacent > 0) {
            facingDown = true;
        } else {
            facingDown = false;
        }
        double characterAngle = Math.atan(opposite/adjacent);
        mainCharacter.updateInfo(characterAngle,facingDown,opposite,adjacent);
        frameWidth = frame.getWidth();
        frameHeight = frame.getHeight();


    }
    public void setMap() {
        mainCharacter.setCurrentMap(currentMap);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_A) {
            mainCharacter.moveSideways(MAIN_CHARACTER_SPEED,-1);// direction 0 is forwards
        }
        if (arg0.getKeyCode() == KeyEvent.VK_D) {
            mainCharacter.moveSideways(MAIN_CHARACTER_SPEED,1);// direction 0 is forwards
        }
        if (arg0.getKeyCode() == KeyEvent.VK_W) {
            mainCharacter.moveBackForth(MAIN_CHARACTER_SPEED,1);// direction 0 is x
        }
        if (arg0.getKeyCode() == KeyEvent.VK_S) {
            mainCharacter.moveBackForth(MAIN_CHARACTER_SPEED,-1);// direction 1 is backwards
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
