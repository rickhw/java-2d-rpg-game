package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.system.Sound;

public class OBJ_Key extends Entity {
    GamePanel gp;
    public OBJ_Key(GamePanel gp) {
        super(gp);
        this.gp = gp;
        name = "Key";
        type = EntityType.CONSUMABLE;
        down1 = setup("/gtcafe/rpg/assets/objects/key.png", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nIt opens a door.";
        price = 350;
        stackable = true;
    }

    public boolean use(Entity entity) {
        gp.gameState = GameState.DIALOGUE;

        int objIndex = getDetected(entity, gp.obj, "Door");
        System.out.printf("[OBJ_Key#use] objIndex [%s] by getDetected\n", objIndex);
        if (objIndex != 999) {
            gp.ui.currentDialogue = "You use the " + name + " and open the door";
            gp.playSoundEffect(Sound.FX_UNLOCK);
            gp.obj[gp.currentMap.index][objIndex] = null;
            
            return true;    // means delete it.
        } else {
            gp.ui.currentDialogue = "What are you doing? Nothing happened."; 
            return false;   
        }
    }
}