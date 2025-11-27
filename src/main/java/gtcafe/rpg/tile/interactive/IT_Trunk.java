package gtcafe.rpg.tile.interactive;

import gtcafe.rpg.GamePanel;

public class IT_Trunk extends InteractiveTile {
    GamePanel gp;

    public IT_Trunk(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;

        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        down1 = setup("/gtcafe/rpg/assets/tiles_interactive/trunk.png", gp.tileSize, gp.tileSize);
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
