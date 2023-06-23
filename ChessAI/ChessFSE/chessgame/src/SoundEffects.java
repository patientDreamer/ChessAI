import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @SoundEffects: a class contains useful methods for generating sound effects*/
public class SoundEffects {
    private HashMap<String, File> sounds; //a hashmap contains the key (the type of the sound effect) and the wav file for the actual sound effect
    private String[] keys = {"button","check","move","take"}; //different types of the sound effect
    private String[] soundDir = {
            "src/Sound/button.wav",
            "src/Sound/check.wav",
            "src/Sound/move.wav",
            "src/Sound/take.wav"
    }; //directories for the sound effect files

    /**
     * @SoundEffects: constructor that add all the sounds into the hashmap
     */
    public SoundEffects() {
        sounds = new HashMap<>();
        for (int i = 0; i < soundDir.length; i++){
            sounds.put(keys[i], new File (soundDir[i]));
        }
    }

    /**
     * @playSoundEffect: method that use for playing sound effect
     * @param soundEffectName: 4 different sound effects available (button, check, move, take),
     * and just pass in the type of the sound effect needed in lower cases (button, check, move, or take) */
    public void playSoundEffect(String soundEffectName) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        //instantiate the audio stream and set up a clip object to play the sound effect
        AudioInputStream audioSteam = AudioSystem.getAudioInputStream(sounds.get(soundEffectName));
        Clip clip = AudioSystem.getClip();
        clip.open(audioSteam);
        clip.start(); //play the sound effect

    }
}
