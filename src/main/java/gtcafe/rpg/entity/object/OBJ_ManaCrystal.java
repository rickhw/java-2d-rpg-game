package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.system.Sound;

public class OBJ_ManaCrystal extends Entity {
    public static final String OBJ_NAME = "Mana Crystal";
    GamePanel gp;

    public OBJ_ManaCrystal(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = OBJ_NAME;
        type = EntityType.PICKUPONLY;
        value = 1;
        // for put to map
        down1 = setup("/gtcafe/rpg/assets/objects/manacrystal_full.png", gp.tileSize, gp.tileSize);
        // for draw the life on top
        image = setup("/gtcafe/rpg/assets/objects/manacrystal_full.png", gp.tileSize, gp.tileSize);
        image2 = setup("/gtcafe/rpg/assets/objects/manacrystal_blank.png", gp.tileSize, gp.tileSize);
    }

    public boolean use(Entity entity) {
        gp.playSoundEffect(Sound.FX_POWER_UP);
        gp.ui.addMessage("Mana +" + value);
        entity.setMana(entity.getMana() + value);

        return true; // means delete it.
    }

}
