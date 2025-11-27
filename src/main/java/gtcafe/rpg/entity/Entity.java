package gtcafe.rpg.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Graphics2DUtils;
import gtcafe.rpg.Sound;

// a blueprint
public class Entity {
    // 2D Animation
    public GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage image, image2, image3;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);    // Hit deteciton, be overwrite by subclass
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    String dialogues[] = new String[20];
    Graphics2DUtils g2Utils = new Graphics2DUtils();

    // STATE
    public int worldX, worldY;
    public Direction direction = Direction.DOWN;
    public int spriteNum = 1;
    int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false;  // 暫時無敵
    boolean attacking = false;
    // boolean showInfo = false;   // for debugging, show the screenX, screenY
    public boolean alive = true;
    public boolean dying = false;
    boolean hpBarOn = false;

    // COUNTER
    public int spriteCounter = 0;
    public int actionLockCounter = 0;   // To set the counter for action, to avoid quick update by FPS number.
    public int invincibleCounter = 0;
    // public int drawCounter = 0;         // for debugging, show the screenX, screenY
    public int shotAvailableCounter = 0;    // to avoid double shoot
    int dyingCounter = 0;
    int hpBarCounter = 0;

    // CHARACTER ATTRIBUTES: share player and monster
    public String name;
    public int speed;
    public int maxLife; // 最大生命值
    public int life;    // 目前生命值
    public int maxMana; // 最大法力
    public int mana;    // 現在的法力
    public int ammo;    // 現在的彈藥數 for OBJ_Rock

    public int level;
    public int strength;    // 力量
    public int dexterity;   // 敏捷
    public int attack;      // 攻擊力, 透過 getAttack() 計算
    public int defense;     // 防禦力, 透過 getDefense() 計算
    
    public int exp;
    public int nextLevelExp;
    public int coin;
    public Entity currentWeapon;
    public Entity currentShield;
    public Projectiles projectiles; // 拋射物

    // ITEM ATTRIBUTES
    public int attackValue;     // 攻擊力
    public int defenseValue;    // 防禦力
    public String description = "";
    public int useCost; // spend mana

    // TYPES
    public EntityType type; 

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public BufferedImage setup(String imagePath, int width, int height) {
        Graphics2DUtils uTools = new Graphics2DUtils();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
            image = uTools.scaleImage(image, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // overwirte by subclass
    public void setAction() {}
    // overwirte by subclass
    public void damageReaction() {}
    // overwrite by subclass
    public void use(Entity entity) {}

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
        if (this.type == EntityType.MONSTER && contactPlayer == true) {
            damagePlayer(attack);
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

        // to avoid double shoot the projectiles for next 30 frames
        if (shotAvailableCounter < 30) {
            shotAvailableCounter++;
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

            // Monster HP Bar
            if (type == EntityType.MONSTER && hpBarOn == true) {    // type 2 is monster

                double oneScale = (double) gp.tileSize / maxLife;
                double hpBarValue = oneScale * life;    // find the col length of bar

                g2.setColor(new Color(35,35,30)); 
                g2.fillRect(screenX - 1 , screenY - 16, gp.tileSize+2, 10+2);

                g2.setColor(new Color(255,0,30)); 
                g2.fillRect(screenX, screenY - 15, (int)hpBarValue, 10);

                hpBarCounter ++;

                // hpBar disapper after 5s
                if (hpBarCounter > 300) {
                    hpBarCounter = 0;
                    hpBarOn = false;
                }
            }

            // Visual effect to transparent the entity for invincible state
            if (invincible == true) {
                hpBarOn = true;
                hpBarCounter = 0;
                g2Utils.changeAlpha(g2, 0.4f);
            }
            
            // monster dying
            if (dying == true) {
                dyingAnimation(g2);
            }

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

            // reset the alpha value for next frame
            g2Utils.changeAlpha(g2, 1f);
        }
    }

    public void damagePlayer(int attack) {
        if (gp.player.invincible == false) {
            // we can give damage
            gp.playSoundEffect(Sound.FX_RECEIVE_DAMAGE);

            //  攻擊力 - Player 的防禦力
            int damage = attack - gp.player.defense;
            if (damage < 0) { damage = 0; }

            gp.player.life -= damage;
            gp.player.invincible = true;
        }
    }

    // Monster Death Effect
    private void dyingAnimation(Graphics2D g2) {
        dyingCounter++;
        int i = 5;

        if (dyingCounter <= i) { g2Utils.changeAlpha(g2, 0f); }
        if (dyingCounter > i && dyingCounter <= i*2) { g2Utils.changeAlpha(g2, 1f); }
        if (dyingCounter > i*2 && dyingCounter <= i*3) { g2Utils.changeAlpha(g2, 0f); }
        if (dyingCounter > i*3 && dyingCounter <= i*4) { g2Utils.changeAlpha(g2, 1f); }
        if (dyingCounter > i*4 && dyingCounter <= i*5) { g2Utils.changeAlpha(g2, 0f); }
        if (dyingCounter > i*5 && dyingCounter <= i*6) { g2Utils.changeAlpha(g2, 1f); }
        if (dyingCounter > i*6 && dyingCounter <= i*7) { g2Utils.changeAlpha(g2, 0f); }
        if (dyingCounter > i*7 && dyingCounter <= i*8) { g2Utils.changeAlpha(g2, 1f); }

        if (dyingCounter > i*8) {
            dyingCounter = 0;
            // dying = false;
            alive = false;
        }
    }


}
