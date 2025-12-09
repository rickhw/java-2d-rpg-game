package gtcafe.rpg.tile.interactive;
import gtcafe.rpg.core.GameContext;

import java.awt.Graphics2D;

import gtcafe.rpg.entity.Entity;

public class InteractiveTile extends Entity {
    GameContext context;

    // // 可以摧毀的物件
    public boolean destructible = false;

    public InteractiveTile(GameContext context) {
        super(context);
        this.context = context;
    }

    // overwrite by subclass
    public void update() { }

    // overwrite parent to ignore the invicible effect.
    public void draw(Graphics2D g2) { 
        int screenX = worldX - context.getPlayer().worldX + context.getPlayer().screenX;
        int screenY = worldY - context.getPlayer().worldY + context.getPlayer().screenY;

        if (worldX * context.getTileSize() > context.getPlayer().worldX - context.getPlayer().screenX
            && worldX - context.getTileSize() < context.getPlayer().worldX + context.getPlayer().screenX 
            && worldY + context.getTileSize() > context.getPlayer().worldY - context.getPlayer().screenY 
            && worldY - context.getTileSize() < context.getPlayer().worldY + context.getPlayer().screenY) {

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
