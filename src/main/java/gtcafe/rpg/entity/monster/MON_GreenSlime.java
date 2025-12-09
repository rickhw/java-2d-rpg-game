package gtcafe.rpg.entity.monster;
import gtcafe.rpg.core.GameContext;

import java.util.Random;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;
import gtcafe.rpg.entity.projectile.OBJ_Rock;
import gtcafe.rpg.state.Direction;

public class MON_GreenSlime extends Entity {
    GameContext context;
    
    public MON_GreenSlime(GameContext context) {
        super(context);
        this.context = context;
        
        type = EntityType.MONSTER;
        name = "GreenSlime";
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxLife = 10;
        life = maxLife;
        attack = 2;
        defense = 0;
        exp = 2; // how much can get the exp
        projectile = new OBJ_Rock(context);

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", context.getTileSize(), context.getTileSize());
        up2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", context.getTileSize(), context.getTileSize());
        down1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", context.getTileSize(), context.getTileSize());
        down2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", context.getTileSize(), context.getTileSize());
        left1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", context.getTileSize(), context.getTileSize());
        left2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", context.getTileSize(), context.getTileSize());
        right1 = setup("/gtcafe/rpg/assets/monster/greenslime_down_1.png", context.getTileSize(), context.getTileSize());
        right2 = setup("/gtcafe/rpg/assets/monster/greenslime_down_2.png", context.getTileSize(), context.getTileSize());
    }

    // Setting Slime's behavior
    // call 60 times per second
    public void setAction() {

        if (onPath == true) {

            // Check if it stops chasing. 超過 N 格就不要跟蹤了
            checkStopChasingOrNot(context.getPlayer(), 5, 100, "The Slime has stopped chasing!");

            // Search the direction to gp (==> follow the player)
            searchPath(getGoalCol(context.getPlayer()), getGoalRow(context.getPlayer()));

            // Check if it shoots a projectile (shooting the player when aggro (侵略))
            checkShootOrNot(200, 30);
            
        } else {

            // Check if it starts chasing (Player 距離 N 格以內, Monster 就主動跟蹤)
            checkStartChasingOrNot(context.getPlayer(), 3, 100, "You've been targeted by Slime!");

            // Get a random direction
            getRandomDirection();
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
