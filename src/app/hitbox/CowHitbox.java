package app.hitbox;

import java.awt.geom.Point2D;

public class CowHitbox extends Hitbox {
	
	private static Point2D.Double[] model= {new Point2D.Double(0.5d,0.25d),new Point2D.Double(0.5d,-0.25d),
			new Point2D.Double(-0.25d,-0.5d),new Point2D.Double(-0.5d,-0.25d),
			new Point2D.Double(-0.5d,0.25d),new Point2D.Double(-0.25d,0.5d)};

    public CowHitbox(double width, double height) {
        super(model, width, height);
    }
    
}
