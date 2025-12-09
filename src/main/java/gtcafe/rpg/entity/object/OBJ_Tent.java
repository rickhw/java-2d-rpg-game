package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Tent extends Entity {
    public OBJ_Tent(GameContext context) {
        super(context);
        name = "Tent";
        type = EntityType.CONSUMABLE;
        description = "[Tent]\nYou can sleep until\nnext morning.";
        down1 = setup("/gtcafe/rpg/assets/objects/tent.png", context.getTileSize(), context.getTileSize());
        price = 300;
        stackable = true;
    }

    public boolean use(Entity entity) {
        context.setGameState(GameState.SLEEP);
        context.playSoundEffect(Sound.FX__SLEEP);
        context.getPlayer().life = context.getPlayer().maxLife;
        context.getPlayer().mana = context.getPlayer().maxMana;
        context.getPlayer().getSleepingImage(down1);

        return true; // diseaper after using
    }
}
