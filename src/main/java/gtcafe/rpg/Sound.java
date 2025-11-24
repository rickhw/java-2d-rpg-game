package gtcafe.rpg;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    Clip clip;

    URL soundURL[] = new URL[30];   // store the sound url

    public final static int MUSIC__MAIN_THEME = 0;
    public final static int MUSIC__FANFARE = 1;

    public final static int FX_COIN = 2;
    public final static int FX_POWER_UP= 3;
    public final static int FX_UNLOCK = 4;

    public final static int FX_HIT_MONSTER = 5;
    public final static int FX_RECEIVE_DAMAGE= 6;
    public final static int FX_SWING_WEAPON = 7;

    public Sound() {
        soundURL[MUSIC__MAIN_THEME] = getClass().getResource("/gtcafe/rpg/assets/bgm/BlueBoyAdventure.wav");
        soundURL[MUSIC__FANFARE] = getClass().getResource("/gtcafe/rpg/assets/bgm/fanfare.wav");

        soundURL[FX_COIN] = getClass().getResource("/gtcafe/rpg/assets/sound/coin.wav");
        soundURL[FX_POWER_UP] = getClass().getResource("/gtcafe/rpg/assets/sound/powerup.wav");
        soundURL[FX_UNLOCK] = getClass().getResource("/gtcafe/rpg/assets/sound/unlock.wav");

        soundURL[FX_HIT_MONSTER] = getClass().getResource("/gtcafe/rpg/assets/sound/hitmonster.wav");
        soundURL[FX_RECEIVE_DAMAGE] = getClass().getResource("/gtcafe/rpg/assets/sound/receivedamage.wav");
        soundURL[FX_SWING_WEAPON] = getClass().getResource("/gtcafe/rpg/assets/sound/swingweapon.wav");
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