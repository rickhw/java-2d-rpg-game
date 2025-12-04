package gtcafe.rpg.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Graphics2DUtils;
import gtcafe.rpg.Sound;
import gtcafe.rpg.state.Direction;

// a blueprint
public class Entity {
    // 2D Animation
    public GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage image, image2, image3;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);   // 碰撞偵測
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);    // Hit deteciton, be overwrite by subclass
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    protected String dialogues[] = new String[20];
    Graphics2DUtils g2Utils = new Graphics2DUtils();
    int animationSpeed = 24;

    // STATE
    public int worldX, worldY;
    public Direction direction = Direction.DOWN;
    public int spriteNum = 1;
    int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false;  // 暫時無敵
    boolean attacking = false;
    public boolean alive = true;
    public boolean dying = false;
    boolean hpBarOn = false;
    public boolean onPath = false;
    public boolean knockBack = false;       // day42

    // COUNTER
    public int spriteCounter = 0;
    public int actionLockCounter = 0;   // To set the counter for action, to avoid quick update by FPS number.
    public int invincibleCounter = 0;
    public int shotAvailableCounter = 0;    // to avoid double shoot
    int dyingCounter = 0;
    int hpBarCounter = 0;
    int knockBackCounter = 0;               // day42

    // CHARACTER ATTRIBUTES: share player and monster
    public String name;
    public int defaultSpeed;
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
    public Projectile projectile; // 拋射物

    // ITEM ATTRIBUTES
    public int value;
    public int attackValue;     // 攻擊力
    public int defenseValue;    // 防禦力
    public String description = "";
    public int useCost; // spend mana
    public int price;
    public int knockBackPower = 0;  // for different weapon only.   // day42
    public boolean stackable = false;       // day43
    public int amount = 1;                  // day43

    // INVENTORY
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

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

    public int getLeftX() { return worldX + solidArea.x; }
    public int getRightX() { return worldX + solidArea.x + solidArea.width; }
    public int getTopY() { return worldY + solidArea.y; }
    public int getBottomY() { return worldY + solidArea.y + solidArea.height; }
    public int getCol() { return (worldX + solidArea.x) / gp.tileSize; }
    public int getRow() { return (worldY + solidArea.y) / gp.tileSize; }

    // overwirte by subclass
    public void setAction() {}
    // overwirte by subclass
    public void damageReaction() {}
    // overwrite by subclass
    public boolean use(Entity entity) { return false; }
    
    // overwrite by subclass
    // for OBSTACLE
    public void interact() {}

    public int getDetected(Entity user, Entity target[][], String targetName) {
        int index = 999;

        // Check the surrounding object
        int nextWorldX = user.getLeftX();
        int nextWorldY = user.getTopY();

        System.out.printf("[Entity#getDetected] user.direction: [%s]\n", user.direction);
        System.out.printf("[Entity#getDetected] before: nextWorldX: [%s], nextWorldY: [%s]\n", nextWorldX, nextWorldY);
        switch (user.direction) {
            case UP -> nextWorldY = user.getTopY() - gp.tileSize;         // TODO: FIXME
            case DOWN -> nextWorldY = user.getBottomY() + 1;
            case LEFT -> nextWorldX = user.getLeftX() - 1;
            case RIGHT -> nextWorldX = user.getRightX() + 1;
        }

        System.out.printf("[Entity#getDetected] after: nextWorldX: [%s], nextWorldY: [%s]\n", nextWorldX, nextWorldY);
        int col = nextWorldX / gp.tileSize;
        int row = nextWorldY / gp.tileSize;

        // Detect the target, ex: Door
        for (int i=0; i<target[1].length; i++) {    // TODO
            if (target[gp.currentMap.index][i] != null) {
                if(target[gp.currentMap.index][i].getCol() == col &&
                    target[gp.currentMap.index][i].getRow() == row &&
                    target[gp.currentMap.index][i].name.equals(targetName) )
                {
                    index = i;
                    break;
                }
            }
        }

        return index;

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

    public void checkCollision() {
        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkObject(this, false);
        gp.collisionChecker.checkEntity(this, gp.npc);
        gp.collisionChecker.checkEntity(this, gp.monster);
        gp.collisionChecker.checkEntity(this, gp.iTile);
        boolean contactPlayer = gp.collisionChecker.checkPlayer(this);

        // handle: monster attack player
        if (this.type == EntityType.MONSTER && contactPlayer == true) {
            damagePlayer(attack);
        }
    }

    public void update() {

        // 擊退效果
        if(knockBack == true) {
            
            checkCollision();

            if (collisionOn == true) {
                knockBackCounter = 0;
                knockBack = false;
                speed = defaultSpeed;
            } else if (collisionOn == false) {
                // move entity
                switch (direction) {
                    case UP -> worldY -= speed;
                    case DOWN -> worldY += speed;
                    case LEFT -> worldX -= speed;
                    case RIGHT -> worldX += speed;
                    default -> throw new IllegalArgumentException("Unexpected value: " + direction);
                }

                knockBackCounter++;
                if (knockBackCounter == 10) {   // knockBack distance
                    knockBackCounter = 0;
                    knockBack = false;
                    speed = defaultSpeed;
                }
            }
        } else {
            setAction();
            checkCollision();

            // IF COLLISION IS FALSE, ENTITY CAN MOVE
            if (collisionOn == false) {
                switch (direction) {
                    case UP -> worldY -= speed;
                    case DOWN -> worldY += speed;
                    case LEFT -> worldX -= speed;
                    case RIGHT -> worldX += speed;
                    default -> throw new IllegalArgumentException("Unexpected value: " + direction);
                }
            }
        }

        // ANIMATION
        spriteCounter++;
        if(spriteCounter > animationSpeed) {
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

            // 顯示底色
            if (onPath == true) {
                gp.g2.setColor(new Color(255, 0,75));
                gp.g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
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

            g2.drawImage(image, screenX, screenY, null); // setup() has re-scale the image, we don't need pass weight and height any more.

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


    public void checkDrop() {
    }

    // Handle the dropItem action
    public void dropItem(Entity droppedItem) {
        int mapIndex = gp.currentMap.index;
        for (int i=0; i< gp.obj[1].length; i++ ) {
            if (gp.obj[mapIndex][i] == null) {
                gp.obj[mapIndex][i] = droppedItem;
                // the dead monster's worldX/Y
                gp.obj[mapIndex][i].worldX = worldX; 
                gp.obj[mapIndex][i].worldY = worldY;
                break; // break the check.
            }
        }
    }

    // 粒子 (Particle) 效果使用
    // always overwrite by subclass
    public Color getParticleColor() {
        Color color = null;
        return color;
    }

    public int getParticleSize() {
        int size = 0; // 6 pixels
        return size;
    }

    // how fast it can fly
    public int getParticleSpeed() {
        int speed = 0;
        return speed;
    }

    public int getParitcleMaxLife() {
        int maxLife = 0;
        return maxLife;
    }
    
    /**
     * 
     * @param generator 像是 Fireball, Rock, DryTree 都是粒子特效的產生者
     * @param target 粒子特效的主要目標, DryTree, Player, Monster ... etc.
     */
    public void generateParticle(Entity generator, Entity target) {
        Color color = generator.getParticleColor();
        int size = generator.getParticleSize();
        int speed = generator.getParticleSpeed();
        int maxLife = generator.getParitcleMaxLife();
        
        // 產生四個粒子
        Particle p1 = new Particle(gp, target, color, size, speed, maxLife, -2, -1);
        Particle p2 = new Particle(gp, target, color, size, speed, maxLife, 2, -1);
        Particle p3 = new Particle(gp, target, color, size, speed, maxLife, -2, 1);
        Particle p4 = new Particle(gp, target, color, size, speed, maxLife, 2, 1);
        
        gp.particleList.add(p1);
        gp.particleList.add(p2);
        gp.particleList.add(p3);
        gp.particleList.add(p4);
    }

    public void searchPath(int goalCol, int goalRow) {
        int startCol = (worldX + solidArea.x) / gp.tileSize;
        int startRow = (worldY + solidArea.y) / gp.tileSize;

        if (startCol == goalCol && startRow == goalRow) {
            onPath = false;
            return;
        }

        gp.pathFinder.setNodes(startCol, startRow, goalCol, goalRow, this);

        if (gp.pathFinder.search() == true) {
            
            // Next worldX & worldY
            if (gp.pathFinder.pathList.size() > 0) {
                int nextX = gp.pathFinder.pathList.get(0).col * gp.tileSize;
                int nextY = gp.pathFinder.pathList.get(0).row * gp.tileSize;

                // Entity's solidArea position
                int enLeftX = worldX + solidArea.x;
                int enRightX = worldX + solidArea.x + solidArea.width;
                int enTopY = worldY + solidArea.y;
                int enBottomY = worldY + solidArea.y + solidArea.height;

                // Based on the current NPC's position,
                // find out the relative direction of the next node
                if (enTopY > nextY && enLeftX >= nextX && enRightX < nextX + gp.tileSize) {
                    direction = Direction.UP;
                } else if (enTopY < nextY && enLeftX >= nextX && enRightX < nextX + gp.tileSize) {
                    direction = Direction.DOWN;
                } else if (enTopY >= nextY && enBottomY < nextY + gp.tileSize) {
                    // left or right
                    if (enLeftX > nextX) { direction = Direction.LEFT; }
                    if (enLeftX < nextX) { direction = Direction.RIGHT; }
                } else if (enTopY > nextY && enLeftX > nextX) {
                    // up or left
                    direction = Direction.UP;
                    checkCollision();
                    if (collisionOn == true) { direction = Direction.LEFT; }
                } else if (enTopY > nextY && enLeftX < nextX) {
                    // up or right
                    direction = Direction.UP;
                    checkCollision();
                    if (collisionOn == true) { direction = Direction.RIGHT; }
                } else if (enTopY < nextY && enLeftX > nextX) {
                    // down or left
                    direction = Direction.DOWN;
                    checkCollision();
                    if (collisionOn == true) { direction = Direction.LEFT; }
                } else if (enTopY < nextY && enLeftX < nextX) {
                    // down or right
                    direction = Direction.DOWN;
                    checkCollision();
                    if (collisionOn == true) { direction = Direction.RIGHT; }
                }

                // System.out.printf("[Entity#searchPath] entity: [%s], direction: [%s]\n", name, direction);

                // If reaches the goal, stop the search
                // int nextCol = gp.pathFinder.pathList.get(0).col;
                // int nextRow = gp.pathFinder.pathList.get(0).row;
                // if (nextCol == goalCol && nextRow == goalRow) {
                    // onPath = false; // Let it reach the goal
                // }
            }
        }
    }
}
