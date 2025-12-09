package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_ManaCrystal extends Entity {
    GameContext context;

    public OBJ_ManaCrystal(GameContext context) {
        super(context);
        this.context = context;

        name = "Mana Crystal";
        type = EntityType.PICKUPONLY;
        value = 1;
        // for put to map
        down1 = setup("/gtcafe/rpg/assets/objects/manacrystal_full.png", context.getTileSize(), context.getTileSize());
        // for draw the life on top
        image = setup("/gtcafe/rpg/assets/objects/manacrystal_full.png", context.getTileSize(), context.getTileSize());
        image2 = setup("/gtcafe/rpg/assets/objects/manacrystal_blank.png", context.getTileSize(), context.getTileSize());
    }

    public boolean use(Entity entity) {
        context.playSoundEffect(Sound.FX_POWER_UP);
        context.getGameUI().addMessage("Mana +" + value);
        entity.mana += value;
        
        return true;    // means delete it.
    }

}
