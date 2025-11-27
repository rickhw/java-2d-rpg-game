package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Sound;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_ManaCrystal extends Entity {
    GamePanel gp;

    public OBJ_ManaCrystal(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Mana Crystal";
        type = EntityType.PICKUPONLY;
        value = 1;
        // for put to map
        down1 = setup("/gtcafe/rpg/assets/objects/manacrystal_full.png", gp.tileSize, gp.tileSize);
        // for draw the life on top
        image = setup("/gtcafe/rpg/assets/objects/manacrystal_full.png", gp.tileSize, gp.tileSize);
        image2 = setup("/gtcafe/rpg/assets/objects/manacrystal_blank.png", gp.tileSize, gp.tileSize);
    }

    public void use(Entity entity) {
        gp.playSoundEffect(Sound.FX_POWER_UP);
        gp.ui.addMessage("Mana +" + value);
        entity.mana += value;
    }

}
