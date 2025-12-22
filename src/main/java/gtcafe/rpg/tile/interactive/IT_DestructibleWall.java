package gtcafe.rpg.tile.interactive;

import java.awt.Color;
import java.util.Random;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.system.Sound;

public class IT_DestructibleWall extends InteractiveTile {
    GamePanel gp;

    public IT_DestructibleWall(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;

        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        life = 3;

        down1 = setup("/gtcafe/rpg/assets/tiles_interactive/destructiblewall.png", gp.tileSize, gp.tileSize);
        destructible = true;    
    }

    // 檢查是否可以砍樹 XD
    public boolean isCorrectItem(Entity entity) {
        boolean isCurrectItem = false;
        if (entity.currentWeapon.type == EntityType.PICKAXE) {
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
        gp.playSoundEffect(Sound.FX__CHIP_WALL);
    }

    public InteractiveTile getDestroyedForm() {
        return null;
    }

    // Particle 粒子效果的參數
    public Color getParticleColor() {
        Color color = new Color(65,65,65);
        return color;
    }

    public int getParticleSize() {
        int size = 6; // 6 pixels
        return size;
    }

    // how fast it can fly
    public int getParticleSpeed() {
        int speed = 1;
        return speed;
    }

    // 出現多久？ 20 frame 數量 (0.33 second)
    public int getParitcleMaxLife() {
        int maxLife = 20;
        return maxLife;
    }

     // called when destroyed by player
    public void checkDrop() {
        // CAST A DIE
        int i = new Random().nextInt(100) + 1;

        // SET THE MONSTER DROP
        if (i < 50) { dropItem(new OBJ_Coin_Bronze(gp)); }
        // if (i >= 50 && i < 75) { dropItem(new OBJ_Heart(gp)); }
        // if (i >= 75 && i < 100) { dropItem(new OBJ_ManaCrystal(gp)); }
    }
}
