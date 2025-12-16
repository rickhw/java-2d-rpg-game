package gtcafe.rpg;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.equipable.OBJ_Lantern;
import gtcafe.rpg.entity.monster.MON_Bat;
import gtcafe.rpg.entity.monster.MON_GreenSlime;
import gtcafe.rpg.entity.monster.MON_Orc;
import gtcafe.rpg.entity.monster.MON_RedSlime;
import gtcafe.rpg.entity.monster.MON_SkeletonLord;
import gtcafe.rpg.entity.npc.NPC_BigRock;
import gtcafe.rpg.entity.npc.NPC_Merchant;
import gtcafe.rpg.entity.npc.NPC_OldMan;
import gtcafe.rpg.entity.object.OBJ_BlueHeart;
import gtcafe.rpg.entity.object.OBJ_Chest;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Door;
import gtcafe.rpg.entity.object.OBJ_Door_Iron;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.object.OBJ_Postion_Red;
import gtcafe.rpg.entity.object.OBJ_Tent;
import gtcafe.rpg.entity.shield.OBJ_Shield_Blue;
import gtcafe.rpg.entity.weapon.OBJ_Axe;
import gtcafe.rpg.entity.weapon.OBJ_Pickaxe;

import gtcafe.rpg.tile.Map;
import gtcafe.rpg.tile.interactive.IT_DestructibleWall;
import gtcafe.rpg.tile.interactive.IT_DryTree;
import gtcafe.rpg.tile.interactive.IT_MetalPlate;

public class AssetSetter {

    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    // instantiate objects
    public void setObject() {
        int mapIndex = Map.WORLD_MAP.index;

        addObj(mapIndex, new OBJ_Coin_Bronze(gp), 25, 23);
        addObj(mapIndex, new OBJ_Coin_Bronze(gp), 21, 19);
        addObj(mapIndex, new OBJ_Lantern(gp), 18, 20);
        addObj(mapIndex, new OBJ_Tent(gp), 19, 20);
        addObj(mapIndex, new OBJ_Coin_Bronze(gp), 26, 21);
        addObj(mapIndex, new OBJ_Axe(gp), 33, 7);
        addObj(mapIndex, new OBJ_Shield_Blue(gp), 10, 34);

        // Door
        addObj(mapIndex, new OBJ_Door(gp), 14, 28);
        addObj(mapIndex, new OBJ_Door(gp), 12, 12);

        // 寶箱
        OBJ_Chest chest;
        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Key(gp));
        addObj(mapIndex, chest, 30, 28);

        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Heart(gp));
        addObj(mapIndex, chest, 12, 8);

        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Key(gp));
        addObj(mapIndex, chest, 17, 22);

        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Heart(gp));
        addObj(mapIndex, chest, 16, 22);

        // Postion
        addObj(mapIndex, new OBJ_Postion_Red(gp), 27, 16);
        addObj(mapIndex, new OBJ_Shield_Blue(gp), 30, 12);
        addObj(mapIndex, new OBJ_Postion_Red(gp), 21, 20);

        // DONGEON01
        mapIndex = Map.DONGEON01.index;

        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Pickaxe(gp));
        addObj(mapIndex, chest, 40, 41);

        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Postion_Red(gp));
        addObj(mapIndex, chest, 13, 16);

        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Postion_Red(gp));
        addObj(mapIndex, chest, 26, 34);

        chest = new OBJ_Chest(gp);
        chest.setLoot(new OBJ_Postion_Red(gp));
        addObj(mapIndex, chest, 27, 15);

        addObj(mapIndex, new OBJ_Door_Iron(gp), 18, 23);

        // DONGEON02
        mapIndex = Map.DONGEON02.index;

        addObj(mapIndex, new OBJ_BlueHeart(gp), 25, 8);
        addObj(mapIndex, new OBJ_Door_Iron(gp), 25, 15);
    }

    public void addObj(int mapIndex, Entity obj, int col, int row) {
        obj.setWorldX(gp.tileSize * col);
        obj.setWorldY(gp.tileSize * row);
        gp.obj[mapIndex].add(obj);
    }

    public void addEntity(java.util.ArrayList<Entity>[] list, int mapIndex, Entity entity, int col, int row) {
        entity.setWorldX(gp.tileSize * col);
        entity.setWorldY(gp.tileSize * row);
        list[mapIndex].add(entity);
    }

    public void setNPC() {
        int mapIndex = Map.WORLD_MAP.index;
        addEntity(gp.npc, mapIndex, new NPC_OldMan(gp, "Steve"), 21, 21);

        mapIndex = Map.STORE.index;
        addEntity(gp.npc, mapIndex, new NPC_Merchant(gp), 12, 7);

        mapIndex = Map.DONGEON01.index;
        addEntity(gp.npc, mapIndex, new NPC_BigRock(gp), 20, 25);
        addEntity(gp.npc, mapIndex, new NPC_BigRock(gp), 11, 18);
        addEntity(gp.npc, mapIndex, new NPC_BigRock(gp), 23, 14);

        mapIndex = Map.DONGEON02.index;
        // none
    }

    public void setMonster() {
        int mapIndex = Map.WORLD_MAP.index;
        addEntity(gp.monster, mapIndex, new MON_GreenSlime(gp), 23, 36);
        addEntity(gp.monster, mapIndex, new MON_GreenSlime(gp), 23, 37);
        addEntity(gp.monster, mapIndex, new MON_GreenSlime(gp), 24, 37);
        addEntity(gp.monster, mapIndex, new MON_GreenSlime(gp), 38, 42);
        addEntity(gp.monster, mapIndex, new MON_GreenSlime(gp), 34, 42);

        addEntity(gp.monster, mapIndex, new MON_RedSlime(gp), 30, 6);
        addEntity(gp.monster, mapIndex, new MON_RedSlime(gp), 30, 7);
        addEntity(gp.monster, mapIndex, new MON_RedSlime(gp), 30, 8);
        addEntity(gp.monster, mapIndex, new MON_RedSlime(gp), 30, 9);

        addEntity(gp.monster, mapIndex, new MON_Orc(gp), 12, 33);

        mapIndex = Map.DONGEON01.index;
        addEntity(gp.monster, mapIndex, new MON_Bat(gp), 26, 31);
        addEntity(gp.monster, mapIndex, new MON_Bat(gp), 26, 30);
        addEntity(gp.monster, mapIndex, new MON_Bat(gp), 26, 29);

        mapIndex = Map.DONGEON02.index;
        addEntity(gp.monster, mapIndex, new MON_Bat(gp), 17, 19);
        addEntity(gp.monster, mapIndex, new MON_SkeletonLord(gp), 23, 16);
    }

    public void setInteractiveTiles() {
        int mapIndex = Map.WORLD_MAP.index;
        addEntity(gp.iTile, mapIndex, new IT_DryTree(gp, 27, 12), 27, 12);
        addEntity(gp.iTile, mapIndex, new IT_DryTree(gp, 28, 12), 28, 12);
        addEntity(gp.iTile, mapIndex, new IT_DryTree(gp, 29, 12), 29, 12);
        addEntity(gp.iTile, mapIndex, new IT_DryTree(gp, 31, 12), 31, 12);
        addEntity(gp.iTile, mapIndex, new IT_DryTree(gp, 32, 12), 32, 12);
        addEntity(gp.iTile, mapIndex, new IT_DryTree(gp, 33, 12), 33, 12);
        // for testing
        addEntity(gp.iTile, mapIndex, new IT_DryTree(gp, 30, 21), 30, 21);

        // Destructible Wall
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 21), 18, 21);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 22), 18, 22);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 17, 22), 17, 22);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 10, 31), 10, 31);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 10, 32), 10, 32);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 10, 33), 10, 33);

        // Dungeon 01
        mapIndex = Map.DONGEON01.index;
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 30), 18, 30);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 17, 30), 17, 30);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 17, 31), 17, 31);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 17, 32), 17, 32);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 34), 18, 34);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 33), 18, 33);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 10, 22), 10, 22);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 10, 24), 10, 24);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 38, 18), 38, 18);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 38, 19), 38, 19);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 38, 20), 38, 20);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 38, 21), 38, 21);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 13), 18, 13);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 14), 18, 14);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 22, 28), 22, 28);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 30, 28), 30, 28);
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 32, 28), 32, 28);

        // Dungeon 02
        mapIndex = Map.DONGEON02.index;
        addEntity(gp.iTile, mapIndex, new IT_DestructibleWall(gp, 18, 30), 18, 30);
        addEntity(gp.iTile, mapIndex, new IT_MetalPlate(gp, 20, 22), 20, 22);
        addEntity(gp.iTile, mapIndex, new IT_MetalPlate(gp, 8, 17), 8, 17);
        addEntity(gp.iTile, mapIndex, new IT_MetalPlate(gp, 39, 31), 39, 31);
    }
}
