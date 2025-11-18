package gtcafe.rpg.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.KeyHandler;

public class Player extends Entity {
    KeyHandler keyHandler;

    // Animation
    public final static int ANIMATION_SPEED = 10;

    // camera position
    public final int screenX; 
    public final int screenY;

    public Player(GamePanel gp, KeyHandler keyHandler) {
        super(gp);

        this.keyHandler = keyHandler;

        // camera position
        screenX = gp.screenWidth / 2 - (gp.tileSize/2);
        screenY = gp.screenHight / 2 - (gp.tileSize/2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;  
        solidAreaDefaultY = solidArea.y;  

        setDefaultValues();
        getPlayerImages();
    }

    public void setDefaultValues() {
        // start position
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        
        speed = 5;
        direction = "down";
    }

    public void getPlayerImages() {
        String packagePath = "/gtcafe/rpg/assets/player/";
        up1 = setup(packagePath + "boy_up_1");
        up2 = setup(packagePath + "boy_up_2");
        down1 = setup(packagePath + "boy_down_1");
        down2 = setup(packagePath + "boy_down_2");
        left1 = setup(packagePath + "boy_left_1");
        left2 = setup(packagePath + "boy_left_2");
        right1 = setup(packagePath + "boy_right_1");
        right2 = setup(packagePath + "boy_right_2");
    }

    public void update() {
        if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
            String newDirection = null;
            if(keyHandler.upPressed) {
                newDirection = "up";
            } else if(keyHandler.downPressed) {
                newDirection = "down";
            } else if(keyHandler.leftPressed) {
                newDirection = "left";
            } else if(keyHandler.rightPressed) {
                newDirection = "right";
            }

            if (newDirection != null) {
                direction = newDirection;
                // CHECK TILE COLLISION
                collisionOn = false;
                gp.collisionChecker.checkTile(this);

                // CHECK OBJECT COLLSISION
                int objIndex = gp.collisionChecker.checkObject(this, true);
                pickUpObject(objIndex);

                // CHECK NPC COLLISION
                int npcIndex = gp.collisionChecker.checkEntity(this, gp.npc);
                interactNPC(npcIndex);

                // IF COLLISION IS FALSE, PLAYER CAN MOVE
                if (collisionOn == false) {
                    switch (direction) {
                        case "up":
                            worldY -= speed;
                            break;
                        case "down":
                            worldY += speed;
                            break;
                        case "left":
                            worldX -= speed;
                            break;
                        case "right":
                            worldX += speed;
                            break;
                    }
                }

                // ANIMATION
                spriteCounter++;
                if(spriteCounter > ANIMATION_SPEED) {
                    if (spriteNum == 1) {
                        spriteNum = 2;
                    } else if (spriteNum == 2) {
                        spriteNum = 1;
                    }
                    spriteCounter = 0;
                }
            }
        }
    }

    // OBJECT REACTION
    public void pickUpObject(int index) {
        // 999 MEANS NOT TOUCH ANY OBJECT
        if (index != 999) {
        }
    }

    public void interactNPC(int index) {
        if (index != 999) {
            System.out.println("[Player#interactNPC] You are hitting an NPC!!");
        } 
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch(direction) {
            case "up":
                image = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                image = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                image = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                image = (spriteNum == 1) ? right1 : right2;
                break;
        }
        g2.drawImage(image, screenX, screenY, null);
    }
}