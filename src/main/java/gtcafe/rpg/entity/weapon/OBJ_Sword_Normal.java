package gtcafe.rpg.entity.weapon;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

// increase player's speed
public class OBJ_Sword_Normal extends Entity {
    public OBJ_Sword_Normal(GamePanel gp) {
        super(gp);
        type = EntityType.SWORD;
        name = "Normal Sword";
        down1 = setup("/gtcafe/rpg/assets/objects/sword_normal.png", gp.tileSize, gp.tileSize);
        attackValue = 2;    
        description = "[" + name + "]\nAn old sword.\nAttack: " + attackValue;
        price = 150;
        knockBackPower = 2;
        
        attackArea.width = 30;
        attackArea.height = 30;

        // 揮舞的速度，值代表 FPT，值越高，速度越慢
        motion1_duration = 5;
        motion2_duration = 25;

    }
}