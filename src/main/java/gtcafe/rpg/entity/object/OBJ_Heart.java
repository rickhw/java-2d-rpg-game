package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Heart extends Entity {
    GameContext context;
    public OBJ_Heart(GameContext context) {
        super(context);
        this.context = context;
        name = "Heart";
        type = EntityType.PICKUPONLY;
        value = 2;
        // for put object to map
        down1 = setup("/gtcafe/rpg/assets/objects/heart_full.png", context.getTileSize(), context.getTileSize()); 
        // for draw the life on top
        image = setup("/gtcafe/rpg/assets/objects/heart_full.png", context.getTileSize(), context.getTileSize());
        image2 = setup("/gtcafe/rpg/assets/objects/heart_half.png", context.getTileSize(), context.getTileSize());
        image3 = setup("/gtcafe/rpg/assets/objects/heart_blank.png", context.getTileSize(), context.getTileSize());
    }

    public boolean use(Entity entity) {
        context.playSoundEffect(Sound.FX_POWER_UP);
        context.getGameUI().addMessage("Life +" + value);
        entity.life += value;

        return true;    // means delete it.
    }
}
