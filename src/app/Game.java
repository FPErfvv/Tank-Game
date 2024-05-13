package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import app.gameElements.MainCharacter;
import app.gameElements.Sprite;

public class Game extends JPanel implements ActionListener {
	//private static Game m_game;
	
	private int unitSize = 44;
	private Graphics2D m_g2d;
	private Rectangle repaintRectangle;
	
	private Timer timer;
	
    private MainCharacter m_mainCharacter;
    private GameMap m_currentMap;
    
    private boolean showHitboxes = false;
    
    private Color m_floorColor = new Color(0, 154, 23);
    
    private int debugCounter = 0;

    public Game(MainCharacter mainCharacter, GameMap map) {
        setBackground(m_floorColor);
        setVisible(true);
        //setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        
        repaintRectangle = new Rectangle(0, 0, 0, 0);
        
        int delay = 1;
        timer = new Timer(delay, this);
        
        m_mainCharacter = mainCharacter;
        m_currentMap = map;
        
        //addKeyListener(new PlayerControls(mainCharacter, timer, this, map));
        addMouseListener(new PlayerControls(mainCharacter, timer, this, map));
        
        timer.start();
    }

    // public static Game getInstance() {
    //     if (m_game == null) {
    //         m_game = new Game();
    //     }
    //     return m_game;
    // }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        m_g2d = (Graphics2D) g;
        
        AffineTransform tr = new AffineTransform();
        
        m_g2d.translate(getWidth() * 0.5d, getHeight() * 0.5d);
        
        // when map gets added, it will be drawn here
        
        List<Sprite> spriteList = m_currentMap.getSpriteList();
        List<Projectile> projectileList = m_currentMap.getProjectileList();
        
        for (Sprite s: spriteList) {
        	s.draw(m_g2d, tr, m_mainCharacter.getMapCoodinate(), showHitboxes);
        }
        
        m_mainCharacter.draw(m_g2d, tr, m_mainCharacter.getMapCoodinate(), showHitboxes);
        
        for(Projectile p: projectileList) {
            p.draw(m_g2d, tr, m_mainCharacter.getMapCoodinate(), showHitboxes);
        }
        
        m_g2d.translate(-getWidth() * 0.5d, -getHeight() * 0.5d);
        
        m_g2d.setPaint(Color.WHITE);
        
        // convert the angle to degrees
        int angle = (int) (-m_mainCharacter.getAngle() * 180.0d / Math.PI);
        m_g2d.drawString("Angle: " + angle + " deg", 10, 10);
        
        Point2D.Double coor = m_mainCharacter.getMapCoodinate();
        double xCoor = coor.x;
        double yCoor = coor.y;
        m_g2d.drawString(String.format("Map Coodinate: (x: %04.2f px, y: %04.2f px)", xCoor, yCoor), 10, 30);
        
        Point2D.Double vel = m_mainCharacter.getVelocity();
        double vX = vel.getX();
        double vY = vel.getY();
        m_g2d.drawString(String.format("Velocity: (v_x: %04.2f px/t, v_y: %04.2f px/t)", vX, vY), 10, 50);
        
        double speed = m_mainCharacter.getSpeed();
        m_g2d.drawString(String.format("Speed: %04.2f px/t", speed), 10, 70);
        
        double rotationalVel = Math.toDegrees(m_mainCharacter.getRotationalVel());
        m_g2d.drawString(String.format("Rotational Velocity: %04.2f deg/t", rotationalVel), 10, 90);
        
        double rotationalSpeed = Math.toDegrees(m_mainCharacter.getRotationalSpeed());
        m_g2d.drawString(String.format("Rotational Speed: %04.2f deg/t", rotationalSpeed), 10, 110);
        
        int entityCount = spriteList.size();
        m_g2d.drawString("Entity Count: " + entityCount, 10, 130);
        
        int projectileCount = projectileList.size();
        m_g2d.drawString("Projectile Count: " + projectileCount, 10, 150);
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
    	revalidate();
        repaint(repaintRectangle);
        
        //debugCounter++;
        
        /*
        if (debugCounter > 200) {
            for (Sprite s: currentMap.getSpriteList()) {
                s.turn(-s.getTurningDirection());
            }
            debugCounter = 0;
        }
        */
        
        for (Sprite s : m_currentMap.getSpriteList()) {
            //s.periodic();
        	s.moveAI(m_mainCharacter.getMapCoodinate());
        }
        
        m_mainCharacter.periodic();
        
        for(Projectile p : m_currentMap.getProjectileList()) {
            p.periodic(); 
        }
    }
    
    public void setBounds(Dimension dim) {
    	setPreferredSize(dim);
        repaintRectangle.setBounds(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
    }

    public void setMainCharacter(MainCharacter mainCharacter) {
        m_mainCharacter = mainCharacter;
    }
    
    public void setCurrentMap(GameMap map) {
    	m_currentMap = map;
    }
    
    public void toggleHitboxes() {
    	showHitboxes = !showHitboxes;
    }
    
    public Timer getTimer() {
    	return timer;
    }
}
