// day9-1 start
package gtcafe.rpg;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    Clip clip;

    URL soundURL[] = new URL[30];   // store the sound url

    public Sound() {
        soundURL[0] = getClass().getResource("/gtcafe/rpg/assets/sound/BlueBoyAdventure.wav");
        soundURL[1] = getClass().getResource("/gtcafe/rpg/assets/sound/coin.wav");
        soundURL[2] = getClass().getResource("/gtcafe/rpg/assets/sound/powerup.wav");
        soundURL[3] = getClass().getResource("/gtcafe/rpg/assets/sound/unlock.wav");
        soundURL[4] = getClass().getResource("/gtcafe/rpg/assets/sound/fanfare.wav");
    }

    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void play() {
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }
}
// day9-1 end
