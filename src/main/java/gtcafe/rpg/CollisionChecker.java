package gtcafe.rpg;

import gtcafe.rpg.entity.Entity;

/**
 * Handles collision detection for entities with game tiles.
 */
public class CollisionChecker {
    GamePanel gp;

    /**
     * Constructs a CollisionChecker with a reference to the GamePanel.
     * @param gp The GamePanel instance, providing access to game-wide data like tile size and tile manager.
     */
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
            case "up":
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
            case "down":
                // Calculate the row the entity would be in if it moved 'speed' pixels down
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                // Get the two tiles that the entity's bottom edge would potentially collide with
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                // Check if either of these tiles has collision enabled
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                // Calculate the column the entity would be in if it moved 'speed' pixels left
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                // Get the two tiles that the entity's left edge would potentially collide with
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                // Check if either of these tiles has collision enabled
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                // Calculate the column the entity would be in if it moved 'speed' pixels right
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                // Get the two tiles that the entity's right edge would potentially collide with
                tileNum1 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                // Check if either of these tiles has collision enabled
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

}
