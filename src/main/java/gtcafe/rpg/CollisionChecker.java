package gtcafe.rpg;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.state.Direction;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Checks for tile collisions for a given entity.
     * This method predicts the entity's next position based on its current direction and speed,
     * then checks if the tiles at that predicted position are solid.
     * If a collision is detected, the entity's 'collisionOn' flag is set to true.
     *
     * @param entity The entity to check for collisions.
     */
    public void checkTile(Entity entity) {
        // Calculate the entity's solid area boundaries in world coordinates
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Determine the current tile grid columns and rows that the entity's solid area occupies
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2; // To store the IDs of the two tiles being checked for collision
        int mapIndex = gp.currentMap.index;

        // Use a temporal direction when it's being knockbacked
        Direction direction = entity.direction;
        if (entity.knockBack == true) {
            direction = entity.knockBackDirection;
        }

        // Predict the entity's future position and check for collision with tiles
        switch (direction) {
            case UP:
                // Calculate the row the entity would be in if it moved 'speed' pixels up
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                // Get the two tiles that the entity's top edge would potentially collide with
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityTopRow];
                // Check if either of these tiles has collision enabled
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case DOWN:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case LEFT:
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case RIGHT:
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }

        // if (entity.collisionOn = true && gp.debugMode) {
        //     System.out.println("[Collision#checkPlayer] Entity hitting tiles.");
        // }
    }

    // OBJECT REACTION
    public int checkObject(Entity entity, boolean player) {

        int index = 999;    // ??
        int mapIndex = gp.currentMap.index;

        // scan objs
        for(int i=0; i<gp.obj[1].length; i++) {
            if(gp.obj[mapIndex][i] != null) {

                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Get the object's solid area position
                gp.obj[mapIndex][i].solidArea.x = gp.obj[mapIndex][i].worldX + gp.obj[mapIndex][i].solidArea.x;
                gp.obj[mapIndex][i].solidArea.y = gp.obj[mapIndex][i].worldY + gp.obj[mapIndex][i].solidArea.y;

                switch (entity.direction) {
                    case UP -> entity.solidArea.y -= entity.speed;
                    case DOWN -> entity.solidArea.y += entity.speed;
                    case LEFT -> entity.solidArea.x -= entity.speed;
                    case RIGHT -> entity.solidArea.x += entity.speed;
                }
                // intersect() help check only limit object num.
                if (entity.solidArea.intersects(gp.obj[mapIndex][i].solidArea)) {
                    if (gp.obj[mapIndex][i].collision) { entity.collisionOn = true; }
                    if (player) { index = i; }
                }
                // reset
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[mapIndex][i].solidArea.x = gp.obj[mapIndex][i].solidAreaDefaultX;
                gp.obj[mapIndex][i].solidArea.y = gp.obj[mapIndex][i].solidAreaDefaultY;
            }
        }

        return index;
    }

    // for NPC OR MONSTER
    public int checkEntity(Entity entity, Entity[][] target) {

        int index = 999;    // ??
        int mapIndex = gp.currentMap.index;

        // scan objs
        for(int i=0; i<target[1].length; i++) {
            if(target[mapIndex][i] != null) {

                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Get the object's solid area position
                target[mapIndex][i].solidArea.x = target[mapIndex][i].worldX + target[mapIndex][i].solidArea.x;
                target[mapIndex][i].solidArea.y = target[mapIndex][i].worldY + target[mapIndex][i].solidArea.y;

                // Use a temporal direction when it's being knockbacked
                Direction direction = entity.direction;
                if (entity.knockBack == true) {
                    direction = entity.knockBackDirection;
                }
                switch (direction) {
                    case UP -> entity.solidArea.y -= entity.speed;
                    case DOWN -> entity.solidArea.y += entity.speed;
                    case LEFT -> entity.solidArea.x -= entity.speed;
                    case RIGHT -> entity.solidArea.x += entity.speed;
                }
                // intersect() help check only limit object num.
                if (entity.solidArea.intersects(target[mapIndex][i].solidArea)) {
                    if (target[mapIndex][i] != entity) {
                        entity.collisionOn = true;
                        index = i;
                        // if (gp.debugMode) System.out.println("[Collision#checkPlayer] Entity hitting entity, monster/NPC/Player");
                    }
                }

                // reset
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[mapIndex][i].solidArea.x = target[mapIndex][i].solidAreaDefaultX;
                target[mapIndex][i].solidArea.y = target[mapIndex][i].solidAreaDefaultY;
            }
        }

        // System.out.printf("EntityCollision, index: [%s]\n", index);

        return index;
    }

    // check the npc if hit player
    public boolean checkPlayer(Entity entity) {

        boolean contactPlayer = false;

        // Get entity's solid area position
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;

        // Get the object's solid area position
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        switch (entity.direction) {
            case UP -> entity.solidArea.y -= entity.speed;
            case DOWN -> entity.solidArea.y += entity.speed;
            case LEFT -> entity.solidArea.x -= entity.speed;
            case RIGHT -> entity.solidArea.x += entity.speed;
        }

        // intersect() help check only limit object num.
        if (entity.solidArea.intersects(gp.player.solidArea)) {
            // System.out.println("[CollisionChecker#checkPlayer] up collision");
            entity.collisionOn = true;
            contactPlayer = true; // for monster attack player
            // if (gp.debugMode) System.out.println("[Collision#checkPlayer] Entity hitting player");
        }
        // reset
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }
}
