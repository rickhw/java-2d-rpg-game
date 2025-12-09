package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Key extends Entity {
    GameContext context;
    public OBJ_Key(GameContext context) {
        super(context);
        this.context = context;
        name = "Key";
        type = EntityType.CONSUMABLE;
        down1 = setup("/gtcafe/rpg/assets/objects/key.png", context.getTileSize(), context.getTileSize());
        description = "[" + name + "]\nIt opens a door.";
        price = 350;
        stackable = true;
    }

    public boolean use(Entity entity) {
        context.setGameState(GameState.DIALOGUE);

        int objIndex = getDetected(entity, context.getObj(), "Door");
        System.out.printf("[OBJ_Key#use] objIndex [%s] by getDetected\n", objIndex);
        if (objIndex != 999) {
            context.getGameUI().currentDialogue = "You use the " + name + " and open the door";
            context.playSoundEffect(Sound.FX_UNLOCK);
            context.getObj()[context.getCurrentMap().index][objIndex] = null;
            
            return true;    // means delete it.
        } else {
            context.getGameUI().currentDialogue = "What are you doing? Nothing happened."; 
            return false;   
        }
    }
}
