package gtcafe.rpg.entity.equipable;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

// increase player's speed
public class OBJ_Boots extends Entity {
    public OBJ_Boots(GameContext context) {
        super(context);
        type = EntityType.SHOE;
        name = "Boots";
        down1 = setup("/gtcafe/rpg/assets/objects/boots.png", context.getTileSize(), context.getTileSize());
        price = 150;
    }

    // public boolean use(Entity entity) {
    //     context.playSoundEffect(Sound.FX_POWER_UP);
    //     // context.getGameUI().addMessage("ife +" + value);
    //     entity.speed += 5;

    //     return false;    // means delete it.
    // }

}
