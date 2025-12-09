package gtcafe.rpg.entity.shield;
import gtcafe.rpg.core.GameContext;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Shield_Blue extends Entity {
    public OBJ_Shield_Blue(GameContext context) {
        super(context);
        type = EntityType.SHIELD;
        name = "Blue Shield";
        down1 = setup("/gtcafe/rpg/assets/objects/shield_blue.png", context.getTileSize(), context.getTileSize());
        defenseValue = 2;
        description = "[" + name + "]\nA shiny blue shield.\nDefense: " + defenseValue;
        price = 300;
    }
}
