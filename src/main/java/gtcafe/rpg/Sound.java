package gtcafe.rpg;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[30];   // store the sound url
    FloatControl fc;
    int volumeScale = 3;
    float volume;

    public final static int MUSIC__MAIN_THEME = 0;
    public final static int MUSIC__FANFARE = 1;

    public final static int FX_COIN = 2;
    public final static int FX_POWER_UP= 3;
    public final static int FX_UNLOCK = 4;

    public final static int FX_HIT_MONSTER = 5;
    public final static int FX_RECEIVE_DAMAGE= 6;
    public final static int FX_SWING_WEAPON = 7;
    
    public final static int FX__LEVELUP = 8;
    public final static int FX__CURSOR = 9;
    public final static int FX__BURNING = 10;
    public final static int FX__CUT_TREE = 11;

    public Sound () {
        soundURL[MUSIC__MAIN_THEME] = getClass().getResource("/gtcafe/rpg/assets/bgm/BlueBoyAdventure.wav");
        soundURL[MUSIC__FANFARE] = getClass().getResource("/gtcafe/rpg/assets/bgm/fanfare.wav");

        soundURL[FX_COIN] = getClass().getResource("/gtcafe/rpg/assets/sound/coin.wav");
        soundURL[FX_POWER_UP] = getClass().getResource("/gtcafe/rpg/assets/sound/powerup.wav");
        soundURL[FX_UNLOCK] = getClass().getResource("/gtcafe/rpg/assets/sound/unlock.wav");

        soundURL[FX_HIT_MONSTER] = getClass().getResource("/gtcafe/rpg/assets/sound/hitmonster.wav");
        soundURL[FX_RECEIVE_DAMAGE] = getClass().getResource("/gtcafe/rpg/assets/sound/receivedamage.wav");
        soundURL[FX_SWING_WEAPON] = getClass().getResource("/gtcafe/rpg/assets/sound/swingweapon.wav");

        soundURL[FX__LEVELUP] = getClass().getResource("/gtcafe/rpg/assets/sound/levelup.wav");
        soundURL[FX__CURSOR] = getClass().getResource("/gtcafe/rpg/assets/sound/cursor.wav");
        soundURL[FX__BURNING] = getClass().getResource("/gtcafe/rpg/assets/sound/burning.wav");
        soundURL[FX__CUT_TREE] = getClass().getResource("/gtcafe/rpg/assets/sound/cuttree.wav");
    }


     public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            checkVolume();
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

    public void checkVolume() {
        // pass the float control between -80f to 6f
        switch (volumeScale) {
            case 0: volume = -80f; break; // no sound
            case 1: volume = -20f; break;
            case 2: volume = -12; break;
            case 3: volume = -5f; break;
            case 4: volume = 1f; break;
            case 5: volume = 6f; break;     // max
        }
        fc.setValue(volume);
    }
}