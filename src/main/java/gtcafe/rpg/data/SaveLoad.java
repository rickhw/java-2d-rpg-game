package gtcafe.rpg.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.EntityGenerator;

public class SaveLoad {
    final static String FILE_NAME = "save.data";
    EntityGenerator entityGenerator;
    GamePanel gp;
    
    public SaveLoad(GamePanel gp, EntityGenerator entityGenerator) {
        this.gp = gp;
        this.entityGenerator = entityGenerator;
    }

    public void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(FILE_NAME)));

            DataStorage ds = new DataStorage();

            // PLAYER STATSU
            ds.level = gp.player.level;
            ds.maxLife = gp.player.maxLife;
            ds.life = gp.player.life;
            ds.maxMana = gp.player.maxMana;
            ds.mana = gp.player.mana;
            ds.strength = gp.player.strength;
            ds.dexterity = gp.player.dexterity;
            ds.nextLevelExp = gp.player.nextLevelExp;
            ds.exp = gp.player.exp;
            ds.coin = gp.player.coin;

            // PLAYER INVENTORY
            for(int i=0; i<gp.player.inventory.size();i++) {
                ds.itemNames.add(gp.player.inventory.get(i).name);
                ds.itemAmounts.add(gp.player.inventory.get(i).amount);
            }

            // PLAYER EQUIPMENT
            ds.currentWeaponSlot = gp.player.getCurrentSlot(gp.player.currentWeapon);
            ds.currentShieldSlot = gp.player.getCurrentSlot(gp.player.currentShield);

            // OBJECT ITEM
            ds.mapObjectNames = new String[gp.maxMap][gp.obj[1].length];
            ds.mapObjectWorldX = new int[gp.maxMap][gp.obj[1].length];
            ds.mapObjectWorldY = new int[gp.maxMap][gp.obj[1].length];
            ds.mapObjectLootNames = new String[gp.maxMap][gp.obj[1].length];
            ds.mapObjectOpened = new boolean[gp.maxMap][gp.obj[1].length];

            for(int mapNum=0; mapNum<gp.maxMap; mapNum++) {

                for (int i=0; i < gp.obj[1].length; i++) {
                    if(gp.obj[mapNum][i] == null) {
                        ds.mapObjectNames[mapNum][i] = "NA";
                    }
                    else {
                        ds.mapObjectNames[mapNum][i] = gp.obj[mapNum][i].name;
                        ds.mapObjectWorldX[mapNum][i] = gp.obj[mapNum][i].worldX;
                        ds.mapObjectWorldY[mapNum][i] = gp.obj[mapNum][i].worldY;

                        if (gp.obj[mapNum][i].loot !=null) {
                           ds.mapObjectLootNames[mapNum][i] = gp.obj[mapNum][i].loot.name; 
                        }
                        ds.mapObjectOpened[mapNum][i] = gp.obj[mapNum][i].opened;
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
            gp.player.level = ds.level;
            gp.player.maxLife = ds.maxLife;
            gp.player.life = ds.life;
            gp.player.maxMana = ds.maxMana;
            gp.player.mana = ds.mana;
            gp.player.strength = ds.strength;
            gp.player.dexterity = ds.dexterity;
            gp.player.nextLevelExp = ds.nextLevelExp;
            gp.player.exp = ds.exp;
            gp.player.coin = ds.coin;

            // PLAYER INVENTORY
            gp.player.inventory.clear();
            for(int i=0; i<ds.itemNames.size(); i++) {
                gp.player.inventory.add(entityGenerator.getObject(ds.itemNames.get(i)));
                gp.player.inventory.get(i).amount = ds.itemAmounts.get(i);
            }

            // PLAYER EQUIPMENT
            gp.player.currentWeapon = gp.player.inventory.get(ds.currentWeaponSlot);
            gp.player.currentShield = gp.player.inventory.get(ds.currentShieldSlot);
            gp.player.getAttack();
            gp.player.getDefense();
            gp.player.getAttackImage();

            // OBJECT ON MAP
            for(int mapNum=0; mapNum<gp.maxMap; mapNum++) {

                for (int i=0; i < gp.obj[1].length; i++) {
                    if(ds.mapObjectNames[mapNum][i].equals("NA")) {
                        gp.obj[mapNum][i] = null;
                    }
                    else {
                        System.out.println("Name: " + ds.mapObjectNames[mapNum][i]);
                        gp.obj[mapNum][i] = entityGenerator.getObject(ds.mapObjectNames[mapNum][i]);
                        gp.obj[mapNum][i].worldX = ds.mapObjectWorldX[mapNum][i];
                        gp.obj[mapNum][i].worldY = ds.mapObjectWorldY[mapNum][i];

                        if (ds.mapObjectLootNames[mapNum][i] !=null) {
                            gp.obj[mapNum][i].loot = entityGenerator.getObject(ds.mapObjectLootNames[mapNum][i]); 
                        }
                        gp.obj[mapNum][i].opened = ds.mapObjectOpened[mapNum][i];
                        if(gp.obj[mapNum][i].opened == true) {
                            gp.obj[mapNum][i].down1 = gp.obj[mapNum][i].image2;
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
