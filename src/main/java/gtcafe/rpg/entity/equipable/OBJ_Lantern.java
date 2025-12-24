package gtcafe.rpg.entity.equipable;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Lantern extends Entity {
    public static final String OBJ_NAME = "Lantern";
    GamePanel gp;

    public OBJ_Lantern(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = EntityType.LIGHT;
        name = OBJ_NAME;
        down1 = setup("/gtcafe/rpg/assets/objects/lantern.png", gp.tileSize, gp.tileSize);
        description = "[Lantern]\nIlluminates your\nsurroundings.";
        price = 200;
        lightRadius = 250;
    }
}
