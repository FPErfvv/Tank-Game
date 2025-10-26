
package app.gameElements;

import app.gameElements.entityComponentSystem.EntityComponentSystem;

public final class GameLevel {
	private final EntityComponentSystem ecs;
	
	public GameLevel() {
		ecs = new EntityComponentSystem();
	}
	
	public void tick() {
		ecs.tick();
	}
	
	public EntityComponentSystem getEntityComponentSystem() { return ecs; }
}
