package gtcafe.rpg.tile.interactive;

import gtcafe.rpg.GamePanel;

public class IT_MetalPlate extends InteractiveTile {
    GamePanel gp;

    public static final String OBJ_NAME = "Metal Plate";

    public IT_MetalPlate(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;
        this.name = OBJ_NAME;

        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        down1 = setup("/gtcafe/rpg/assets/tiles_interactive/metalplate.png", gp.tileSize, gp.tileSize);
        // destructible = true;    

        // no collision
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 0;
        solidArea.height = 0;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

}
