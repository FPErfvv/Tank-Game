package app.gameElements.hitbox;

import app.gameElements.Sprite;

public class CollisionEvent {
    private Sprite m_sprite1;
    private Sprite m_sprite2;

    public CollisionEvent(Sprite sprite1, Sprite sprite2) {
        m_sprite1 = sprite1;
        m_sprite2 = sprite2;
    }

    /**
     * This method compares the sprites bewteen two {@link CollisionEvent}s. If they share the same two sprites, the method returns true.
     * @param collisionEvent 
     */
    public boolean equals(CollisionEvent collisionEvent) {
        if (collisionEvent.getSprites()[0].equals(m_sprite1) && collisionEvent.getSprites()[1].equals(m_sprite2) 
            || collisionEvent.getSprites()[1].equals(m_sprite1) && collisionEvent.getSprites()[0].equals(m_sprite2)) {
                return true;
        }
        return false;
    }

    public Sprite[] getSprites() {
        return new Sprite[] {m_sprite1, m_sprite1};
    }
}
