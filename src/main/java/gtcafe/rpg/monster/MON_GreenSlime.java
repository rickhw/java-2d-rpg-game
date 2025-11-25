package gtcafe.rpg.monster;

import java.util.Random;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

public class MON_GreenSlime extends Entity {
    GamePanel gp;
    
    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        this.gp = gp;
        
        type = 2;
        name = "GreenSlime";
        speed = 1;
        maxLife = 4;
        life = maxLife;
        attack = 2;
        defense = 0;
        exp = 2; // how much can get the exp

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", gp.tileSize, gp.tileSize);
        up2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", gp.tileSize, gp.tileSize);
        down1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", gp.tileSize, gp.tileSize);
        left2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", gp.tileSize, gp.tileSize);
        right1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", gp.tileSize, gp.tileSize);
        right2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", gp.tileSize, gp.tileSize);
    }

    // Setting Slime's behavior
    public void setAction() {
        actionLockCounter++;

        if (actionLockCounter == 120) { // 120 fps change the direction
            Random r = new Random();
            int i = r.nextInt(100) + 1; // pick up a number from 1 to 100

            if (i <= 25) {
                direction = Direction.UP;
            }
            if (i > 25 && i <= 50) {
                direction = Direction.DOWN;
            }
            if (i > 50 && i <= 75) {
                direction = Direction.LEFT;
            }
            if (i > 75 && i <= 100) {
                direction = Direction.RIGHT;
            }
            actionLockCounter = 0;

            System.out.println("[MON_GreenSlime#setAction] direction: " + direction);   
        }
    }

    public void damageReaction() {
        actionLockCounter = 0;
        direction = gp.player.direction;
    }
}
