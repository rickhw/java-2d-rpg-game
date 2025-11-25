// package gtcafe.rpg;

// import java.net.URL;

// import javax.sound.sampled.AudioInputStream;
// import javax.sound.sampled.AudioSystem;

// public class SoundEffect extends Sound {

//     URL soundURL[] = new URL[30];   // store the sound url

//     public final static int FX_COIN = 0;
//     public final static int FX_POWER_UP= 1;
//     public final static int FX_UNLOCK = 2;

//     public final static int FX_HIT_MONSTER = 3;
//     public final static int FX_RECEIVE_DAMAGE= 4;
//     public final static int FX_SWING_WEAPON = 5;
//     public final static int FX__LEVELUP = 6;
//     public final static int FX__CURSOR = 7;

//     public SoundEffect() {

//         soundURL[FX_COIN] = getClass().getResource("/gtcafe/rpg/assets/sound/coin.wav");
//         soundURL[FX_POWER_UP] = getClass().getResource("/gtcafe/rpg/assets/sound/powerup.wav");
//         soundURL[FX_UNLOCK] = getClass().getResource("/gtcafe/rpg/assets/sound/unlock.wav");

//         soundURL[FX_HIT_MONSTER] = getClass().getResource("/gtcafe/rpg/assets/sound/hitmonster.wav");
//         soundURL[FX_RECEIVE_DAMAGE] = getClass().getResource("/gtcafe/rpg/assets/sound/receivedamage.wav");
//         soundURL[FX_SWING_WEAPON] = getClass().getResource("/gtcafe/rpg/assets/sound/swingweapon.wav");

//         soundURL[FX__LEVELUP] = getClass().getResource("/gtcafe/rpg/assets/sound/levelup.wav");
//         soundURL[FX__CURSOR] = getClass().getResource("/gtcafe/rpg/assets/sound/cursor.wav");
//     }

//      public void setFile(int i) {
//         try {
//             AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
//             clip = AudioSystem.getClip();
//             clip.open(ais);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }