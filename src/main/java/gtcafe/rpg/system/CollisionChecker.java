package gtcafe.rpg.system;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.state.Direction;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Checks for tile collisions for a given entity.
     * This method predicts the entity's next position based on its current
     * direction and speed,
     * then checks if the tiles at that predicted position are solid.
     * If a collision is detected, the entity's 'collisionOn' flag is set to true.
     *
     * @param entity The entity to check for collisions.
     */
    public void checkTile(Entity entity) {
        // Calculate the entity's solid area boundaries in world coordinates
        int entityLeftWorldX = entity.getWorldX() + entity.solidArea.x;
        int entityRightWorldX = entity.getWorldX() + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.getWorldY() + entity.solidArea.y;
        int entityBottomWorldY = entity.getWorldY() + entity.solidArea.y + entity.solidArea.height;

        // Determine the current tile grid columns and rows that the entity's solid area
        // occupies
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
                entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.tileSize;
                // Get the two tiles that the entity's top edge would potentially collide with
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityTopRow];
                // Check if either of these tiles has collision enabled
                if (gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case DOWN:
                entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityBottomRow];
                if (gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case LEFT:
                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityLeftCol][entityBottomRow];
                if (gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case RIGHT:
                entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[mapIndex][entityRightCol][entityBottomRow];
                if (gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case ANY:
                break;
        }

        // if (entity.collisionOn = true && gp.debugMode) {
        // System.out.println("[Collision#checkPlayer] Entity hitting tiles.");
        // }
    }

    // OBJECT REACTION
    public int checkObject(Entity entity, boolean player) {

        int index = 999; // ??
        int mapIndex = gp.currentMap.index;

        // Use a temporal direction when it's being knockbacked
        Direction direction = entity.direction;
        if (entity.knockBack == true) {
            direction = entity.knockBackDirection;
        }

        // scan objs
        for (int i = 0; i < gp.obj[mapIndex].size(); i++) {
            Entity target = gp.obj[mapIndex].get(i);
            if (target != null) {

                // Get entity's solid area position
                entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
                entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;

                // Get the object's solid area position
                target.solidArea.x = target.getWorldX() + target.solidArea.x;
                target.solidArea.y = target.getWorldY() + target.solidArea.y;

                switch (direction) {
                    case UP -> entity.solidArea.y -= entity.getSpeed();
                    case DOWN -> entity.solidArea.y += entity.getSpeed();
                    case LEFT -> entity.solidArea.x -= entity.getSpeed();
                    case RIGHT -> entity.solidArea.x += entity.getSpeed();
                    case ANY -> {
                    }
                }
                // intersect() help check only limit object num.
                if (entity.solidArea.intersects(target.solidArea)) {
                    if (target.collision) {
                        entity.collisionOn = true;
                    }
                    if (player) {
                        index = i;
                    }
                }
                // reset
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target.solidArea.x = target.solidAreaDefaultX;
                target.solidArea.y = target.solidAreaDefaultY;
            }
        }

        return index;
    }

    // for NPC OR MONSTER
    public int checkEntity(Entity entity, java.util.ArrayList<Entity>[] target) {

        int index = 999; // ??
        int mapIndex = gp.currentMap.index;

        // scan objs
        for (int i = 0; i < target[mapIndex].size(); i++) {
            Entity targetEntity = target[mapIndex].get(i);

            if (targetEntity != null) {

                // Get entity's solid area position
                entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
                entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;

                // Get the object's solid area position
                targetEntity.solidArea.x = targetEntity.getWorldX() + targetEntity.solidArea.x;
                targetEntity.solidArea.y = targetEntity.getWorldY() + targetEntity.solidArea.y;

                // Use a temporal direction when it's being knockbacked
                Direction direction = entity.direction;
                if (entity.knockBack == true) {
                    direction = entity.knockBackDirection;
                }
                switch (direction) {
                    case UP -> entity.solidArea.y -= entity.getSpeed();
                    case DOWN -> entity.solidArea.y += entity.getSpeed();
                    case LEFT -> entity.solidArea.x -= entity.getSpeed();
                    case RIGHT -> entity.solidArea.x += entity.getSpeed();
                    case ANY -> {
                    }
                }
                // intersect() help check only limit object num.
                if (entity.solidArea.intersects(targetEntity.solidArea)) {
                    if (targetEntity != entity) {
                        entity.collisionOn = true;
                        index = i;
                        // if (gp.debugMode) System.out.println("[Collision#checkPlayer] Entity hitting
                        // entity, monster/NPC/Player");
                    }
                }

                // reset
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                targetEntity.solidArea.x = targetEntity.solidAreaDefaultX;
                targetEntity.solidArea.y = targetEntity.solidAreaDefaultY;
            }
        }

        // System.out.printf("EntityCollision, index: [%s]\n", index);

        return index;
    }

    // check the npc if hit player
    public boolean checkPlayer(Entity entity) {

        boolean contactPlayer = false;

        // Get entity's solid area position
        entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
        entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;

        // Get the object's solid area position
        gp.player.solidArea.x = gp.player.getWorldX() + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.getWorldY() + gp.player.solidArea.y;

        switch (entity.direction) {
            case UP -> entity.solidArea.y -= entity.getSpeed();
            case DOWN -> entity.solidArea.y += entity.getSpeed();
            case LEFT -> entity.solidArea.x -= entity.getSpeed();
            case RIGHT -> entity.solidArea.x += entity.getSpeed();
            case ANY -> {
            }
        }

        // intersect() help check only limit object num.
        if (entity.solidArea.intersects(gp.player.solidArea)) {
            // System.out.println("[CollisionChecker#checkPlayer] up collision");
            entity.collisionOn = true;
            contactPlayer = true; // for monster attack player
            // if (gp.debugMode) System.out.println("[Collision#checkPlayer] Entity hitting
            // player");
        }
        // reset
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }
}
