package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

public class OBJ_Heart extends Entity {
    public OBJ_Heart(GamePanel gp) {
        super(gp);
        this.name = "Heart";
        image = setup("/gtcafe/rpg/assets/objects/heart_full.png");
        image2 = setup("/gtcafe/rpg/assets/objects/heart_half.png");
        image3 = setup("/gtcafe/rpg/assets/objects/heart_blank.png");
    }
}
