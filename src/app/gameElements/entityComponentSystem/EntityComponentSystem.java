
package app.gameElements.entityComponentSystem;

public final class EntityComponentSystem {
	/**
	 * This field will be removed when a more sophisticated physics system is created.
	 */
	private HitBox temporaryCollider;
	
	private final ECSList entityList;
	
	private final ECSList transformComponentList;
	
	private final ECSList hitBoxComponentList;
	private final ECSList moveInputComponentList;
	
	public EntityComponentSystem() {
		entityList = new ECSList();
		
		transformComponentList = new ECSList();
		
		hitBoxComponentList = new ECSList();
		moveInputComponentList = new ECSList();
	}
	
	public void setTemporaryCollider(HitBox newTemporaryCollider) {
		temporaryCollider = newTemporaryCollider;
	}
	
	public void tick() {
		
	}
	
	public ECSList getEntityList() { return entityList; }
	public ECSList getTransformComponentList() { return transformComponentList; }
	
	public void addEntity(int entityId, int entityType, float xPos, float yPos, float angle) {
		entityList.addElement(entityId, entityType);
		transformComponentList.addElement(entityId, new Transform(xPos, yPos, angle));
	}
	public void removeEntity(int entityId) {
		entityList.removeElement(entityId);
		transformComponentList.removeElement(entityId);
	}
	
	private void updateHitBoxComponents() {
		
	}
	
	private void updateMoveInputComponents() {
		
	}
}
