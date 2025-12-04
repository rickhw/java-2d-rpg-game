package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

public class OBJ_Key extends Entity {
    public OBJ_Key(GamePanel gp) {
        super(gp);
        name = "Key";
        down1 = setup("/gtcafe/rpg/assets/objects/key.png", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nIt opens a door.";
        price = 350;
    }
}