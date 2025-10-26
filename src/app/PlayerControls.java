package app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

import javax.swing.Timer;

import app.gameElements.GameMap;
import app.gameElements.MainCharacter;
import app.gameElements.hitbox.hitboxSubClasses.CowHitbox;
import app.gameElements.hitbox.hitboxSubClasses.RectangleHitbox;

public class PlayerControls implements KeyListener, MouseListener {
	
    private final MainCharacter m_mainCharacter;
    private final Timer m_timer;
    private final GameRenderer m_game;
    
    private final GameMap m_map;
    
    private boolean inputChanged = false;

    public PlayerControls(MainCharacter mainCharacter, Timer timer, GameRenderer game, GameMap map) {
        m_mainCharacter = Objects.requireNonNull(mainCharacter, "The m_mainCharacter may not be null");
        m_timer = Objects.requireNonNull(timer, "The timer may not be null");
        m_game = Objects.requireNonNull(game, "The game may not be null");
        
        m_map = Objects.requireNonNull(map, "The map may not be null");
    }
    
    public synchronized boolean inputIsChanged() {
    	boolean result = inputChanged;
    	inputChanged = false;
    	
    	return result;
    }
    
    private synchronized void triggerChangeFlag() {
    	inputChanged = true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
        	if (!m_mainCharacter.isTurningLeft()) triggerChangeFlag();
        	
        	m_mainCharacter.turnLeft();
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
        	if (!m_mainCharacter.isTurningRight()) triggerChangeFlag();
        	
        	m_mainCharacter.turnRight();
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
        	if (!m_mainCharacter.isMovingForward()) triggerChangeFlag();
        	
        	m_mainCharacter.moveForward();
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
        	if (!m_mainCharacter.isMovingBackward()) triggerChangeFlag();
        	
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
            m_mainCharacter.toggleDisguise();
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
        	m_mainCharacter.stopMovingForward();
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
        	m_mainCharacter.stopMovingBackward();
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            m_timer.start();
        }
        
        triggerChangeFlag();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            m_mainCharacter.startShooting();
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
        m_mainCharacter.stopShooting();
    }
}
