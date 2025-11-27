package gtcafe.rpg;

public class EventHandler {
    
    GamePanel gp;
    EventRect eventRect[][];

    // prevent the event happen repeatly.
    int previousEventX, previousEventY;
    boolean canTouchEvent = true;

    public EventHandler(GamePanel gp) {
        this.gp = gp;

        eventRect = new EventRect[gp.maxWorldRow][gp.maxWorldRow];

        // 為整個 世界地圖 建立 EventRect 二維矩陣
        int col = 0;
        int row = 0;
        while( col < gp.maxWorldCol && row < gp.maxWorldRow) {
            // 用來判斷是否觸發事件的範圍
            eventRect[col][row] = new EventRect();
            eventRect[col][row].x = 23;
            eventRect[col][row].y = 23;
            eventRect[col][row].width = 2;
            eventRect[col][row].height = 2; // small
            eventRect[col][row].eventRectDefaultX = eventRect[col][row].x;
            eventRect[col][row].eventRectDefaultY = eventRect[col][row].y;

            col++;
            if( col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    public void checkEvent() {

        // Check if the player character is more than 1 tile away from the last event
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);
        if (distance > gp.tileSize) {
            canTouchEvent = true;
        }

        if (canTouchEvent) {
            // damagePit
            if (hit(27,16,Direction.RIGHT)) { damagePit(27, 16, GameState.DIALOGUE_STATE); }
            if (hit(23,19,Direction.ANY)) { damagePit(27, 16, GameState.DIALOGUE_STATE); }

            // water side
            if (hit(23,12,Direction.UP) == true) { healingPool(23, 12, GameState.DIALOGUE_STATE); }
        }
    }

    private void damagePit(int col, int row, GameState gameState) {
        gp.gameState = gameState;
        gp.ui.currentDialogue = "You fall into a pit!";
        gp.player.life -= 1;

        // eventRect[col][row].eventDone = true;
        canTouchEvent = false;
        System.out.println("[EventHandler#damagePit] Player are hit! Lost helf heart!");
        System.out.printf("[EventHandler#damagePit] Player.Life: [%s]\n", gp.player.life);

    }

    // check the event collision
    public boolean hit(int col, int row, Direction requiredDirection) {
        boolean hit = false;

        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        eventRect[col][row].x = col * gp.tileSize + eventRect[col][row].x;
        eventRect[col][row].y = row * gp.tileSize + eventRect[col][row].y;

        // checking if player's solidArea is colliding with eventRect's solidArea
        if (gp.player.solidArea.intersects(eventRect[col][row]) && 
            eventRect[col][row].eventDone == false)  // happen one time only
        {
            if (gp.player.direction == requiredDirection || requiredDirection == Direction.ANY) {
                hit = true;

                previousEventX = gp.player.worldX;
                previousEventY = gp.player.worldY;
            }
        }

        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
        eventRect[col][row].x = eventRect[col][row].eventRectDefaultX;
        eventRect[col][row].y = eventRect[col][row].eventRectDefaultY;

        return hit;
    }

    public void healingPool(int col, int row, GameState gameState) {
        if (gp.keyHandler.enterPressed == true) {
            gp.gameState = gameState;
            gp.player.attackCanceled = true;
            gp.ui.currentDialogue = "You drink the water. \nYour life and mana have been recovered.";
            gp.player.life = gp.player.maxLife;
            gp.player.mana = gp.player.maxMana;
            gp.playSoundEffect(Sound.FX_COIN);
            // System.out.println("[EventHandler#healingPool] Player's life has been recovered!");

            gp.assetSetter.setMonster(); // restore the monsters.
        }
    }
}
