package gtcafe.rpg.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.KeyHandler;
import gtcafe.rpg.Sound;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyHandler;

    // Animation
    private final int ANIMATION_SPEED = 10;

    // camera position
    public final int screenX; 
    public final int screenY;

    public int hasKey = 0; // day8-2: object reaction

    public Player(GamePanel gp, KeyHandler keyHandler) {
        this.gp = gp;
        this.keyHandler = keyHandler;

        // camera position
        screenX = gp.screenWidth / 2 - (gp.tileSize/2);
        screenY = gp.screenHight / 2 - (gp.tileSize/2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;    // day8-1
        solidAreaDefaultY = solidArea.y;    // day8-1

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
        up1 = setup("/gtcafe/rpg/assets/player/boy_up_1.png");
        up2 = setup("/gtcafe/rpg/assets/player/boy_up_2.png");
        down1 = setup("/gtcafe/rpg/assets/player/boy_down_1.png");
        down2 = setup("/gtcafe/rpg/assets/player/boy_down_2.png");
        left1 = setup("/gtcafe/rpg/assets/player/boy_left_1.png");
        left2 = setup("/gtcafe/rpg/assets/player/boy_left_2.png");
        right1 = setup("/gtcafe/rpg/assets/player/boy_right_1.png");
        right2 = setup("/gtcafe/rpg/assets/player/boy_right_2.png");
    }

    public BufferedImage setup(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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
                // check tile collision
                collisionOn = false;
                gp.collisionChecker.checkTile(this);

                // CHECK OBJECT COLLSISION
                int objIndex = gp.collisionChecker.checktObject(this, true);    // day8-2
                pickUpObject(objIndex);

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
            }
    
            // animation 
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

    // day8-2 object reaction: start
    public void pickUpObject(int index) {
        // 999 MEANS NOT TOUCH ANY OBJECT
        if (index != 999) {
            // gp.obj[index] = null;
            String objName = gp.obj[index].name;

            switch (objName) {
                case "Key":
                    gp.playSE(Sound.FX_COIN); // day9-2
                    hasKey ++;
                    gp.obj[index] = null; // make key disappear
                    gp.ui.showMessage("You got a key!"); // day10-3
                    System.out.println("HasKey: " + hasKey);
                    break;
                case "Door":
                    if (hasKey > 0) {
                        gp.playSE(Sound.FX_UNLOCK); // day9-2
                        gp.obj[index] = null;
                        hasKey--;
                        gp.ui.showMessage("You opened the door!"); // day10-3
                    } else {
                        gp.ui.showMessage("You need a key!");  // day10-3
                    }
                    System.out.println("HasKey: " + hasKey);
                    break;
                // day9-1 start
                case "Boots":
                    gp.playSE(Sound.FX_POWER_UP); // day9-2
                    speed += 2;
                    gp.obj[index] = null; // clean object
                    gp.ui.showMessage("Speed up!!");  // day10-3
                    System.out.println("Got Boots, speed up!!");
                    break;
                // day9-1 end

                // day10-3
                case "Chest":
                    gp.ui.gameFinished = true;
                    gp.stopMusic();
                    gp.playSE(Sound.MUSIC__FANFARE);
                    break;
                // day10-3
            }
        }
    }
    // day8-2 end


    public void draw(Graphics2D g2) {
        // g2.setColor(Color.white);
        // g2.fillRect(x, y, gp.tileSize, gp.tileSize);

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

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);    }

}