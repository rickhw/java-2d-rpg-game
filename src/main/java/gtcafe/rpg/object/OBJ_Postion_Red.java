package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.GameState;
import gtcafe.rpg.Sound;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Postion_Red extends Entity {
    public OBJ_Postion_Red(GamePanel gp) {
        super(gp);
        type = EntityType.CONSUMABLE;
        name = "Red Potion";
        value = 5;
        down1 = setup("/gtcafe/rpg/assets/objects/potion_red.png", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nHeals your life by " + value + "."; 
    }

    public void use(Entity entity) {
        gp.gameState = GameState.DIALOGUE_STATE;
        gp.ui.currentDialogue = "You drink the " + name + "!\n"
            + "Your life has ben recoeved by " + value + ".";
        entity.life += value;

        if (gp.player.life > gp.player.maxLife) {
            gp.player.life = gp.player.maxLife;
        }

        gp.playSoundEffect(Sound.FX_POWER_UP);
    }
}