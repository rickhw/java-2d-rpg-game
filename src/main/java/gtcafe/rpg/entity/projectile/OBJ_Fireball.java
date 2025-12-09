package gtcafe.rpg.entity.projectile;
import gtcafe.rpg.core.GameContext;

import java.awt.Color;

import gtcafe.rpg.entity.Entity;

public class OBJ_Fireball extends Projectile {
    GameContext context;
    public OBJ_Fireball(GameContext context) {
        super(context);
        this.context = context;

        name = "Fireball";
        speed = 8;
        maxLife = 120;   // 80 frame 後會消失
        life = maxLife;
        attack = 3;
        useCost = 1;    // 花費 1 個魔力
        alive = false;
        knockBackPower = 3;

        getImage();

    }

    public void getImage() {
        String packagePath = "/gtcafe/rpg/assets/projectiles/";
        up1 = setup(packagePath + "fireball_up_1.png", context.getTileSize(), context.getTileSize());
        up2 = setup(packagePath + "fireball_up_2.png", context.getTileSize(), context.getTileSize());
        down1 = setup(packagePath + "fireball_down_1.png", context.getTileSize(), context.getTileSize());
        down2 = setup(packagePath + "fireball_down_2.png", context.getTileSize(), context.getTileSize());
        left1 = setup(packagePath + "fireball_left_1.png", context.getTileSize(), context.getTileSize());
        left2 = setup(packagePath + "fireball_left_2.png", context.getTileSize(), context.getTileSize());
        right1 = setup(packagePath + "fireball_right_1.png", context.getTileSize(), context.getTileSize());
        right2 = setup(packagePath + "fireball_right_2.png", context.getTileSize(), context.getTileSize());
    }

    // 判斷是否有足夠的魔力值可以使用
    public boolean haveResource(Entity user) {
        boolean haveResource = false;
        if (user.mana >= useCost) {
            haveResource = true;
        }

        return haveResource;
    }

    // 減去使用者的魔力值
    public void subtractResource(Entity user) {
        user.mana -= useCost;
    }

    // Particle 粒子效果的參數
    public Color getParticleColor() {
        Color color = new Color(240,50,0);
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
