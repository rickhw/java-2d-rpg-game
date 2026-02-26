package gtcafe.rpg.model.entity;

/**
 * Represents a map object (door, chest, key, potion, etc.).
 * Phase 4: Object system.
 */
public class MapObject {

    private final String id;
    private String type; // "door", "chest", "key", etc.
    private String spriteKey; // Matches sprite filename: "door", "chest", "key"
    private int worldX;
    private int worldY;
    private boolean collision; // Blocks movement (doors, chests)
    private boolean pickupable; // Auto-pickup on contact (keys, potions)
    private boolean interactable; // Requires ENTER to interact (chests)
    private boolean active = true; // false = removed/opened

    // For chests: what's inside
    private String lootType;
    private String lootSpriteKey;

    public MapObject(String id, String type, String spriteKey, int worldX, int worldY) {
        this.id = id;
        this.type = type;
        this.spriteKey = spriteKey;
        this.worldX = worldX;
        this.worldY = worldY;
    }

    // === Getters & Setters ===

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpriteKey() {
        return spriteKey;
    }

    public void setSpriteKey(String spriteKey) {
        this.spriteKey = spriteKey;
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

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean isPickupable() {
        return pickupable;
    }

    public void setPickupable(boolean pickupable) {
        this.pickupable = pickupable;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLootType() {
        return lootType;
    }

    public void setLootType(String lootType) {
        this.lootType = lootType;
    }

    public String getLootSpriteKey() {
        return lootSpriteKey;
    }

    public void setLootSpriteKey(String lootSpriteKey) {
        this.lootSpriteKey = lootSpriteKey;
    }
}
