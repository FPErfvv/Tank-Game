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
	private int frameWidth;
    private int frameHeight;
	
    private Timer timer;
    private JFrame frame;
    private Rectangle repaintRectangle;
    
    private MainCharacter mainCharacter;
    private GameMap currentMap;
    
    private Game game;
    
    private Color m_backgroundColor = new Color(0, 154, 23);

    private int debugCounter;

    MainPanel(JFrame f) {
        frame = f;
        setVisible(true);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setBackground(m_backgroundColor);
        
        int delay = 1;
        timer = new Timer(delay, this);
        
        repaintRectangle = new Rectangle(0, 0, frameWidth, frameHeight);
        
        game = new Game();
        currentMap = new GameMap();
        mainCharacter = new MainCharacter(currentMap);
        game.setMainCharacter(mainCharacter);
        game.setCurrentMap(currentMap);
        
        add(game);
        
        addKeyListener(new PlayerControls(mainCharacter, timer, game, currentMap));
        addMouseListener(new PlayerControls(mainCharacter, timer, game, currentMap));
        
        timer.start();
        
        debugCounter = 0;
    }

    public void initialize() {
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        game.revalidate();
        game.repaint(repaintRectangle);
        
        frameWidth = frame.getWidth();
        frameHeight = frame.getHeight();
        
        game.setPreferredSize(new Dimension(frameWidth, frameHeight));
        repaintRectangle.setBounds(0, 0, frameWidth, frameHeight);
        
        debugCounter++;
        
        /*
        if (debugCounter > 200) {
            for (Sprite s: currentMap.getSpriteList()) {
                s.turn(-s.getTurningDirection());
            }
            debugCounter = 0;
        }
        */
        
        for (Sprite s : currentMap.getSpriteList()) {
            //s.periodic();
        	s.moveAI(mainCharacter.getMapCoodinate());
        }
        
        mainCharacter.periodic();
        
        for(Projectile p : currentMap.getProjectileList()) {
            p.periodic(); 
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
