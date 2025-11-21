package gtcafe.rpg.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Utils;

// a blueprint
public class Entity {
    GamePanel gp;

    // Position
    public int worldX, worldY;
    public int speed;

    // Animation
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public Direction direction;

    // Sprite animation
    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public int actionLockCounter = 0;   // To set the counter for action, to avoid quick update by FPS number.

    String dialogues[] = new String[20];
    int dialogueIndex = 0;

    // CHARACTER STATUS: share player and monster
    public int maxLife;
    public int life;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public BufferedImage setup(String imagePath) {
        Utils uTools = new Utils();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTools.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void setAction() {

    }

    public void speak() {
        if (dialogues[dialogueIndex] == null) {
            dialogueIndex = 0; // go back to index zero to avoid the NPE.
        }

        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;

        // make the NPC talks to player by face.
        switch (gp.player.direction) {
            case UP -> direction = Direction.DOWN;
            case DOWN -> direction = Direction.UP;
            case LEFT -> direction = Direction.RIGHT;
            case RIGHT -> direction = Direction.LEFT;
            default -> throw new IllegalArgumentException("Unexpected value: " + gp.player.direction);
        }
    }

    public void update() {
        setAction();

        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkObject(this, false);
        gp.collisionChecker.checkPlayer(this);

        // IF COLLISION IS FALSE, ENTITY CAN MOVE
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

        // ANIMATION
        spriteCounter++;
        if(spriteCounter > Player.ANIMATION_SPEED) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX * gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                    
            switch(direction) {
                case UP -> image = (spriteNum == 1) ? up1 : up2;
                case DOWN -> image = (spriteNum == 1) ? down1 : down2;
                case LEFT -> image = (spriteNum == 1) ? left1 : left2;
                case RIGHT -> image = (spriteNum == 1) ? right1 : right2;
                default -> throw new IllegalArgumentException("Unexpected value: " + direction);
            }
                    
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
