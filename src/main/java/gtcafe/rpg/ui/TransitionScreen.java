package gtcafe.rpg.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.state.GameState;

public class TransitionScreen {
    
    GamePanel gp;
    int counter = 0;

    public TransitionScreen(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        counter++;
        g2.setColor(new Color(0,0,0,counter*5));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (counter == 50) { // The transition is done
            counter = 0;
            System.out.printf("[Transition] from [%s] to [%s]\n", gp.currentMap.name, gp.eventHandler.tempMap.name);
            gp.gameState = GameState.PLAY;
            gp.currentMap = gp.eventHandler.tempMap;
            gp.player.worldX = gp.tileSize * gp.eventHandler.tempCol;
            gp.player.worldY = gp.tileSize * gp.eventHandler.tempRow;
            gp.eventHandler.previousEventX = gp.player.worldX;
            gp.eventHandler.previousEventY = gp.player.worldY;
            gp.chagneArea();
        }
    }
}