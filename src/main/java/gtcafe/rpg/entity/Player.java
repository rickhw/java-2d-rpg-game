package gtcafe.rpg.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;
import gtcafe.rpg.KeyHandler;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyHandler;

    // camera position
    public final int screenX;
    public final int screenY;

    int hasKey = 0;

    private final int ANIMATION_SPEED = 10;

    public Player(GamePanel gp, KeyHandler keyHandler) {
        this.gp = gp;
        this.keyHandler = keyHandler;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImages();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 5;
        direction = Direction.DOWN;
    }

    public void getPlayerImages() {
        up1 = loadImage("/gtcafe/rpg/assets/player/boy_up_1.png");
        up2 = loadImage("/gtcafe/rpg/assets/player/boy_up_2.png");
        down1 = loadImage("/gtcafe/rpg/assets/player/boy_down_1.png");
        down2 = loadImage("/gtcafe/rpg/assets/player/boy_down_2.png");
        left1 = loadImage("/gtcafe/rpg/assets/player/boy_left_1.png");
        left2 = loadImage("/gtcafe/rpg/assets/player/boy_left_2.png");
        right1 = loadImage("/gtcafe/rpg/assets/player/boy_right_1.png");
        right2 = loadImage("/gtcafe/rpg/assets/player/boy_right_2.png");
    }

    private BufferedImage loadImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void update() {
        // We only move and animate if a key is being pressed
        if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
            handleInputAndMove();
            updateAnimation();
        }
    }

    private void handleInputAndMove() {
        if (keyHandler.upPressed) {
            direction = Direction.UP;
        } else if (keyHandler.downPressed) {
            direction = Direction.DOWN;
        } else if (keyHandler.leftPressed) {
            direction = Direction.LEFT;
        } else if (keyHandler.rightPressed) {
            direction = Direction.RIGHT;
        }

        // Check for collisions
        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        int objIndex = gp.collisionChecker.checkObject(this, true);
        interactWithObject(objIndex);

        // If no collision, player can move
        if (!collisionOn) {
            switch (direction) {
                case UP: worldY -= speed; break;
                case DOWN: worldY += speed; break;
                case LEFT: worldX -= speed; break;
                case RIGHT: worldX += speed; break;
            }
        }
    }

    private void updateAnimation() {
        spriteCounter++;
        if (spriteCounter > ANIMATION_SPEED) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void interactWithObject(int index) {
        if (index != -1) {
            // Let the object handle the interaction
            gp.obj[index].interact(this);

            // Check if the object should be removed after interaction
            if (gp.obj[index].toBeRemoved) {
                gp.obj[index] = null;
            }
        }
    }

    public int getKeyCount() {
        return hasKey;
    }

    public void incrementKeyCount() {
        hasKey++;
        // TODO: Add a UI update to show key count
    }

    public void decrementKeyCount() {
        hasKey--;
        // TODO: Add a UI update to show key count
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch (direction) {
            case UP:
                image = (spriteNum == 1) ? up1 : up2;
                break;
            case DOWN:
                image = (spriteNum == 1) ? down1 : down2;
                break;
            case LEFT:
                image = (spriteNum == 1) ? left1 : left2;
                break;
            case RIGHT:
                image = (spriteNum == 1) ? right1 : right2;
                break;
        }
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}