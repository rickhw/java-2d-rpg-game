package gtcafe.rpg.entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;
import gtcafe.rpg.GameState;
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

        // Player 在 Screen 的起始座標, 起始後不會改動. camera position
        screenX = gp.screenWidth / 2 - (gp.tileSize/2);
        screenY = gp.screenHeight / 2 - (gp.tileSize/2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;  
        solidAreaDefaultY = solidArea.y;  

        setDefaultValues();
        getPlayerImages();
    }

    public void setDefaultValues() {
        // player 在整個世界地圖的座標起始位置
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;

        // for testing
        // worldX = gp.tileSize * 12;
        // worldY = gp.tileSize * 15;
        
        speed = 5;  // 每個 Frame 移動 5 個 pixel, 每秒移動 5 * 60 = 300 pixel / 48 = 6 tiles
        direction = Direction.DOWN;
        
        // PLAYER STATUS
        maxLife = 6;
        life = maxLife;
    }

    public void getPlayerImages() {
        String packagePath = "/gtcafe/rpg/assets/player/";
        up1 = setup(packagePath + "boy_up_1.png");
        up2 = setup(packagePath + "boy_up_2.png");
        down1 = setup(packagePath + "boy_down_1.png");
        down2 = setup(packagePath + "boy_down_2.png");
        left1 = setup(packagePath + "boy_left_1.png");
        left2 = setup(packagePath + "boy_left_2.png");
        right1 = setup(packagePath + "boy_right_1.png");
        right2 = setup(packagePath + "boy_right_2.png");
    }

    public void update() {
        if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed || keyHandler.enterPressed) {
            Direction newDirection = null;
            if(keyHandler.upPressed) {
                newDirection = Direction.UP;
            } else if(keyHandler.downPressed) {
                newDirection = Direction.DOWN;
            } else if(keyHandler.leftPressed) {
                newDirection = Direction.LEFT;
            } else if(keyHandler.rightPressed) {
                newDirection = Direction.RIGHT;
            }

            // TODO
            if (keyHandler.enterPressed == true) {
                // CHECK NPC COLLISION
                int npcIndex = gp.collisionChecker.checkEntity(this, gp.npc);
                interactNPC(npcIndex);

                // CHECKT EVENT
                gp.eventHandler.checkEvent();
            }
            gp.keyHandler.enterPressed = false;

            if (newDirection != null ) {
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

                // CHECK MONSTER COLLISION
                int monsterIndex = gp.collisionChecker.checkEntity(this, gp.monster);
                contactMonster(monsterIndex);   // player receives damage

                // CHECKT EVENT
                gp.eventHandler.checkEvent();

                gp.keyHandler.enterPressed = false;

                // IF COLLISION IS FALSE, PLAYER CAN MOVE
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
                }

                // ANIMATION for working
                spriteCounter++;
                if(spriteCounter > ANIMATION_SPEED) {
                    if (spriteNum == 1) {
                        spriteNum = 2;
                    } else if (spriteNum == 2) {
                        spriteNum = 1;
                    }
                    spriteCounter = 0;
                }
            } else {
                // ANIMATION for Standing
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

        // This needs to be outside of key if statement!
        if (invincible == true) {
            invincibleCounter++;
            if(invincibleCounter > 60) { // Frame Counter
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    private void contactMonster(int index) {
        if (index != 999) {
            System.out.println("[Player#contactMonster] You are hitting a Monster!!");
            if (invincible == false) {   
                life -= 1;  // just for testing
                invincible = true;
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
        if (index != 999) { // means player touch NPC
            System.out.println("[Player#interactNPC] You are hitting an NPC!!");
            if (gp.keyHandler.enterPressed == true) {
                gp.gameState = GameState.DIALOGUE_STATE;
                gp.npc[index].speak();
            }
        } 
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch(direction) {
            case UP -> image = (spriteNum == 1) ? up1 : up2;
            case DOWN -> image = (spriteNum == 1) ? down1 : down2;
            case LEFT -> image = (spriteNum == 1) ? left1 : left2;
            case RIGHT -> image = (spriteNum == 1) ? right1 : right2;
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }

        // Visual effect to invincible state
        if (invincible == true) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }

        g2.drawImage(image, screenX, screenY, null);

        // reset alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // DEBUG
        g2.setFont(new Font("Arial", Font.PLAIN, 26));
        g2.setColor(Color.white);
        g2.drawString("Invincible: "+invincibleCounter, 10, 400);
    }
}