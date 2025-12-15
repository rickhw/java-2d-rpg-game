package gtcafe.rpg.entity.npc;

import java.awt.Rectangle;
import java.util.Random;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.state.Direction;

public class NPC_OldMan extends Entity {
    
    public NPC_OldMan(GamePanel gp, String name) {
        super(gp);

        this.direction = Direction.DOWN;
        this.speed = 1;
        this.name = name;

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = (gp.tileSize * 4) / 5;    // 30;
        solidArea.height = (gp.tileSize * 3) / 5;   // 30;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

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
        
        if (onPath == true) {

            // 1. fix position
            // int goalCol = 12;
            // int goalRow = 9;
            // 2. follow the player
            int goalCol = (gp.player.worldX + gp.player.solidArea.x) / gp.tileSize;
            int goalRow = (gp.player.worldY + gp.player.solidArea.y) / gp.tileSize;

            searchPath(goalCol, goalRow);
        } else {

            actionLockCounter++;
            if (actionLockCounter == 120) { // 120 fps change the direction
                Random r = new Random();
                int i = r.nextInt(100) + 1; // pick up a number from 1 to 100

                if (i <= 25) { direction = Direction.UP; }
                if (i > 25 && i <= 50) { direction = Direction.DOWN; }
                if (i > 50 && i <= 75) { direction = Direction.LEFT; }
                if (i > 75 && i <= 100) { direction = Direction.RIGHT; }
                actionLockCounter = 0;
            }
        }
    }

    public void setDialogue() {
        // https://en.wikipedia.org/wiki/Arrow_in_the_knee
        dialogues[0][0] = "Hello, Rick! I used to be an adventurer like \nyou. Then I took an arrow in the knee...";
        dialogues[0][1] = "So you've come to this island to fin the \ntreasure?";
        dialogues[0][2] = "I used to be a great wizard but now ... I'm \na bit too old for taking an adventure.";
        dialogues[0][3] = "Well, good luck on you.";
        dialogues[0][4] = "Hey, you, you're finally awake."; // https://www.youtube.com/watch?v=_WZCvQ5J3pk
    
        dialogues[1][0] = "If you become tired, rest at the water.";
        dialogues[1][1] = "However, the monsters reappear if you rest.\nI don't why but hta's how it works.";
        dialogues[1][2] = "In any case, don't push yourself to hard.";

        dialogues[2][0] = "I wonder how to open that door ...";
    }

    public void speak() {

        // DO this character specific stuff

        facePlayer();
        startDialogue(this, dialogueSet);        

        // for track the player by pathfinding
        // onPath = true;
    }
}
