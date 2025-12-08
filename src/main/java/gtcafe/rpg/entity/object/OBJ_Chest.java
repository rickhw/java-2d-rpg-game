package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Sound;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Chest extends Entity {
    GamePanel gp;
    Entity loot;
    boolean opened = false;

    public OBJ_Chest(GamePanel gp, Entity loot) {
        super(gp);
        this.gp = gp;
        this.loot = loot;

        type = EntityType.OBSTACLE;
        name = "Chest";

        image = setup("/gtcafe/rpg/assets/objects/chest.png", gp.tileSize, gp.tileSize);
        image2 = setup("/gtcafe/rpg/assets/objects/chest_opened.png", gp.tileSize, gp.tileSize);
        down1 = image;
        collision = true;

        solidArea.x = 4;
        solidArea.y = 16;
        solidArea.width = 40;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void interact() {
        gp.gameState = GameState.DIALOGUE;

        if (opened == false) {
            gp.playSoundEffect(Sound.FX_UNLOCK);

            StringBuilder sb = new StringBuilder();
            sb.append("You open the chest and find a " + loot.name + "!");

            if (gp.player.canObtainItem(loot) == false) {
                sb.append("\n... But you cannot carray any more!");
            } else {
                sb.append("\nYou obtain the " + loot.name + "!");
                down1 = image2; // change chest as opened.
                opened = true;
            }

            gp.ui.currentDialogue = sb.toString();
        }
        // the chest is opened;
        else {
            gp.ui.currentDialogue = "It's empty.";
        }
    }
}
