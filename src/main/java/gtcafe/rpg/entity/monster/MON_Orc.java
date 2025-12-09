package gtcafe.rpg.entity.monster;
import gtcafe.rpg.core.GameContext;

import java.util.Random;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;

public class MON_Orc extends Entity {
    GameContext context;
    
    public MON_Orc(GameContext context) {
        super(context);
        this.context = context;
        
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

        solidArea.x = 4;
        solidArea.y = 4;
        solidArea.width = 40;
        solidArea.height = 44;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        attackArea.width = 48;
        attackArea.height = 48;

        motion1_duration = 40;
        motion2_duration = 85;

        getImage();
        getAttackImage();
    }

    public void getImage() {
        up1 = setup("/gtcafe/rpg/assets/monster/orc_up_1.png", context.getTileSize(), context.getTileSize());
        up2 = setup("/gtcafe/rpg/assets/monster/orc_up_2.png", context.getTileSize(), context.getTileSize());
        down1 = setup("/gtcafe/rpg/assets/monster/orc_down_1.png", context.getTileSize(), context.getTileSize());
        down2 = setup("/gtcafe/rpg/assets/monster/orc_down_2.png", context.getTileSize(), context.getTileSize());
        left1 = setup("/gtcafe/rpg/assets/monster/orc_left_1.png", context.getTileSize(), context.getTileSize());
        left2 = setup("/gtcafe/rpg/assets/monster/orc_left_2.png", context.getTileSize(), context.getTileSize());
        right1 = setup("/gtcafe/rpg/assets/monster/orc_right_1.png", context.getTileSize(), context.getTileSize());
        right2 = setup("/gtcafe/rpg/assets/monster/orc_right_2.png", context.getTileSize(), context.getTileSize());
    }

    public void getAttackImage() {
        String packagePath = "/gtcafe/rpg/assets/monster/";

        attackUp1 = setup(packagePath + "orc_attack_up_1.png", context.getTileSize(), context.getTileSize()*2);
        attackUp2 = setup(packagePath + "orc_attack_up_2.png", context.getTileSize(), context.getTileSize()*2);
        attackDown1 = setup(packagePath + "orc_attack_down_1.png", context.getTileSize(), context.getTileSize()*2);
        attackDown2 = setup(packagePath + "orc_attack_down_2.png", context.getTileSize(), context.getTileSize()*2);
        attackLeft1 = setup(packagePath + "orc_attack_left_1.png", context.getTileSize()*2, context.getTileSize());
        attackLeft2 = setup(packagePath + "orc_attack_left_2.png", context.getTileSize()*2, context.getTileSize());
        attackRight1 = setup(packagePath + "orc_attack_right_1.png", context.getTileSize()*2, context.getTileSize());
        attackRight2 = setup(packagePath + "orc_attack_right_2.png", context.getTileSize()*2, context.getTileSize());
    }

    // Setting Slime's behavior
    // call 60 times per second
    public void setAction() {

        if (onPath == true) {

            // Check if it stops chasing. 超過 N 格就不要跟蹤了
            checkStopChasingOrNot(context.getPlayer(), 5, 100, "The Orc has stopped chasing!");

            // Search the direction to gp (==> follow the player)
            searchPath(getGoalCol(context.getPlayer()), getGoalRow(context.getPlayer()));

            
        } else {

            // Check if it starts chasing (Player 距離 N 格以內, Monster 就主動跟蹤)
            checkStartChasingOrNot(context.getPlayer(), 3, 100, "You've been targeted by Orc!");

            // Get a random direction
            getRandomDirection();
        }

        // Check if it attacks
        if (attacking == false) {
            checkAttackOrNot(30, context.getTileSize() * 4, context.getTileSize());
        }
    }

    // monster receive damage/attack
    public void damageReaction() {
        actionLockCounter = 0;
        // direction = context.getPlayer().direction;

        onPath = true;
    }

    // called when monster die (GamePanel.update())
    public void checkDrop() {
        // CAST A DIE
        int i = new Random().nextInt(100) + 1;

        // SET THE MONSTER DROP
        if (i < 50) { dropItem(new OBJ_Coin_Bronze(context)); }
        if (i >= 50 && i < 75) { dropItem(new OBJ_Heart(context)); }
        if (i >= 75 && i < 100) { dropItem(new OBJ_ManaCrystal(context)); }
    }
}
