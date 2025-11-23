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

        attackArea.width = 36;
        attackArea.height = 36;

        setDefaultValues();
        getPlayerImages();
        getPlayerAttackImage();
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
        up1 = setup(packagePath + "boy_up_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "boy_up_2.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "boy_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "boy_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "boy_left_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "boy_left_2.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "boy_right_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "boy_right_2.png", gp.tileSize, gp.tileSize);
    }

    public void getPlayerAttackImage() {
        String packagePath = "/gtcafe/rpg/assets/player_attack/";
        attackUp1 = setup(packagePath + "boy_attack_up_1.png", gp.tileSize, gp.tileSize*2);
        attackUp2 = setup(packagePath + "boy_attack_up_2.png", gp.tileSize, gp.tileSize*2);
        attackDown1 = setup(packagePath + "boy_attack_down_1.png", gp.tileSize, gp.tileSize*2);
        attackDown2 = setup(packagePath + "boy_attack_down_2.png", gp.tileSize, gp.tileSize*2);
        attackLeft1 = setup(packagePath + "boy_attack_left_1.png", gp.tileSize*2, gp.tileSize);
        attackLeft2 = setup(packagePath + "boy_attack_left_2.png", gp.tileSize*2, gp.tileSize);
        attackRight1 = setup(packagePath + "boy_attack_right_1.png", gp.tileSize*2, gp.tileSize);
        attackRight2 = setup(packagePath + "boy_attack_right_2.png", gp.tileSize*2, gp.tileSize);
    }

    public void update() {

        // attack animation
        if (attacking == true) {
            attacking();
        }

        else if (keyHandler.upPressed || keyHandler.downPressed ||
                keyHandler.leftPressed || keyHandler.rightPressed ||
                keyHandler.enterPressed) {

            if(keyHandler.upPressed) {
                direction = Direction.UP;
            } else if(keyHandler.downPressed) {
                direction = Direction.DOWN;
            } else if(keyHandler.leftPressed) {
                direction = Direction.LEFT;
            } else if(keyHandler.rightPressed) {
                direction = Direction.RIGHT;
            }

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

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (collisionOn == false && keyHandler.enterPressed == false) {
                switch (direction) {
                    case UP -> worldY -= speed;
                    case DOWN -> worldY += speed;
                    case LEFT -> worldX -= speed;
                    case RIGHT -> worldX += speed;
                    default -> throw new IllegalArgumentException("Unexpected value: " + direction);
                }
            }

            // RESET enterPressed
            gp.keyHandler.enterPressed = false;

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

        // This needs to be outside of key if statement!
        if (invincible == true) {
            invincibleCounter++;
            if(invincibleCounter > 60) { // Frame Counter
                invincible = false;
                invincibleCounter = 0;
            }
        }

        if (drawCounter > 60) {
            drawCounter = 0;
            showInfo = true;
        } else {
            drawCounter++;
            showInfo = false;
        }
    }

    private void attacking() {
        // MAKE ATTACKING ANIMATION
        spriteCounter++;

        // show image 1 (spriteNum1): 0-5 frame
        if (spriteCounter <= 5) {
            spriteNum = 1;
        }
        // show image 2 (spriteNum2): 6-25 frame
        if (spriteCounter > 5 && spriteCounter <= 25) {
            spriteNum = 2;

            // save the current worldX, worldY and solidArea. for checking the attacking
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Adjust player's workdX/Y for the attackArea
            switch (direction) {
                case UP: worldY -= attackArea.height; break;
                case DOWN: worldY += attackArea.height; break;
                case LEFT: worldX -= attackArea.width; break;
                case RIGHT: worldX += attackArea.width; break;
            }

            // attackArea becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;

            // check monster collision with the updated worldX/Y and solidArea
            int monsterIndex = gp.collisionChecker.checkEntity(this, gp.monster);
            damageMonster(monsterIndex);

            // restore position
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;

        }

        if (spriteCounter > 25) {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    public void damageMonster(int index) {
        if (index != 999) {
            System.out.println("Player hiting the monster!!");

            Entity monster = gp.monster[index];
            // give some damge
            if (monster.invincible == false) {
                monster.life -= 2;
                monster.invincible = true;
                if(monster.life <= 0) {
                    gp.monster[index] = null;
                }
            }
        } else {
            System.out.println("Player hiting miss!!");
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

        if (gp.keyHandler.enterPressed == true) {
            if (index != 999) { // means player touch NPC
                System.out.println("[Player#interactNPC] You are hitting an NPC!!");
                if (gp.keyHandler.enterPressed == true) {
                    gp.gameState = GameState.DIALOGUE_STATE;
                    gp.npc[index].speak();
                }
            } else {
                // player doesn't get NPC index
                System.out.println("[Player#interactNPC] You are attacking!");
                attacking = true;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (showInfo && gp.debugMode) {
            System.out.printf("[Player#draw] screenX: [%s], screenY: [%s], worldX: [%s], worldY: [%s]\n", screenX, screenY, worldX, worldY);
        }

        // for specify cases
        int tempScreenX = screenX;
        int tempScreenY = screenY;

        // Depends on direction and attacking status to pick specific image up.
        switch(direction) {
            case UP:
                if (attacking == false) image = (spriteNum == 1) ? up1 : up2;
                if (attacking == true) {
                    // exception case for up image.
                    tempScreenY = screenY - gp.tileSize;
                    image = (spriteNum == 1) ? attackUp1 : attackUp2;
                }
                break;
            case DOWN:
                if (attacking == false) image = (spriteNum == 1) ? down1 : down2;
                if (attacking == true) image = (spriteNum == 1) ? attackDown1 : attackDown2;
                break;
            case LEFT:
                if (attacking == false) image = (spriteNum == 1) ? left1 : left2;
                if (attacking == true) {
                    // exception case for up image.
                    tempScreenX = screenX - gp.tileSize;
                    image = (spriteNum == 1) ? attackLeft1 : attackLeft2;
                }
                break;
            case RIGHT:
                if (attacking == false) image = (spriteNum == 1) ? right1 : right2;
                if (attacking == true) image = (spriteNum == 1) ? attackRight1 : attackRight2;
                break;
        }

        // Visual effect to invincible state
        if (invincible == true) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }

        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // reset alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // DEBUG
        if (gp.debugMode) {
            g2.setFont(new Font("Arial", Font.PLAIN, 26));
            g2.setColor(Color.white);
            g2.drawString("Invincible: " + invincibleCounter, 10, 400);
        }
    }
}
