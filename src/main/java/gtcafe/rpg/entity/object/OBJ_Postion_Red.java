package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Postion_Red extends Entity {
    public OBJ_Postion_Red(GameContext context) {
        super(context);
        type = EntityType.CONSUMABLE;
        name = "Red Potion";
        value = 5;
        down1 = setup("/gtcafe/rpg/assets/objects/potion_red.png", context.getTileSize(), context.getTileSize());
        description = "[" + name + "]\nHeals your life by " + value + "."; 
        price = 100;
        stackable = true;
    }

    public boolean use(Entity entity) {
        context.setGameState(GameState.DIALOGUE);
        context.getGameUI().currentDialogue = "You drink the " + name + "!\n"
            + "Your life has ben recoeved by " + value + ".";
        entity.life += value;

        if (context.getPlayer().life > context.getPlayer().maxLife) {
            context.getPlayer().life = context.getPlayer().maxLife;
        }

        context.playSoundEffect(Sound.FX_POWER_UP);

        return true;    // means delete it.
    }
}
