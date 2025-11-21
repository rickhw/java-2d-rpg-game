package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

// increase player's speed
public class OBJ_Boots extends Entity {
    public OBJ_Boots(GamePanel gp) {
        super(gp);
        name = "Boots";
        down1 = setup("/gtcafe/rpg/assets/objects/boots.png");
    }
}