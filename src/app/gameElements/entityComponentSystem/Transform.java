
package app.gameElements.entityComponentSystem;

public final class Transform {
	private float xPosition, yPosition;
	private float angle;
	
	public Transform(float xPosition, float yPosition, float angle) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public float getXPosition() { return xPosition; }
	public float getYPosition() { return yPosition; }
	public float getAngle() { return angle; }
	
	public void setXPosition(float newXPosition) { xPosition = newXPosition; }
	public void setYPosition(float newYPosition) { yPosition = newYPosition; }
	public void setAngle(float newAngle) { angle = newAngle; }
}
