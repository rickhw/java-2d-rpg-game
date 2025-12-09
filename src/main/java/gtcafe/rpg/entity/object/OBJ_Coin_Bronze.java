package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Coin_Bronze extends Entity {
    
    public OBJ_Coin_Bronze(GameContext context) {
        super(context);
        name = "Bronze Coin";
        type = EntityType.PICKUPONLY;
        value = 1;
        down1 = setup("/gtcafe/rpg/assets/objects/coin_bronze.png", context.getTileSize(), context.getTileSize());
    }

    public boolean use(Entity entity) {
        context.playSoundEffect(Sound.FX_COIN);
        context.getGameUI().addMessage("Coin +" + value);
        entity.coin += value;

        return true;    // means delete it.
    }
}
