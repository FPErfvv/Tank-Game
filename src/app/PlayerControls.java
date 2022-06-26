package app;

import java.util.Objects;
import javax.swing.Timer;

import app.hitbox.CowHitbox;
import app.hitbox.RectangleHitbox;

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
            m_mainCharacter.turn(Constants.TURNING_LEFT);
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            m_mainCharacter.turn(Constants.TURNING_RIGHT);
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
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (m_mainCharacter.getHitBox() instanceof CowHitbox) {
                m_mainCharacter.setHitbox(new RectangleHitbox(m_mainCharacter));
                m_mainCharacter.setImage("src/images/MainCharacter.png");
            } else {
                m_mainCharacter.setImage("src/images/MainCowPic.png");
                m_mainCharacter.setHitbox(new CowHitbox(m_mainCharacter));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        if (e.getKeyCode() == KeyEvent.VK_A) {
            m_mainCharacter.stopTurn(Constants.TURNING_LEFT);
        } 
        else if (e.getKeyCode() == KeyEvent.VK_D) {
            m_mainCharacter.stopTurn(Constants.TURNING_RIGHT);
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