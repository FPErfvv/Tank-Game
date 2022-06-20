package app;

import java.util.Objects;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class PlayerControls implements KeyListener {
    private final MainCharacter m_mainCharacter;
    private final Timer m_timer;
    private final GameMap m_map;

    PlayerControls(MainCharacter mainCharacter, Timer timer, GameMap map) {
        m_mainCharacter = Objects.requireNonNull(mainCharacter, "The m_mainCharacter may not be null");
        m_timer = Objects.requireNonNull(timer, "The timer may not be null");;
        m_map = Objects.requireNonNull(map, "The map may not be null");;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        if (e.getKeyCode() == KeyEvent.VK_A) {
            m_mainCharacter.startTurning(true, Constants.TURNING_LEFT);
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            m_mainCharacter.startTurning(true, Constants.TURNING_RIGHT);
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            m_mainCharacter.setSpeed(5);
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            m_mainCharacter.setSpeed(-5);
        }

        if (e.getKeyCode() == KeyEvent.VK_E) {
            m_timer.stop();
        }
        if(e.getKeyCode() == KeyEvent.VK_C) {
            SoundFx fx = new SoundFx();
            fx.play50CalSound();
        }
        if(e.getKeyCode() == KeyEvent.VK_R) {
            m_map.addSprite();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        if (e.getKeyCode() == KeyEvent.VK_A) {
            m_mainCharacter.startTurning(false, Constants.TURNING_LEFT);
        } 
        else if (e.getKeyCode() == KeyEvent.VK_D) {
            m_mainCharacter.startTurning(false, Constants.TURNING_RIGHT);
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            m_mainCharacter.setSpeed(0);
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            m_mainCharacter.setSpeed(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            m_timer.start();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }
}