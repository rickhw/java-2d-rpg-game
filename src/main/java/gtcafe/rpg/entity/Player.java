package gtcafe.rpg.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.equipable.OBJ_Lantern;
import gtcafe.rpg.entity.object.OBJ_Key;
import gtcafe.rpg.entity.projectile.OBJ_Fireball;
import gtcafe.rpg.entity.shield.OBJ_Shield_Wood;
import gtcafe.rpg.entity.weapon.OBJ_Sword_Normal;
import gtcafe.rpg.state.Direction;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.system.KeyHandler;
import gtcafe.rpg.system.Sound;
import gtcafe.rpg.tile.Map;

public class Player extends Entity {
    KeyHandler keyHandler;

    // Animation
    public final static int STANDING_ANIMATION_SPEED = 30;  // 越大越慢
    public final static int WALKING_ANIMATION_SPEED = 12;

    // camera position
    public final int screenX;
    public final int screenY;

    public boolean attackCanceled = false;
    public boolean lightUpdated = false;

    public Player(GamePanel gp, KeyHandler keyHandler) {
        super(gp);

        this.keyHandler = keyHandler;

        // Player 在 Screen 的起始座標, 起始後不會改動. camera position
        screenX = gp.screenWidth / 2 - (gp.tileSize/2);
        screenY = gp.screenHeight / 2 - (gp.tileSize/2);

        // SOLID AREA
        solidArea = new Rectangle(8, 16, (gp.tileSize * 4) / 5, (gp.tileSize * 3) / 5);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // ATTACK AREA
        attackArea.width = 36;
        attackArea.height = 36;

        setDefaultValues();
    }

    public void setDefaultValues() {

        setDefaultPosition();;

        defaultSpeed = 5;
        speed = defaultSpeed;  // 每個 Frame 移動 5 個 pixel, 每秒移動 5 * 60 = 300 pixel / 48 = 6 tiles

        // PLAYER STATUS
        level = 1;
        maxLife = 6;
        maxMana = 4;
        ammo = 10;          // for Demo the Rock
        restoreStatus();

        strength = 1;       // the more strength he has, the more damage he gives.
        dexterity = 1;      // the more dexterity the has, the less damage he receives.
        exp = 0;
        nextLevelExp = 5;
        coin = 1000;
        currentWeapon = new OBJ_Sword_Normal(gp);
        // currentWeapon = new OBJ_Pickaxe(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        currentLight = new OBJ_Lantern(gp);
        projectile = new OBJ_Fireball(gp);
        // projectiles = new OBJ_Rock(gp);

        attack = getAttack();       // 計算攻擊力, 由 strength and weapon 決定
        defense = getDefense();     // 計算防禦力, 由 dexterity and shield 決定

        getImages();
        getAttackImage();
        getGuardImage();
        setDialogue();
        setItems();
    }

    public void setDefaultPosition() {
        // player 在整個世界地圖的座標起始位置
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;

        // for testing the interior map
        // indoor 外面
        // worldX = gp.tileSize * 10;
        // worldY = gp.tileSize * 41;

        // 地下城入口        
        // worldX = gp.tileSize * 12;
        // worldY = gp.tileSize * 10;

        // // 地下城 B2 入口  
        // worldX = gp.tileSize * 25;
        // worldY = gp.tileSize * 32;

        // 地下城 B1 進入 B2的 入口
        // gp.currentMap = Map.DONGEON01;
        // worldX = gp.tileSize * 9;
        // worldY = gp.tileSize * 9;

        // Dongeon02 treasure 前
        gp.currentMap = Map.DONGEON02;
        worldX = gp.tileSize * 25;
        worldY = gp.tileSize * 9;


        direction = Direction.DOWN; 
    }

    public void restoreStatus() {
        life = maxLife;
        mana = maxMana;
        invincible = false;
        transparent = false;
        speed = defaultSpeed;

        attacking = false;
        guarding = false;
        knockBack = false;
        lightUpdated = true;
    }

    public void setItems() {
        inventory.clear();  // for restart/retry the game
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
        inventory.add(new OBJ_Lantern(gp));
    }

    // current: currentWeapon or currentShield
    public int getCurrentSlot(Entity current) {
        int slot = 0;
        for(int i=0; i<inventory.size(); i++) {
            if(inventory.get(i) == current) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    // 計算防禦力: 考慮盾牌以及敏捷
    public int getDefense() {
        return defense = dexterity * currentShield.defenseValue;
    }

    public int getAttack() {
        attackArea = currentWeapon.attackArea;
        motion1_duration = currentWeapon.motion1_duration;
        motion2_duration = currentWeapon.motion2_duration;
        return attack = strength * currentWeapon.attackValue;
    }

    public void getImages() {
        String packagePath = "/gtcafe/rpg/assets/player/walking/";
        up1 = setup(packagePath + "boy_up_1.png", gp.tileSize, gp.tileSize);
        up2 = setup(packagePath + "boy_up_2.png", gp.tileSize, gp.tileSize);
        down1 = setup(packagePath + "boy_down_1.png", gp.tileSize, gp.tileSize);
        down2 = setup(packagePath + "boy_down_2.png", gp.tileSize, gp.tileSize);
        left1 = setup(packagePath + "boy_left_1.png", gp.tileSize, gp.tileSize);
        left2 = setup(packagePath + "boy_left_2.png", gp.tileSize, gp.tileSize);
        right1 = setup(packagePath + "boy_right_1.png", gp.tileSize, gp.tileSize);
        right2 = setup(packagePath + "boy_right_2.png", gp.tileSize, gp.tileSize);
    }

    public void getAttackImage() {

        if (currentWeapon.type == EntityType.SWORD) {
            String packagePath = "/gtcafe/rpg/assets/player/attacking/sword/";
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
            String packagePath = "/gtcafe/rpg/assets/player/attacking/axe/";
            attackUp1 = setup(packagePath + "boy_axe_up_1.png", gp.tileSize, gp.tileSize*2);
            attackUp2 = setup(packagePath + "boy_axe_up_2.png", gp.tileSize, gp.tileSize*2);
            attackDown1 = setup(packagePath + "boy_axe_down_1.png", gp.tileSize, gp.tileSize*2);
            attackDown2 = setup(packagePath + "boy_axe_down_2.png", gp.tileSize, gp.tileSize*2);
            attackLeft1 = setup(packagePath + "boy_axe_left_1.png", gp.tileSize*2, gp.tileSize);
            attackLeft2 = setup(packagePath + "boy_axe_left_2.png", gp.tileSize*2, gp.tileSize);
            attackRight1 = setup(packagePath + "boy_axe_right_1.png", gp.tileSize*2, gp.tileSize);
            attackRight2 = setup(packagePath + "boy_axe_right_2.png", gp.tileSize*2, gp.tileSize);
        }
        if (currentWeapon.type == EntityType.PICKAXE) {
            String packagePath = "/gtcafe/rpg/assets/player/attacking/pickaxe/";
            attackUp1 = setup(packagePath + "boy_pick_up_1.png", gp.tileSize, gp.tileSize*2);
            attackUp2 = setup(packagePath + "boy_pick_up_2.png", gp.tileSize, gp.tileSize*2);
            attackDown1 = setup(packagePath + "boy_pick_down_1.png", gp.tileSize, gp.tileSize*2);
            attackDown2 = setup(packagePath + "boy_pick_down_2.png", gp.tileSize, gp.tileSize*2);
            attackLeft1 = setup(packagePath + "boy_pick_left_1.png", gp.tileSize*2, gp.tileSize);
            attackLeft2 = setup(packagePath + "boy_pick_left_2.png", gp.tileSize*2, gp.tileSize);
            attackRight1 = setup(packagePath + "boy_pick_right_1.png", gp.tileSize*2, gp.tileSize);
            attackRight2 = setup(packagePath + "boy_pick_right_2.png", gp.tileSize*2, gp.tileSize);
        } 
    }

    public void getGuardImage() {
        String packagePath = "/gtcafe/rpg/assets/player/guarding/";
        guardUp = setup(packagePath + "boy_guard_up.png", gp.tileSize, gp.tileSize);
        guardDown = setup(packagePath + "boy_guard_down.png", gp.tileSize, gp.tileSize);
        guardLeft = setup(packagePath + "boy_guard_left.png", gp.tileSize, gp.tileSize);
        guardRight = setup(packagePath + "boy_guard_right.png", gp.tileSize, gp.tileSize);
    }

    public void getSleepingImage(BufferedImage image) {
        up1 = image;
        up2 = image;
        down1 = image;
        down2 = image;
        left1 = image;
        left2 = image;
        right1 = image;
        right2 = image;
    }

    public void update() {

        if(knockBack == true) {
            
            // CHECK TILE COLLISION
            collisionOn = false;
            // CHECK COLLSISION to update collisionOn flag flag flag
            gp.collisionChecker.checkTile(this);
            gp.collisionChecker.checkObject(this, true);
            gp.collisionChecker.checkEntity(this, gp.npc);
            gp.collisionChecker.checkEntity(this, gp.monster);
            gp.collisionChecker.checkEntity(this, gp.iTile);            

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

        // attack animation
        else if (attacking == true) {
            attacking();
        }
        else if (keyHandler.spacePressed == true) {
            guarding = true;
            guardCounter++;
        }
        else if (keyHandler.upPressed || keyHandler.downPressed || 
                keyHandler.leftPressed || keyHandler.rightPressed ||
                keyHandler.enterPressed) {

            if(keyHandler.upPressed) { direction = Direction.UP; } 
            else if(keyHandler.downPressed) { direction = Direction.DOWN; } 
            else if(keyHandler.leftPressed) { direction = Direction.LEFT; } 
            else if(keyHandler.rightPressed) { direction = Direction.RIGHT; }

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

            // CHECK EVENT
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
            // RESET enterPressed
            gp.keyHandler.enterPressed = false;
            guarding = false;
            guardCounter = 0;

            // ANIMATION for walking
            spriteCounter++;
            if(spriteCounter > WALKING_ANIMATION_SPEED) {
                if (spriteNum == 1) { spriteNum = 2; } 
                else if (spriteNum == 2) { spriteNum = 1; }
                spriteCounter = 0;
            }
        } 
        else {
            // ANIMATION for Standing
            spriteCounter++;
            if(spriteCounter > STANDING_ANIMATION_SPEED) {
                if (spriteNum == 1) { spriteNum = 2; } 
                else if (spriteNum == 2) { spriteNum = 1; }
                spriteCounter = 0;
            }

            guarding = false;
            guardCounter = 0;
        }

        // check if can shoot the projectiles
        // 1. 按下發射按鈕
        // 2. 拋射物目前沒有發射
        // 3. 拋射物目前還沒有畫出來 (30 FPS)
        // 4. Player 目前的魔力值足夠發射 (根據 Mana 值以及 Projectiles 需要消耗的值判斷)
        // System.out.printf("[Player#update()] shotKeyPressed: [%s], projectile.alive: [%s], shotAvailableCounter: [%s], haveResource: [%s]\n", 
        //     gp.keyHandler.shotKeyPressed, projectile.alive, shotAvailableCounter, projectile.haveResource(this)
        // );
        if (gp.keyHandler.shotKeyPressed == true && projectile.alive == false && 
                shotAvailableCounter == 30 && projectile.haveResource(this) == true) {
            System.out.println("[Player#update()] Shot the projectiles!!");
            // SET DEFAULT COORDINATES, DIRECTION AND USER
            projectile.set(worldX, worldY, direction, true, this);

            // SUBTRACT THE COST (MANA, AMMO 彈藥, etc.)
            projectile.subtractResource(this);
            
            // CHECK VACANCY
            for(int i=0; i< gp.projectile[1].length; i++) {
                if(gp.projectile[gp.currentMap.index][i] == null) {
                    gp.projectile[gp.currentMap.index][i] = projectile;
                    break;
                }
            } 

            shotAvailableCounter = 0;   // reset counter once player shoot.

            gp.ui.addMessage("Shot the " + projectile.name + "!!");

            gp.playSoundEffect(Sound.FX__BURNING);
        }

        // This needs to be outside of key if statement!
        if (invincible == true) {
            invincibleCounter++;
            if(invincibleCounter > 60) { // Frame Counter
                invincible = false;
                transparent = false;
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

        if (gp.keyHandler.godModeOn == true) {
            life = maxLife;
            mana = maxMana;
        };
       
        // Check if game over, 註解後可以變無敵
        if (life <= 0 && gp.keyHandler.godModeOn == false) {
            gp.gameState = GameState.GAME_OVER;
            gp.ui.commandNum = -1;
            gp.stopBackgroundMusic();
            gp.playSoundEffect(Sound.FX__GAME_OVER);
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
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
                if (guarding == true) { image = guardUp; }
                break;
            case DOWN:
                if (attacking == false) image = (spriteNum == 1) ? down1 : down2;
                if (attacking == true) image = (spriteNum == 1) ? attackDown1 : attackDown2;
                if (guarding == true) { image = guardDown; }
                break;
            case LEFT:
                if (attacking == false) image = (spriteNum == 1) ? left1 : left2;
                if (attacking == true) {
                    // exception case for up image.
                    tempScreenX = screenX - gp.tileSize;
                    image = (spriteNum == 1) ? attackLeft1 : attackLeft2;
                }
                if (guarding == true) { image = guardLeft; }
                break;
            case RIGHT:
                if (attacking == false) image = (spriteNum == 1) ? right1 : right2;
                if (attacking == true) image = (spriteNum == 1) ? attackRight1 : attackRight2;
                if (guarding == true) { image = guardRight; }
                break;
        }

        // Visual effect to invincible state
        if (transparent == true) {
            g2Utils.changeAlpha(g2, 0.3f);
        }

        if (drawing == true) {
            g2.drawImage(image, tempScreenX, tempScreenY, null);
        }

        // 畫出 solid area
       drawInteractiveArea(g2, tempScreenX, tempScreenY);

        // reset alpha
        g2Utils.changeAlpha(g2, 1f);
    }

    // Player 跟地圖上的物件的互動
    public void pickUpObject(int index) {
        int mapIndex = gp.currentMap.index;
        // 999 MEANS NOT TOUCH ANY OBJECT
        if (index != 999) {
            // PICKUP ONLY ITEMS, ex: Coin
            if (gp.obj[mapIndex][index].type == EntityType.PICKUPONLY) {
                System.out.printf("[Player#pickUpObject] The player picked up an item on the map: [%s]\n", gp.currentMap.name);
                gp.obj[mapIndex][index].use(this);
                gp.obj[mapIndex][index] = null;
            } 
            else if (gp.obj[mapIndex][index].type == EntityType.OBSTACLE) {
                if (keyHandler.enterPressed == true) {
                    attackCanceled = true;
                    gp.obj[gp.currentMap.index][index].interact();
                }
            }
            // INVENTORY ITEMS
            else {
                String text;
                if (canObtainItem(gp.obj[mapIndex][index]) == true) {
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
        int mapIndex = gp.currentMap.index;
        if (index != 999) { // means player touch NPC
            if (gp.keyHandler.enterPressed == true) {
            
                System.out.println("[Player#interactNPC] You are interacting with an NPC.");
                attackCanceled = true;
                gp.npc[mapIndex][index].speak();
            } 

            gp.npc[gp.currentMap.index][index].move(direction);
        }
    }

    public void damageProjectile(int i) {
        if (i != 999) {
            Entity projectile = gp.projectile[gp.currentMap.index][i];
            projectile.alive = false;
            generateParticle(projectile, projectile);
        }
    }

    // 計算 Player 毀壞 Interactive Tiles 的邏輯
    public void damageInteractiveTiles(int i) {
        int mapIndex = gp.currentMap.index;
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
                gp.iTile[mapIndex][i].checkDrop();
                gp.iTile[mapIndex][i] = gp.iTile[mapIndex][i].getDestroyedForm();
            }
        }
    }

    // 計算 Player 攻擊怪物的值
    public void damageMonster(int index, Entity attacker, int attack, int knockBackPower) {
        int mapIndex = gp.currentMap.index;
        if (index != 999) {
            Entity monster = gp.monster[mapIndex][index];
            // give some damge
            if (monster.invincible == false) {
                System.out.println("[Player#damageMonster] The player is attacking the monster!!");
                gp.playSoundEffect(Sound.FX_HIT_MONSTER);

                if (knockBackPower > 0) {
                    setKnockBack(monster, attacker, knockBackPower); // 怪物反彈的效果
                }

                // Parry
                if (gp.monster[gp.currentMap.index][index].offBalance == true) {
                    attack *= 5;
                }

                int damage = attack - monster.defense;
                if (damage <= 0) { damage = 1; }    // 至少會有 1 的傷害

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
            System.out.println("[Player#damageMonster] The player missed the target!!");
        }
    }

    // 計算 Player 被 Monster 攻擊後生命值的損失
    public void contactMonster(int index) {
        int mapIndex = gp.currentMap.index;
        if (index != 999) {
            if (invincible == false && gp.monster[mapIndex][index].dying == false) {
                System.out.println("[Player#contactMonster] The monster is attacking the player!!");
                gp.playSoundEffect(Sound.FX_RECEIVE_DAMAGE);
                
                // Monster 攻擊力 - Player 的防禦力
                int damage = gp.monster[mapIndex][index].attack - defense;
                if (damage < 1) { damage = 1; }     // 至少會損失 1

                life -= damage;
                invincible = true;
                transparent = true;
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

            gp.gameState = GameState.DIALOGUE;
            startDialogue(this, 0);
        }
    }

    public void setDialogue() {
        dialogues[0][0] = "You are level #" + level + " now!\nYou feel stronger!";
    }

    // ------------------------------------------------------------------------
    // INENTORY
    // ------------------------------------------------------------------------
    // 選擇 Inventory 裡的東西
    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);

        if (itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);
            if (selectedItem.type == EntityType.SWORD 
                    || selectedItem.type == EntityType.AXE 
                    || selectedItem.type == EntityType.PICKAXE) {
                currentWeapon = selectedItem;
                attack = getAttack();
                getAttackImage();
            }
            if (selectedItem.type == EntityType.SHIELD) {
                currentShield= selectedItem;
                defense = getDefense();
            }
            // if (selectedItem.type == EntityType.SHOE) {
            //     speed += 5;
            // }
            if (selectedItem.type == EntityType.LIGHT) {
                if(currentLight == selectedItem) {
                    currentLight = null;    // un-equip this item
                }
                else {
                    currentLight = selectedItem;
                }
                lightUpdated = true;
            }
            if (selectedItem.type == EntityType.CONSUMABLE) {
                if (selectedItem.use(this) == true) {
                    if(selectedItem.amount > 1) {
                        selectedItem.amount--;
                    } else {
                        inventory.remove(itemIndex);
                    }
                }
            }
        }
    }

    // day43
    // This method also can be used when you want to check if player has a certain quest item tec.
    public int searchItemInInventory(String itemName) {

        int itemIndex = 999;
        for (int i=0; i<inventory.size(); i++) {
            if (inventory.get(i).name.equals(itemName)) {
                itemIndex = i;
                break;
            }
        }

        return itemIndex;
    }

    // day43
    // check if we can obtain item
    public boolean canObtainItem(Entity item) {
        boolean canObtain = false;

        Entity newItem = gp.eGenerator.getObject(item.name);

        // CHECK IF STACKABLE
        if(newItem.stackable == true) {
            int index = searchItemInInventory(newItem.name);
            if (index != 999) {
                inventory.get(index).amount++;
                canObtain = true;
            }
            else { // this is a new item, need to check vacancy
                if(inventory.size() != maxInventorySize) {
                    inventory.add(newItem);
                    canObtain = true;
                }
            }
        } 
        // NOT STACKABLE
        else {
            if(inventory.size() != maxInventorySize) {
                inventory.add(newItem);
                canObtain = true;
            }
        }

        return canObtain;
    }

}
