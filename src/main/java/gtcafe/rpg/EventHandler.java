package gtcafe.rpg;

import java.awt.Rectangle;

public class EventHandler {
    
    GamePanel gp;
    Rectangle eventRect;
    int eventRectDefaultX, eventRectDefaultY;

    public EventHandler(GamePanel gp) {
        this.gp = gp;

        // 用來判斷是否觸發事件的範圍
        eventRect = new Rectangle();
        eventRect.x = 23;
        eventRect.y = 23;
        eventRect.width = 2;
        eventRect.height = 2; // small
        eventRectDefaultX = eventRect.x;
        eventRectDefaultY = eventRect.y;
    }

    public void checkEvent() {
        if (hit(27,16,"right") == true) {
            // event happens
            damagePit(GameState.DIALOGUE_STATE);
        }

        // water side
        if (hit(23,12,"up") == true) {
            healingPool(GameState.DIALOGUE_STATE);
        }
    }

    private void damagePit(GameState gameState) {
        gp.gameState = gameState;
        gp.ui.currentDialogue = "You fall into a pit!";
        gp.player.life -= 1;
        System.out.println("[EventHandler#damagePit] Player are hit! Lost helf heart!");
        System.out.printf("[EventHandler#damagePit] Player.Life: [%s]\n", gp.player.life);
    }

    // check the event collision
    public boolean hit(int eventCol, int eventRow, String requiredDirection) {
        boolean hit = false;

        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        eventRect.x = eventCol * gp.tileSize + eventRect.x;
        eventRect.y = eventRow * gp.tileSize + eventRect.y;

        // checking if player's solidArea is colliding with eventRect's solidArea
        if (gp.player.solidArea.intersects(eventRect)) {
            if (gp.player.direction.contentEquals(requiredDirection) || requiredDirection.contains("any")) {
                hit = true;
            }
        }

        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
        eventRect.x = eventRectDefaultX;
        eventRect.y = eventRectDefaultY;

        return hit;
    }

    public void healingPool(GameState gameState) {
        if (gp.keyHandler.enterPressed == true) {
            gp.gameState = gameState;
            gp.ui.currentDialogue = "You drink the water. \nYour life has been recovered.";
            gp.player.life = gp.player.maxLife;
            System.out.println("[EventHandler#healingPool] Player's life has been recovered!");
        }
    }
}
