package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Sound;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Tent extends Entity {
    public OBJ_Tent(GamePanel gp) {
        super(gp);
        name = "Tent";
        type = EntityType.CONSUMABLE;
        description = "[Tent]\nYou can sleep until\nnext morning.";
        down1 = setup("/gtcafe/rpg/assets/objects/tent.png", gp.tileSize, gp.tileSize);
        price = 300;
        stackable = true;
    }

    public boolean use(Entity entity) {
        gp.gameState = GameState.SLEEP;
        gp.playSoundEffect(Sound.FX__SLEEP);
        gp.player.life = gp.player.maxLife;
        gp.player.mana = gp.player.maxMana;
        gp.player.getSleepingImage(down1);

        return true; // diseaper after using
    }
}