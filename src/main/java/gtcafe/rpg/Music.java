// package gtcafe.rpg;

// import java.net.URL;

// import javax.sound.sampled.AudioInputStream;
// import javax.sound.sampled.AudioSystem;

// public class Music extends Sound {
//     URL soundURL[] = new URL[30];   // store the sound url

//     public final static int MUSIC__MAIN_THEME = 0;
//     public final static int MUSIC__FANFARE = 1;

//     public Music () {
//         soundURL[MUSIC__MAIN_THEME] = getClass().getResource("/gtcafe/rpg/assets/bgm/BlueBoyAdventure.wav");
//         soundURL[MUSIC__FANFARE] = getClass().getResource("/gtcafe/rpg/assets/bgm/fanfare.wav");
//     }

//     public void setFile(int i) {
//         try {
//             AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
//             clip = AudioSystem.getClip();
//             clip.open(ais);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }