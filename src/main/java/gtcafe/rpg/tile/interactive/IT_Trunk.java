package gtcafe.rpg.tile.interactive;
import gtcafe.rpg.core.GameContext;


public class IT_Trunk extends InteractiveTile {
    GameContext context;

    public IT_Trunk(GameContext context, int col, int row) {
        super(context);
        this.context = context;

        this.worldX = context.getTileSize() * col;
        this.worldY = context.getTileSize() * row;

        down1 = setup("/gtcafe/rpg/assets/tiles_interactive/trunk.png", context.getTileSize(), context.getTileSize());
        // destructible = true;    

        // disable solidArea
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 0;
        solidArea.height = 0;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

}
