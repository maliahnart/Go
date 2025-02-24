package Utils;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound {
    private Clip clip;
    private FloatControl volumeControl;

    public Sound(String soundFileName) {
        initClip(soundFileName);
    }
    private void initClip(String soundFileName) {
        try {
            File soundFile = new File(soundFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error with playing sound: " + e.getMessage());
        }
    }

    public void playSound() {
        if (clip != null && !clip.isRunning()) {
            clip.stop();
            clip.setFramePosition(0);  // rewind to the beginning
            clip.start();  // Start playing
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void stopSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void setVolume(float volume) {
        if (clip != null) {
            if (volume < 0f) volume = 0f;
            if (volume > 1f) volume = 1f;
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }


    public Clip getClip() {
        return clip;
    }
}