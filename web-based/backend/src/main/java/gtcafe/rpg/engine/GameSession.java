package gtcafe.rpg.engine;

import java.util.ArrayList;
import java.util.List;

import gtcafe.rpg.model.entity.GameEntity;
import gtcafe.rpg.model.entity.MapObject;
import gtcafe.rpg.model.map.MapId;
import gtcafe.rpg.model.state.AreaType;
import gtcafe.rpg.model.state.DayState;
import gtcafe.rpg.model.state.Direction;
import gtcafe.rpg.model.state.GameState;

/**
 * Represents a single game session.
 * Each player has a unique session holding their complete game state.
 */
public class GameSession {

    private final String id;
    private GameState gameState;
    private MapId currentMap;
    private AreaType currentArea;
    private DayState dayState;

    // Player entity
    private GameEntity player;

    // NPCs on the current map
    private List<GameEntity> npcs = new ArrayList<>();

    // Map objects on the current map
    private List<MapObject> mapObjects = new ArrayList<>();

    // Player inventory
    private List<InventoryItem> inventory = new ArrayList<>();
    private static final int MAX_INVENTORY_SIZE = 20;

    private String equippedWeaponId = "sword_normal";
    private String equippedShieldId = "shield_wood";
    private String equippedLightId = null;

    /**
     * An item in the player's inventory.
     */
    public record InventoryItem(String itemId, int quantity) {
        public InventoryItem withQuantity(int newQuantity) {
            return new InventoryItem(itemId, newQuantity);
        }
    }

    // Dialogue state
    private boolean inDialogue = false;
    private String dialogueSpeakerName;
    private String[] dialogueLines; // All lines in the current dialogue set
    private int dialogueLineIndex; // Current line being shown

    // Map transition state
    private int transitionTimer = 0;
    private MapId targetMap;
    private int targetCol;
    private int targetRow;
    private AreaType targetArea;
    private boolean needsFullState = false;

    // Inventory Cursor
    private int inventorySlotCol = 0;
    private int inventorySlotRow = 0;

    // Position-based portal re-trigger prevention (like original canTouchEvent)
    private MapId lastTeleportMap;
    private int lastTeleportCol = -1;
    private int lastTeleportRow = -1;

    // Input state
    private boolean moving = false;
    private Direction moveDirection = Direction.DOWN;

    // Debug
    private boolean debugMode = false;

    // Tick counter
    private long tick = 0;

    public GameSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MapId getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(MapId currentMap) {
        this.currentMap = currentMap;
    }

    public AreaType getCurrentArea() {
        return currentArea;
    }

    public void setCurrentArea(AreaType currentArea) {
        this.currentArea = currentArea;
    }

    public DayState getDayState() {
        return dayState;
    }

    public void setDayState(DayState dayState) {
        this.dayState = dayState;
    }

    public GameEntity getPlayer() {
        return player;
    }

    public void setPlayer(GameEntity player) {
        this.player = player;
    }

    public List<GameEntity> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<GameEntity> npcs) {
        this.npcs = npcs;
    }

    public List<MapObject> getMapObjects() {
        return mapObjects;
    }

    public void setMapObjects(List<MapObject> mapObjects) {
        this.mapObjects = mapObjects;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    public String getEquippedWeaponId() {
        return equippedWeaponId;
    }

    public void setEquippedWeaponId(String id) {
        this.equippedWeaponId = id;
    }

    public String getEquippedShieldId() {
        return equippedShieldId;
    }

    public void setEquippedShieldId(String id) {
        this.equippedShieldId = id;
    }

    public String getEquippedLightId() {
        return equippedLightId;
    }

    public void setEquippedLightId(String id) {
        this.equippedLightId = id;
    }

    /**
     * Add an item to inventory, stacking if allowed and exists.
     * Returns true if successfully obtained, false if inventory is full.
     */
    public boolean addToInventory(String itemId) {
        gtcafe.rpg.model.item.ItemData itemData = gtcafe.rpg.model.item.ItemRegistry.get(itemId);
        if (itemData == null)
            return false;

        if (itemData.stackable()) {
            for (int i = 0; i < inventory.size(); i++) {
                InventoryItem item = inventory.get(i);
                if (item.itemId().equals(itemId)) {
                    inventory.set(i, item.withQuantity(item.quantity() + 1));
                    return true;
                }
            }
        }

        if (inventory.size() < MAX_INVENTORY_SIZE) {
            inventory.add(new InventoryItem(itemId, 1));
            return true;
        }

        return false;
    }

    /**
     * Remove an item from inventory.
     */
    public void removeFromInventory(int index) {
        if (index >= 0 && index < inventory.size()) {
            inventory.remove(index);
        }
    }

    /**
     * Decrease quantity of an item.
     */
    public void decreaseInventoryAmount(int index) {
        if (index >= 0 && index < inventory.size()) {
            InventoryItem item = inventory.get(index);
            if (item.quantity() > 1) {
                inventory.set(index, item.withQuantity(item.quantity() - 1));
            } else {
                inventory.remove(index);
            }
        }
    }

    /**
     * Check if player has a specific item type in inventory.
     */
    public boolean hasItem(String itemId) {
        return inventory.stream().anyMatch(i -> i.itemId().equals(itemId));
    }

    public boolean isInDialogue() {
        return inDialogue;
    }

    public void setInDialogue(boolean inDialogue) {
        this.inDialogue = inDialogue;
    }

    public String getDialogueSpeakerName() {
        return dialogueSpeakerName;
    }

    public void setDialogueSpeakerName(String name) {
        this.dialogueSpeakerName = name;
    }

    public String[] getDialogueLines() {
        return dialogueLines;
    }

    public void setDialogueLines(String[] lines) {
        this.dialogueLines = lines;
    }

    public int getDialogueLineIndex() {
        return dialogueLineIndex;
    }

    public void setDialogueLineIndex(int index) {
        this.dialogueLineIndex = index;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Direction getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(Direction moveDirection) {
        this.moveDirection = moveDirection;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public long getTick() {
        return tick;
    }

    public void incrementTick() {
        this.tick++;
    }

    // === Transition state ===

    public int getTransitionTimer() {
        return transitionTimer;
    }

    public void setTransitionTimer(int transitionTimer) {
        this.transitionTimer = transitionTimer;
    }

    public MapId getTargetMap() {
        return targetMap;
    }

    public void setTargetMap(MapId targetMap) {
        this.targetMap = targetMap;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public void setTargetCol(int targetCol) {
        this.targetCol = targetCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public void setTargetRow(int targetRow) {
        this.targetRow = targetRow;
    }

    public AreaType getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(AreaType targetArea) {
        this.targetArea = targetArea;
    }

    public boolean isNeedsFullState() {
        return needsFullState;
    }

    public void setNeedsFullState(boolean needsFullState) {
        this.needsFullState = needsFullState;
    }

    public int getInventorySlotCol() {
        return inventorySlotCol;
    }

    public void setInventorySlotCol(int inventorySlotCol) {
        this.inventorySlotCol = inventorySlotCol;
    }

    public int getInventorySlotRow() {
        return inventorySlotRow;
    }

    public void setInventorySlotRow(int inventorySlotRow) {
        this.inventorySlotRow = inventorySlotRow;
    }

    public MapId getLastTeleportMap() {
        return lastTeleportMap;
    }

    public void setLastTeleportMap(MapId lastTeleportMap) {
        this.lastTeleportMap = lastTeleportMap;
    }

    public int getLastTeleportCol() {
        return lastTeleportCol;
    }

    public void setLastTeleportCol(int lastTeleportCol) {
        this.lastTeleportCol = lastTeleportCol;
    }

    public int getLastTeleportRow() {
        return lastTeleportRow;
    }

    public void setLastTeleportRow(int lastTeleportRow) {
        this.lastTeleportRow = lastTeleportRow;
    }
}
