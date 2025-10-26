package app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

import app.gameElements.GameMap;
import app.gameElements.MainCharacter;

public class MainPanel extends JPanel implements ActionListener {
	// delay in ms
	private static final int TIMER_DELAY = 1;
	
	private JFrame frame;
	
	private int frameWidth;
    private int frameHeight;
    
    private Timer m_timer;
    
    private MainCharacter m_mainCharacter;
    private GameMap m_currentMap;
    
    private GameRenderer m_gameRenderer;
    
    private Color m_backgroundColor = new Color(0, 154, 23);
    
    private SoundEngine soundEngine;
    
    private PlayerControls playerInput;

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
                    
                    m_gameRenderer.setBounds(new Dimension(frameWidth, frameHeight));
                }
        });
        
        m_currentMap = new GameMap();
        m_currentMap.addTrees();
        
        m_mainCharacter = new MainCharacter();
        m_mainCharacter.moveTo(new Point2D.Double(0.0d, 0.0d));
        m_mainCharacter.setAngle(Math.toRadians(0.0d));
        m_mainCharacter.setCurrentMap(m_currentMap);
        
        m_gameRenderer = new GameRenderer();
        m_gameRenderer.setScene(m_mainCharacter, m_currentMap);
        
        add(m_gameRenderer);
        
        m_timer = new Timer(TIMER_DELAY, this);
        
        playerInput = new PlayerControls(m_mainCharacter, m_timer, m_gameRenderer, m_currentMap);
        
        addKeyListener(playerInput);
        addMouseListener(playerInput);
        
        soundEngine = new SoundEngine();
    }
    
    public void run() {
    	m_timer.start();
    }
    public void stopRunning() {
    	m_timer.stop();
    }

    public int getFrameHeight() { return frameHeight; }
    public int getFrameWidth() { return frameWidth; }

    public Timer getTimer() { return m_timer; }

	@Override
	public void actionPerformed(ActionEvent e) {
		soundEngine.update();
		
		m_currentMap.tick(m_mainCharacter.getMapCoodinate(), soundEngine);
        m_mainCharacter.periodic(soundEngine);
        
        m_gameRenderer.renderFrame();
	}
}
