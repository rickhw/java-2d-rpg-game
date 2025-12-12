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
                    worldY -= speed;
                    break;
                case DOWN:
                    worldY += speed;
                    break;
                case LEFT:
                    worldX -= speed;
                    break;
                case RIGHT:
                    worldX += speed;
                    break;
            }

            detectPlate();
        }
    }

    public void detectPlate() {
        ArrayList<Entity> plateList = new ArrayList<>();
        ArrayList<Entity> rockList = new ArrayList<>();
        
        // Create a plate list
        for(int i=0; i<gp.iTile[1].length; i++) {
            if (gp.iTile[gp.currentMap.index][i] != null 
                    && gp.iTile[gp.currentMap.index][i].name != null 
                    && gp.iTile[gp.currentMap.index][i].name.equals(IT_MetalPlate.OBJ_NAME)) {
                plateList.add(gp.iTile[gp.currentMap.index][i]);                
            }
        }

        // Create a rock list
        for(int i=0; i<gp.npc[1].length; i++) {
            if (gp.npc[gp.currentMap.index][i] != null 
                    && gp.npc[gp.currentMap.index][i].name != null
                    && gp.npc[gp.currentMap.index][i].name.equals(NPC_BigRock.OBJ_NAME)) {
                rockList.add(gp.npc[gp.currentMap.index][i]);                
            }
        }

        int count = 0;

        // Scan the plate list
        for(int i=0; i<plateList.size(); i++) {
            int xDistance = Math.abs(worldX - plateList.get(i).worldX);
            int yDistance = Math.abs(worldY - plateList.get(i).worldY);
            int distance = Math.max(xDistance, yDistance);

            if (distance < 8) {
                if(linkedEntity == null) {
                    linkedEntity = plateList.get(i);
                    gp.playSoundEffect(Sound.FX_UNLOCK);
                }
            }
            else {
                if(linkedEntity == plateList.get(i)) {
                    linkedEntity = null;
                }
            }
        }

        // Scan the rock list
        for(int i=0; i<rockList.size(); i++) {
            // Count the rocks on the plate
            if (rockList.get(i).linkedEntity != null) {
                count++;
            }
        }

        // If all the rocks area on the plates, the iron door opens
        if (count == rockList.size()) {
            for (int i=0; i<gp.obj[1].length; i++) {
                if (gp.obj[gp.currentMap.index][i] != null 
                        && gp.obj[gp.currentMap.index][i].name.equals(OBJ_Door_Iron.OBJ_NAME)) {
                    gp.obj[gp.currentMap.index][i] = null;
                    gp.playSoundEffect(Sound.FX__DOOR_OPEN);
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
