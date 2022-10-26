package app.gameElements.hitbox.hitboxSubClasses;

import java.awt.geom.Point2D;

import app.Constants;
import app.Utility;
import app.gameElements.Sprite;
import app.gameElements.hitbox.Hitbox;

public class CowHitbox extends Hitbox {
	
	private static Point2D.Double[] model= {new Point2D.Double(0.5d,0.25d),new Point2D.Double(0.5d,-0.25d),
			new Point2D.Double(-0.25d,-0.5d),new Point2D.Double(-0.5d,-0.25d),
			new Point2D.Double(-0.5d,0.25d),new Point2D.Double(-0.25d,0.5d)};

    public CowHitbox(double width, double height) {
        super(model, width, height);
    }
}
