package gtcafe.rpg.entity;

import java.util.Random;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;

public class NPC_OldMan extends Entity {
    
    public NPC_OldMan(GamePanel gp) {
        super(gp);

        direction = Direction.DOWN;
        speed = 1;

        getImages();
        setDialogue();
    }

    public void getImages() {
        String packagePath = "/gtcafe/rpg/assets/npc/";
        up1 = setup(packagePath + "oldman_up_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "oldman_up_2.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "oldman_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "oldman_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "oldman_left_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "oldman_left_2.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "oldman_right_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "oldman_right_2.png", gp.tileSize, gp.tileSize);
    }

    // set the action behavior for different actors
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

            // System.out.println("[NPC_OldMan#setAction] direction: " + direction);   
        }
    }

    public void setDialogue() {
        // https://en.wikipedia.org/wiki/Arrow_in_the_knee
        dialogues[0] = "Hello, Rick! I used to be an adventurer like \nyou. Then I took an arrow in the knee...";
        dialogues[1] = "So you've come to this island to fin the \ntreasure?";
        dialogues[2] = "I used to be a great wizard but now ... I'm \na bit too old for taking an adventure.";
        dialogues[3] = "Well, good luck on you.";
        dialogues[4] = "Hey, you, you're finally awake."; // https://www.youtube.com/watch?v=_WZCvQ5J3pk
    }

    public void speak() {

        // DO this character specific stuff

        super.speak();
    }
}
