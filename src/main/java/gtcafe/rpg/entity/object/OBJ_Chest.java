package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.system.Sound;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Chest extends Entity {
    GameContext context;
    // Entity loot;
    // boolean opened = false;

    public OBJ_Chest(GameContext context) {
        super(context);
        this.context = context;
        // this.loot = loot;

        type = EntityType.OBSTACLE;
        name = "Chest";

        image = setup("/gtcafe/rpg/assets/objects/chest.png", context.getTileSize(), context.getTileSize());
        image2 = setup("/gtcafe/rpg/assets/objects/chest_opened.png", context.getTileSize(), context.getTileSize());
        down1 = image;
        collision = true;

        solidArea.x = 4;
        solidArea.y = 16;
        solidArea.width = 40;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void setLoot(Entity loot) {
        this.loot = loot;
    }

    public void interact() {
        context.setGameState(GameState.DIALOGUE);

        if (opened == false) {
            context.playSoundEffect(Sound.FX_UNLOCK);

            StringBuilder sb = new StringBuilder();
            sb.append("You open the chest and find a " + loot.name + "!");

            if (context.getPlayer().canObtainItem(loot) == false) {
                sb.append("\n... But you cannot carray any more!");
            } else {
                sb.append("\nYou obtain the " + loot.name + "!");
                down1 = image2; // change chest as opened.
                opened = true;
            }

            context.getGameUI().currentDialogue = sb.toString();
        }
        // the chest is opened;
        else {
            context.getGameUI().currentDialogue = "It's empty.";
        }
    }
}
