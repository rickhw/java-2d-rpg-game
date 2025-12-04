package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Shield_Blue extends Entity {
    public OBJ_Shield_Blue(GamePanel gp) {
        super(gp);
        type = EntityType.SHIELD;
        name = "Blue Shield";
        down1 = setup("/gtcafe/rpg/assets/objects/shield_blue.png", gp.tileSize, gp.tileSize);
        defenseValue = 2;
        description = "[" + name + "]\nA shiny blue shield.\nDefense: " + defenseValue;
        price = 300;
    }
}