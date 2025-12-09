package gtcafe.rpg.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Graphics2DUtils;
import gtcafe.rpg.entity.projectile.Projectile;
import gtcafe.rpg.state.Direction;
import gtcafe.rpg.system.Sound;

// a blueprint
public class Entity {
    // 2D Animation
    public GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage guardUp, guardDown, guardLeft, guardRight;
    public BufferedImage image, image2, image3;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);   // 碰撞偵測
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);    // Hit deteciton, be overwrite by subclass
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    protected String dialogues[] = new String[20];
    Graphics2DUtils g2Utils = new Graphics2DUtils();
    int animationSpeed = 24;
    public Entity attacker;

    // STATE
    public int worldX, worldY;
    public Direction direction = Direction.DOWN;
    public int spriteNum = 1;
    int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false;  // 暫時無敵
    public boolean attacking = false;
    public boolean alive = true;
    public boolean dying = false;
    boolean hpBarOn = false;
    public boolean onPath = false;
    public boolean knockBack = false;       // day42
    public Direction knockBackDirection;    // to avoid direction changed by player when player move quickly.
    public boolean guarding = false;
    public boolean transparent = false;
    public boolean offBalance = false;      // for parry
    public Entity loot;                 // for chest
    public boolean opened = false;      // for chest

    // COUNTER
    public int spriteCounter = 0;
    public int actionLockCounter = 0;   // To set the counter for action, to avoid quick update by FPS number.
    public int invincibleCounter = 0;
    public int shotAvailableCounter = 0;    // to avoid double shoot
    int dyingCounter = 0;
    int hpBarCounter = 0;
    int knockBackCounter = 0;               // day42
    public int guardCounter = 0;            // for parry
    public int offBalanceCounter = 0;       // for parry

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

    public int motion1_duration; // 揮動武器的速度，值代表 FPS，值越高，速度越慢
    public int motion2_duration; // 揮動武器的速度，值代表 FPS，值越高，速度越慢
    public Entity currentWeapon;
    public Entity currentShield;
    public Entity currentLight;     // 目前手持的燈具
    public Projectile projectile;   // 拋射物

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
    public int lightRadius;     // day45: light scope

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
    public int getXdistance(Entity target) { return Math.abs(worldX - target.worldX); }
    public int getYdistance(Entity target) { return Math.abs(worldY - target.worldY); }
    public int getTileDistance(Entity target) { return (getXdistance(target) + getYdistance(target))/gp.tileSize; }
    public int getGoalCol(Entity target) { return (target.worldX + target.solidArea.x) / gp.tileSize; }
    public int getGoalRow(Entity target) { return (target.worldY + target.solidArea.y) / gp.tileSize; }


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
            case UP -> nextWorldY = user.getTopY() - gp.player.speed;
            case DOWN -> nextWorldY = user.getBottomY() + gp.player.speed;
            case LEFT -> nextWorldX = user.getLeftX() - gp.player.speed;
            case RIGHT -> nextWorldX = user.getRightX() + gp.player.speed;
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
                switch (knockBackDirection) {
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
        } 
        else if (attacking == true) {
            attacking();
        } 
        else {
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

        // Parry
        if (offBalance == true) {
            offBalanceCounter++;
            if(offBalanceCounter > 60) {    // 60 FPS (1 second)
                offBalance = false;
                offBalanceCounter = 0;
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
            // switch(direction) {
            //     case UP -> image = (spriteNum == 1) ? up1 : up2;
            //     case DOWN -> image = (spriteNum == 1) ? down1 : down2;
            //     case LEFT -> image = (spriteNum == 1) ? left1 : left2;
            //     case RIGHT -> image = (spriteNum == 1) ? right1 : right2;
            //     default -> throw new IllegalArgumentException("Unexpected value: " + direction);
            // }

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

            // 顯示路徑
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

            g2.drawImage(image, tempScreenX, tempScreenY, null); // setup() has re-scale the image, we don't need pass weight and height any more.

            // reset the alpha value for next frame
            g2Utils.changeAlpha(g2, 1f);
        }
    }

    public void checkShootOrNot(int rate, int shortInterval) {
        int i = new Random().nextInt(rate);
        if (i == 0 && projectile.alive == false && shotAvailableCounter == shortInterval) {
            projectile.set(worldX, worldY, direction, true, this);

            // enable projectile or not.
            // CHECK VACANCY
            for(int ii=0; ii<gp.projectile[1].length; ii++) {
                if(gp.projectile[gp.currentMap.index][ii] == null) {
                    gp.projectile[gp.currentMap.index][ii] = projectile;
                    break;
                }
            }
            shotAvailableCounter = 0;
        }
    }

    public void checkStartChasingOrNot(Entity target, int distance, int rate, String uiMessage) {
        if(getTileDistance(target) < distance) {
            int i = new Random().nextInt(rate);
            if (i == 0) {
                onPath = true;
                if ( uiMessage != null && !uiMessage.equals("")) {
                    gp.ui.addMessage(uiMessage);
                }
            }
        }
    }

    // 隨機的追蹤
    public void checkStopChasingOrNot(Entity target, int distance, int rate, String uiMessage) {
        if(getTileDistance(target) > distance) {
            int i = new Random().nextInt(rate);
            if (i == 0) {
                onPath = false;
                if ( uiMessage != null && !uiMessage.equals("")) {
                    gp.ui.addMessage(uiMessage);
                }
            }
        }
    }

    public void getRandomDirection() {
        actionLockCounter++;

        if (actionLockCounter == 120) { // 120 fps change the direction
            Random r = new Random();
            int i = r.nextInt(100) + 1; // pick up a number from 1 to 100

            if (i <= 25) { direction = Direction.UP; }
            if (i > 25 && i <= 50) { direction = Direction.DOWN; }
            if (i > 50 && i <= 75) { direction = Direction.LEFT; }
            if (i > 75 && i <= 100) { direction = Direction.RIGHT; }
            actionLockCounter = 0;
        }
    }

    public void checkAttackOrNot(int rate, int straight, int horizontal) {

        boolean targetInRange = false;
        int xDis = getXdistance(gp.player);
        int yDis = getYdistance(gp.player);

        switch (direction) {
            case UP:
                if (gp.player.worldY < worldY && yDis < straight && xDis < horizontal) {
                    targetInRange = true;
                }
                break;
            case DOWN:
                if (gp.player.worldY > worldY && yDis < straight && xDis < horizontal) {
                    targetInRange = true;
                }
            case LEFT:
                if (gp.player.worldX < worldX && xDis < straight && yDis < horizontal) {
                    targetInRange = true;
                }
            case RIGHT:
                if (gp.player.worldX > worldX && xDis < straight && yDis < horizontal) {
                    targetInRange = true;
                }
            default:
                break;
        }

        if (targetInRange == true) {
            // Check if it initiates an attack
            int i = new Random().nextInt(rate);
            if ( i == 0 ) {
                attacking = true;
                spriteNum = 1;
                spriteCounter = 0;
                shotAvailableCounter = 0;
            }
        }

    }

    public Direction getOppositeDirection(Direction direction) {
        Direction oppositeDirection = Direction.ANY;
        switch (direction) {
            case UP -> oppositeDirection = Direction.DOWN;
            case DOWN -> oppositeDirection = Direction.UP;
            case LEFT -> oppositeDirection = Direction.RIGHT;
            case RIGHT -> oppositeDirection = Direction.LEFT;
        }
        return oppositeDirection;
    }

    // 正在攻擊的計算
    public void attacking() {
        // MAKE ATTACKING ANIMATION
        spriteCounter++;

        // show image 1 (spriteNum1): 0-5 frame
        if (spriteCounter <= motion1_duration) {
            spriteNum = 1;
        }
        // show image 2 (spriteNum2): 6-25 frame
        // 5-25 is the window of opportunity to hit the target; 
        // the shorter the window, the more difficult it is.
        // 揮動武器的速度
        if (spriteCounter > motion1_duration && spriteCounter <= motion2_duration) {
            spriteNum = 2;

            // save the current worldX, worldY and solidArea. for checking the attacking
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Adjust player's workdX/Y for the attackArea
            switch (direction) {
                case UP -> worldY -= attackArea.height; 
                case DOWN -> worldY += attackArea.height; 
                case LEFT -> worldX -= attackArea.width; 
                case RIGHT -> worldX += attackArea.width;
                default -> throw new IllegalArgumentException("Unexpected value: " + direction);
            }

            // attackArea becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;

            if (type == EntityType.MONSTER) {
                // hitting player
                if (gp.collisionChecker.checkPlayer(this) == true) {
                    damagePlayer(attack);
                } 
            }
            // Player
            else {
                // check monster collision with the updated worldX/Y and solidArea
                int monsterIndex = gp.collisionChecker.checkEntity(this, gp.monster);
                gp.player.damageMonster(monsterIndex, this, attack, currentWeapon.knockBackPower);

                // check player attack the interactive tiles
                int iTileIndex = gp.collisionChecker.checkEntity(this, gp.iTile);
                gp.player.damageInteractiveTiles(iTileIndex);

                int projectileIndex = gp.collisionChecker.checkEntity(this, gp.projectile);
                gp.player.damageProjectile(projectileIndex);
            }

            // restore position
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }

        if (spriteCounter > motion2_duration) {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    public void damagePlayer(int attack) {
        if (gp.player.invincible == false) {
            
            // we can give damage
            //  攻擊力 - Player 的防禦力
            int damage = attack - gp.player.defense;

            // Get an opposite direction of this attacker
            Direction canGuardDirection = getOppositeDirection(direction);
            
            // player can guard the attack (防禦成功，損失減少 1/3)
            if (gp.player.guarding == true && gp.player.direction == canGuardDirection) {

                // Parry
                if (gp.player.guardCounter < 10) {  // 10 Frame Window
                    damage = 0;
                    gp.playSoundEffect(Sound.FX__PARRY);
                    setKnockBack(this, gp.player, knockBackPower);
                    gp.ui.addMessage("Parry!");
                    offBalance = true;
                    spriteCounter =- 60;    // make the monster frozen effect at moment
                }
                // Normal
                else {
                    damage /= 3;    // reduce the damage
                    gp.playSoundEffect(Sound.FX__BLOCKED);
                    gp.ui.addMessage("Guarding!");
                }

            } 
            else {
                if (damage < 1) { damage = 1; }
                gp.playSoundEffect(Sound.FX_RECEIVE_DAMAGE);
            }

            if (damage != 0) {
                gp.player.transparent = true;
                setKnockBack(gp.player, this, knockBackPower);
            }

            gp.player.life -= damage;
            gp.player.invincible = true;
        }
    }

    // for chest
    public void setLoot(Entity loot) {
    }


    // call when hit monster
    // can set the condition for any type.
    public void setKnockBack(Entity target, Entity attacker, int knockBackPower) {
        this.attacker = attacker;
        target.knockBackDirection = attacker.direction;
        target.speed += knockBackPower; 
        target.knockBack = true;
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
