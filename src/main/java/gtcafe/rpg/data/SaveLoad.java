package gtcafe.rpg.data;
import gtcafe.rpg.core.GameContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.equipable.OBJ_Boots;
import gtcafe.rpg.entity.equipable.OBJ_Lantern;
import gtcafe.rpg.entity.object.OBJ_Chest;
import gtcafe.rpg.entity.object.OBJ_Door;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;
import gtcafe.rpg.entity.object.OBJ_Postion_Red;
import gtcafe.rpg.entity.object.OBJ_Tent;
import gtcafe.rpg.entity.shield.OBJ_Shield_Blue;
import gtcafe.rpg.entity.shield.OBJ_Shield_Wood;
import gtcafe.rpg.entity.weapon.OBJ_Axe;
import gtcafe.rpg.entity.weapon.OBJ_Sword_Normal;

public class SaveLoad {
    GameContext context;
    String FILE_NAME = "save.data";
    
    public SaveLoad(GameContext context) {
        this.context = context;
    }

    public Entity getObject(String itemName) {

        Entity obj = null;

        switch(itemName) {
            case "Boots" -> obj = new OBJ_Boots(context);
            case "Lantern" -> obj = new OBJ_Lantern(context);
            case "Key" -> obj = new OBJ_Key(context);
            case "Red Potion" -> obj = new OBJ_Postion_Red(context);
            case "Tent" -> obj = new OBJ_Tent(context);
            case "Door" -> obj = new OBJ_Door(context);
            case "Chest" -> obj = new OBJ_Chest(context);
            case "Wood Shield" -> obj = new OBJ_Shield_Wood(context);
            case "Blue Shield" -> obj = new OBJ_Shield_Blue(context);
            case "Normal Sword" -> obj = new OBJ_Sword_Normal(context);
            case "Woodcutter's Axe" -> obj = new OBJ_Axe(context);
            case "Heart" -> obj = new OBJ_Heart(context);
            case "Mana Crystal" -> obj = new OBJ_ManaCrystal(context);
        }

        return obj;
    }

    public void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(FILE_NAME)));

            DataStorage ds = new DataStorage();

            // PLAYER STATSU
            ds.level = context.getPlayer().level;
            ds.maxLife = context.getPlayer().maxLife;
            ds.life = context.getPlayer().life;
            ds.maxMana = context.getPlayer().maxMana;
            ds.mana = context.getPlayer().mana;
            ds.strength = context.getPlayer().strength;
            ds.dexterity = context.getPlayer().dexterity;
            ds.nextLevelExp = context.getPlayer().nextLevelExp;
            ds.exp = context.getPlayer().exp;
            ds.coin = context.getPlayer().coin;

            // PLAYER INVENTORY
            for(int i=0; i<context.getPlayer().inventory.size();i++) {
                ds.itemNames.add(context.getPlayer().inventory.get(i).name);
                ds.itemAmounts.add(context.getPlayer().inventory.get(i).amount);
            }

            // PLAYER EQUIPMENT
            ds.currentWeaponSlot = context.getPlayer().getCurrentSlot(context.getPlayer().currentWeapon);
            ds.currentShieldSlot = context.getPlayer().getCurrentSlot(context.getPlayer().currentShield);

            // OBJECT ITEM
            ds.mapObjectNames = new String[context.getMaxMap()][context.getObj()[1].length];
            ds.mapObjectWorldX = new int[context.getMaxMap()][context.getObj()[1].length];
            ds.mapObjectWorldY = new int[context.getMaxMap()][context.getObj()[1].length];
            ds.mapObjectLootNames = new String[context.getMaxMap()][context.getObj()[1].length];
            ds.mapObjectOpened = new boolean[context.getMaxMap()][context.getObj()[1].length];

            for(int mapNum=0; mapNum<context.getMaxMap(); mapNum++) {

                for (int i=0; i < context.getObj()[1].length; i++) {
                    if(context.getObj()[mapNum][i] == null) {
                        ds.mapObjectNames[mapNum][i] = "NA";
                    }
                    else {
                        ds.mapObjectNames[mapNum][i] = context.getObj()[mapNum][i].name;
                        ds.mapObjectWorldX[mapNum][i] = context.getObj()[mapNum][i].worldX;
                        ds.mapObjectWorldY[mapNum][i] = context.getObj()[mapNum][i].worldY;

                        if (context.getObj()[mapNum][i].loot !=null) {
                           ds.mapObjectLootNames[mapNum][i] = context.getObj()[mapNum][i].loot.name; 
                        }
                        ds.mapObjectOpened[mapNum][i] = context.getObj()[mapNum][i].opened;
                    }
                }
            }

            // Write the DataStorage object
            oos.writeObject(ds);
            oos.close();
        } 
        catch(Exception e) {
            System.out.println("Save exception!");
            e.printStackTrace();
        }
    }

    public void load() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(new File(FILE_NAME)));
            
            // Read the DataStorage object
            DataStorage ds = (DataStorage)ois.readObject();

            // PLAYER STATUS
            context.getPlayer().level = ds.level;
            context.getPlayer().maxLife = ds.maxLife;
            context.getPlayer().life = ds.life;
            context.getPlayer().maxMana = ds.maxMana;
            context.getPlayer().mana = ds.mana;
            context.getPlayer().strength = ds.strength;
            context.getPlayer().dexterity = ds.dexterity;
            context.getPlayer().nextLevelExp = ds.nextLevelExp;
            context.getPlayer().exp = ds.exp;
            context.getPlayer().coin = ds.coin;

            // PLAYER INVENTORY
            context.getPlayer().inventory.clear();
            for(int i=0; i<ds.itemNames.size(); i++) {
                context.getPlayer().inventory.add(getObject(ds.itemNames.get(i)));
                context.getPlayer().inventory.get(i).amount = ds.itemAmounts.get(i);
            }

            // PLAYER EQUIPMENT
            context.getPlayer().currentWeapon = context.getPlayer().inventory.get(ds.currentWeaponSlot);
            context.getPlayer().currentShield = context.getPlayer().inventory.get(ds.currentShieldSlot);
            context.getPlayer().getAttack();
            context.getPlayer().getDefense();
            context.getPlayer().getAttackImage();

            // OBJECT ON MAP
            for(int mapNum=0; mapNum<context.getMaxMap(); mapNum++) {

                for (int i=0; i < context.getObj()[1].length; i++) {
                    if(ds.mapObjectNames[mapNum][i].equals("NA")) {
                        context.getObj()[mapNum][i] = null;
                    }
                    else {
                        System.out.println("Name: " + ds.mapObjectNames[mapNum][i]);
                        context.getObj()[mapNum][i] = getObject(ds.mapObjectNames[mapNum][i]);
                        context.getObj()[mapNum][i].worldX = ds.mapObjectWorldX[mapNum][i];
                        context.getObj()[mapNum][i].worldY = ds.mapObjectWorldY[mapNum][i];

                        if (ds.mapObjectLootNames[mapNum][i] !=null) {
                            context.getObj()[mapNum][i].loot = getObject(ds.mapObjectLootNames[mapNum][i]); 
                        }
                        context.getObj()[mapNum][i].opened = ds.mapObjectOpened[mapNum][i];
                        if(context.getObj()[mapNum][i].opened == true) {
                            context.getObj()[mapNum][i].down1 = context.getObj()[mapNum][i].image2;
                        }
                    }
                }
            }

            ois.close();
        }
        catch(Exception e) {
            System.out.println("Save exception!");
            e.printStackTrace();
        }
    }
}
