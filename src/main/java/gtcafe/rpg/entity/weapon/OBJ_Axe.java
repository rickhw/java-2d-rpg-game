package gtcafe.rpg.entity.weapon;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityType;

public class OBJ_Axe extends Entity {
    public static final String OBJ_NAME = "Woodcutter's Axe";

    public OBJ_Axe(GamePanel gp) {
        super(gp);
        type = EntityType.AXE;
        name = OBJ_NAME;
        down1 = setup("/gtcafe/rpg/assets/objects/axe.png", gp.tileSize, gp.tileSize);
        attackValue = 4;
        description = "[Woodcutters' Axe]\nA bit rusty but still \ncan cut some trees.\nAttack: " + attackValue;
        price = 75;
        knockBackPower = 10;

        // Player 有預設的攻擊範圍, 但每個武器攻擊範圍不一樣.
        // 當 Player 拿起武器時, 在 Player.getAttack() 會改成 武器的攻擊範圍.
        attackArea.width = 30;
        attackArea.height = 30;

        motion1_duration = 20;
        motion2_duration = 40;

    }
}
