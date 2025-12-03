package gtcafe.rpg.monster;

import java.util.Random;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.object.OBJ_Coin_Bronze;
import gtcafe.rpg.object.OBJ_Heart;
import gtcafe.rpg.object.OBJ_ManaCrystal;
import gtcafe.rpg.object.OBJ_Rock;

public class MON_GreenSlime extends Entity {
    GamePanel gp;
    
    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        this.gp = gp;
        
        type = EntityType.MONSTER;
        name = "GreenSlime";
        speed = 1;
        maxLife = 4;
        life = maxLife;
        attack = 2;
        defense = 0;
        exp = 2; // how much can get the exp
        projectiles = new OBJ_Rock(gp);

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

    public void update() {
        super.update();

        int xDistance = Math.abs(worldX - gp.player.worldX);
        int yDistance = Math.abs(worldY - gp.player.worldY);
        int tileDistance =  (xDistance + yDistance) / gp.tileSize;

        // System.out.printf("tileDistance: [%s]\n", tileDistance);

        // Player 距離 N 格以內, Monster 就主動跟蹤
        if (onPath == false && tileDistance < 3) {  // TODO, as random by role characterics
            int i = new Random().nextInt(100) + 1; // 增加隨機性
            if (i > 70) {
                onPath = true;
                gp.ui.addMessage("You've been targeted by a slime!");
            }
        }

        // 超過 N 格就不要跟蹤了
        if (onPath == true && tileDistance > 5) {
            onPath = false;
            gp.ui.addMessage("The slime has given up attacking you!");
            // System.out.printf("Slime is skip to follow player!!\n");
        }
    }

    // Setting Slime's behavior
    // call 60 times per second
    public void setAction() {
        if (onPath == true) {

            // 2. follow the player
            int goalCol = (gp.player.worldX + gp.player.solidArea.x) / gp.tileSize;
            int goalRow = (gp.player.worldY + gp.player.solidArea.y) / gp.tileSize;

            searchPath(goalCol, goalRow);

            // shooting the player when aggro (侵略)
            int i = new Random().nextInt(200) + 1;
            if (i > 197 && projectiles.alive == false) {
                projectiles.set(worldX, worldY, direction, true, this);
                gp.projectilesList.add(projectiles);
                shotAvailableCounter = 0;
            }
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

        // // logic to shoot the projectiles
        // int i = new Random().nextInt(100) + 1;
        // if (i > 99 && projectiles.alive == false) {
        //     projectiles.set(worldX, worldY, direction, true, this);
        //     gp.projectilesList.add(projectiles);

        //     shotAvailableCounter = 0;
        // }
    }

    // monster receive damage/attack
    public void damageReaction() {
        actionLockCounter = 0;
        // direction = gp.player.direction;

        onPath = true;
    }

    // called when monster die (GamePanel.update())
    public void checkDrop() {
        // CAST A DIE
        int i = new Random().nextInt(100) + 1;

        // SET THE MONSTER DROP
        if (i < 50) { dropItem(new OBJ_Coin_Bronze(gp)); }
        if (i >= 50 && i < 75) { dropItem(new OBJ_Heart(gp)); }
        if (i >= 75 && i < 100) { dropItem(new OBJ_ManaCrystal(gp)); }
    }
}
