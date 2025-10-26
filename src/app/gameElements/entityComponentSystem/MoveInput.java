
package app.gameElements.entityComponentSystem;

public final class MoveInput {
	private boolean isMovingForward = false;
	private boolean isMovingBackward = false;
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false;
	
	private float inputMovingSpeed = 0.0f;
	private float inputTurningSpeed = 0.0f;
	
	public MoveInput() {}
	
	public void setInputMovingSpeed(float newInputMovingSpeed) {
		inputMovingSpeed = newInputMovingSpeed;
	}
	public void setInputTurningSpeed(float newInputTurningSpeed) {
		inputTurningSpeed = newInputTurningSpeed;
	}
	
	public boolean isMovingForward() { return isMovingForward; }
	public void moveForward() { isMovingForward = true; }
	public void stopMovingForward() { isMovingForward = false; }
	
	public boolean isMovingBackward() { return isMovingBackward; }
	public void moveBackward() { isMovingBackward = true; }
	public void stopMovingBackward() { isMovingBackward = false; }
	
	public boolean isTurningLeft() { return isTurningLeft; }
	public void turnLeft() { isTurningLeft = true; }
	public void stopTurningLeft() { isTurningLeft = false; }
	
	public boolean isTurningRight() { return isTurningRight; }
	public void turnRight() { isTurningRight = true; }
	public void stopTurningRight() { isTurningRight = false; }
}
