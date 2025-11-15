package gtcafe.rpg;

import gtcafe.rpg.entity.Entity;
import java.awt.Rectangle;

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

        // Predict the entity's future position and check for collision with tiles
        switch (entity.direction) {
            case UP:
                // Calculate the row the entity would be in if it moved 'speed' pixels up
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                // Get the two tiles that the entity's top edge would potentially collide with
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                // Check if either of these tiles has collision enabled
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case DOWN:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case LEFT:
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case RIGHT:
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    /**
     * Checks if the entity is colliding with any objects and returns the index of the object.
     * This check is performed for player entities to handle interactions.
     *
     * @param entity The entity to check for collisions.
     * @param isPlayer True if the entity is the player.
     * @return The index of the collided object if the entity is a player and a collision occurs, otherwise -1.
     */
    public int checkObject(Entity entity, boolean isPlayer) {
        int index = -1;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null) {
                // Get entity's solid area position and predict its next move
                Rectangle entityPredictedSolidArea = new Rectangle(
                    entity.worldX + entity.solidArea.x,
                    entity.worldY + entity.solidArea.y,
                    entity.solidArea.width,
                    entity.solidArea.height
                );

                // Get object's solid area position
                Rectangle objectSolidArea = new Rectangle(
                    gp.obj[i].worldX + gp.obj[i].solidArea.x,
                    gp.obj[i].worldY + gp.obj[i].solidArea.y,
                    gp.obj[i].solidArea.width,
                    gp.obj[i].solidArea.height
                );

                switch (entity.direction) {
                    case UP: entityPredictedSolidArea.y -= entity.speed; break;
                    case DOWN: entityPredictedSolidArea.y += entity.speed; break;
                    case LEFT: entityPredictedSolidArea.x -= entity.speed; break;
                    case RIGHT: entityPredictedSolidArea.x += entity.speed; break;
                }

                if (entityPredictedSolidArea.intersects(objectSolidArea)) {
                    if (gp.obj[i].collision) {
                        entity.collisionOn = true;
                    }
                    if (isPlayer) {
                        index = i;
                    }
                }
            }
        }
        return index;
    }
}
