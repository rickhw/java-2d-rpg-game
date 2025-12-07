package gtcafe.rpg.entity.npc;

import java.awt.Rectangle;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.object.OBJ_Axe;
import gtcafe.rpg.entity.object.OBJ_Boots;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.object.OBJ_Lantern;
import gtcafe.rpg.entity.object.OBJ_Postion_Red;
import gtcafe.rpg.entity.object.OBJ_Shield_Blue;
import gtcafe.rpg.entity.object.OBJ_Shield_Wood;
import gtcafe.rpg.entity.object.OBJ_Sword_Normal;
import gtcafe.rpg.entity.object.OBJ_Tent;
import gtcafe.rpg.state.Direction;
import gtcafe.rpg.state.GameState;

public class NPC_Merchant extends Entity {
    
    public NPC_Merchant(GamePanel gp) {
        super(gp);

        direction = Direction.DOWN;
        speed = 1;

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImages();
        setDialogue();
        setItem();
    }

    public void getImages() {
        String packagePath = "/gtcafe/rpg/assets/npc/";
        up1 = setup(packagePath + "merchant_down_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "merchant_down_2.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "merchant_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "merchant_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "merchant_down_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "merchant_down_2.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "merchant_down_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "merchant_down_2.png", gp.tileSize, gp.tileSize);
    }

    // set the action behavior for different actors
    public void setAction() {
    }

    public void setDialogue() {
        dialogues[0] = "Hello, Rick! You're finally awake and found me. I have some good stuff.\nDo you ant to trade?";
    }

    public void setItem() {
        inventory.add(new OBJ_Postion_Red(gp));
        inventory.add(new OBJ_Key(gp));
        inventory.add(new OBJ_Axe(gp));
        inventory.add(new OBJ_Sword_Normal(gp));
        inventory.add(new OBJ_Shield_Blue(gp));
        inventory.add(new OBJ_Shield_Wood(gp));
        inventory.add(new OBJ_Tent(gp));
        inventory.add(new OBJ_Lantern(gp));
        inventory.add(new OBJ_Boots(gp));
    }

    public void speak() {
        super.speak();

        gp.gameState = GameState.TRADE;
        gp.ui.npc = this;
    }
}
