package gtcafe.rpg;

import gtcafe.rpg.entity.NPC_OldMan;
import gtcafe.rpg.monster.MON_GreenSlime;
import gtcafe.rpg.object.OBJ_Axe;
import gtcafe.rpg.object.OBJ_Coin_Bronze;
import gtcafe.rpg.object.OBJ_Heart;
import gtcafe.rpg.object.OBJ_ManaCrystal;
import gtcafe.rpg.object.OBJ_Postion_Red;
import gtcafe.rpg.object.OBJ_Shield_Blue;
import gtcafe.rpg.object.OBJ_Sword_Normal;
import gtcafe.rpg.tile.Map;
import gtcafe.rpg.tile.interactive.IT_DryTree;

public class AssetSetter {

    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    // instantiate objects
    public void setObject() {
        int i = 0;
        int mapIndex = Map.WORLD_MAP.index;
        
        gp.obj[mapIndex][i] = new OBJ_Coin_Bronze(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 25;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 23;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Coin_Bronze(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 21;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 19;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Coin_Bronze(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 26;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 21;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Sword_Normal(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 33;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 21;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Shield_Blue(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 35;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 21;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Postion_Red(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 22;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 27;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Heart(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 22;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 29;

        i++;
        gp.obj[mapIndex][i] = new OBJ_ManaCrystal(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 22;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 31;

        // for Testing
        // gp.obj[0] = new OBJ_Door(gp);
        // gp.obj[0].worldX = gp.tileSize * 25;
        // gp.obj[0].worldY = gp.tileSize * 19;

        // gp.obj[1] = new OBJ_Door(gp);
        // gp.obj[1].worldX = gp.tileSize * 23;
        // gp.obj[1].worldY = gp.tileSize * 25;
    }

    public void setNPC() {
        int i = 0;
        int mapIndex = Map.WORLD_MAP.index;

        gp.npc[mapIndex][i] = new NPC_OldMan(gp);
        gp.npc[mapIndex][i].worldX = gp.tileSize * 18;
        gp.npc[mapIndex][i].worldY = gp.tileSize * 20;

        i++;
        gp.npc[mapIndex][i] = new NPC_OldMan(gp);
        gp.npc[mapIndex][i].worldX = gp.tileSize * 28;
        gp.npc[mapIndex][i].worldY = gp.tileSize * 20;


        // For Testing
        // i++;
        i = 0;
        mapIndex = Map.INTERIOR_01.index;
        gp.npc[mapIndex][i] = new NPC_OldMan(gp);
        gp.npc[mapIndex][i].worldX = gp.tileSize * 10;
        gp.npc[mapIndex][i].worldY = gp.tileSize * 11;

        // i++;
        // gp.npc[mapIndex][i] = new NPC_OldMan(gp);
        // gp.npc[mapIndex][i].worldX = gp.tileSize * 10;
        // gp.npc[mapIndex][i].worldY = gp.tileSize * 10;

        // gp.npc[3] = new NPC_OldMan(gp);
        // gp.npc[3].worldX = gp.tileSize * 21;
        // gp.npc[3].worldY = gp.tileSize * 11;

        // gp.npc[4] = new NPC_OldMan(gp);
        // gp.npc[4].worldX = gp.tileSize * 21;
        // gp.npc[4].worldY = gp.tileSize * 31;
    }

    public void setMonster() {
        int i = 0;
        int mapIndex = Map.WORLD_MAP.index;

        gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        gp.monster[mapIndex][i].worldX = gp.tileSize * 23;
        gp.monster[mapIndex][i].worldY = gp.tileSize * 36;

        i++;
        gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        gp.monster[mapIndex][i].worldX = gp.tileSize * 23;
        gp.monster[mapIndex][i].worldY = gp.tileSize * 37;

        i++;
        gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        gp.monster[mapIndex][i].worldX = gp.tileSize * 23;
        gp.monster[mapIndex][i].worldY = gp.tileSize * 38;

        i++;
        gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        gp.monster[mapIndex][i].worldX = gp.tileSize * 23;
        gp.monster[mapIndex][i].worldY = gp.tileSize * 39;

        // for testing
        // i++;
        // gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        // gp.monster[mapIndex][i].worldX = gp.tileSize * 11;
        // gp.monster[mapIndex][i].worldY = gp.tileSize * 8;

        // i++;
        // gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        // gp.monster[mapIndex][i].worldX = gp.tileSize * 11;
        // gp.monster[mapIndex][i].worldY = gp.tileSize * 9;

        i++;
        gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        gp.monster[mapIndex][i].worldX = gp.tileSize * 33;
        gp.monster[mapIndex][i].worldY = gp.tileSize * 39;

        i++;
        gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        gp.monster[mapIndex][i].worldX = gp.tileSize * 34;
        gp.monster[mapIndex][i].worldY = gp.tileSize * 39;

        i++;
        gp.monster[mapIndex][i] = new MON_GreenSlime(gp);
        gp.monster[mapIndex][i].worldX = gp.tileSize * 35;
        gp.monster[mapIndex][i].worldY = gp.tileSize * 39;

    }

    public void setInteractiveTiles() {
        int i = 0;
        int mapIndex = Map.WORLD_MAP.index;

        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 27, 12);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 28, 12);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 29, 12);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 30, 12);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 31, 12);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 32, 12);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 33, 12);

        // for testing
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 30, 20);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 30, 21);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 30, 22);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 20, 20);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 20, 21);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 20, 22);

    }
}
