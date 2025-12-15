package gtcafe.rpg.entity.weapon;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Pickaxe extends Entity {
    public static final String OBJ_NAME = "Pickaxe";

    public OBJ_Pickaxe(GamePanel gp) {
        super(gp);
        type = EntityType.PICKAXE;
        name = OBJ_NAME;
        down1 = setup("/gtcafe/rpg/assets/objects/pickaxe.png", gp.tileSize, gp.tileSize);
        attackValue = 4;
        description = String.format("[%s]\nYou will dig it.\nAttack: %s", OBJ_NAME, attackValue);
        price = 75;
        
        // Player 有預設的攻擊範圍, 但每個武器攻擊範圍不一樣.
        // 當 Player 拿起武器時, 在 Player.getAttack() 會改成 武器的攻擊範圍.
        attackArea.width = gp.tileSize - (solidAreaBaseUnit * 2); // 30;
        attackArea.height = gp.tileSize - (solidAreaBaseUnit * 2); // 30;

        knockBackPower = 10;
        motion1_duration = 10;
        motion2_duration = 20;

    }
}
