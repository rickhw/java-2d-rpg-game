package gtcafe.rpg.entity.equipable;
import gtcafe.rpg.core.GameContext;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Lantern extends Entity {
    GameContext context;

    public OBJ_Lantern(GameContext context) {
        super(context);
        this.context = context;

        type = EntityType.LIGHT;
        name = "Lantern";
        down1 = setup("/gtcafe/rpg/assets/objects/lantern.png", context.getTileSize(), context.getTileSize());
        description = "[Lantern]\nIlluminates your\nsurroundings.";
        price = 200;
        lightRadius = 250;
    }

    
}
