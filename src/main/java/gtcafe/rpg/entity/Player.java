package gtcafe.rpg.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;
import gtcafe.rpg.GameState;
import gtcafe.rpg.KeyHandler;
import gtcafe.rpg.Sound;
import gtcafe.rpg.object.OBJ_Axe;
import gtcafe.rpg.object.OBJ_Fireball;
import gtcafe.rpg.object.OBJ_Key;
import gtcafe.rpg.object.OBJ_Shield_Wood;

public class Player extends Entity {
    KeyHandler keyHandler;

    // Animation
    public final static int ANIMATION_SPEED = 10;

    // camera position
    public final int screenX;
    public final int screenY;

    public boolean attackCanceled = false;

    // INVENTORY
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

    public Player(GamePanel gp, KeyHandler keyHandler) {
        super(gp);

        this.keyHandler = keyHandler;

        // Player 在 Screen 的起始座標, 起始後不會改動. camera position
        screenX = gp.screenWidth / 2 - (gp.tileSize/2);
        screenY = gp.screenHeight / 2 - (gp.tileSize/2);

        // SOLID AREA
        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // ATTACK AREA
        attackArea.width = 36;
        attackArea.height = 36;

        setDefaultValues();
        getPlayerImages();
        getPlayerAttackImage();
        setItems();
    }

    public void setDefaultValues() {

        setDefaultPosition();;

        speed = 5;  // 每個 Frame 移動 5 個 pixel, 每秒移動 5 * 60 = 300 pixel / 48 = 6 tiles

        // PLAYER STATUS
        level = 1;
        maxLife = 6;
        maxMana = 4;
        ammo = 10;          // for Demo the Rock
        restoreLifeAndMana();

        strength = 1;       // the more strength he has, the more damage he gives.
        dexterity = 1;      // the more dexterity the has, the less damage he receives.
        exp = 0;
        nextLevelExp = 5;
        coin = 0;
        // currentWeapon = new OBJ_Sword_Normal(gp);
        currentWeapon = new OBJ_Axe(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        projectiles = new OBJ_Fireball(gp);
        // projectiles = new OBJ_Rock(gp);

        attack = getAttack();       // 計算攻擊力, 由 strength and weapon 決定
        defense = getDefense();     // 計算防禦力, 由 dexterity and shield 決定
    }


    public void setDefaultPosition() {
        // player 在整個世界地圖的座標起始位置
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;

        // for testing the interior map
        // worldX = gp.tileSize * 12;
        // worldY = gp.tileSize * 13;

        direction = Direction.DOWN; 
    }

    public void restoreLifeAndMana() {
        life = maxLife;
        mana = maxMana;
        invincible = false;
    }

    public void setItems() {
        inventory.clear();  // for restart/retry the game
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
    }

    // 計算防禦力: 考慮盾牌以及敏捷
    public int getDefense() {
        return defense = dexterity * currentShield.defenseValue;
    }

    public int getAttack() {
        attackArea = currentWeapon.attackArea;
        return attack = strength * currentWeapon.attackValue;
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

        if (currentWeapon.type == EntityType.SWORD) {
            attackUp1 = setup(packagePath + "boy_attack_up_1.png", gp.tileSize, gp.tileSize*2);
            attackUp2 = setup(packagePath + "boy_attack_up_2.png", gp.tileSize, gp.tileSize*2);
            attackDown1 = setup(packagePath + "boy_attack_down_1.png", gp.tileSize, gp.tileSize*2);
            attackDown2 = setup(packagePath + "boy_attack_down_2.png", gp.tileSize, gp.tileSize*2);
            attackLeft1 = setup(packagePath + "boy_attack_left_1.png", gp.tileSize*2, gp.tileSize);
            attackLeft2 = setup(packagePath + "boy_attack_left_2.png", gp.tileSize*2, gp.tileSize);
            attackRight1 = setup(packagePath + "boy_attack_right_1.png", gp.tileSize*2, gp.tileSize);
            attackRight2 = setup(packagePath + "boy_attack_right_2.png", gp.tileSize*2, gp.tileSize);
        }
        if (currentWeapon.type == EntityType.AXE) {
            attackUp1 = setup(packagePath + "boy_axe_up_1.png", gp.tileSize, gp.tileSize*2);
            attackUp2 = setup(packagePath + "boy_axe_up_2.png", gp.tileSize, gp.tileSize*2);
            attackDown1 = setup(packagePath + "boy_axe_down_1.png", gp.tileSize, gp.tileSize*2);
            attackDown2 = setup(packagePath + "boy_axe_down_2.png", gp.tileSize, gp.tileSize*2);
            attackLeft1 = setup(packagePath + "boy_axe_left_1.png", gp.tileSize*2, gp.tileSize);
            attackLeft2 = setup(packagePath + "boy_axe_left_2.png", gp.tileSize*2, gp.tileSize);
            attackRight1 = setup(packagePath + "boy_axe_right_1.png", gp.tileSize*2, gp.tileSize);
            attackRight2 = setup(packagePath + "boy_axe_right_2.png", gp.tileSize*2, gp.tileSize);
        }
        
    }

    public void update() {

        // attack animation
        if (attacking == true) {
            attacking();
        }

        else if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed ||
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

            // CHECK INTERACTIVE TILES
            int iTileIndex = gp.collisionChecker.checkEntity(this, gp.iTile);

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

            if (keyHandler.enterPressed == true && attackCanceled == false) {
                gp.playSoundEffect(Sound.FX_SWING_WEAPON);
                attacking = true;
                spriteCounter = 0;
            }
            attackCanceled = false;
            gp.keyHandler.enterPressed = false;

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
        } 
        else {
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

        // check if can shoot the projectiles
        // 1. 按下發射按鈕
        // 2. 拋射物目前沒有發射
        // 3. 拋射物目前還沒有畫出來 (30 FPS)
        // 4. Player 目前的魔力值足夠發射 (根據 Mana 值以及 Projectiles 需要消耗的值判斷)
        if (gp.keyHandler.shotKeyPressed == true && projectiles.alive == false && 
                shotAvailableCounter == 30 && projectiles.haveResource(this) == true) {
            // SET DEFAULT COORDINATES, DIRECTION AND USER
            projectiles.set(worldX, worldY, direction, true, this);

            // SUBTRACT THE COST (MANA, AMMO 彈藥, etc.)
            projectiles.subtractResource(this);
            
            // ADD IT TO THE LIST
            gp.projectilesList.add(projectiles);

            shotAvailableCounter = 0;   // reset counter once player shoot.

            gp.playSoundEffect(Sound.FX__BURNING);
        }

        // This needs to be outside of key if statement!
        if (invincible == true) {
            invincibleCounter++;
            if(invincibleCounter > 60) { // Frame Counter
                invincible = false;
                invincibleCounter = 0;
            }
        }

        // to avoid double shoot the projectiles for next 30 frames
        if (shotAvailableCounter < 30) {
            shotAvailableCounter++;
        }

        // Check the life value to avoid grant to maxLife
        // such player pick the heart.
        if (life > maxLife) { life = maxLife; }
        if (mana > maxMana) { mana = maxMana; }
       
        // Check if game over
        if (life <= 0) {
            gp.gameState = GameState.GAME_OVER_STATE;
            gp.ui.commandNum = -1;
            gp.stopBackgroundMusic();
            gp.playSoundEffect(Sound.FX__GAME_OVER);
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        // if (showInfo && gp.debugMode) {
        //     System.out.printf("[Player#draw] screenX: [%s], screenY: [%s], worldX: [%s], worldY: [%s]\n", screenX, screenY, worldX, worldY);
        // }

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
            g2Utils.changeAlpha(g2, 0.3f);
        }

        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // reset alpha
        g2Utils.changeAlpha(g2, 1f);

        // DEBUG
        // if (gp.debugMode) {
        //     g2.setFont(new Font("Arial", Font.PLAIN, 26));
        //     g2.setColor(Color.white);
        //     g2.drawString("Invincible: " + invincibleCounter, 5, 400);
        // }
    }

    // Player 跟地圖上的物件的互動
    public void pickUpObject(int index) {
        int mapIndex = gp.currentMap.value;
        // 999 MEANS NOT TOUCH ANY OBJECT
        if (index != 999) {
            // PICKUP ONLY ITEMS, ex: Coin
            if (gp.obj[mapIndex][index].type == EntityType.PICKUPONLY) {
                gp.obj[mapIndex][index].use(this);
                gp.obj[mapIndex][index] = null;
            } 
            // INVENTORY ITEMS
            else {
                String text;
                if (inventory.size() != maxInventorySize) {
                    inventory.add(gp.obj[mapIndex][index]);
                    gp.playSoundEffect(Sound.FX_COIN);
                    text = "Got a " + gp.obj[mapIndex][index].name + "!";
                } else {
                    text = "You cannot carry any more!";
                }
                gp.ui.addMessage(text);
                gp.obj[mapIndex][index] = null;
            }
        }
    }

    // Player 跟 NPC 互動
    public void interactNPC(int index) {
        int mapIndex = gp.currentMap.value;
        if (gp.keyHandler.enterPressed == true) {
            if (index != 999) { // means player touch NPC
                System.out.println("[Player#interactNPC] You are hitting an NPC!!");
                attackCanceled = true;
                gp.gameState = GameState.DIALOGUE_STATE;
                gp.npc[mapIndex][index].speak();
            } 
        }
    }

    // 正在攻擊的計算
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
            damageMonster(monsterIndex, attack);

            // check player attack the interactive tiles
            int iTileIndex = gp.collisionChecker.checkEntity(this, gp.iTile);
            damageInteractiveTiles(iTileIndex);

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

    // 計算 Player 毀壞 Interactive Tiles 的邏輯
    private void damageInteractiveTiles(int i) {
        int mapIndex = gp.currentMap.value;
        if (i != 999                                        // 1. Tile Index 是否在合理位置
                && gp.iTile[mapIndex][i].destructible == true         // 2. 判斷 Tiles 是否已經宣告成可摧毀的物件
                && gp.iTile[mapIndex][i].isCorrectItem(this) == true  // 3. 判斷目前 Entity (Player) 的武器，是否可以摧毀 Tiles
                && gp.iTile[mapIndex][i].invincible == false          // 4. 是否在暫時無敵狀態
            ) { 
            gp.iTile[mapIndex][i].playSoundEffect();
            gp.iTile[mapIndex][i].life --;
            gp.iTile[mapIndex][i].invincible = true;

            // PARTICAL 粒子效果
            generateParticle(gp.iTile[mapIndex][i], gp.iTile[mapIndex][i]);

            if (gp.iTile[mapIndex][i].life == 0) {
                gp.iTile[mapIndex][i] = gp.iTile[mapIndex][i].getDestroyedForm();
            }
        }
    }

    // 計算 Player 攻擊怪物的值
    public void damageMonster(int index, int attack) {
        int mapIndex = gp.currentMap.value;
        if (index != 999) {
            System.out.println("Player is hiting the monster!!");

            Entity monster = gp.monster[mapIndex][index];
            // give some damge
            if (monster.invincible == false) {
                gp.playSoundEffect(Sound.FX_HIT_MONSTER);

                int damage = attack - monster.defense;
                if (damage < 0) { damage = 1; }

                monster.life -= damage;
                gp.ui.addMessage(damage + " damage!");
                monster.invincible = true;
                monster.damageReaction();

                // handling monster dying
                if(monster.life <= 0) {
                    monster.dying = true;
                    exp += monster.exp;

                    gp.ui.addMessage("Killed the " + monster.name + "!");
                    gp.ui.addMessage("Exp +" + monster.exp + "!");

                    checkLevelUp();
                }
            }
        } else {
            System.out.println("Player hiting miss!!");
        }
    }

    // 計算 Player 被 Monster 攻擊後生命值的損失
    public void contactMonster(int index) {
        int mapIndex = gp.currentMap.value;
        if (index != 999) {
            System.out.println("[Player#contactMonster] Monster are attacking Player!!");
            if (invincible == false && gp.monster[mapIndex][index].dying == false) {
                gp.playSoundEffect(Sound.FX_RECEIVE_DAMAGE);
                
                // Monster 攻擊力 - Player 的防禦力
                int damage = gp.monster[mapIndex][index].attack - defense;
                if (damage < 0) { damage = 1; }

                life -= damage;
                invincible = true;
            }
        }
    }

    // 檢查是否提升等級
    private void checkLevelUp() {
        if (exp >= nextLevelExp) {
            System.out.printf("[Player#checkLevelUp] exp: [%s], nextLevelExp: [%s]\n", exp, nextLevelExp);
            level++;
            nextLevelExp = nextLevelExp * 2;
            maxLife += 2; // one heart
            maxMana += 1; // one mana
            strength++;
            dexterity++;
            attack = getAttack();
            defense = getDefense();

            gp.playSoundEffect(Sound.FX__LEVELUP);

            gp.gameState = GameState.DIALOGUE_STATE;
            gp.ui.currentDialogue = "You are level #" + level + " now!\n"
                + "You feel stronger!";
        }
    }

    // 選擇 Inventory 裡的東西
    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot();

        if (itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);
            if (selectedItem.type == EntityType.SWORD || selectedItem.type == EntityType.AXE) {
                currentWeapon = selectedItem;
                attack = getAttack();
                getPlayerAttackImage();
            }
            if (selectedItem.type == EntityType.SHIELD) {
                currentShield= selectedItem;
                defense = getDefense();
            }
            if (selectedItem.type == EntityType.CONSUMABLE) {
                selectedItem.use(this);
                inventory.remove(itemIndex);
            }
        }
    }

}
