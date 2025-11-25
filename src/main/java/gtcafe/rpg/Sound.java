package gtcafe.rpg;

import javax.sound.sampled.Clip;

public class Sound {
    Clip clip;

    public void setFile(int i) {}

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