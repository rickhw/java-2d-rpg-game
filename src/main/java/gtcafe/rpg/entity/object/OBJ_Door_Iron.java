package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Door_Iron extends Entity {
    public static final String OBJ_NAME = "Iron Door";
    GamePanel gp;
    public OBJ_Door_Iron(GamePanel gp) {
        super(gp);
        this.gp = gp;
        name = OBJ_NAME;
        this.type = EntityType.OBSTACLE;
        down1 = setup("/gtcafe/rpg/assets/objects/door_iron.png", gp.tileSize, gp.tileSize);
        collision = true;

        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = gp.tileSize;
        solidArea.height = gp.tileSize;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDialogue();
    }

    public void interact() {
        startDialogue(this, 0);
    }

    public void setDialogue() {
        dialogues[0][0] = "It won't budge. \nPerhaps there's a key?";
    }
}
