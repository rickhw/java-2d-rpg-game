package gtcafe.rpg.entity.shield;
import gtcafe.rpg.core.GameContext;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

// increase player's speed
public class OBJ_Shield_Wood extends Entity {
    public OBJ_Shield_Wood(GameContext context) {
        super(context);
        type = EntityType.SHIELD;
        name = "Wood Shield";
        down1 = setup("/gtcafe/rpg/assets/objects/shield_wood.png", context.getTileSize(), context.getTileSize());
        defenseValue = 1;
        description = "[" + name + "]\nMade by wood.\nDefense: " + defenseValue;
        price = 50;
    }
}
