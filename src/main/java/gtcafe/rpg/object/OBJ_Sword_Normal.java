package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

// increase player's speed
public class OBJ_Sword_Normal extends Entity {
    public OBJ_Sword_Normal(GamePanel gp) {
        super(gp);
        name = "Normal Sword";
        down1 = setup("/gtcafe/rpg/assets/objects/sword_normal.png", gp.tileSize, gp.tileSize);
        attackValue = 3;    
        description = "[" + name + "]\nAn old sword.";
    }
}