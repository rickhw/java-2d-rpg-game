package gtcafe.rpg.tile.interactive;

import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

public class InteractiveTile extends Entity {
    GamePanel gp;

    // // 可以摧毀的物件
    public boolean destructible = false;

    public InteractiveTile(GamePanel gp) {
        super(gp);
        this.gp = gp;
    }

    // overwrite by subclass
    public void update() { }

    // overwrite parent to ignore the invicible effect.
    public void draw(Graphics2D g2) { 
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX * gp.tileSize > gp.player.worldX - gp.player.screenX
            && worldX - gp.tileSize < gp.player.worldX + gp.player.screenX 
            && worldY + gp.tileSize > gp.player.worldY - gp.player.screenY 
            && worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            g2.drawImage(down1, screenX, screenY, null);
        }
    }

    // overwrite by subclass
    public boolean isCorrectItem(Entity entity) {
        boolean isCurrectItem = false;
        return isCurrectItem;
    }

    // overwrite by subclass
    public void playSoundEffect() {}

    // overwrite by subclass
    public InteractiveTile getDestroyedForm() {
        InteractiveTile i = null;
        return i;
    }
}
