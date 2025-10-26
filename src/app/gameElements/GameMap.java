package app.gameElements;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.Constants;
import app.SoundEngine;
import app.gameElements.hitbox.hitboxSubClasses.CowHitbox;

public class GameMap {
	private final List<Sprite> spriteList;
    private final List<Projectile> projectileList;
    
    private int debugCounter = 0;
    
    public GameMap() {
    	spriteList = new ArrayList<>();
    	projectileList = new ArrayList<>();
    }
    
    public void addSprite(Sprite s) {
    	spriteList.add(s);
    }
    
    public void addProjectile(Projectile p) {
    	projectileList.add(p);
    }
    
    public List<Sprite> getSpriteList() {
        return Collections.unmodifiableList(spriteList);
    }
    
    public List<Projectile> getProjectileList() {
    	return Collections.unmodifiableList(projectileList);
    }
    
    public void tick(Point2D.Double mainCharCoor, SoundEngine soundEngine) {
    	debugCounter++;
        
        if (debugCounter > 200) {
            for (Sprite s : getSpriteList()) {
                s.turn(-s.getInputTurningDirection());
            }
            debugCounter = 0;
        }
        
    	for (Sprite s : spriteList) {
            //s.periodic(soundEngine);
        	s.moveAI(mainCharCoor, soundEngine);
        }
    	
    	for(Projectile p : projectileList) {
            p.periodic(soundEngine);
        }
    }
    
    public void addTrees() {
    	for (int i = 0; i < 10; i++) {
            Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
            
            Sprite sprite = new Sprite("src/images/top-tree-png-1.png");
            sprite.moveTo(coor);
            sprite.setAngle(Math.toRadians(0.0d));
            sprite.setCurrentMap(this);
            sprite.setHitbox(new CowHitbox(sprite.getWidth(), sprite.getHeight()));
            
            spriteList.add(sprite);
        }
    }
    
    public void addArmedCow() {
    	//Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
        Point2D.Double coor = new Point2D.Double(0.0d, 50.0d);
        
        Sprite sprite = new Sprite("src/images/MainCowPic.png");  
        sprite.moveTo(coor);
        sprite.setAngle(Math.toRadians(0.0d));
        sprite.setCurrentMap(this);
        sprite.setHitbox(new CowHitbox(sprite.getWidth(), sprite.getHeight()));
        sprite.setInputMovingSpeed(5);
        sprite.setInputTurningSpeed(Math.toRadians(5));
        sprite.moveForward();
        sprite.turnLeft();
        sprite.turn(Constants.TURNING_LEFT);
        spriteList.add(sprite);

        Sprite g = new Sprite("src/images/Cow50Cal.png");
        g.moveTo(coor);
        g.setAngle(Math.toRadians(0.0d));
        g.setCurrentMap(this);
        g.setHitbox(new CowHitbox(g.getWidth(), g.getHeight()));
        g.setInputMovingSpeed(5);
        g.setInputTurningSpeed(Math.toRadians(5));
        g.moveForward();
        g.turnLeft();
        g.turn(Constants.TURNING_LEFT);
        spriteList.add(g);
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
