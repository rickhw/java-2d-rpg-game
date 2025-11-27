package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.Projectiles;

public class OBJ_Rock extends Projectiles {
    GamePanel gp;
    public OBJ_Rock(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Rock";
        speed = 8;
        maxLife = 80;   // 80 frame 後會消失
        life = maxLife;
        attack = 2;
        useCost = 1;    // 花費 1 個魔力
        alive = false;

        getImage();

    }

    public void getImage() {
        String packagePath = "/gtcafe/rpg/assets/projectiles/";
        up1 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "rock_down_1.png", gp.tileSize, gp.tileSize);
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
}
