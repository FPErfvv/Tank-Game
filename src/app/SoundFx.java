package app;

import javax.sound.sampled.*;
import java.io.*;

public class SoundFx {

    private boolean shouldPlaySound = true;
    private int timeToRepeat;
    
    public void play50CalSound()
    {
        File audioFile = new File("src/soundfx/50CalSound.wav");
        if(!audioFile.exists()) {
            System.out.print("error");
        } try {
        
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audiocClip = (Clip) AudioSystem.getLine(info);

            audiocClip.open(audioStream);

            if(shouldPlaySound == true)
                audiocClip.start();

        
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
    }
    public void repeat50Cal() {
        timeToRepeat++;
        if(timeToRepeat >= 3) {
            play50CalSound();
            timeToRepeat = 0;
        }
    }
    public void resetFireTime() {
        timeToRepeat = 3;
    }
}