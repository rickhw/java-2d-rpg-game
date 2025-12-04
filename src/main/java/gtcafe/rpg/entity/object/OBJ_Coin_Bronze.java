package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Sound;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Coin_Bronze extends Entity {
    
    public OBJ_Coin_Bronze(GamePanel gp) {
        super(gp);
        name = "Bronze Coin";
        type = EntityType.PICKUPONLY;
        value = 1;
        down1 = setup("/gtcafe/rpg/assets/objects/coin_bronze.png", gp.tileSize, gp.tileSize);
    }

    public void use(Entity entity) {
        gp.playSoundEffect(Sound.FX_COIN);
        gp.ui.addMessage("Coin +" + value);
        entity.coin += value;
    }
}