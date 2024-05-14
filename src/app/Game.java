package app;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.List;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    
    private boolean showDebugMenu = false;
    
    private Color m_floorColor = new Color(0, 154, 23);

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
        
        if (showDebugMenu) {
        	drawDebugMenu();
        }
    }
    
    public void drawDebugMenu() {
        m_g2d.setPaint(Color.WHITE);
        
        // mainCharacter debug info
        
        Point2D.Double coor = m_mainCharacter.getMapCoodinate();
        double xCoor = coor.x;
        double yCoor = -coor.y;
        m_g2d.drawString(String.format("Map Coodinate: (x: %04.2f px, y: %04.2f px)", xCoor, yCoor), 10, 10);
        
        Point2D.Double vel = m_mainCharacter.getVelocity();
        double vX = vel.getX();
        double vY = -vel.getY();
        m_g2d.drawString(String.format("Translational Velocity: (Vx: %04.2f px/t, Vy: %04.2f px/t)", vX, vY), 10, 30);
        
        double speed = m_mainCharacter.getSpeed();
        m_g2d.drawString(String.format("Translational Speed: %04.2f px/t", speed), 10, 50);
        
        // convert the angle to degrees
        int angle = (int) (-m_mainCharacter.getAngle() * 180.0d / Math.PI);
        m_g2d.drawString("Angle: " + angle + " deg", 10, 70);
        
        double rotationalVel = -Math.toDegrees(m_mainCharacter.getRotationalVel());
        m_g2d.drawString(String.format("Rotational Velocity: %04.2f deg/t", rotationalVel), 10, 90);
        
        double rotationalSpeed = Math.toDegrees(m_mainCharacter.getRotationalSpeed());
        m_g2d.drawString(String.format("Rotational Speed: %04.2f deg/t", rotationalSpeed), 10, 110);
        
        int movingDirection = m_mainCharacter.getInputMovingDirection();
        String printedMovingDirection;
        if (movingDirection == Constants.DIRECTION_FORWARDS) {
        	printedMovingDirection = "DIRECTION_FORWARDS";
        }
        else if (movingDirection == Constants.DIRECTION_BACKWARDS) {
        	printedMovingDirection = "DIRECTION_BACKWARDS";
        }
        else {
        	printedMovingDirection = "DIRECTION_STOP";
        }
        m_g2d.drawString("Input Moving Direction: " + printedMovingDirection, 10, 130);
        
        boolean movingForward = m_mainCharacter.isMovingForward();
        m_g2d.drawString("Is Moving Forward: " + movingForward, 10, 150);
        
        boolean movingBackward = m_mainCharacter.isMovingBackward();
        m_g2d.drawString("Is Moving Forward: " + movingBackward, 10, 170);
        
        int turningDirection = m_mainCharacter.getInputTurningDirection();
        String printedTurningDirection;
        if (turningDirection == Constants.TURNING_LEFT) {
        	printedTurningDirection = "TURNING_LEFT";
        }
        else if (turningDirection == Constants.TURNING_RIGHT) {
        	printedTurningDirection = "TURNING_RIGHT";
        }
        else {
        	printedTurningDirection = "TURNING_STOP";
        }
        m_g2d.drawString("Input Turning Direction: " + printedTurningDirection, 10, 190);
        
        boolean turningLeft = m_mainCharacter.isTurningLeft();
        m_g2d.drawString("Is Turning Left: " + turningLeft, 10, 210);
        
        boolean turningRight = m_mainCharacter.isTurningRight();
        m_g2d.drawString("Is Turning Right: " + turningRight, 10, 230);
        
        // GameMap debug info
        
        List<Sprite> spriteList = m_currentMap.getSpriteList();
        List<Projectile> projectileList = m_currentMap.getProjectileList();
        
        int entityCount = spriteList.size();
        m_g2d.drawString("Entity Count: " + entityCount, 10, 250);
        
        int projectileCount = projectileList.size();
        m_g2d.drawString("Projectile Count: " + projectileCount, 10, 270);
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
    	revalidate();
        repaint(repaintRectangle);
        
        m_currentMap.tick(m_mainCharacter.getMapCoodinate());
        m_mainCharacter.periodic();
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
    
    public void toggleDebugMenu() {
    	showDebugMenu = !showDebugMenu;
    }
    
    public Timer getTimer() {
    	return timer;
    }
}
