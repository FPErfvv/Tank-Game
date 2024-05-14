package app;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.gameElements.Sprite;
import app.gameElements.hitbox.hitboxSubClasses.CowHitbox;

public class GameMap {
	private final List<Sprite> spriteList;
    private final List<Projectile> projectileList;
    
    public GameMap() {
    	spriteList = new ArrayList<>();
    	projectileList = new ArrayList<>();
    	
    	addTrees();
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
    
    public void tick(Point2D.Double mainCharCoor) {
    	for (Sprite s : spriteList) {
            s.periodic();
        	//s.moveAI(mainCharCoor);
        }
    	
    	for(Projectile p : projectileList) {
            p.periodic(); 
        }
    }
    
    public void addTrees() {
    	for (int i = 0; i < 10; i++) {
            Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
            //Point2D.Double coor = new Point2D.Double(100,0);
            Sprite sprite = new Sprite("src/images/top-tree-png-1.png", coor, Math.toRadians(0), this);
            sprite.setHitbox(new CowHitbox(sprite.getWidth(), sprite.getHeight()));
            spriteList.add(sprite);
            
            // Sprite g = new Sprite(coor, "src/images/Cow50Cal.png", Math.toRadians(0), this);
            // spriteList.add(g);
        }
    }
    
    public void addArmedCow() {
    	//Point2D.Double coor = new Point2D.Double((int) (Math.random() * 1000), (int) (Math.random() * 1000));
        Point2D.Double coor = new Point2D.Double(0, 50);
        Sprite sprite = new Sprite("src/images/MainCowPic.png", coor, Math.toRadians(0), this);     
        sprite.setHitbox(new CowHitbox(sprite.getWidth(), sprite.getHeight()));
        sprite.setInputMovingSpeed(5);
        //sprite.moveForward();
        sprite.setInputTurningSpeed(Math.toRadians(5));
        //sprite.turn(Constants.TURNING_LEFT);
        spriteList.add(sprite);

        Sprite g = new Sprite("src/images/Cow50Cal.png", coor, Math.toRadians(0), this);
        g.setHitbox(new CowHitbox(g.getWidth(), g.getHeight()));
        g.setInputMovingSpeed(5);
        //g.moveForward();
        g.setInputTurningSpeed(Math.toRadians(5));
        //g.turn(Constants.TURNING_LEFT);
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
