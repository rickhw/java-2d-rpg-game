package gtcafe.rpg.ui;

import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.state.DayState;
import gtcafe.rpg.state.GameState;

public class SleepScreen {
    
    GamePanel gp;
    int counter = 0;

    public SleepScreen(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        counter++;
        if (counter < 120) {    // 120 fps, 2 second
            gp.eManager.lighting.filterAlpha += 0.01f;
            if(gp.eManager.lighting.filterAlpha > 1f) {
                gp.eManager.lighting.filterAlpha = 1f;
            }
        }
        if (counter >= 120) {
            gp.eManager.lighting.filterAlpha -= 0.01f;
            if(gp.eManager.lighting.filterAlpha <= 0f) {
                gp.eManager.lighting.filterAlpha = 0f;
                counter = 0;
                gp.eManager.lighting.dayState = DayState.DAY;
                gp.eManager.lighting.dayCounter = 0;
                gp.gameState = GameState.PLAY;
                gp.player.getImages();
            }
        }
    }
}