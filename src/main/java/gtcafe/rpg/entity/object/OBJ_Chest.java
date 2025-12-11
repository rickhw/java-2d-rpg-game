package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.system.Sound;

public class OBJ_Chest extends Entity {
    public static final String OBJ_NAME = "Chest";
    GamePanel gp;

    public OBJ_Chest(GamePanel gp) {
        super(gp);
        this.gp = gp;
        // this.loot = loot;

        type = EntityType.OBSTACLE;
        name = OBJ_NAME;

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

    public void setLoot(Entity loot) {
        this.loot = loot;
        // need this loot in message.
        setDialogue();
    }

    public void setDialogue() {
        dialogues[0][0] = "You open the chest and find a " + loot.name + "!\n... But you cannot carray any more!";
        dialogues[1][0] = "You open the chest and find a " + loot.name + "!\nYou obtain the " + loot.name + "!";

        dialogues[2][0] = "It's empty.";
    }

    public void interact() {
        gp.gameState = GameState.DIALOGUE;

        if (opened == false) {
            gp.playSoundEffect(Sound.FX_UNLOCK);

            if (gp.player.canObtainItem(loot) == false) {
                startDialogue(this, 0);
            } else {
                startDialogue(this, 1);
                down1 = image2; // change chest as opened.
                opened = true;
            }

        }
        // the chest is opened;
        else {
            startDialogue(this, 2);
        }
    }
}
