package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Door extends Entity {
    GamePanel gp;
    public OBJ_Door(GamePanel gp) {
        super(gp);
        this.gp = gp;
        name = "Door";
        this.type = EntityType.OBSTACLE;
        down1 = setup("/gtcafe/rpg/assets/objects/door.png", gp.tileSize, gp.tileSize);
        collision = true;

        solidArea.x = 0;
        solidArea.y = 16;
        solidArea.width = 48;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void interact() {
        gp.gameState = GameState.DIALOGUE;
        gp.ui.currentDialogue = "You need a key to open this.";

    }
}
