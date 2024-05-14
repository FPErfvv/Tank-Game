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
        
        // translate g2d to the center of the screen
        m_g2d.translate(getWidth() * 0.5d, getHeight() * 0.5d);
        
        // when map gets added, it will be drawn here
        
        List<Sprite> spriteList = m_currentMap.getSpriteList();
        List<Projectile> projectileList = m_currentMap.getProjectileList();
        
        Point2D.Double scroll = m_mainCharacter.getMapCoodinate();
        
        for (Sprite s: spriteList) {
        	s.draw(m_g2d, tr, scroll, showHitboxes);
        }
        
        m_mainCharacter.draw(m_g2d, tr, scroll, showHitboxes);
        
        for(Projectile p: projectileList) {
            p.draw(m_g2d, tr, scroll, showHitboxes);
        }
        
        m_g2d.translate(-getWidth() * 0.5d, -getHeight() * 0.5d);
        
        if (showDebugMenu) {
        	drawDebugMenu(m_mainCharacter);
        }
    }
    
    public void drawDebugMenu(Sprite s) {
        m_g2d.setPaint(Color.WHITE);
        
        // mainCharacter debug info
        
        // Translation
        
        Point2D.Double coor = s.getMapCoodinate();
        double xCoor = coor.x;
        double yCoor = -coor.y;
        m_g2d.drawString(String.format("Map Coodinate: (x: %04.2f px, y: %04.2f px)", xCoor, yCoor), 10, 10);
        
        Point2D.Double vel = s.getVelocity();
        double vX = vel.getX();
        double vY = -vel.getY();
        m_g2d.drawString(String.format("Translational Velocity: (Vx: %04.2f px/t, Vy: %04.2f px/t)", vX, vY), 10, 30);
        
        double speed = s.getSpeed();
        m_g2d.drawString(String.format("Translational Speed: %04.2f px/t", speed), 10, 50);
        
        // Rotation
        
        double angle = -Math.toDegrees(s.getAngle());
        m_g2d.drawString(String.format("Angle: %04.2f deg", angle), 10, 70);
        
        double rotationalVel = -Math.toDegrees(s.getRotationalVel());
        m_g2d.drawString(String.format("Rotational Velocity: %04.2f deg/t", rotationalVel), 10, 90);
        
        double rotationalSpeed = Math.toDegrees(s.getRotationalSpeed());
        m_g2d.drawString(String.format("Rotational Speed: %04.2f deg/t", rotationalSpeed), 10, 110);
        
        // Input
        
        // Translational Input
        
        double inputMovingSpeed = s.getInputMovingSpeed();
        m_g2d.drawString(String.format("Input Moving Speed: %04.2f px/t", inputMovingSpeed), 10, 130);
        
        int movingDirection = s.getInputMovingDirection();
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
        m_g2d.drawString("Input Moving Direction: " + printedMovingDirection, 10, 150);
        
        boolean movingForward = s.isMovingForward();
        m_g2d.drawString("Is Moving Forward: " + movingForward, 10, 170);
        
        boolean movingBackward = s.isMovingBackward();
        m_g2d.drawString("Is Moving Backward: " + movingBackward, 10, 190);
        
        // Rotational Input
        
        double inputTurningSpeed = Math.toDegrees(s.getInputTurningSpeed());
        m_g2d.drawString(String.format("Input Turning Speed: %04.2f deg/t", inputTurningSpeed), 10, 210);
        
        int turningDirection = s.getInputTurningDirection();
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
        m_g2d.drawString("Input Turning Direction: " + printedTurningDirection, 10, 230);
        
        boolean turningLeft = s.isTurningLeft();
        m_g2d.drawString("Is Turning Left: " + turningLeft, 10, 250);
        
        boolean turningRight = s.isTurningRight();
        m_g2d.drawString("Is Turning Right: " + turningRight, 10, 270);
        
        // GameMap debug info
        
        List<Sprite> spriteList = m_currentMap.getSpriteList();
        List<Projectile> projectileList = m_currentMap.getProjectileList();
        
        int entityCount = spriteList.size();
        m_g2d.drawString("Entity Count: " + entityCount, 10, 290);
        
        int projectileCount = projectileList.size();
        m_g2d.drawString("Projectile Count: " + projectileCount, 10, 310);
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
