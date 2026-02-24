package gtcafe.rpg.model.entity;

import gtcafe.rpg.model.state.Direction;

/**
 * Base game entity - pure logic, no graphics.
 * Migrated from the original Entity.java (866 lines) with all rendering
 * removed.
 */
public class GameEntity {

    // Identity
    private String id;
    private String name;
    private EntityType type;

    // Position
    private int worldX;
    private int worldY;
    private Direction direction = Direction.DOWN;

    // Collision
    private int solidAreaX;
    private int solidAreaY;
    private int solidAreaWidth;
    private int solidAreaHeight;
    private int solidAreaDefaultX;
    private int solidAreaDefaultY;
    private boolean collision = false;
    private boolean collisionOn = false;

    // Attack area
    private int attackAreaX;
    private int attackAreaY;
    private int attackAreaWidth;
    private int attackAreaHeight;

    // Movement
    private int speed;
    private int defaultSpeed;

    // Animation state (sent to client)
    private int spriteNum = 1;

    // Character attributes
    private int maxLife;
    private int life;
    private int maxMana;
    private int mana;
    private int level;
    private int strength;
    private int dexterity;
    private int attack;
    private int defense;
    private int exp;
    private int nextLevelExp;
    private int coin;

    // Combat state
    private boolean alive = true;
    private boolean dying = false;
    private boolean invincible = false;
    private boolean attacking = false;
    private boolean guarding = false;
    private boolean knockBack = false;
    private Direction knockBackDirection;
    private boolean transparent = false;
    private boolean offBalance = false;
    private boolean onPath = false;
    private boolean boss = false;
    private boolean inRage = false;
    private boolean sleep = false;

    // Item attributes
    private int attackValue;
    private int defenseValue;
    private String description = "";
    private int useCost;
    private int price;
    private int knockBackPower = 0;
    private boolean stackable = false;
    private int amount = 1;
    private int lightRadius;

    // Weapon animation timing
    private int motion1Duration;
    private int motion2Duration;

    // Sprite keys (client uses these to load correct sprites)
    private String spriteKey;

    // Counters (server-side only, not sent to client)
    private transient int spriteCounter = 0;
    private transient int actionLockCounter = 0;
    private transient int invincibleCounter = 0;
    private transient int shotAvailableCounter = 0;
    private transient int dyingCounter = 0;
    private transient int hpBarCounter = 0;
    private transient int knockBackCounter = 0;
    private transient int guardCounter = 0;
    private transient int offBalanceCounter = 0;

    public GameEntity() {
    }

    public GameEntity(String id, String name, EntityType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // === Computed Properties (migrated from Entity.java) ===

    public int getLeftX() {
        return worldX + solidAreaX;
    }

    public int getRightX() {
        return worldX + solidAreaX + solidAreaWidth;
    }

    public int getTopY() {
        return worldY + solidAreaY;
    }

    public int getBottomY() {
        return worldY + solidAreaY + solidAreaHeight;
    }

    public int getCol(int tileSize) {
        return (worldX + solidAreaX) / tileSize;
    }

    public int getRow(int tileSize) {
        return (worldY + solidAreaY) / tileSize;
    }

    public int getXdistance(GameEntity target) {
        return Math.abs(worldX - target.worldX);
    }

    public int getYdistance(GameEntity target) {
        return Math.abs(worldY - target.worldY);
    }

    public int getTileDistance(GameEntity target, int tileSize) {
        return (getXdistance(target) + getYdistance(target)) / tileSize;
    }

    public Direction getOppositeDirection(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
            case ANY -> Direction.ANY;
        };
    }

    public void resetCounters() {
        spriteCounter = 0;
        actionLockCounter = 0;
        invincibleCounter = 0;
        shotAvailableCounter = 0;
        dyingCounter = 0;
        hpBarCounter = 0;
        knockBackCounter = 0;
        guardCounter = 0;
        offBalanceCounter = 0;
    }

    // === Getters and Setters ===

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public int getWorldX() {
        return worldX;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getSolidAreaX() {
        return solidAreaX;
    }

    public void setSolidAreaX(int solidAreaX) {
        this.solidAreaX = solidAreaX;
    }

    public int getSolidAreaY() {
        return solidAreaY;
    }

    public void setSolidAreaY(int solidAreaY) {
        this.solidAreaY = solidAreaY;
    }

    public int getSolidAreaWidth() {
        return solidAreaWidth;
    }

    public void setSolidAreaWidth(int solidAreaWidth) {
        this.solidAreaWidth = solidAreaWidth;
    }

    public int getSolidAreaHeight() {
        return solidAreaHeight;
    }

    public void setSolidAreaHeight(int solidAreaHeight) {
        this.solidAreaHeight = solidAreaHeight;
    }

    public int getSolidAreaDefaultX() {
        return solidAreaDefaultX;
    }

    public void setSolidAreaDefaultX(int solidAreaDefaultX) {
        this.solidAreaDefaultX = solidAreaDefaultX;
    }

    public int getSolidAreaDefaultY() {
        return solidAreaDefaultY;
    }

    public void setSolidAreaDefaultY(int solidAreaDefaultY) {
        this.solidAreaDefaultY = solidAreaDefaultY;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean isCollisionOn() {
        return collisionOn;
    }

    public void setCollisionOn(boolean collisionOn) {
        this.collisionOn = collisionOn;
    }

    public int getAttackAreaX() {
        return attackAreaX;
    }

    public void setAttackAreaX(int v) {
        this.attackAreaX = v;
    }

    public int getAttackAreaY() {
        return attackAreaY;
    }

    public void setAttackAreaY(int v) {
        this.attackAreaY = v;
    }

    public int getAttackAreaWidth() {
        return attackAreaWidth;
    }

    public void setAttackAreaWidth(int v) {
        this.attackAreaWidth = v;
    }

    public int getAttackAreaHeight() {
        return attackAreaHeight;
    }

    public void setAttackAreaHeight(int v) {
        this.attackAreaHeight = v;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDefaultSpeed() {
        return defaultSpeed;
    }

    public void setDefaultSpeed(int defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public void setSpriteNum(int spriteNum) {
        this.spriteNum = spriteNum;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(int maxLife) {
        this.maxLife = maxLife;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getNextLevelExp() {
        return nextLevelExp;
    }

    public void setNextLevelExp(int nextLevelExp) {
        this.nextLevelExp = nextLevelExp;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isDying() {
        return dying;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isGuarding() {
        return guarding;
    }

    public void setGuarding(boolean guarding) {
        this.guarding = guarding;
    }

    public boolean isKnockBack() {
        return knockBack;
    }

    public void setKnockBack(boolean knockBack) {
        this.knockBack = knockBack;
    }

    public Direction getKnockBackDirection() {
        return knockBackDirection;
    }

    public void setKnockBackDirection(Direction d) {
        this.knockBackDirection = d;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public boolean isOffBalance() {
        return offBalance;
    }

    public void setOffBalance(boolean offBalance) {
        this.offBalance = offBalance;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public void setOnPath(boolean onPath) {
        this.onPath = onPath;
    }

    public boolean isBoss() {
        return boss;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    public boolean isInRage() {
        return inRage;
    }

    public void setInRage(boolean inRage) {
        this.inRage = inRage;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public int getAttackValue() {
        return attackValue;
    }

    public void setAttackValue(int v) {
        this.attackValue = v;
    }

    public int getDefenseValue() {
        return defenseValue;
    }

    public void setDefenseValue(int v) {
        this.defenseValue = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public int getUseCost() {
        return useCost;
    }

    public void setUseCost(int v) {
        this.useCost = v;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int v) {
        this.price = v;
    }

    public int getKnockBackPower() {
        return knockBackPower;
    }

    public void setKnockBackPower(int v) {
        this.knockBackPower = v;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean v) {
        this.stackable = v;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int v) {
        this.amount = v;
    }

    public int getLightRadius() {
        return lightRadius;
    }

    public void setLightRadius(int v) {
        this.lightRadius = v;
    }

    public int getMotion1Duration() {
        return motion1Duration;
    }

    public void setMotion1Duration(int v) {
        this.motion1Duration = v;
    }

    public int getMotion2Duration() {
        return motion2Duration;
    }

    public void setMotion2Duration(int v) {
        this.motion2Duration = v;
    }

    public String getSpriteKey() {
        return spriteKey;
    }

    public void setSpriteKey(String v) {
        this.spriteKey = v;
    }

    public int getSpriteCounter() {
        return spriteCounter;
    }

    public void setSpriteCounter(int v) {
        this.spriteCounter = v;
    }

    public int getActionLockCounter() {
        return actionLockCounter;
    }

    public void setActionLockCounter(int v) {
        this.actionLockCounter = v;
    }

    public int getInvincibleCounter() {
        return invincibleCounter;
    }

    public void setInvincibleCounter(int v) {
        this.invincibleCounter = v;
    }

    public int getShotAvailableCounter() {
        return shotAvailableCounter;
    }

    public void setShotAvailableCounter(int v) {
        this.shotAvailableCounter = v;
    }

    public int getDyingCounter() {
        return dyingCounter;
    }

    public void setDyingCounter(int v) {
        this.dyingCounter = v;
    }

    public int getHpBarCounter() {
        return hpBarCounter;
    }

    public void setHpBarCounter(int v) {
        this.hpBarCounter = v;
    }

    public int getKnockBackCounter() {
        return knockBackCounter;
    }

    public void setKnockBackCounter(int v) {
        this.knockBackCounter = v;
    }

    public int getGuardCounter() {
        return guardCounter;
    }

    public void setGuardCounter(int v) {
        this.guardCounter = v;
    }

    public int getOffBalanceCounter() {
        return offBalanceCounter;
    }

    public void setOffBalanceCounter(int v) {
        this.offBalanceCounter = v;
    }
}
