package app;

import javax.swing.JPanel;

import java.util.List;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import app.gameElements.GameMap;
import app.gameElements.MainCharacter;
import app.gameElements.Projectile;
import app.gameElements.Sprite;

public class GameRenderer extends JPanel {
	private Graphics2D m_g2d;
	private Rectangle repaintRectangle;
	
    private MainCharacter m_mainCharacterHandle;
    private GameMap m_currentMapHandle;
    
    private MainCharacter[] otherPlayers;
    private int otherPlayerCount = 0;
    
    private boolean showHitboxes = false;
    private boolean showDebugMenu = false;
    
    private Color m_floorColor = new Color(0, 154, 23);
    
    private AffineTransform tr = new AffineTransform();

    public GameRenderer() {
        setBackground(m_floorColor);
        setVisible(true);
        //setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        
        repaintRectangle = new Rectangle(0, 0, 0, 0);
    }
    
    public void setScene(MainCharacter mainCharacterHandle, GameMap mapHandle) {
    	m_mainCharacterHandle = mainCharacterHandle;
    	m_currentMapHandle = mapHandle;
    }
    
    public void renderFrame() {
    	revalidate();
        repaint(repaintRectangle);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        m_g2d = (Graphics2D) g;
        
        //AffineTransform tr = new AffineTransform();
        
        // translate g2d to the center of the screen
        m_g2d.translate(getWidth() * 0.5d, getHeight() * 0.5d);
        
        // when map gets added, it will be drawn here
        
        List<Sprite> spriteList = m_currentMapHandle.getSpriteList();
        List<Projectile> projectileList = m_currentMapHandle.getProjectileList();
        
        Point2D.Double scroll;
        if (m_mainCharacterHandle != null)
        	scroll = m_mainCharacterHandle.getMapCoodinate();
        else
        	scroll = new Point2D.Double();
        
        for (Sprite s: spriteList) {
        	s.draw(m_g2d, tr, scroll, showHitboxes);
        }
        
        if (m_mainCharacterHandle != null)
        	m_mainCharacterHandle.draw(m_g2d, tr, scroll, showHitboxes);
        
        for (int i = 0; i < otherPlayerCount; i++) {
        	otherPlayers[i].draw(m_g2d, tr, scroll, showHitboxes);
        }
        
        for(Projectile p: projectileList) {
            p.draw(m_g2d, tr, scroll, showHitboxes);
        }
        
        m_g2d.translate(-getWidth() * 0.5d, -getHeight() * 0.5d);
        
        if (showDebugMenu && m_mainCharacterHandle != null) {
        	drawDebugMenu(m_mainCharacterHandle);
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
        
        List<Sprite> spriteList = m_currentMapHandle.getSpriteList();
        List<Projectile> projectileList = m_currentMapHandle.getProjectileList();
        
        int entityCount = spriteList.size();
        m_g2d.drawString("Entity Count: " + entityCount, 10, 290);
        
        int projectileCount = projectileList.size();
        m_g2d.drawString("Projectile Count: " + projectileCount, 10, 310);
    }
    
    public void setBounds(Dimension dim) {
    	setPreferredSize(dim);
        repaintRectangle.setBounds(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
    }

    public void setMainCharacter(MainCharacter mainCharacter) {
    	m_mainCharacterHandle = mainCharacter;
    }
    
    public void setCurrentMap(GameMap map) {
    	m_currentMapHandle = map;
    }
    
    public void toggleHitboxes() {
    	showHitboxes = !showHitboxes;
    }
    
    public void toggleDebugMenu() {
    	showDebugMenu = !showDebugMenu;
    }
    
    // networking stuff
    public void drawPlayers(MainCharacter[] players, int playerCount) {
    	this.otherPlayerCount = playerCount;
    	this.otherPlayers = players;
    }
}
