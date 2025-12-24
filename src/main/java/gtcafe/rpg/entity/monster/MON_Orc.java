package gtcafe.rpg.entity.monster;

import java.util.Random;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;

public class MON_Orc extends Entity {
    GamePanel gp;
    
    public MON_Orc(GamePanel gp) {
        super(gp);
        this.gp = gp;
        
        type = EntityType.MONSTER;
        name = "Orc";
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxLife = 10;
        life = maxLife;
        attack = 8;
        defense = 2;
        exp = 20; // how much can get the exp
        knockBackPower = 5;

        solidArea.x = solidAreaBaseUnit;
        solidArea.y = solidAreaBaseUnit;
        solidArea.width = gp.tileSize - (solidAreaBaseUnit * 2);    // 40;
        solidArea.height = gp.tileSize - solidAreaBaseUnit;   // 44;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        attackArea.width = gp.tileSize;     // 48;
        attackArea.height = gp.tileSize;    // 48;

        motion1_duration = 40; // fps
        motion2_duration = 85; // fps

        getImage();
        getAttackImage();
    }

    public void getImage() {
        String path = "/gtcafe/rpg/assets/monster/orc/walking/";
        up1 = setup(path + "orc_up_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(path + "orc_up_2.png", gp.tileSize, gp.tileSize);
        down1 = setup(path + "orc_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(path + "orc_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup(path + "orc_left_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(path + "orc_left_2.png", gp.tileSize, gp.tileSize);
        right1 = setup(path + "orc_right_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(path + "orc_right_2.png", gp.tileSize, gp.tileSize);
    }

    public void getAttackImage() {
        String packagePath = "/gtcafe/rpg/assets/monster/orc/attacking/";

        attackUp1 = setup(packagePath + "orc_attack_up_1.png", gp.tileSize, gp.tileSize*2);
        attackUp2 = setup(packagePath + "orc_attack_up_2.png", gp.tileSize, gp.tileSize*2);
        attackDown1 = setup(packagePath + "orc_attack_down_1.png", gp.tileSize, gp.tileSize*2);
        attackDown2 = setup(packagePath + "orc_attack_down_2.png", gp.tileSize, gp.tileSize*2);
        attackLeft1 = setup(packagePath + "orc_attack_left_1.png", gp.tileSize*2, gp.tileSize);
        attackLeft2 = setup(packagePath + "orc_attack_left_2.png", gp.tileSize*2, gp.tileSize);
        attackRight1 = setup(packagePath + "orc_attack_right_1.png", gp.tileSize*2, gp.tileSize);
        attackRight2 = setup(packagePath + "orc_attack_right_2.png", gp.tileSize*2, gp.tileSize);
    }

    // call 60 times per second
    public void setAction() {

        if (onPath == true) {

            // Check if it stops chasing. 超過 N 格就不要跟蹤了
            checkStopChasingOrNot(gp.player, 5, 100, "The Orc has stopped chasing!");

            // Search the direction to gp (==> follow the player)
            searchPath(getGoalCol(gp.player), getGoalRow(gp.player));
            
        } else {

            // Check if it starts chasing (Player 距離 N 格以內, Monster 就主動跟蹤)
            checkStartChasingOrNot(gp.player, 3, 100, "You've been targeted by Orc!");

            // Get a random direction
            getRandomDirection(120);
        }

        // Check if it attacks
        if (attacking == false) {
            checkAttackOrNot(30, gp.tileSize * 4, gp.tileSize);
        }
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
