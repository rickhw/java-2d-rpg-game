package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.system.Sound;

public class OBJ_Heart extends Entity {
    public static final String OBJ_NAME = "Heart";
    GamePanel gp;
    public OBJ_Heart(GamePanel gp) {
        super(gp);
        this.gp = gp;
        name = OBJ_NAME; 
        type = EntityType.PICKUPONLY;
        value = 2;
        // for put object to map
        down1 = setup("/gtcafe/rpg/assets/objects/heart_full.png", gp.tileSize, gp.tileSize); 
        // for draw the life on top
        image = setup("/gtcafe/rpg/assets/objects/heart_full.png", gp.tileSize, gp.tileSize);
        image2 = setup("/gtcafe/rpg/assets/objects/heart_half.png", gp.tileSize, gp.tileSize);
        image3 = setup("/gtcafe/rpg/assets/objects/heart_blank.png", gp.tileSize, gp.tileSize);
    }

    public boolean use(Entity entity) {
        gp.playSoundEffect(Sound.FX_POWER_UP);
        gp.ui.addMessage("Life +" + value);
        entity.life += value;

        return true;    // means delete it.
    }
}
