package gtcafe.rpg.entity.monster;

import java.util.Random;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;

public class MON_SkeletonLord extends Entity {
    GamePanel gp;
    public static final String OBJ_NAME = "Skeleton Lord";
    
    public MON_SkeletonLord(GamePanel gp) {
        super(gp);
        this.gp = gp;
        
        type = EntityType.MONSTER;
        boss = true;
        name = OBJ_NAME;
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxLife = 100;
        life = maxLife;
        attack = 10;
        defense = 2;
        exp = 100; // how much can get the exp
        knockBackPower = 5;

        int size = gp.tileSize * 5;
        solidArea.x = 48;
        solidArea.y = 48;
        solidArea.width = size - 48 * 2;
        solidArea.height = size - 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        attackArea.width = 170;
        attackArea.height = 170;

        motion1_duration = 25;
        motion2_duration = 50;

        getImage();
        getAttackImage();
    }

    public void getImage() {
        // size
        int i = 5;
        if (inRage == false) {
            String path = "/gtcafe/rpg/assets/monster/skeletonlord/walking/";
            up1 = setup(path + "skeletonlord_up_1.png", gp.tileSize * i, gp.tileSize * i);
            up2 = setup(path + "skeletonlord_up_2.png", gp.tileSize * i, gp.tileSize * i);
            down1 = setup(path + "skeletonlord_down_1.png", gp.tileSize * i, gp.tileSize * i);
            down2 = setup(path + "skeletonlord_down_2.png", gp.tileSize * i, gp.tileSize * i);
            left1 = setup(path + "skeletonlord_left_1.png", gp.tileSize * i, gp.tileSize * i);
            left2 = setup(path + "skeletonlord_left_2.png", gp.tileSize * i, gp.tileSize * i);
            right1 = setup(path + "skeletonlord_right_1.png", gp.tileSize * i, gp.tileSize * i);
            right2 = setup(path + "skeletonlord_right_2.png", gp.tileSize * i, gp.tileSize * i);
        }
        else {
            String path = "/gtcafe/rpg/assets/monster/skeletonlord/walking_phase2/";
            up1 = setup(path + "skeletonlord_phase2_up_1.png", gp.tileSize * i, gp.tileSize * i);
            up2 = setup(path + "skeletonlord_phase2_up_2.png", gp.tileSize * i, gp.tileSize * i);
            down1 = setup(path + "skeletonlord_phase2_down_1.png", gp.tileSize * i, gp.tileSize * i);
            down2 = setup(path + "skeletonlord_phase2_down_2.png", gp.tileSize * i, gp.tileSize * i);
            left1 = setup(path + "skeletonlord_phase2_left_1.png", gp.tileSize * i, gp.tileSize * i);
            left2 = setup(path + "skeletonlord_phase2_left_2.png", gp.tileSize * i, gp.tileSize * i);
            right1 = setup(path + "skeletonlord_phase2_right_1.png", gp.tileSize * i, gp.tileSize * i);
            right2 = setup(path + "skeletonlord_phase2_right_2.png", gp.tileSize * i, gp.tileSize * i);    
        }
    }

    public void getAttackImage() {
        // size
        int i = 5;

        if(inRage == false) {
            String packagePath = "/gtcafe/rpg/assets/monster/skeletonlord/attacking/";

            attackUp1 = setup(packagePath + "skeletonlord_attack_up_1.png", gp.tileSize * i, gp.tileSize * i*2);
            attackUp2 = setup(packagePath + "skeletonlord_attack_up_2.png", gp.tileSize * i, gp.tileSize * i*2);
            attackDown1 = setup(packagePath + "skeletonlord_attack_down_1.png", gp.tileSize * i, gp.tileSize * i*2);
            attackDown2 = setup(packagePath + "skeletonlord_attack_down_2.png", gp.tileSize * i, gp.tileSize * i*2);
            attackLeft1 = setup(packagePath + "skeletonlord_attack_left_1.png", gp.tileSize * i*2, gp.tileSize * i);
            attackLeft2 = setup(packagePath + "skeletonlord_attack_left_2.png", gp.tileSize * i*2, gp.tileSize * i);
            attackRight1 = setup(packagePath + "skeletonlord_attack_right_1.png", gp.tileSize * i*2, gp.tileSize * i);
            attackRight2 = setup(packagePath + "skeletonlord_attack_right_2.png", gp.tileSize * i*2, gp.tileSize * i);
        }
        else {
            String packagePath = "/gtcafe/rpg/assets/monster/skeletonlord/attacking_phase2/";

            attackUp1 = setup(packagePath + "skeletonlord_phase2_attack_up_1.png", gp.tileSize * i, gp.tileSize * i*2);
            attackUp2 = setup(packagePath + "skeletonlord_phase2_attack_up_2.png", gp.tileSize * i, gp.tileSize * i*2);
            attackDown1 = setup(packagePath + "skeletonlord_phase2_attack_down_1.png", gp.tileSize * i, gp.tileSize * i*2);
            attackDown2 = setup(packagePath + "skeletonlord_phase2_attack_down_2.png", gp.tileSize * i, gp.tileSize * i*2);
            attackLeft1 = setup(packagePath + "skeletonlord_phase2_attack_left_1.png", gp.tileSize * i*2, gp.tileSize * i);
            attackLeft2 = setup(packagePath + "skeletonlord_phase2_attack_left_2.png", gp.tileSize * i*2, gp.tileSize * i);
            attackRight1 = setup(packagePath + "skeletonlord_phase2_attack_right_1.png", gp.tileSize * i*2, gp.tileSize * i);
            attackRight2 = setup(packagePath + "skeletonlord_phase2_attack_right_2.png", gp.tileSize * i*2, gp.tileSize * i); 
        }
    }

    // Setting Slime's behavior
    // call 60 times per second
    public void setAction() {

        // 激怒模式
        if (inRage == false && life < maxLife / 2) {
            inRage = true;
            speed += 3;
            attack += 5;
            getImage();
            getAttackImage();
        }

        // Check the distance to the player
        if (getTileDistance(gp.player) < 10) {
            
            // Move toward the player, every 60 frames
            moveTowardPlayer(60);
            
        } else {

            // Get a random direction
            getRandomDirection(120);
        }

        // Check if it attacks
        if (attacking == false) {
            checkAttackOrNot(60, gp.tileSize * 7, gp.tileSize * 5);
        }
    }

    // monster receive damage/attack
    public void damageReaction() {
        actionLockCounter = 0;
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
