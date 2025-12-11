package gtcafe.rpg.entity.shield;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Shield_Wood extends Entity {
    public static final String OBJ_NAME = "Wood Shield";
    public OBJ_Shield_Wood(GamePanel gp) {
        super(gp);
        type = EntityType.SHIELD;
        name = OBJ_NAME; 
        down1 = setup("/gtcafe/rpg/assets/objects/shield_wood.png", gp.tileSize, gp.tileSize);
        defenseValue = 1;
        description = "[" + name + "]\nMade by wood.\nDefense: " + defenseValue;
        price = 50;
    }
}