package app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

import javax.swing.Timer;

import app.gameElements.MainCharacter;
import app.gameElements.hitbox.hitboxSubClasses.CowHitbox;
import app.gameElements.hitbox.hitboxSubClasses.RectangleHitbox;

public class PlayerControls implements KeyListener, MouseListener {
	
    private final MainCharacter m_mainCharacter;
    private final Timer m_timer;
    private final Game m_game;
    
    private final GameMap m_map;

    public static boolean fireTime;

    PlayerControls(MainCharacter mainCharacter, Timer timer, Game game, GameMap map) {
        m_mainCharacter = Objects.requireNonNull(mainCharacter, "The m_mainCharacter may not be null");
        m_timer = Objects.requireNonNull(timer, "The timer may not be null");
        m_game = Objects.requireNonNull(game, "The game may not be null");
        
        m_map = Objects.requireNonNull(map, "The map may not be null");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
        	m_mainCharacter.turnLeft();
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
        	m_mainCharacter.turnRight();
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
        	m_mainCharacter.moveForward();
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
        	m_mainCharacter.moveBackward();
        }

        if (e.getKeyCode() == KeyEvent.VK_E) {
            m_timer.stop();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_B) {
        	m_game.toggleHitboxes();
        }
       
        if(e.getKeyCode() == KeyEvent.VK_R) {
            m_map.addArmedCow();
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (m_mainCharacter.getHitbox() instanceof CowHitbox) {
                m_mainCharacter.setImage("src/images/MainCharacter.png");
                m_mainCharacter.setHitbox(new RectangleHitbox(m_mainCharacter.getWidth(), m_mainCharacter.getHeight()));
                m_mainCharacter.weaponized = true;
            }
            else {
                m_mainCharacter.setImage("src/images/MainCowPic.png");
                m_mainCharacter.setHitbox(new CowHitbox(m_mainCharacter.getWidth(), m_mainCharacter.getHeight()));
                m_mainCharacter.weaponized = false;
            }
        }
        
        // Debug Related
        
        if (e.getKeyCode() == KeyEvent.VK_F3) {
    		m_game.toggleDebugMenu();
    	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
        	m_mainCharacter.stopTurningLeft();
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
        	m_mainCharacter.stopTurningRight();
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            // TODO: instead of setting speed to zero, use this as the place to reduce velocity by the amount that was added prevously by the move method.
        	m_mainCharacter.stopMovingForward();
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
        	m_mainCharacter.stopMovingBackward();
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            m_timer.start();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            fireTime = true;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {
        fireTime = false;
    }
}
