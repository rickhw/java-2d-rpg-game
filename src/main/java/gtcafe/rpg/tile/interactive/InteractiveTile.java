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
    public void update() {
    }

    // overwrite parent to ignore the invicible effect.
    public void draw(Graphics2D g2) {
        int screenX = getWorldX() - gp.player.getWorldX() + gp.player.screenX;
        int screenY = getWorldY() - gp.player.getWorldY() + gp.player.screenY;

        if (getWorldX() * gp.tileSize > gp.player.getWorldX() - gp.player.screenX
                && getWorldX() - gp.tileSize < gp.player.getWorldX() + gp.player.screenX
                && getWorldY() + gp.tileSize > gp.player.getWorldY() - gp.player.screenY
                && getWorldY() - gp.tileSize < gp.player.getWorldY() + gp.player.screenY) {

            g2.drawImage(down1, screenX, screenY, null);
            drawInteractiveArea(g2, screenX, screenY);
        }
    }

    // overwrite by subclass
    public boolean isCorrectItem(Entity entity) {
        boolean isCurrectItem = false;
        return isCurrectItem;
    }

    // overwrite by subclass
    public void playSoundEffect() {
    }

    // overwrite by subclass
    public InteractiveTile getDestroyedForm() {
        InteractiveTile i = null;
        return i;
    }
}
