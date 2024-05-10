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

public class GameMap extends JPanel {
	private int unitSize = 44;
    private final List<Sprite> spriteList;
    private MainCharacter m_mainCharacter;
    private Graphics2D m_g2d;
    private static GameMap m_gameMap;
    
    private boolean showHitboxes = false;
    
    private Color m_floorColor = new Color(0, 154, 23);

    public GameMap() {
        setBackground(m_floorColor);
        setVisible(true);
        setFocusable(true);
        
        spriteList = new ArrayList<Sprite>();
        populateList();
    }

    // public static GameMap getInstance() {
    //     if (m_gameMap == null) {
    //         m_gameMap = new GameMap();

    //     }
    //     return m_gameMap;
    // }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        m_g2d = (Graphics2D) g;
        
        AffineTransform tr = new AffineTransform();
        
        m_g2d.translate(getWidth() * 0.5d, getHeight() * 0.5d);
        
        // when map gets added, it will be drawn here
        
        for (Sprite s: spriteList) {
        	s.draw(m_g2d, tr, m_mainCharacter.getMapCoodinate(), showHitboxes);
        }
        
        m_mainCharacter.draw(m_g2d, tr, m_mainCharacter.getMapCoodinate(), showHitboxes);
        
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
        
        int projectileCount = m_mainCharacter.getProjectileCount();
        m_g2d.drawString("Projectile Count: " + projectileCount, 10, 150);
    }
    
    public void populateList() {
        for (int i = 0; i < 10; i++) {
            Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
            //Point2D.Double coor = new Point2D.Double(100,0);
            Sprite sprite = new Sprite(coor, "src/images/top-tree-png-1.png", Math.toRadians(0), this);
            spriteList.add(sprite);
            
            // Sprite g = new Sprite(coor, "src/images/Cow50Cal.png", Math.toRadians(0), this);
            // spriteList.add(g);
        }
    }

    public void addSprite() {
        //Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
        Point2D.Double coor = new Point2D.Double(0, 50);
        Sprite sprite = new Sprite(coor, "src/images/MainCowPic.png", Math.toRadians(0), this);        
        sprite.setMovingSpeed(5);
        sprite.moveForward();
        sprite.setInputTurningSpeed(Math.toRadians(5));
        sprite.turn(Constants.TURNING_LEFT);
        spriteList.add(sprite);

        Sprite g = new Sprite(coor, "src/images/Cow50Cal.png", Math.toRadians(0), this);
        g.setMovingSpeed(5);
        g.moveForward();
        g.setInputTurningSpeed(Math.toRadians(5));
        g.turn(Constants.TURNING_LEFT);
        spriteList.add(g);
    }

    public void setMainCharacter(MainCharacter mainCharacter) {
        m_mainCharacter = mainCharacter;
    }


    public List<Sprite> getSpriteList() {
        return Collections.unmodifiableList(spriteList);
    }
    
    public void toggleHitboxes() {
    	showHitboxes = !showHitboxes;
    }

    /**
     * Links to Terrain Generation
     * https://www.procjam.com/tutorials/en/ooze/
     * https://gamedev.stackexchange.com/questions/186194/how-to-randomly-generate-biome-with-perlin-noise
     * https://www.redblobgames.com/x/1721-voronoi-alternative/
     * 
     * Link to hexagon class + hexagon grid
     * https://github.com/javagl/Hexagon
     **/ 
}
