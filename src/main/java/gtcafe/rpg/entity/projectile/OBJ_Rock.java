package gtcafe.rpg.entity.projectile;
import gtcafe.rpg.core.GameContext;

import java.awt.Color;

import gtcafe.rpg.entity.Entity;

public class OBJ_Rock extends Projectile {
    GameContext context;
    public OBJ_Rock(GameContext context) {
        super(context);
        this.context = context;

        name = "Rock";
        speed = 3;
        maxLife = 80;   // N frame 後會消失
        life = maxLife;
        attack = 2;
        useCost = 1;    // 花費 1 個魔力
        alive = false;

        getImage();

    }

    public void getImage() {
        String packagePath = "/gtcafe/rpg/assets/projectiles/";
        up1 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
        up2 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
        down1 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
        down2 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
        left1 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
        left2 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
        right1 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
        right2 = setup(packagePath + "rock_down_1.png", context.getTileSize(), context.getTileSize());
    }

    // 判斷是否有足夠的彈藥數可以使用
    public boolean haveResource(Entity user) {
        boolean haveResource = false;
        if (user.ammo >= useCost) {
            haveResource = true;
        }

        return haveResource;
    }

    // 減去使用者的彈藥數
    public void subtractResource(Entity user) {
        user.ammo -= useCost;
    }


    // Particle 粒子效果的參數
    public Color getParticleColor() {
        Color color = new Color(40,50,0);
        return color;
    }

    public int getParticleSize() {
        int size = 10; // 10 pixels
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
}
