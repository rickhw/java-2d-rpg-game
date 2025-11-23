package gtcafe.rpg.entity;

import java.awt.AlphaComposite;
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
    // 2D Animation
    GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage image, image2, image3;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);    // Hit deteciton, be overwrite by subclass
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    String dialogues[] = new String[20];

    // STATE
    public int worldX, worldY;
    public Direction direction = Direction.DOWN;
    public int spriteNum = 1;
    int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false;  // 暫時無敵
    boolean attacking = false;
    boolean showInfo = false;   // for debugging, show the screenX, screenY

    // COUNTER
    public int spriteCounter = 0;
    public int actionLockCounter = 0;   // To set the counter for action, to avoid quick update by FPS number.
    public int invincibleCounter = 0;
    public int drawCounter = 0;         // for debugging, show the screenX, screenY

    // CHARACTER ATTRIBUTES: share player and monster
    public int type; // 0: player, 1: npc, 2: monster
    public String name;
    public int speed;
    public int maxLife;
    public int life;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public BufferedImage setup(String imagePath, int width, int height) {
        Utils uTools = new Utils();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
            image = uTools.scaleImage(image, width, height);
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
        gp.collisionChecker.checkEntity(this, gp.npc);
        gp.collisionChecker.checkEntity(this, gp.monster);
        boolean contactPlayer = gp.collisionChecker.checkPlayer(this);

        // handle: monster attack player
        if (this.type == 2 && contactPlayer == true) {
            if (gp.player.invincible == false) {
                // we can give damage
                gp.player.life -= 1;
                gp.player.invincible = true;
            }
        }

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

        // Keep the invincible state
        if (invincible == true) {
            invincibleCounter++;
            if(invincibleCounter > 40) { // Default Frame Counter
                invincible = false;
                invincibleCounter = 0;
            }
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

            // Visual effect to transparent the entity for invincible state
            if (invincible == true) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            }

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

            // reset the alpha value for next frame
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
}
