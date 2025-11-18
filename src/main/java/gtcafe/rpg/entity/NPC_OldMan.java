package gtcafe.rpg.entity;

import java.util.Random;

import gtcafe.rpg.GamePanel;

public class NPC_OldMan extends Entity {
    
    public NPC_OldMan(GamePanel gp) {
        super(gp);

        direction = "down";
        speed = 1;

        getImages();
    }

    public void getImages() {
        String packagePath = "/gtcafe/rpg/assets/npc/";
        up1 = setup(packagePath + "oldman_up_1");
        up2 = setup(packagePath + "oldman_up_2");
        down1 = setup(packagePath + "oldman_down_1");
        down2 = setup(packagePath + "oldman_down_2");
        left1 = setup(packagePath + "oldman_left_1");
        left2 = setup(packagePath + "oldman_left_2");
        right1 = setup(packagePath + "oldman_right_1");
        right2 = setup(packagePath + "oldman_right_2");
    }

    // set the action behavior for different actors
    public void setAction() {

        actionLockCounter++;

        if (actionLockCounter == 120) { // 120 fps change the direction
            Random r = new Random();
            int i = r.nextInt(100) + 1; // pick up a number from 1 to 100

            if (i <= 25) {
                direction = "up";
            }
            if (i > 25 && i <= 50) {
                direction = "down";
            }
            if (i > 50 && i <= 75) {
                direction = "left";
            }
            if (i > 75 && i <= 100) {
                direction = "right";
            }
            actionLockCounter = 0;

            System.out.println("[NPC_OldMan#setAction] direction: " + direction);   
        }
    }

}
