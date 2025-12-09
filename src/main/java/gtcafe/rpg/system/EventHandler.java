package gtcafe.rpg.system;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.ui.UI;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.state.Direction;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.tile.Scense;

public class EventHandler {
    
    GameContext context;
    EventRect eventRect[][][];

    // prevent the event happen repeatly.
    public int previousEventX, previousEventY;
    boolean canTouchEvent = true;
    public Scense tempMap;
    public int tempCol, tempRow;

    public EventHandler(GameContext context) {
        this.context = context;

        eventRect = new EventRect[context.getMaxMap()][context.getMaxWorldRow()][context.getMaxWorldRow()];

        // 為整個 世界地圖 建立 EventRect 二維矩陣
        int col = 0;
        int row = 0;
        int map = 0;
        while( map < context.getMaxMap() && col < context.getMaxWorldCol() && row < context.getMaxWorldRow()) {
            // 用來判斷是否觸發事件的範圍
            eventRect[map][col][row] = new EventRect();
            eventRect[map][col][row].x = 23;
            eventRect[map][col][row].y = 23;
            eventRect[map][col][row].width = 2;
            eventRect[map][col][row].height = 2; // small
            eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
            eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;

            col++;
            if( col == context.getMaxWorldCol()) {
                col = 0;
                row++;

                // reset event for each map
                if (row == context.getMaxWorldRow()) {
                    row = 0; 
                    map++;
                }
            }
        }
    }

    // check the event collision
    public boolean hit(Scense map, int col, int row, Direction requiredDirection) {
        boolean hit = false;
        int mapIndex = map.index;

        if (mapIndex == context.getCurrentMap().index) {
            context.getPlayer().solidArea.x = context.getPlayer().worldX + context.getPlayer().solidArea.x;
            context.getPlayer().solidArea.y = context.getPlayer().worldY + context.getPlayer().solidArea.y;

            eventRect[mapIndex][col][row].x = col * context.getTileSize() + eventRect[mapIndex][col][row].x;
            eventRect[mapIndex][col][row].y = row * context.getTileSize() + eventRect[mapIndex][col][row].y;

            // checking if player's solidArea is colliding with eventRect's solidArea
            if (context.getPlayer().solidArea.intersects(eventRect[mapIndex][col][row]) && 
                eventRect[mapIndex][col][row].eventDone == false)  // happen one time only
            {
                if (context.getPlayer().direction == requiredDirection || requiredDirection == Direction.ANY) {
                    hit = true;

                    previousEventX = context.getPlayer().worldX;
                    previousEventY = context.getPlayer().worldY;
                }
            }

            context.getPlayer().solidArea.x = context.getPlayer().solidAreaDefaultX;
            context.getPlayer().solidArea.y = context.getPlayer().solidAreaDefaultY;
            eventRect[mapIndex][col][row].x = eventRect[mapIndex][col][row].eventRectDefaultX;
            eventRect[mapIndex][col][row].y = eventRect[mapIndex][col][row].eventRectDefaultY;
        }

        
        return hit;
    }


    public void checkEvent() {

        // Check if the player character is more than 1 tile away from the last event
        int xDistance = Math.abs(context.getPlayer().worldX - previousEventX);
        int yDistance = Math.abs(context.getPlayer().worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);
        if (distance > context.getTileSize()) {
            canTouchEvent = true;
        }

        if (canTouchEvent) {
            // damagePit
            // if (hit(Map.WORLD_MAP, 27,16,Direction.RIGHT)) { damagePit(GameState.DIALOGUE_STATE); }
            // else if (hit(Map.WORLD_MAP, 23,19,Direction.ANY)) { damagePit(GameState.DIALOGUE_STATE); }

            // water side
            if (hit(Scense.WORLD_MAP, 23,12,Direction.UP)) { healingPool(GameState.DIALOGUE); }

            // TRANSISTION MAP
            else if (hit(Scense.WORLD_MAP, 10,39,Direction.ANY)) { teleport(Scense.STORE, 12, 13); }
            else if (hit(Scense.STORE, 12,13,Direction.ANY)) { teleport(Scense.WORLD_MAP, 10, 39); }

            else if (hit(Scense.STORE, 12,9,Direction.UP)) { speak(context.getNpc()[1][0]); } // TODO
        }
    }

    private void speak(Entity entity) {
        if (context.getKeyHandler().enterPressed == true) {
            context.setGameState(GameState.DIALOGUE);
            context.getPlayer().attackCanceled = true;
            entity.speak();
        }
    }

    // update player's position
    private void teleport(Scense map, int col, int row) {
        context.setGameState(GameState.TRANSITION);

        // 暫存座標, 在 UI 做 Transistion 處理後使用
        tempMap = map;
        tempCol = col;
        tempRow = row;

        canTouchEvent = false;

        context.playSoundEffect(Sound.FX__STAIRS);
    }

    private void damagePit(GameState gameState) {
        context.setGameState(gameState);
        context.getGameUI().currentDialogue = "You fall into a pit!";
        context.getPlayer().life -= 1;

        canTouchEvent = false;
        System.out.println("[EventHandler#damagePit] Player are hit! Lost helf heart!");
        System.out.printf("[EventHandler#damagePit] Player.Life: [%s]\n", context.getPlayer().life);

    }

    public void healingPool(GameState gameState) {
        if (context.getKeyHandler().enterPressed == true) {
            context.setGameState(gameState);
            context.getPlayer().attackCanceled = true;
            context.getGameUI().currentDialogue = "You drink the water. \nYour life and mana have been recovered.\n(The progress has been saved)";
            context.getPlayer().life = context.getPlayer().maxLife;
            context.getPlayer().mana = context.getPlayer().maxMana;
            context.playSoundEffect(Sound.FX_COIN);
            context.getSaveLoad().save();
        }
    }
}
