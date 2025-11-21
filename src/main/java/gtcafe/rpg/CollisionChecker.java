package gtcafe.rpg;

import gtcafe.rpg.entity.Entity;

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
                    // System.out.println("[CollisionChecker#checkTile] up collision!");
                }
                break;

            case DOWN:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                    // System.out.println("[CollisionChecker#checkTile] down collision!");
                }
                break;

            case LEFT:
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                    // System.out.println("[CollisionChecker#checkTile] left collision!");
                }
                break;

            case RIGHT:
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileManager.tiles[tileNum1].collision || gp.tileManager.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                    // System.out.println("[CollisionChecker#checkTile] right collision!");
                }
                break;
        }
    }

    // OBJECT REACTION 
    public int checkObject(Entity entity, boolean player) {

        int index = 999;    // ??

        // scan objs
        for(int i=0; i<gp.obj.length; i++) {
            if(gp.obj[i] != null) {

                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Get the object's solid area position
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch (entity.direction) {
                    case UP:
                        entity.solidArea.y -= entity.speed;
                        // intersect() help check only limit object num.
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkObject] up collision");   
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i; 
                            }
                        }
                        break;
                    case DOWN:
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkObject] down collision");   
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i; 
                            }
                        }
                        break;
                    case LEFT:
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkObject] left collision");   
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i; 
                            }
                         }
                       break;
                    case RIGHT:
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkObject] right collision");   
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i; 
                            }
                         }
                        break;
                }

                // reset
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
                
            }
        }

        return index;
    }

    // for NPC OR MONSTER
    public int checkEntity(Entity entity, Entity[] target) {

        int index = 999;    // ??

        // scan objs
        for(int i=0; i<target.length; i++) {
            if(target[i] != null) {

                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Get the object's solid area position
                target[i].solidArea.x = target[i].worldX + target[i].solidArea.x;
                target[i].solidArea.y = target[i].worldY + target[i].solidArea.y;

                switch (entity.direction) {
                    case UP:
                        entity.solidArea.y -= entity.speed;
                        // intersect() help check only limit object num.
                        if (entity.solidArea.intersects(target[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkEntity] up collision");   
                            entity.collisionOn = true;
                            index = i; 
                        }
                        break;
                    case DOWN:
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(target[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkEntity] down collision");   
                            entity.collisionOn = true;
                            index = i; 
                        }
                        break;
                    case LEFT:
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(target[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkEntity] left collision");   
                            entity.collisionOn = true;
                            index = i; 
                         }
                       break;
                    case RIGHT:
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(target[i].solidArea)) {
                            // System.out.println("[CollisionChecker#checkEntity] right collision");   
                            entity.collisionOn = true;
                            index = i; 
                         }
                        break;
                }

                // reset
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }

        // System.out.printf("EntityCollision, index: [%s]\n", index);   

        return index;
    }

    // day13: check the npc if hit player
    public void checkPlayer(Entity entity) {

        // Get entity's solid area position
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;

        // Get the object's solid area position
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        switch (entity.direction) {
            case UP:
                entity.solidArea.y -= entity.speed;
                // intersect() help check only limit object num.
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    // System.out.println("[CollisionChecker#checkPlayer] up collision");   
                    entity.collisionOn = true;
                }
                break;
            case DOWN:
                entity.solidArea.y += entity.speed;
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    // System.out.println("[CollisionChecker#checkPlayer] down collision");   
                    entity.collisionOn = true;
                }
                break;
            case LEFT:
                entity.solidArea.x -= entity.speed;
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    // System.out.println("[CollisionChecker#checkPlayer] left collision");   
                    entity.collisionOn = true;
                }
                break;
            case RIGHT:
                entity.solidArea.x += entity.speed;
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    // System.out.println("[CollisionChecker#checkPlayer] right collision");   
                    entity.collisionOn = true;
                }
                break;
        }

        // reset
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
    }
}
