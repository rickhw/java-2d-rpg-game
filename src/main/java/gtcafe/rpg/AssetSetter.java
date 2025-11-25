package gtcafe.rpg;

import gtcafe.rpg.entity.NPC_OldMan;
import gtcafe.rpg.monster.MON_GreenSlime;

public class AssetSetter {

    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    // instantiate objects
    public void setObject() {
        // for Testing
        // gp.obj[0] = new OBJ_Door(gp);
        // gp.obj[0].worldX = gp.tileSize * 21;
        // gp.obj[0].worldY = gp.tileSize * 22;
        // gp.obj[1] = new OBJ_Door(gp);
        // gp.obj[1].worldX = gp.tileSize * 23;
        // gp.obj[1].worldY = gp.tileSize * 25;
    }

    public void setNPC() {
        gp.npc[0] = new NPC_OldMan(gp);
        gp.npc[0].worldX = gp.tileSize * 18;
        gp.npc[0].worldY = gp.tileSize * 20;

        gp.npc[1] = new NPC_OldMan(gp);
        gp.npc[1].worldX = gp.tileSize * 28;
        gp.npc[1].worldY = gp.tileSize * 20;


        // For Testing
        gp.npc[2] = new NPC_OldMan(gp);
        gp.npc[2].worldX = gp.tileSize * 10;
        gp.npc[2].worldY = gp.tileSize * 11;

        gp.npc[3] = new NPC_OldMan(gp);
        gp.npc[3].worldX = gp.tileSize * 10;
        gp.npc[3].worldY = gp.tileSize * 10;

        // gp.npc[3] = new NPC_OldMan(gp);
        // gp.npc[3].worldX = gp.tileSize * 21;
        // gp.npc[3].worldY = gp.tileSize * 11;

        // gp.npc[4] = new NPC_OldMan(gp);
        // gp.npc[4].worldX = gp.tileSize * 21;
        // gp.npc[4].worldY = gp.tileSize * 31;
    }

    public void setMonster() {
        int i = 0;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 23;
        gp.monster[i].worldY = gp.tileSize * 36;

        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 23;
        gp.monster[i].worldY = gp.tileSize * 37;

        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 23;
        gp.monster[i].worldY = gp.tileSize * 38;

        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 23;
        gp.monster[i].worldY = gp.tileSize * 39;

        // for testing
        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 11;
        gp.monster[i].worldY = gp.tileSize * 8;

        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 11;
        gp.monster[i].worldY = gp.tileSize * 9;

        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 33;
        gp.monster[i].worldY = gp.tileSize * 39;

        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 34;
        gp.monster[i].worldY = gp.tileSize * 39;

        i++;
        gp.monster[i] = new MON_GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 35;
        gp.monster[i].worldY = gp.tileSize * 39;

    }
}
