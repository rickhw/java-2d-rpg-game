package gtcafe.rpg.system;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.state.Direction;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.tile.Scene;

public class EventHandler {
    
    GamePanel gp;
    EventRect eventRect[][][];
    Entity eventSource;

    // prevent the event happen repeatly.
    public int previousEventX, previousEventY;
    boolean canTouchEvent = true;
    public Scene tempMap;
    public int tempCol, tempRow;

    public EventHandler(GamePanel gp) {
        this.gp = gp;
        this.eventSource = new Entity(gp);

        eventRect = new EventRect[gp.maxMap][gp.maxWorldRow][gp.maxWorldRow];

        // 為整個 世界地圖 建立 EventRect 二維矩陣
        int col = 0;
        int row = 0;
        int map = 0;
        while( map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow) {
            // 用來判斷是否觸發事件的範圍
            eventRect[map][col][row] = new EventRect();
            eventRect[map][col][row].x = 23;
            eventRect[map][col][row].y = 23;
            eventRect[map][col][row].width = 2;
            eventRect[map][col][row].height = 2; // small
            eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
            eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;

            col++;
            if( col == gp.maxWorldCol) {
                col = 0;
                row++;

                // reset event for each map
                if (row == gp.maxWorldRow) {
                    row = 0; 
                    map++;
                }
            }
        }

        setDialogue();
    }

    public void setDialogue() {
        eventSource.dialogues[0][0] = "You fall into a pit!";

        eventSource.dialogues[1][0] = "You drink the water. \nYour life and mana have been recovered.\n(The progress has been saved)";
        eventSource.dialogues[1][1] = "Damn, this is good water.";
    }

    // check the event collision
    public boolean hit(Scene map, int col, int row, Direction requiredDirection) {
        boolean hit = false;
        int mapIndex = map.index;

        if (mapIndex == gp.currentMap.index) {
            gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
            gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

            eventRect[mapIndex][col][row].x = col * gp.tileSize + eventRect[mapIndex][col][row].x;
            eventRect[mapIndex][col][row].y = row * gp.tileSize + eventRect[mapIndex][col][row].y;

            // checking if player's solidArea is colliding with eventRect's solidArea
            if (gp.player.solidArea.intersects(eventRect[mapIndex][col][row]) && 
                eventRect[mapIndex][col][row].eventDone == false)  // happen one time only
            {
                if (gp.player.direction == requiredDirection || requiredDirection == Direction.ANY) {
                    hit = true;

                    previousEventX = gp.player.worldX;
                    previousEventY = gp.player.worldY;
                }
            }

            gp.player.solidArea.x = gp.player.solidAreaDefaultX;
            gp.player.solidArea.y = gp.player.solidAreaDefaultY;
            eventRect[mapIndex][col][row].x = eventRect[mapIndex][col][row].eventRectDefaultX;
            eventRect[mapIndex][col][row].y = eventRect[mapIndex][col][row].eventRectDefaultY;
        }

        
        return hit;
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
            // if (hit(Map.WORLD_MAP, 27,16,Direction.RIGHT)) { damagePit(GameState.DIALOGUE_STATE); }
            // else if (hit(Map.WORLD_MAP, 23,19,Direction.ANY)) { damagePit(GameState.DIALOGUE_STATE); }

            // water side
            if (hit(Scene.WORLD_MAP, 23,12,Direction.UP)) { healingPool(GameState.DIALOGUE); }

            // Speak to NPC in store
            else if (hit(Scene.STORE, 12,9,Direction.UP)) { speak(gp.npc[1][0]); } // TODO

            // TRANSITE TO STORE 
            else if (hit(Scene.WORLD_MAP, 10,39,Direction.ANY)) { teleport(Scene.STORE, 12, 13, GamePanel.INDOOR); }
            else if (hit(Scene.STORE, 12,13,Direction.ANY)) { teleport(Scene.WORLD_MAP, 10, 39, GamePanel.OUTSIDE); }

            // TO DUNGDEON B1
            else if (hit(Scene.WORLD_MAP, 12,9,Direction.ANY)) { teleport(Scene.DONGEON01, 9, 41, GamePanel.DUNGEON);} // To the Dungeon
            else if (hit(Scene.DONGEON01, 9,41,Direction.ANY)) { teleport(Scene.WORLD_MAP, 12, 9, GamePanel.OUTSIDE);} // To the World

            // TO DUNGDEON B2
            else if (hit(Scene.DONGEON01, 8,7,Direction.ANY)) { teleport(Scene.DONGEON02, 26, 41, GamePanel.DUNGEON);} // To the Dungeon 02 / B2
            else if (hit(Scene.DONGEON02, 26,41,Direction.ANY)) { teleport(Scene.DONGEON01, 8, 7, GamePanel.DUNGEON);} // To the Dungeon 01 / B1
        }
    }

    private void speak(Entity entity) {
        if (gp.keyHandler.enterPressed == true) {
            gp.gameState = GameState.DIALOGUE;
            gp.player.attackCanceled = true;
            entity.speak();
        }
    }

    // update player's position
    private void teleport(Scene map, int col, int row, int area) {
        gp.gameState = GameState.TRANSITION;
        gp.nextArea = area;

        // 暫存座標, 在 UI 做 Transistion 處理後使用
        tempMap = map;
        tempCol = col;
        tempRow = row;

        canTouchEvent = false;

        gp.playSoundEffect(Sound.FX__STAIRS);
    }

    private void damagePit(GameState gameState) {
        gp.gameState = gameState;
        gp.ui.currentDialogue = "You fall into a pit!";
        eventSource.startDialogue(eventSource, 0);
        gp.player.life -= 1;

        canTouchEvent = false;
        System.out.println("[EventHandler#damagePit] Player are hit! Lost helf heart!");
        System.out.printf("[EventHandler#damagePit] Player.Life: [%s]\n", gp.player.life);

    }

    public void healingPool(GameState gameState) {
        if (gp.keyHandler.enterPressed == true) {
            gp.gameState = gameState;
            gp.player.attackCanceled = true;
            eventSource.startDialogue(eventSource, 1);
            gp.player.life = gp.player.maxLife;
            gp.player.mana = gp.player.maxMana;
            gp.playSoundEffect(Sound.FX_COIN);
            gp.saveLoad.save();
        }
    }
}
