package gtcafe.rpg.entity.equipable;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

// increase player's speed
public class OBJ_Boots extends Entity {
    public static final String OBJ_NAME = "Boots";
    public OBJ_Boots(GamePanel gp) {
        super(gp);
        type = EntityType.SHOE;
        name = OBJ_NAME;
        down1 = setup("/gtcafe/rpg/assets/objects/boots.png", gp.tileSize, gp.tileSize);
        price = 150;
    }

    // public boolean use(Entity entity) {
    //     gp.playSoundEffect(Sound.FX_POWER_UP);
    //     // gp.ui.addMessage("ife +" + value);
    //     entity.speed += 5;

    //     return false;    // means delete it.
    // }

}