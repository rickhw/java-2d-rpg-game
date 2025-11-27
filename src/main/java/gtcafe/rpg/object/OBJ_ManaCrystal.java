package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

public class OBJ_ManaCrystal extends Entity {
    GamePanel gp;

    public OBJ_ManaCrystal(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Mana Crystal";
        image = setup("/gtcafe/rpg/assets/objects/manacrystal_full.png", gp.tileSize, gp.tileSize);
        image2 = setup("/gtcafe/rpg/assets/objects/manacrystal_blank.png", gp.tileSize, gp.tileSize);
    }
}
