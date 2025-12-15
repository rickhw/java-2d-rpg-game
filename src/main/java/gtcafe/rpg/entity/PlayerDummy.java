package gtcafe.rpg.entity;

import gtcafe.rpg.GamePanel;

public class PlayerDummy extends Entity {
    public static final String OBJ_NAME = "PlayerDummy";
    public PlayerDummy(GamePanel gp) {
        super(gp);
        name = OBJ_NAME;
        getImages();
    }

    public void getImages() {
        String packagePath = "/gtcafe/rpg/assets/player/walking/";
        up1 = setup(packagePath + "boy_up_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "boy_up_2.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "boy_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "boy_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "boy_left_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "boy_left_2.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "boy_right_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "boy_right_2.png", gp.tileSize, gp.tileSize);
    }
}
