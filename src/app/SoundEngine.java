
package app;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundEngine {
	
	private int maxPlayingClipCount = 10;
	
	private int playingClipCount = 0;
	private Clip playingClips[];
	
	private AudioInputStream shootSoundStream;
	
	private Clip shootSoundClip;
	
	public SoundEngine() {
		playingClips = new Clip[maxPlayingClipCount];
		for (int i = 0; i < maxPlayingClipCount; i++) {
			try {
				playingClips[i] = AudioSystem.getClip();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
		
		shootSoundStream = loadSound("src/soundfx/50CalSound.wav");
		
		loadShootSound();
	}
	
	public int getMaxPlayingClipCount() { return maxPlayingClipCount; }
	public int getPlayingClipCount() { return playingClipCount; }
	
	public void update() {
		for (int i = 0; i < playingClipCount; i++) {
			if (!playingClips[i].isActive()) {
				stopPlayingSoundClip(i--);
			}
		}
	}
	
	public void pauseSoundClips() {
		for (int i = 0; i < playingClipCount; i++) {
			playingClips[i].stop();
		}
	}
	public void resumeSoundClips() {
		for (int i = 0; i < playingClipCount; i++) {
			playingClips[i].start();
		}
	}
	
	public void playShootSoundClip() {
		//playSoundClip(shootSoundStream);
		
		shootSoundClip.setFramePosition(0);
		shootSoundClip.start();
	}
	
	private void playSoundClip(AudioInputStream audioStream) {
		if (playingClipCount == maxPlayingClipCount) {
			stopPlayingSoundClip(0);
		}
		
		try {
			playingClips[playingClipCount].open(audioStream);
			playingClips[playingClipCount].setFramePosition(0);
			playingClips[playingClipCount].start();
			playingClipCount++;
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}
	private void stopPlayingSoundClip(int idx) {
		playingClips[idx].stop();
		playingClips[idx].close();
		
		Clip temp = playingClips[idx];
		playingClips[idx] = playingClips[playingClipCount - 1];
		playingClips[playingClipCount - 1] = temp;
		playingClipCount--;
	}
	
	private AudioInputStream loadSound(String filePath) {
		File soundFile = new File(filePath);
		
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			
			return audioInputStream;
		} catch(IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void loadShootSound() {
		File soundFile = new File("src/soundfx/50CalSound.wav");
		
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			
			shootSoundClip = AudioSystem.getClip();
			shootSoundClip.open(audioInputStream);
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
