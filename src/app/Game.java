package app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.geom.AffineTransform;


import javax.swing.JPanel;

import app.gameElements.MainCharacter;
import app.gameElements.Sprite;

public class Game extends JPanel {
	//private static Game m_game;
	
	private int unitSize = 44;
	private Graphics2D m_g2d;
	
    private MainCharacter m_mainCharacter;
    private GameMap m_currentMap;
    
    private boolean showHitboxes = false;
    
    private Color m_floorColor = new Color(0, 154, 23);

    public Game() {
        setBackground(m_floorColor);
        setVisible(true);
        setFocusable(true);
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

    public void setMainCharacter(MainCharacter mainCharacter) {
        m_mainCharacter = mainCharacter;
    }
    
    public void setCurrentMap(GameMap map) {
    	m_currentMap = map;
    }
    
    public void toggleHitboxes() {
    	showHitboxes = !showHitboxes;
    }
}
