package gtcafe.rpg.entity.object;
import gtcafe.rpg.core.GameContext;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;

public class OBJ_Door extends Entity {
    GameContext context;
    public OBJ_Door(GameContext context) {
        super(context);
        this.context = context;
        name = "Door";
        this.type = EntityType.OBSTACLE;
        down1 = setup("/gtcafe/rpg/assets/objects/door.png", context.getTileSize(), context.getTileSize());
        collision = true;

        solidArea.x = 0;
        solidArea.y = 16;
        solidArea.width = 48;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void interact() {
        context.setGameState(GameState.DIALOGUE);
        context.getGameUI().currentDialogue = "You need a key to open this.";

    }
}
