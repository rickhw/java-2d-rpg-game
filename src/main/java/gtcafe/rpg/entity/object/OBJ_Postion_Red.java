package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.system.Sound;

public class OBJ_Postion_Red extends Entity {
    public OBJ_Postion_Red(GamePanel gp) {
        super(gp);
        type = EntityType.CONSUMABLE;
        name = "Red Potion";
        value = 5;
        down1 = setup("/gtcafe/rpg/assets/objects/potion_red.png", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nHeals your life by " + value + "."; 
        price = 100;
        stackable = true;

        setDialogue();
    }

    public void setDialogue() {
        dialogues[0][0] = "You drink the " + name + "!\n"
            + "Your life has ben recoeved by " + value + ".";

    }

    public boolean use(Entity entity) {
        startDialogue(this, 0);
        entity.life += value;

        gp.playSoundEffect(Sound.FX_POWER_UP);

        return true;    // means delete it.
    }
}