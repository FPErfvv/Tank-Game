package app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D.Double;

import app.gameElements.MainCharacter;

public class MainPanel extends JPanel {
	private JFrame frame;
	
	private int frameWidth;
    private int frameHeight;
    
    private Game game;
    
    private Color m_backgroundColor = new Color(0, 154, 23);

    MainPanel(JFrame f) {
        frame = f;
        setVisible(true);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setBackground(m_backgroundColor);
        
        frame.addComponentListener(new ComponentAdapter() 
        {  
                public void componentResized(ComponentEvent evt) {
                    Component c = (Component)evt.getSource();

                    frameWidth = frame.getWidth();
                    frameHeight = frame.getHeight();
                    
                    game.setBounds(new Dimension(frameWidth, frameHeight));
                }
        });
        
        GameMap currentMap = new GameMap();
        MainCharacter mainCharacter = new MainCharacter(currentMap);
        game = new Game(mainCharacter, currentMap);
        
        add(game);
        
        /*
         * TODO:
         * Figure out how to have this on the Game class without the game not recognizing key input
         */
        addKeyListener(new PlayerControls(mainCharacter, game.getTimer(), game, currentMap));
    }

    public void initialize() {
    }

    public int getFrameHeight() {
        return frameHeight;
    }
    public int getFrameWidth() {
        return frameWidth;
    }

    public Timer getTimer() {
    	return game.getTimer();
    }
}
