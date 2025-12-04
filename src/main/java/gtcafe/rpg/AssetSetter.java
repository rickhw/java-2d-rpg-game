package gtcafe.rpg;

import gtcafe.rpg.entity.monster.MON_GreenSlime;
import gtcafe.rpg.entity.npc.NPC_Merchant;
import gtcafe.rpg.entity.npc.NPC_OldMan;
import gtcafe.rpg.entity.object.OBJ_Chest;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Door;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;
import gtcafe.rpg.entity.object.OBJ_Postion_Red;
import gtcafe.rpg.entity.object.OBJ_Shield_Blue;
import gtcafe.rpg.entity.object.OBJ_Sword_Normal;
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


        i++;
        gp.obj[mapIndex][i] = new OBJ_Door(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 14;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 28;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Door(gp);
        gp.obj[mapIndex][i].worldX = gp.tileSize * 12;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 12;

        i++;
        gp.obj[mapIndex][i] = new OBJ_Chest(gp, new OBJ_Key(gp));
        gp.obj[mapIndex][i].worldX = gp.tileSize * 24;
        gp.obj[mapIndex][i].worldY = gp.tileSize * 27;


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

        gp.npc[mapIndex][i] = new NPC_OldMan(gp, "Steve");
        gp.npc[mapIndex][i].worldX = gp.tileSize * 18;
        gp.npc[mapIndex][i].worldY = gp.tileSize * 20;

        i++;
        gp.npc[mapIndex][i] = new NPC_OldMan(gp, "Erica");
        gp.npc[mapIndex][i].worldX = gp.tileSize * 28;
        gp.npc[mapIndex][i].worldY = gp.tileSize * 20;


        // For Testing
        // i++;
        i = 0;
        mapIndex = Map.INTERIOR_01.index;
        gp.npc[mapIndex][i] = new NPC_Merchant(gp);
        gp.npc[mapIndex][i].worldX = gp.tileSize * 12;
        gp.npc[mapIndex][i].worldY = gp.tileSize * 7;

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
        // gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 30, 20);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 30, 21);
        // gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 30, 22);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 14, 40);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 13, 40);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 13, 41);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 12, 41);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 11, 41);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 10, 41);
        gp.iTile[mapIndex][i++] = new IT_DryTree(gp, 10, 40);

    }
}
