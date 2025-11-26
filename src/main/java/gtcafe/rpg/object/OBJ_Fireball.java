package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Projectiles;

public class OBJ_Fireball extends Projectiles {
    GamePanel gp;
    public OBJ_Fireball(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Fireball";
        speed = 6;
        maxLife = 80;   // 80 frame 後會消失
        life = maxLife;
        attack = 3;
        useCost = 1;    // 花費 1 個魔力
        alive = false;

        getImage();

    }

    public void getImage() {
        String packagePath = "/gtcafe/rpg/assets/projectiles/";
        up1 = setup(packagePath + "fireball_up_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "fireball_up_2.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "fireball_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "fireball_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "fireball_left_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "fireball_left_2.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "fireball_right_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "fireball_right_2.png", gp.tileSize, gp.tileSize);
    }
}
