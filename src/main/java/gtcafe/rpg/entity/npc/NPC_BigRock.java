package gtcafe.rpg.entity.npc;

import java.awt.Rectangle;
import java.util.ArrayList;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.object.OBJ_Door_Iron;
import gtcafe.rpg.state.Direction;
import gtcafe.rpg.system.Sound;
import gtcafe.rpg.tile.interactive.IT_MetalPlate;

public class NPC_BigRock extends Entity {

    public static final String OBJ_NAME = "Big Rock";

    public NPC_BigRock(GamePanel gp) {
        super(gp);

        this.direction = Direction.DOWN;
        this.name = OBJ_NAME;
        this.speed = 4;

        solidArea = new Rectangle();
        solidArea.x = 2;
        solidArea.y = 6;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 44;
        solidArea.height = 40;

        getImages();
        setDialogue();
    }

    public void getImages() {
        String packagePath = "/gtcafe/rpg/assets/npc/";
        up1 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "bigrock.png", gp.tileSize, gp.tileSize);
    }

    public void update() {
        // NPC 不動
    }

    public void move(Direction direction) {
        this.direction = direction;

        checkCollision();

        if (collisionOn == false) {
            switch (direction) {
                case UP:
                    setWorldY(getWorldY() - getSpeed());
                    break;
                case DOWN:
                    setWorldY(getWorldY() + getSpeed());
                    break;
                case LEFT:
                    setWorldX(getWorldX() - getSpeed());
                    break;
                case RIGHT:
                    setWorldX(getWorldX() + getSpeed());
                    break;
            }

            detectPlate();
        }
    }

    public void detectPlate() {
        ArrayList<Entity> plateList = new ArrayList<>();
        ArrayList<Entity> rockList = new ArrayList<>();

        // Handle iTile (Plates)
        for (int i = 0; i < gp.iTile[gp.currentMap.index].size(); i++) {
            Entity e = gp.iTile[gp.currentMap.index].get(i);
            // IT_MetalPlate.OBJ_NAME based on previous code
            if (e != null && e.name != null && e.name.equals(IT_MetalPlate.OBJ_NAME)) {
                plateList.add(e);
            }
        }

        // Handle NPC (BigRock)
        for (int i = 0; i < gp.npc[gp.currentMap.index].size(); i++) {
            Entity e = gp.npc[gp.currentMap.index].get(i);
            if (e != null && e.name != null && e.name.equals(NPC_BigRock.OBJ_NAME)) {
                rockList.add(e);
            }
        }

        int count = 0;

        // Scan plate list
        for (int i = 0; i < plateList.size(); i++) {
            int xDistance = Math.abs(getWorldX() - plateList.get(i).getWorldX());
            int yDistance = Math.abs(getWorldY() - plateList.get(i).getWorldY());
            int distance = Math.max(xDistance, yDistance);

            if (distance < 8) {
                if (linkedEntity == null) {
                    linkedEntity = plateList.get(i);
                    gp.playSoundEffect(Sound.FX_UNLOCK);
                }
            }
        }

        // Count rocks on plates
        for (int i = 0; i < rockList.size(); i++) {
            NPC_BigRock rock = (NPC_BigRock) rockList.get(i);
            if (rock.linkedEntity != null) {
                count++;
            }
        }

        if (count == rockList.size()) {
            // open the door
            int mapIndex = gp.currentMap.index;
            for (int i = 0; i < gp.obj[mapIndex].size(); i++) {
                Entity obj = gp.obj[mapIndex].get(i);
                if (obj != null && obj.name != null && obj.name.equals(OBJ_Door_Iron.OBJ_NAME)) {
                    gp.obj[mapIndex].remove(i);
                    gp.playSoundEffect(Sound.FX__DOOR_OPEN);
                    // break? removing from list while iterating is risky if we don't adjust i/break
                    // Since we remove and maybe there are multiple doors?
                    // assume one door or multiple, removing all?
                    // The original code set it to null.
                    i--;
                }
            }
        }
    }

    public void setDialogue() {
        dialogues[0][0] = "It's a big rock.";
    }

    // set the action behavior for different actors
    public void setAction() {
    }

    public void speak() {
        facePlayer();
        startDialogue(this, dialogueSet);
    }

}
