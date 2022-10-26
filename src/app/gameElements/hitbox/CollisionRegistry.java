// class for registering + tracking collision events.
package app.gameElements.hitbox;

import java.util.ArrayList;

public class CollisionRegistry {
    private ArrayList<CollisionEvent> collisionEvents;
    public CollisionRegistry() {
        collisionEvents = new ArrayList<CollisionEvent>();
    }

    /**
     * This method registers a collision with the Collision Registry. It 
     * compares all collision with all of the registered collisions and 
     * registers the collision if there are no other duplicates.
     * @param collisionEvent
     */
    public void registerCollision(CollisionEvent collisionEvent) {
        for (CollisionEvent e: collisionEvents) {
            if (!e.equals(collisionEvent)) {
                continue;
            }
            collisionEvents.add(collisionEvent);
        }
    }
    
}