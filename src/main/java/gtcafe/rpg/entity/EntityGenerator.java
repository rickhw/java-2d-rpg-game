package gtcafe.rpg.entity;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.equipable.OBJ_Boots;
import gtcafe.rpg.entity.equipable.OBJ_Lantern;
import gtcafe.rpg.entity.object.OBJ_Chest;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Door;
import gtcafe.rpg.entity.object.OBJ_Door_Iron;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;
import gtcafe.rpg.entity.object.OBJ_Postion_Red;
import gtcafe.rpg.entity.object.OBJ_Tent;
import gtcafe.rpg.entity.shield.OBJ_Shield_Blue;
import gtcafe.rpg.entity.shield.OBJ_Shield_Wood;
import gtcafe.rpg.entity.weapon.OBJ_Axe;
import gtcafe.rpg.entity.weapon.OBJ_Pickaxe;
import gtcafe.rpg.entity.weapon.OBJ_Sword_Normal;

public class EntityGenerator {
    GamePanel gp;

    public EntityGenerator(GamePanel gp) {
        this.gp = gp;
    }

     public Entity getObject(String itemName) {

        Entity obj = null;

        switch(itemName) {
            case OBJ_Boots.OBJ_NAME -> obj = new OBJ_Boots(gp);
            case OBJ_Lantern.OBJ_NAME -> obj = new OBJ_Lantern(gp);
            case OBJ_Key.OBJ_NAME -> obj = new OBJ_Key(gp);
            case OBJ_Postion_Red.OBJ_NAME -> obj = new OBJ_Postion_Red(gp);
            case OBJ_Tent.OBJ_NAME -> obj = new OBJ_Tent(gp);
            case OBJ_Door.OBJ_NAME -> obj = new OBJ_Door(gp);
            case OBJ_Door_Iron.OBJ_NAME -> obj = new OBJ_Door_Iron(gp);
            case OBJ_Chest.OBJ_NAME -> obj = new OBJ_Chest(gp);

            case OBJ_Shield_Wood.OBJ_NAME -> obj = new OBJ_Shield_Wood(gp);
            case OBJ_Shield_Blue.OBJ_NAME -> obj = new OBJ_Shield_Blue(gp);
            
            case OBJ_Sword_Normal.OBJ_NAME -> obj = new OBJ_Sword_Normal(gp);

            case OBJ_Axe.OBJ_NAME -> obj = new OBJ_Axe(gp);
            case OBJ_Pickaxe.OBJ_NAME -> obj = new OBJ_Pickaxe(gp);
            case OBJ_Heart.OBJ_NAME -> obj = new OBJ_Heart(gp);
            case OBJ_ManaCrystal.OBJ_NAME -> obj = new OBJ_ManaCrystal(gp);
            case OBJ_Coin_Bronze.OBJ_NAME -> obj = new OBJ_Coin_Bronze(gp);
        }

        return obj;
    }


}
