package gtcafe.rpg.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
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
            ds.level = gp.player.getLevel();
            ds.maxLife = gp.player.getMaxLife();
            ds.life = gp.player.getLife();
            ds.maxMana = gp.player.getMaxMana();
            ds.mana = gp.player.getMana();
            ds.strength = gp.player.getStrength();
            ds.dexterity = gp.player.getDexterity();
            ds.nextLevelExp = gp.player.getNextLevelExp();
            ds.exp = gp.player.getExp();
            ds.coin = gp.player.getCoin();

            // PLAYER INVENTORY
            for (int i = 0; i < gp.player.inventory.size(); i++) {
                ds.itemNames.add(gp.player.inventory.get(i).name);
                ds.itemAmounts.add(gp.player.inventory.get(i).amount);
            }

            // PLAYER EQUIPMENT
            ds.currentWeaponSlot = gp.player.getCurrentSlot(gp.player.currentWeapon);
            ds.currentShieldSlot = gp.player.getCurrentSlot(gp.player.currentShield);

            // OBJECT ITEM
            ds.mapObjectNames = new String[gp.maxMap][];
            ds.mapObjectWorldX = new int[gp.maxMap][];
            ds.mapObjectWorldY = new int[gp.maxMap][];
            ds.mapObjectLootNames = new String[gp.maxMap][];
            ds.mapObjectOpened = new boolean[gp.maxMap][];

            for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
                int size = gp.obj[mapNum].size();
                ds.mapObjectNames[mapNum] = new String[size];
                ds.mapObjectWorldX[mapNum] = new int[size];
                ds.mapObjectWorldY[mapNum] = new int[size];
                ds.mapObjectLootNames[mapNum] = new String[size];
                ds.mapObjectOpened[mapNum] = new boolean[size];

                for (int i = 0; i < size; i++) {
                    Entity obj = gp.obj[mapNum].get(i);
                    // No need to check null if ArrayList doesn't have holes, but for safety:
                    if (obj == null) {
                        ds.mapObjectNames[mapNum][i] = "NA";
                    } else {
                        ds.mapObjectNames[mapNum][i] = obj.name;
                        ds.mapObjectWorldX[mapNum][i] = obj.getWorldX();
                        ds.mapObjectWorldY[mapNum][i] = obj.getWorldY();
                        if (obj.loot != null) {
                            ds.mapObjectLootNames[mapNum][i] = obj.loot.name;
                        }
                        ds.mapObjectOpened[mapNum][i] = obj.opened;
                    }
                }
            }

            // Write the DataStorage object
            oos.writeObject(ds);
            oos.close();
        } catch (Exception e) {
            System.out.println("Save exception!");
            e.printStackTrace();
        }
    }

    public void load() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(new File(FILE_NAME)));

            // Read the DataStorage object
            DataStorage ds = (DataStorage) ois.readObject();

            // PLAYER STATUS
            gp.player.setLevel(ds.level);
            gp.player.setMaxLife(ds.maxLife);
            gp.player.setLife(ds.life);
            gp.player.setMaxMana(ds.maxMana);
            gp.player.setMana(ds.mana);
            gp.player.setStrength(ds.strength);
            gp.player.setDexterity(ds.dexterity);
            gp.player.setNextLevelExp(ds.nextLevelExp);
            gp.player.setExp(ds.exp);
            gp.player.setCoin(ds.coin);

            // PLAYER INVENTORY
            gp.player.inventory.clear();
            for (int i = 0; i < ds.itemNames.size(); i++) {
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
            for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
                gp.obj[mapNum].clear(); // Clear existing

                for (int i = 0; i < ds.mapObjectNames[mapNum].length; i++) {
                    if (ds.mapObjectNames[mapNum][i].equals("NA")) {
                        // null? do nothing since we cleared it.
                    } else {
                        Entity obj = entityGenerator.getObject(ds.mapObjectNames[mapNum][i]);
                        obj.setWorldX(ds.mapObjectWorldX[mapNum][i]);
                        obj.setWorldY(ds.mapObjectWorldY[mapNum][i]);

                        if (ds.mapObjectLootNames[mapNum][i] != null) {
                            obj.setLoot(entityGenerator.getObject(ds.mapObjectLootNames[mapNum][i]));
                        }
                        obj.opened = ds.mapObjectOpened[mapNum][i];
                        if (obj.opened == true) {
                            obj.down1 = obj.image2;
                        }

                        gp.obj[mapNum].add(obj);
                    }
                }
            }

            ois.close();
        } catch (Exception e) {
            System.out.println("Save exception!");
            e.printStackTrace();
        }
    }
}
