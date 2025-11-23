package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

public class OBJ_Chest extends Entity {
    public OBJ_Chest(GamePanel gp) {
        super(gp);
        name = "Chest";
        down1 = setup("/gtcafe/rpg/assets/objects/chest.png", gp.tileSize, gp.tileSize);
    }
}
