package gtcafe.rpg.tile.interactive;

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
