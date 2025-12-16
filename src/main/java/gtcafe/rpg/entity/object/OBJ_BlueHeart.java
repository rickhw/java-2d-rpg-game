package gtcafe.rpg.entity.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.state.Scene;

public class OBJ_BlueHeart extends Entity {
    public static final String OBJ_NAME = "Blue Heart";
    GamePanel gp;
    public OBJ_BlueHeart(GamePanel gp) {
        super(gp);
        this.gp = gp;
        name = OBJ_NAME; 
        type = EntityType.PICKUPONLY;
        value = 2;
        down1 = setup("/gtcafe/rpg/assets/objects/blueheart.png", gp.tileSize, gp.tileSize); 
        setDialogues();
    }

    public void setDialogues() {
        dialogues[0][0] = "You pick up a beautiful blue gem.";
        dialogues[0][1] = "You find the Blue Heart, the legendary treasure!";
    }

    public boolean use(Entity entity) {
        gp.gameState = GameState.CUTSENSE;
        gp.csManager.sceneNum = Scene.ENDING;

        return true;
    }
}
