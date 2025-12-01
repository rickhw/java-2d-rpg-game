package gtcafe.rpg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
    GamePanel gp;
    public Config(GamePanel gp) {
        this.gp = gp;
    }

    public void saveConfig() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"))) {

            // Full Screen
            if (gp.fullScreenOn == true) {
                bw.write("On");
            }
            if (gp.fullScreenOn == false) {
                bw.write("Off");
            }
            bw.newLine();

            // Music Volume
            bw.write(String.valueOf(gp.music.volumeScale));
            bw.newLine();

            // Sound Effect
            bw.write(String.valueOf(gp.soundEffect.volumeScale));
            bw.newLine();

            // 
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("config.txt"));

            String s = br.readLine();
            
            // Full screen
            if(s.equalsIgnoreCase("On")) {
                gp.fullScreenOn = true;
            } else {
                gp.fullScreenOn = false;
            }

            // Music Volume
            s = br.readLine();
            gp.music.volumeScale = Integer.parseInt(s);

            // Sound Effect;
            s = br.readLine();
            gp.soundEffect.volumeScale = Integer.parseInt(s);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
