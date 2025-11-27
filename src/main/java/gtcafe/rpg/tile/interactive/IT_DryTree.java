package gtcafe.rpg.tile.interactive;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Sound;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class IT_DryTree extends InteractiveTile {
    GamePanel gp;

    public IT_DryTree(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;

        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        life = 2;

        down1 = setup("/gtcafe/rpg/assets/tiles_interactive/drytree.png", gp.tileSize, gp.tileSize);
        destructible = true;    
    }

    // 檢查是否可以砍樹 XD
    public boolean isCorrectItem(Entity entity) {
        boolean isCurrectItem = false;
        if (entity.currentWeapon.type == EntityType.AXE) {
            isCurrectItem = true;
        }
        return isCurrectItem;
    }
    public void update() {
        // Keep the invincible state
        if (invincible == true) {
            invincibleCounter++;
            if(invincibleCounter > 20) { // Default Frame Counter
                invincible = false;
                invincibleCounter = 0;
            }
        }

    }


    // overwrite by subclass
    public void playSoundEffect() {
        gp.playSoundEffect(Sound.FX__CUT_TREE);
    }

    // overwrite by subclass
    public InteractiveTile getDestroyedForm() {
        InteractiveTile i = new IT_Trunk(gp, worldX / gp.tileSize, worldY / gp.tileSize);
        return i;
    }
}
