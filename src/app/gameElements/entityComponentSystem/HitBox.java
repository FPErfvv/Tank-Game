
package app.gameElements.entityComponentSystem;

import java.awt.geom.Point2D;

public class HitBox {
	private final Transform transform;
	
	private final Point2D.Double[] m_modelVertexPositions;
	private final Point2D.Double[] m_transformedVertexPositions;
	
	private float m_translationalVelocityX;
	private float m_translationalVelocityY;
	private float m_rotationalVelocity;
	
	public HitBox(Transform transform, Point2D.Double[] modelPoints) {
		this.transform = transform;
		
		m_modelVertexPositions = modelPoints;
		m_transformedVertexPositions = new Point2D.Double[modelPoints.length];
		for (int i = 0; i < modelPoints.length; i++) {
			m_transformedVertexPositions[i] = new Point2D.Double();
		}
		
		computeVertexPositions();
	}
	
	public void tick() {
		float newAngle = transform.getAngle() + m_rotationalVelocity;
		
		float newXPosition = transform.getXPosition() + m_translationalVelocityX;
		float newYPosition = transform.getYPosition() + m_translationalVelocityY;
		
		transform.setAngle(newAngle);
		transform.setXPosition(newXPosition);
		transform.setYPosition(newYPosition);
		
		computeVertexPositions();
	}
	
	public boolean intersectsSAT(HitBox hitboxOther, Point2D.Double outMTVec) { return false; }
	
	private void computeVertexPositions() {
		float x = transform.getXPosition();
    	float y = transform.getYPosition();
    	
    	float angle = transform.getAngle();
    	
    	int vertexCount = m_transformedVertexPositions.length;
    	
    	for(int i = 0; i < vertexCount; i++) {
    		float mx = (float) m_modelVertexPositions[i].x;
    		float my = (float) m_modelVertexPositions[i].y;
    		
    		float cosine = (float) Math.cos(angle);
    		float sine = (float) Math.sin(angle);
    		
    		// the 2d rotation transformation
    		float ptx = mx * cosine - my * sine + x;
    		float pty = my * cosine + mx * sine + y;
    		
    		m_transformedVertexPositions[i].setLocation(ptx, pty);
    	}
	}
}
