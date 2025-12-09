package gtcafe.rpg.entity.npc;
import gtcafe.rpg.core.GameContext;

import java.awt.Rectangle;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.equipable.OBJ_Boots;
import gtcafe.rpg.entity.equipable.OBJ_Lantern;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.object.OBJ_Postion_Red;
import gtcafe.rpg.entity.object.OBJ_Tent;
import gtcafe.rpg.entity.shield.OBJ_Shield_Blue;
import gtcafe.rpg.entity.shield.OBJ_Shield_Wood;
import gtcafe.rpg.entity.weapon.OBJ_Axe;
import gtcafe.rpg.entity.weapon.OBJ_Sword_Normal;
import gtcafe.rpg.state.Direction;
import gtcafe.rpg.state.GameState;

public class NPC_Merchant extends Entity {
    
    public NPC_Merchant(GameContext context) {
        super(context);

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
        up1 = setup(packagePath + "merchant_down_1.png", context.getTileSize(), context.getTileSize());
        up2 = setup(packagePath + "merchant_down_2.png", context.getTileSize(), context.getTileSize());
        down1 = setup(packagePath + "merchant_down_1.png", context.getTileSize(), context.getTileSize());
        down2 = setup(packagePath + "merchant_down_2.png", context.getTileSize(), context.getTileSize());
        left1 = setup(packagePath + "merchant_down_1.png", context.getTileSize(), context.getTileSize());
        left2 = setup(packagePath + "merchant_down_2.png", context.getTileSize(), context.getTileSize());
        right1 = setup(packagePath + "merchant_down_1.png", context.getTileSize(), context.getTileSize());
        right2 = setup(packagePath + "merchant_down_2.png", context.getTileSize(), context.getTileSize());
    }

    // set the action behavior for different actors
    public void setAction() {
    }

    public void setDialogue() {
        dialogues[0] = "Hello, Rick! You're finally awake and found me. I have some good stuff.\nDo you ant to trade?";
    }

    public void setItem() {
        inventory.add(new OBJ_Postion_Red(context));
        inventory.add(new OBJ_Key(context));
        inventory.add(new OBJ_Axe(context));
        inventory.add(new OBJ_Sword_Normal(context));
        inventory.add(new OBJ_Shield_Blue(context));
        inventory.add(new OBJ_Shield_Wood(context));
        inventory.add(new OBJ_Tent(context));
        inventory.add(new OBJ_Lantern(context));
        inventory.add(new OBJ_Boots(context));
    }

    public void speak() {
        super.speak();

        context.setGameState(GameState.TRADE);
        context.getGameUI().npc = this;
    }
}
