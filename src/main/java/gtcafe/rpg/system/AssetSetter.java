package gtcafe.rpg.system;
import gtcafe.rpg.core.GameContext;

import gtcafe.rpg.entity.equipable.OBJ_Lantern;
import gtcafe.rpg.entity.monster.MON_GreenSlime;
import gtcafe.rpg.entity.monster.MON_Orc;
import gtcafe.rpg.entity.npc.NPC_Merchant;
import gtcafe.rpg.entity.npc.NPC_OldMan;
import gtcafe.rpg.entity.object.OBJ_Chest;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Door;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;
import gtcafe.rpg.entity.object.OBJ_Postion_Red;
import gtcafe.rpg.entity.object.OBJ_Tent;
import gtcafe.rpg.entity.shield.OBJ_Shield_Blue;
import gtcafe.rpg.entity.weapon.OBJ_Sword_Normal;
import gtcafe.rpg.tile.Scense;
import gtcafe.rpg.tile.interactive.IT_DryTree;

public class AssetSetter {

    GameContext context;

    public AssetSetter(GameContext context) {
        this.context = context;
    }

    // instantiate objects
    public void setObject() {
        int i = 0;
        int mapIndex = Scense.WORLD_MAP.index;
        
        context.getObj()[mapIndex][i] = new OBJ_Coin_Bronze(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 25;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 23;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Coin_Bronze(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 21;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 19;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Lantern(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 18;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 20;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Tent(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 19;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 20;


        i++;
        context.getObj()[mapIndex][i] = new OBJ_Coin_Bronze(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 26;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 21;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Sword_Normal(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 33;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 7;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Shield_Blue(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 10;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 34;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Postion_Red(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 22;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 27;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Heart(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 22;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 29;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_ManaCrystal(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 22;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 31;


        // Door
        i++;
        context.getObj()[mapIndex][i] = new OBJ_Door(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 14;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 28;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Door(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 12;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 12;

        // Door for Testing
        // i++;
        // context.getObj()[mapIndex][i] = new OBJ_Door(context);
        // context.getObj()[mapIndex][i].worldX = context.getTileSize() * 23;
        // context.getObj()[mapIndex][i].worldY = context.getTileSize() * 14;

        // i++;
        // context.getObj()[mapIndex][i] = new OBJ_Door(context);
        // context.getObj()[mapIndex][i].worldX = context.getTileSize() * 23;
        // context.getObj()[mapIndex][i].worldY = context.getTileSize() * 16;

        // 寶箱
        i++;
        context.getObj()[mapIndex][i] = new OBJ_Chest(context);
        context.getObj()[mapIndex][i].setLoot(new OBJ_Key(context));
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 30;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 28;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Chest(context);
        context.getObj()[mapIndex][i].setLoot(new OBJ_Heart(context));
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 12;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 8;


        // Postion
        i++;
        context.getObj()[mapIndex][i] = new OBJ_Postion_Red(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 27;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 16;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Postion_Red(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 30;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 12;

        i++;
        context.getObj()[mapIndex][i] = new OBJ_Postion_Red(context);
        context.getObj()[mapIndex][i].worldX = context.getTileSize() * 21;
        context.getObj()[mapIndex][i].worldY = context.getTileSize() * 20;

        // for Testing
        // context.getObj()[0] = new OBJ_Door(context);
        // context.getObj()[0].worldX = context.getTileSize() * 25;
        // context.getObj()[0].worldY = context.getTileSize() * 19;

        // context.getObj()[1] = new OBJ_Door(context);
        // context.getObj()[1].worldX = context.getTileSize() * 23;
        // context.getObj()[1].worldY = context.getTileSize() * 25;
    }

    public void setNPC() {
        int i = 0;
        int mapIndex = Scense.WORLD_MAP.index;

        context.getNpc()[mapIndex][i] = new NPC_OldMan(context, "Steve");
        context.getNpc()[mapIndex][i].worldX = context.getTileSize() * 18;
        context.getNpc()[mapIndex][i].worldY = context.getTileSize() * 20;

        // i++;
        // context.getNpc()[mapIndex][i] = new NPC_OldMan(context, "Erica");
        // context.getNpc()[mapIndex][i].worldX = context.getTileSize() * 28;
        // context.getNpc()[mapIndex][i].worldY = context.getTileSize() * 20;


        // NPC in Scense.STORE
        i = 0;
        mapIndex = Scense.STORE.index;
        context.getNpc()[mapIndex][i] = new NPC_Merchant(context);
        context.getNpc()[mapIndex][i].worldX = context.getTileSize() * 12;
        context.getNpc()[mapIndex][i].worldY = context.getTileSize() * 7;

        // i++;
        // context.getNpc()[mapIndex][i] = new NPC_OldMan(context);
        // context.getNpc()[mapIndex][i].worldX = context.getTileSize() * 10;
        // context.getNpc()[mapIndex][i].worldY = context.getTileSize() * 10;

        // context.getNpc()[3] = new NPC_OldMan(context);
        // context.getNpc()[3].worldX = context.getTileSize() * 21;
        // context.getNpc()[3].worldY = context.getTileSize() * 11;

        // context.getNpc()[4] = new NPC_OldMan(context);
        // context.getNpc()[4].worldX = context.getTileSize() * 21;
        // context.getNpc()[4].worldY = context.getTileSize() * 31;
    }

    public void setMonster() {
        int i = 0;
        int mapIndex = Scense.WORLD_MAP.index;

        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 23;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 36;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 23;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 37;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 23;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 38;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 23;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 39;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 37;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 9;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 12;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 32;

        // for testing
        // i++;
        // context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        // context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 11;
        // context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 8;

        // i++;
        // context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        // context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 11;
        // context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 9;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 33;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 39;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 34;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 39;

        i++;
        context.getMonster()[mapIndex][i] = new MON_GreenSlime(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 35;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 39;


        i++;
        context.getMonster()[mapIndex][i] = new MON_Orc(context);
        context.getMonster()[mapIndex][i].worldX = context.getTileSize() * 12;
        context.getMonster()[mapIndex][i].worldY = context.getTileSize() * 33;
    }

    public void setInteractiveTiles() {
        int i = 0;
        int mapIndex = Scense.WORLD_MAP.index;

        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 27, 12);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 28, 12);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 29, 12);
        // context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 30, 12);    // postion
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 31, 12);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 32, 12);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 33, 12);

        // for testing
        // context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 30, 20);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 30, 21);
        // context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 30, 22);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 14, 40);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 13, 40);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 13, 41);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 12, 41);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 11, 41);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 10, 41);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 10, 40);

        // chest
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 30, 29);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 29, 29);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 28, 29);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 27, 29);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 27, 28);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 27, 27);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 26, 27);
        context.getInteractiveTile()[mapIndex][i++] = new IT_DryTree(context, 25, 27);

    }
}
