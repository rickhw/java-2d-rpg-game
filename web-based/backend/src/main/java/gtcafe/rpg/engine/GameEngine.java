package gtcafe.rpg.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import gtcafe.rpg.model.entity.EntityType;
import gtcafe.rpg.model.entity.GameEntity;
import gtcafe.rpg.model.entity.MapObject;
import gtcafe.rpg.model.map.MapId;
import gtcafe.rpg.model.state.AreaType;
import gtcafe.rpg.model.state.DayState;
import gtcafe.rpg.model.state.Direction;
import gtcafe.rpg.model.state.GameState;
import gtcafe.rpg.service.MapService;

/**
 * Core game engine - manages game sessions and the server-side game loop.
 * Phase 4: Added map transition system with teleport events.
 */
@Component
public class GameEngine {

    private static final int TILE_SIZE = 16;
    private static final int SCALE = 4;
    private static final int EFFECTIVE_TILE_SIZE = TILE_SIZE * SCALE; // 64

    private final MapService mapService;
    private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // NPC dialogue data (shared across all sessions)
    private static final Map<String, String[][]> NPC_DIALOGUES = new HashMap<>();
    static {
        NPC_DIALOGUES.put("oldman", new String[][] {
                {
                        "Hello, lad! I used to be an\nadventurer like you. Then I took\nan arrow in the knee...",
                        "So you've come to this island\nto find the treasure?",
                        "I used to be a great wizard but\nnow... I'm a bit too old for\ntaking an adventure.",
                        "Well, good luck to you."
                },
                {
                        "If you become tired, rest at\nthe water.",
                        "However, the monsters reappear\nif you rest. I don't know why\nbut that's how it works.",
                        "In any case, don't push\nyourself too hard."
                },
                {
                        "I wonder how to open\nthat door..."
                }
        });
    }

    public GameEngine(MapService mapService) {
        this.mapService = mapService;
    }

    // === Teleport event definitions (from original EventHandler.java) ===
    private record TeleportEvent(MapId fromMap, int fromCol, int fromRow,
            MapId toMap, int toCol, int toRow, AreaType toArea) {
    }

    private static final List<TeleportEvent> TELEPORT_EVENTS = List.of(
            // World Map ↔ Store
            new TeleportEvent(MapId.WORLD_MAP, 10, 39, MapId.STORE, 12, 13, AreaType.INDOOR),
            new TeleportEvent(MapId.STORE, 12, 13, MapId.WORLD_MAP, 10, 39, AreaType.OUTSIDE),
            // World Map ↔ Dungeon B1
            new TeleportEvent(MapId.WORLD_MAP, 12, 9, MapId.DUNGEON01, 9, 41, AreaType.DUNGEON),
            new TeleportEvent(MapId.DUNGEON01, 9, 41, MapId.WORLD_MAP, 12, 9, AreaType.OUTSIDE),
            // Dungeon B1 ↔ Dungeon B2
            new TeleportEvent(MapId.DUNGEON01, 8, 7, MapId.DUNGEON02, 26, 41, AreaType.DUNGEON),
            new TeleportEvent(MapId.DUNGEON02, 26, 41, MapId.DUNGEON01, 8, 7, AreaType.DUNGEON));

    /**
     * Create a new game session with initial state.
     */
    public String createNewSession() {
        String sessionId = UUID.randomUUID().toString();

        GameSession session = new GameSession(sessionId);
        session.setGameState(GameState.TITLE);
        session.setCurrentMap(MapId.WORLD_MAP);
        session.setCurrentArea(AreaType.OUTSIDE);
        session.setDayState(DayState.DAY);

        // Initialize player
        GameEntity player = new GameEntity("player", "Player", EntityType.PLAYER);
        player.setWorldX(23 * EFFECTIVE_TILE_SIZE);
        player.setWorldY(21 * EFFECTIVE_TILE_SIZE);
        player.setDirection(Direction.DOWN);
        player.setSpeed(4);
        player.setDefaultSpeed(4);
        player.setMaxLife(6);
        player.setLife(6);
        player.setMaxMana(4);
        player.setMana(4);
        player.setLevel(1);
        player.setStrength(1);
        player.setDexterity(1);
        player.setExp(0);
        player.setNextLevelExp(5);
        player.setCoin(500);
        player.setAttack(2);
        player.setDefense(1);
        player.setSpriteKey("player");

        // Solid area
        player.setSolidAreaX(16);
        player.setSolidAreaY(16);
        player.setSolidAreaWidth(32);
        player.setSolidAreaHeight(32);
        player.setSolidAreaDefaultX(16);
        player.setSolidAreaDefaultY(16);

        // Attack area
        player.setAttackAreaWidth(36);
        player.setAttackAreaHeight(36);

        // Weapon timing (Sword)
        player.setMotion1Duration(5);
        player.setMotion2Duration(25);

        session.setPlayer(player);

        // Initialize NPCs for the world map
        session.setNpcs(createWorldMapNPCs());

        // Initialize map objects
        session.setMapObjects(createMapObjects(MapId.WORLD_MAP));

        // Initial inventory
        session.addToInventory(session.getEquippedWeaponId());
        session.addToInventory(session.getEquippedShieldId());

        sessions.put(sessionId, session);
        System.out.printf("[GameEngine] New session created: %s%n", sessionId);
        return sessionId;
    }

    /**
     * Create NPCs for the world map (from original AssetSetter.setNPC).
     */
    private List<GameEntity> createWorldMapNPCs() {
        List<GameEntity> npcs = new ArrayList<>();

        // Old Man "Steve" at tile (18, 20) - matching original game
        GameEntity oldMan = new GameEntity("npc_oldman_1", "Steve", EntityType.NPC);
        oldMan.setWorldX(18 * EFFECTIVE_TILE_SIZE);
        oldMan.setWorldY(20 * EFFECTIVE_TILE_SIZE);
        oldMan.setDirection(Direction.DOWN);
        oldMan.setSpeed(1);
        oldMan.setDefaultSpeed(1);
        oldMan.setSpriteKey("oldman");
        oldMan.setSolidAreaX(8);
        oldMan.setSolidAreaY(16);
        oldMan.setSolidAreaWidth(48);
        oldMan.setSolidAreaHeight(48);
        oldMan.setSolidAreaDefaultX(8);
        oldMan.setSolidAreaDefaultY(16);
        npcs.add(oldMan);

        return npcs;
    }

    /**
     * Create NPCs for a given map.
     */
    private List<GameEntity> createNpcsForMap(MapId mapId) {
        return switch (mapId) {
            case WORLD_MAP -> createWorldMapNPCs();
            // TODO: STORE → Merchant NPC, DUNGEON01 → BigRock NPCs
            default -> new ArrayList<>();
        };
    }

    /**
     * Create map objects for a given map (from original AssetSetter.setObject).
     */
    private List<MapObject> createMapObjects(MapId mapId) {
        List<MapObject> objects = new ArrayList<>();
        int t = EFFECTIVE_TILE_SIZE;

        switch (mapId) {
            case WORLD_MAP -> {
                // Doors
                MapObject door1 = new MapObject("obj_door_1", "door", "door", 14 * t, 28 * t);
                door1.setCollision(true);
                objects.add(door1);

                MapObject door2 = new MapObject("obj_door_2", "door", "door", 12 * t, 12 * t);
                door2.setCollision(true);
                objects.add(door2);

                // Chest with key
                MapObject chest1 = new MapObject("obj_chest_1", "chest", "chest", 30 * t, 29 * t);
                chest1.setCollision(true);
                chest1.setInteractable(true);
                chest1.setLootType("key");
                chest1.setLootSpriteKey("key");
                objects.add(chest1);

                // Pickupable items
                MapObject axe = new MapObject("obj_axe_1", "axe", "axe", 33 * t, 7 * t);
                axe.setPickupable(true);
                objects.add(axe);

                MapObject shield = new MapObject("obj_shield_1", "shield_blue", "shield_blue", 10 * t, 34 * t);
                shield.setPickupable(true);
                objects.add(shield);

                MapObject lantern = new MapObject("obj_lantern_1", "lantern", "lantern", 27 * t, 16 * t);
                lantern.setPickupable(true);
                objects.add(lantern);

                MapObject tent = new MapObject("obj_tent_1", "tent", "tent", 30 * t, 12 * t);
                tent.setPickupable(true);
                objects.add(tent);
            }
            case DUNGEON01 -> {
                MapObject chest2 = new MapObject("obj_chest_d1_1", "chest", "chest", 40 * t, 41 * t);
                chest2.setCollision(true);
                chest2.setInteractable(true);
                chest2.setLootType("pickaxe");
                chest2.setLootSpriteKey("pickaxe");
                objects.add(chest2);

                MapObject chest3 = new MapObject("obj_chest_d1_2", "chest", "chest", 13 * t, 16 * t);
                chest3.setCollision(true);
                chest3.setInteractable(true);
                chest3.setLootType("potion_red");
                chest3.setLootSpriteKey("potion_red");
                objects.add(chest3);

                MapObject chest4 = new MapObject("obj_chest_d1_3", "chest", "chest", 26 * t, 34 * t);
                chest4.setCollision(true);
                chest4.setInteractable(true);
                chest4.setLootType("potion_red");
                chest4.setLootSpriteKey("potion_red");
                objects.add(chest4);

                MapObject chest5 = new MapObject("obj_chest_d1_4", "chest", "chest", 27 * t, 15 * t);
                chest5.setCollision(true);
                chest5.setInteractable(true);
                chest5.setLootType("potion_red");
                chest5.setLootSpriteKey("potion_red");
                objects.add(chest5);

                MapObject ironDoor = new MapObject("obj_iron_door_1", "door_iron", "door_iron", 18 * t, 23 * t);
                ironDoor.setCollision(true);
                objects.add(ironDoor);
            }
            case DUNGEON02 -> {
                MapObject blueHeart = new MapObject("obj_blueheart_1", "blueheart", "blueheart", 25 * t, 8 * t);
                blueHeart.setPickupable(true);
                objects.add(blueHeart);

                MapObject ironDoor2 = new MapObject("obj_iron_door_2", "door_iron", "door_iron", 25 * t, 15 * t);
                ironDoor2.setCollision(true);
                objects.add(ironDoor2);
            }
            default -> {
            }
        }

        return objects;
    }

    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        System.out.printf("[GameEngine] Session removed: %s%n", sessionId);
    }

    /**
     * Get the full game state for initial sync or reconnection.
     */
    public Map<String, Object> getFullState(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null)
            return null;

        Map<String, Object> state = new HashMap<>();
        state.put("sessionId", sessionId);
        state.put("gameState", session.getGameState().name());
        state.put("inventoryRow", session.getInventorySlotRow());
        state.put("inventoryCol", session.getInventorySlotCol());
        state.put("currentMap", session.getCurrentMap().name());
        state.put("currentArea", session.getCurrentArea().name());
        state.put("dayState", session.getDayState().name());
        state.put("player", entityToMap(session.getPlayer()));
        state.put("mapData", mapService.getMapDataForClient(session.getCurrentMap()));

        // Include transition progress if in transition
        if (session.getGameState() == GameState.TRANSITION) {
            state.put("transitionProgress", session.getTransitionTimer() / 60.0);
        }

        // Include NPCs
        List<Map<String, Object>> npcList = new ArrayList<>();
        for (GameEntity npc : session.getNpcs()) {
            npcList.add(entityToMap(npc));
        }
        state.put("npcs", npcList);

        // Include dialogue state if active
        if (session.isInDialogue()) {
            Map<String, Object> dialogue = new HashMap<>();
            dialogue.put("speaker", session.getDialogueSpeakerName());
            dialogue.put("line", session.getDialogueLines()[session.getDialogueLineIndex()]);
            dialogue.put("hasNext", session.getDialogueLineIndex() < session.getDialogueLines().length - 1);
            state.put("dialogue", dialogue);
        }

        // Include map objects
        List<Map<String, Object>> objList = new ArrayList<>();
        for (MapObject obj : session.getMapObjects()) {
            if (obj.isActive()) {
                objList.add(mapObjectToMap(obj));
            }
        }
        state.put("mapObjects", objList);

        // Include detailed inventory
        state.put("inventory", buildInventoryList(session));

        return state;
    }

    /**
     * Process a player input command.
     */
    public void processInput(String sessionId, String type, Map<String, Object> data) {
        GameSession session = sessions.get(sessionId);
        if (session == null)
            return;

        GameEntity player = session.getPlayer();

        switch (type) {
            case "INVENTORY_TOGGLE" -> {
                if (session.getGameState() == GameState.PLAY) {
                    session.setGameState(GameState.CHARACTER);
                    session.setMoving(false);
                    session.setNeedsFullState(true);
                } else if (session.getGameState() == GameState.CHARACTER) {
                    session.setGameState(GameState.PLAY);
                    session.setNeedsFullState(true);
                }
            }
            case "MOVE" -> {
                String dirStr = (String) data.get("direction");
                Direction dir = Direction.valueOf(dirStr);

                if (session.getGameState() == GameState.PLAY && !session.isInDialogue()) {
                    session.setMoving(true);
                    session.setMoveDirection(dir);
                } else if (session.getGameState() == GameState.CHARACTER) {
                    // Move inventory cursor
                    int col = session.getInventorySlotCol();
                    int row = session.getInventorySlotRow();
                    if (dir == Direction.UP && row > 0)
                        session.setInventorySlotRow(row - 1);
                    else if (dir == Direction.DOWN && row < 3)
                        session.setInventorySlotRow(row + 1);
                    else if (dir == Direction.LEFT && col > 0)
                        session.setInventorySlotCol(col - 1);
                    else if (dir == Direction.RIGHT && col < 4)
                        session.setInventorySlotCol(col + 1);
                    session.setNeedsFullState(true);
                }
            }
            case "MOVE_STOP" -> {
                session.setMoving(false);
            }
            case "ENTER_KEY" -> {
                switch (session.getGameState()) {
                    case TITLE -> {
                        session.setGameState(GameState.PLAY);
                        System.out.printf("[GameEngine] Session %s: Starting new game%n", session.getId());
                    }
                    case CHARACTER -> {
                        int slotIndex = session.getInventorySlotCol() + (session.getInventorySlotRow() * 5);
                        handleInventoryAction(session, slotIndex);
                    }
                    case PLAY -> {
                        if (session.isInDialogue()) {
                            // Advance or close dialogue
                            advanceDialogue(session);
                        } else {
                            // Check if facing an NPC to talk to
                            GameEntity nearbyNpc = findNpcInFront(session);
                            if (nearbyNpc != null) {
                                startDialogue(session, nearbyNpc);
                            } else {
                                // Check if facing an interactable object (chest)
                                MapObject obj = findObjectInFront(session);
                                if (obj != null) {
                                    interactWithObject(session, obj);
                                } else {
                                    // Attack
                                    player.setAttacking(true);
                                    player.setSpriteNum(1);
                                    player.setSpriteCounter(0);
                                }
                            }
                        }
                    }
                    case DIALOGUE -> {
                        advanceDialogue(session);
                    }
                    default -> {
                    }
                }
            }
            case "ATTACK" -> {
                if (session.getGameState() == GameState.PLAY && !session.isInDialogue()) {
                    player.setAttacking(true);
                    player.setSpriteNum(1);
                    player.setSpriteCounter(0);
                }
            }
            case "GUARD_START" -> {
                if (session.getGameState() == GameState.PLAY && !session.isInDialogue()) {
                    player.setGuarding(true);
                    player.setGuardCounter(0);
                }
            }
            case "GUARD_STOP" -> {
                player.setGuarding(false);
            }
            case "DEBUG_TOGGLE" -> {
                session.setDebugMode(!session.isDebugMode());
            }
            case "INVENTORY_ACTION" -> {
                Number slotObj = (Number) data.get("slot");
                if (slotObj != null) {
                    handleInventoryAction(session, slotObj.intValue());
                }
            }
            default -> {
                System.out.printf("[GameEngine] Unknown input type: %s%n", type);
            }
        }
    }

    private void handleInventoryAction(GameSession session, int slotIndex) {
        var inventory = session.getInventory();
        if (slotIndex < 0 || slotIndex >= inventory.size())
            return;

        var item = inventory.get(slotIndex);
        gtcafe.rpg.model.item.ItemData data = gtcafe.rpg.model.item.ItemRegistry.get(item.itemId());
        if (data == null)
            return;

        switch (data.type()) {
            case WEAPON -> session.setEquippedWeaponId(item.itemId());
            case SHIELD -> session.setEquippedShieldId(item.itemId());
            case TOOL -> {
                if ("lantern".equals(item.itemId())) {
                    if (item.itemId().equals(session.getEquippedLightId())) {
                        session.setEquippedLightId(null);
                    } else {
                        session.setEquippedLightId(item.itemId());
                    }
                }
            }
            case CONSUMABLE -> {
                if (data.healValue() > 0) {
                    GameEntity p = session.getPlayer();
                    if (p.getLife() < p.getMaxLife()) {
                        p.setLife(Math.min(p.getMaxLife(), p.getLife() + data.healValue()));
                        session.decreaseInventoryAmount(slotIndex);
                    }
                }
            }
            default -> {
                // Key items usually cannot be "used" from the menu, just passively.
            }
        }

        session.setNeedsFullState(true);
    }

    // ======================== NPC Interaction ========================

    /**
     * Find an NPC in front of the player (within interaction range).
     */
    private GameEntity findNpcInFront(GameSession session) {
        GameEntity player = session.getPlayer();
        int px = player.getWorldX();
        int py = player.getWorldY();

        // Check area in front of player based on direction
        int checkX = px, checkY = py;
        switch (player.getDirection()) {
            case UP -> checkY -= EFFECTIVE_TILE_SIZE;
            case DOWN -> checkY += EFFECTIVE_TILE_SIZE;
            case LEFT -> checkX -= EFFECTIVE_TILE_SIZE;
            case RIGHT -> checkX += EFFECTIVE_TILE_SIZE;
            default -> {
            }
        }

        for (GameEntity npc : session.getNpcs()) {
            int dx = Math.abs(npc.getWorldX() - checkX);
            int dy = Math.abs(npc.getWorldY() - checkY);
            if (dx < EFFECTIVE_TILE_SIZE && dy < EFFECTIVE_TILE_SIZE) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Find an interactable object in front of the player.
     */
    private MapObject findObjectInFront(GameSession session) {
        GameEntity player = session.getPlayer();
        int px = player.getWorldX();
        int py = player.getWorldY();

        int checkX = px, checkY = py;
        switch (player.getDirection()) {
            case UP -> checkY -= EFFECTIVE_TILE_SIZE;
            case DOWN -> checkY += EFFECTIVE_TILE_SIZE;
            case LEFT -> checkX -= EFFECTIVE_TILE_SIZE;
            case RIGHT -> checkX += EFFECTIVE_TILE_SIZE;
            default -> {
            }
        }

        for (MapObject obj : session.getMapObjects()) {
            if (!obj.isActive() || !obj.isInteractable())
                continue;
            int dx = Math.abs(obj.getWorldX() - checkX);
            int dy = Math.abs(obj.getWorldY() - checkY);
            if (dx < EFFECTIVE_TILE_SIZE && dy < EFFECTIVE_TILE_SIZE) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Interact with a map object (open chest, etc.).
     */
    private void interactWithObject(GameSession session, MapObject obj) {
        if ("chest".equals(obj.getType())) {
            // Open chest: change sprite, remove collision, show loot
            obj.setSpriteKey("chest_opened");
            obj.setCollision(false);
            obj.setInteractable(false);

            String lootName = obj.getLootType() != null ? obj.getLootType() : "something";
            String lootSprite = obj.getLootSpriteKey() != null ? obj.getLootSpriteKey() : lootName;

            // Add loot to inventory
            boolean success = session.addToInventory(lootName);
            session.setInDialogue(true);
            session.setDialogueSpeakerName("Chest");
            session.setDialogueLines(new String[] {
                    "You opened the chest and\nfound a " + lootName + "!"
            });
            session.setDialogueLineIndex(0);

            System.out.printf("[GameEngine] Opened chest %s, loot: %s%n", obj.getId(), lootName);
        }
    }

    private void startDialogue(GameSession session, GameEntity npc) {
        // NPC faces the player
        GameEntity player = session.getPlayer();
        npc.setDirection(player.getDirection().opposite());

        // Get dialogue lines
        String[][] allSets = NPC_DIALOGUES.get(npc.getSpriteKey());
        if (allSets == null || allSets.length == 0)
            return;

        // Cycle through dialogue sets
        int setIndex = random.nextInt(allSets.length);
        String[] lines = allSets[setIndex];

        session.setInDialogue(true);
        session.setDialogueSpeakerName(npc.getName());
        session.setDialogueLines(lines);
        session.setDialogueLineIndex(0);
        session.setMoving(false); // Stop player movement

        System.out.printf("[GameEngine] Dialogue started with %s%n", npc.getName());
    }

    /**
     * Advance to the next dialogue line, or close the dialogue.
     */
    private void advanceDialogue(GameSession session) {
        if (!session.isInDialogue())
            return;

        int nextIndex = session.getDialogueLineIndex() + 1;
        if (nextIndex < session.getDialogueLines().length) {
            session.setDialogueLineIndex(nextIndex);
        } else {
            // End dialogue
            session.setInDialogue(false);
            session.setDialogueSpeakerName(null);
            session.setDialogueLines(null);
            session.setDialogueLineIndex(0);
            System.out.println("[GameEngine] Dialogue ended");
        }
    }

    // ======================== Game Tick ========================

    /**
     * Perform one game tick for a session.
     */
    public void tick(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null)
            return;

        session.incrementTick();

        if (session.getGameState() == GameState.PLAY) {
            updatePlayer(session);
            updateNPCs(session);
            checkTeleportEvents(session);
            checkPickupObjects(session);
            checkHealingPool(session);
        } else if (session.getGameState() == GameState.CHARACTER) {
            // In character menu, NPCs should still move or animate, player might need to
            // finish animation
            updatePlayer(session);
            updateNPCs(session);
        } else if (session.getGameState() == GameState.TRANSITION) {
            updateTransition(session);
        }
    }

    /**
     * Check if player is standing on a teleport tile.
     */
    private void checkTeleportEvents(GameSession session) {
        GameEntity player = session.getPlayer();
        int playerCol = (player.getWorldX() + player.getSolidAreaX() + player.getSolidAreaWidth() / 2)
                / EFFECTIVE_TILE_SIZE;
        int playerRow = (player.getWorldY() + player.getSolidAreaY() + player.getSolidAreaHeight() / 2)
                / EFFECTIVE_TILE_SIZE;

        // Position-based re-trigger prevention: skip if player is still on the last
        // teleport tile
        if (session.getLastTeleportMap() == session.getCurrentMap()
                && session.getLastTeleportCol() == playerCol
                && session.getLastTeleportRow() == playerRow) {
            return;
        }
        // Player has moved off the last teleport tile, clear it
        if (session.getLastTeleportCol() != -1) {
            session.setLastTeleportCol(-1);
            session.setLastTeleportRow(-1);
            session.setLastTeleportMap(null);
        }

        for (TeleportEvent evt : TELEPORT_EVENTS) {
            if (evt.fromMap() == session.getCurrentMap()
                    && evt.fromCol() == playerCol
                    && evt.fromRow() == playerRow) {
                // Start transition
                session.setGameState(GameState.TRANSITION);
                session.setTransitionTimer(0);
                session.setTargetMap(evt.toMap());
                session.setTargetCol(evt.toCol());
                session.setTargetRow(evt.toRow());
                session.setTargetArea(evt.toArea());
                session.setMoving(false);
                System.out.printf("[GameEngine] Teleport: %s(%d,%d) → %s(%d,%d)%n",
                        evt.fromMap(), evt.fromCol(), evt.fromRow(),
                        evt.toMap(), evt.toCol(), evt.toRow());
                break;
            }
        }
    }

    /**
     * Auto-pickup objects the player walks over.
     */
    private void checkPickupObjects(GameSession session) {
        GameEntity player = session.getPlayer();
        int pCenterX = player.getWorldX() + player.getSolidAreaX() + player.getSolidAreaWidth() / 2;
        int pCenterY = player.getWorldY() + player.getSolidAreaY() + player.getSolidAreaHeight() / 2;

        for (MapObject obj : session.getMapObjects()) {
            if (!obj.isActive() || !obj.isPickupable())
                continue;

            int oCenterX = obj.getWorldX() + EFFECTIVE_TILE_SIZE / 2;
            int oCenterY = obj.getWorldY() + EFFECTIVE_TILE_SIZE / 2;

            if (Math.abs(pCenterX - oCenterX) < EFFECTIVE_TILE_SIZE / 2
                    && Math.abs(pCenterY - oCenterY) < EFFECTIVE_TILE_SIZE / 2) {
                obj.setActive(false);

                // Add to inventory
                boolean success = session.addToInventory(obj.getType());

                session.setInDialogue(true);
                session.setDialogueSpeakerName("Item");
                session.setDialogueLines(new String[] {
                        "You obtained a " + obj.getType() + "!"
                });
                session.setDialogueLineIndex(0);

                System.out.printf("[GameEngine] Picked up %s (%s)%n", obj.getId(), obj.getType());
                break; // One pickup per tick
            }
        }
    }

    /**
     * Healing pool event at World Map (23, 12) — facing UP.
     * Restores full HP and MP.
     */
    private void checkHealingPool(GameSession session) {
        if (session.getCurrentMap() != MapId.WORLD_MAP)
            return;
        if (session.isInDialogue())
            return;

        GameEntity player = session.getPlayer();
        int playerCol = (player.getWorldX() + player.getSolidAreaX() + player.getSolidAreaWidth() / 2)
                / EFFECTIVE_TILE_SIZE;
        int playerRow = (player.getWorldY() + player.getSolidAreaY() + player.getSolidAreaHeight() / 2)
                / EFFECTIVE_TILE_SIZE;

        if (playerCol == 23 && playerRow == 12 && player.getDirection() == Direction.UP) {
            // Only heal if not at full health
            if (player.getLife() < player.getMaxLife() || player.getMana() < player.getMaxMana()) {
                player.setLife(player.getMaxLife());
                player.setMana(player.getMaxMana());

                session.setInDialogue(true);
                session.setDialogueSpeakerName("Healing Pool");
                session.setDialogueLines(new String[] {
                        "Your HP and MP have been\\nfully restored!"
                });
                session.setDialogueLineIndex(0);

                System.out.println("[GameEngine] Healing pool activated");
            }
        }
    }

    /**
     * Handle TRANSITION state: fade out (30 ticks) → switch map → fade in (30
     * ticks) → PLAY.
     */
    private void updateTransition(GameSession session) {
        int timer = session.getTransitionTimer() + 1;
        session.setTransitionTimer(timer);

        // At halfway point (30 ticks = 0.5s), do the actual map switch
        if (timer == 30) {
            session.setCurrentMap(session.getTargetMap());
            session.setCurrentArea(session.getTargetArea());

            // Move player to target position
            GameEntity player = session.getPlayer();
            player.setWorldX(session.getTargetCol() * EFFECTIVE_TILE_SIZE);
            player.setWorldY(session.getTargetRow() * EFFECTIVE_TILE_SIZE);

            // Reload NPCs and objects for the new map
            session.setNpcs(createNpcsForMap(session.getTargetMap()));
            session.setMapObjects(createMapObjects(session.getTargetMap()));

            // Flag for FULL_STATE broadcast (new mapData needed)
            session.setNeedsFullState(true);

            System.out.printf("[GameEngine] Map switched to %s, player at (%d,%d)%n",
                    session.getTargetMap(), session.getTargetCol(), session.getTargetRow());
        }

        // Transition complete after 60 ticks (1 second)
        if (timer >= 60) {
            session.setGameState(GameState.PLAY);
            session.setTransitionTimer(0);
            // Remember destination to prevent re-trigger while standing on it
            session.setLastTeleportMap(session.getCurrentMap());
            session.setLastTeleportCol(session.getTargetCol());
            session.setLastTeleportRow(session.getTargetRow());
        }
    }

    private void updatePlayer(GameSession session) {
        GameEntity player = session.getPlayer();

        // Attack animation
        if (player.isAttacking()) {
            int counter = player.getSpriteCounter() + 1;
            player.setSpriteCounter(counter);
            if (counter <= player.getMotion1Duration()) {
                player.setSpriteNum(1);
            } else if (counter <= player.getMotion2Duration()) {
                player.setSpriteNum(2);
            } else {
                player.setAttacking(false);
                player.setSpriteNum(1);
                player.setSpriteCounter(0);
            }
        }
        // Movement
        else if (session.isMoving() && !player.isGuarding() && !session.isInDialogue()) {
            Direction dir = session.getMoveDirection();
            player.setDirection(dir);

            int nextX = player.getWorldX();
            int nextY = player.getWorldY();
            int speed = player.getSpeed();

            switch (dir) {
                case UP -> nextY -= speed;
                case DOWN -> nextY += speed;
                case LEFT -> nextX -= speed;
                case RIGHT -> nextX += speed;
                default -> {
                }
            }

            // Check tile collision, NPC collision, AND object collision
            if (!checkTileCollision(session, player, nextX, nextY)
                    && !checkNpcCollision(session, player, nextX, nextY)
                    && !checkObjectCollision(session, nextX, nextY)) {
                player.setWorldX(nextX);
                player.setWorldY(nextY);
            }

            // Walking animation
            int counter = player.getSpriteCounter() + 1;
            player.setSpriteCounter(counter);
            if (counter > 12) {
                player.setSpriteNum(player.getSpriteNum() == 1 ? 2 : 1);
                player.setSpriteCounter(0);
            }
        }

        // Invincibility timer
        if (player.isInvincible()) {
            int counter = player.getInvincibleCounter() + 1;
            player.setInvincibleCounter(counter);
            if (counter > 40) {
                player.setInvincible(false);
                player.setInvincibleCounter(0);
            }
        }
    }

    /**
     * Update all NPCs - random movement AI (from NPC_OldMan.setAction).
     */
    private void updateNPCs(GameSession session) {
        for (GameEntity npc : session.getNpcs()) {
            // Freeze NPCs during dialogue
            if (session.isInDialogue())
                continue;

            if (npc.getSpeed() <= 0)
                continue; // Static NPCs don't move

            // Random direction change every 120 ticks (~2 seconds at 60 ticks/s)
            int lockCounter = npc.getActionLockCounter() + 1;
            npc.setActionLockCounter(lockCounter);

            if (lockCounter >= 120) {
                int r = random.nextInt(100) + 1;
                if (r <= 25)
                    npc.setDirection(Direction.UP);
                else if (r <= 50)
                    npc.setDirection(Direction.DOWN);
                else if (r <= 75)
                    npc.setDirection(Direction.LEFT);
                else
                    npc.setDirection(Direction.RIGHT);
                npc.setActionLockCounter(0);
            }

            // Move NPC
            int nextX = npc.getWorldX();
            int nextY = npc.getWorldY();

            switch (npc.getDirection()) {
                case UP -> nextY -= npc.getSpeed();
                case DOWN -> nextY += npc.getSpeed();
                case LEFT -> nextX -= npc.getSpeed();
                case RIGHT -> nextX += npc.getSpeed();
                default -> {
                }
            }

            // Check tile collision AND player collision
            if (!checkTileCollision(session, npc, nextX, nextY)
                    && !checkEntityCollision(session.getPlayer(), npc, nextX, nextY)) {
                npc.setWorldX(nextX);
                npc.setWorldY(nextY);
            }

            // Walking animation
            int spriteCounter = npc.getSpriteCounter() + 1;
            npc.setSpriteCounter(spriteCounter);
            if (spriteCounter > 24) { // Slower animation for NPCs
                npc.setSpriteNum(npc.getSpriteNum() == 1 ? 2 : 1);
                npc.setSpriteCounter(0);
            }
        }
    }

    /**
     * Check AABB collision between a moving entity and a target entity.
     */
    private boolean checkEntityCollision(GameEntity target, GameEntity mover, int nextX, int nextY) {
        int mLeft = nextX + mover.getSolidAreaX();
        int mRight = nextX + mover.getSolidAreaX() + mover.getSolidAreaWidth();
        int mTop = nextY + mover.getSolidAreaY();
        int mBottom = nextY + mover.getSolidAreaY() + mover.getSolidAreaHeight();

        int tLeft = target.getWorldX() + target.getSolidAreaX();
        int tRight = target.getWorldX() + target.getSolidAreaX() + target.getSolidAreaWidth();
        int tTop = target.getWorldY() + target.getSolidAreaY();
        int tBottom = target.getWorldY() + target.getSolidAreaY() + target.getSolidAreaHeight();

        return mLeft < tRight && mRight > tLeft && mTop < tBottom && mBottom > tTop;
    }

    // ======================== Collision ========================

    /**
     * Check collision between an entity and NPCs.
     */
    private boolean checkNpcCollision(GameSession session, GameEntity entity, int nextX, int nextY) {
        int eLeft = nextX + entity.getSolidAreaX();
        int eRight = nextX + entity.getSolidAreaX() + entity.getSolidAreaWidth();
        int eTop = nextY + entity.getSolidAreaY();
        int eBottom = nextY + entity.getSolidAreaY() + entity.getSolidAreaHeight();

        for (GameEntity npc : session.getNpcs()) {
            int nLeft = npc.getWorldX() + npc.getSolidAreaX();
            int nRight = npc.getWorldX() + npc.getSolidAreaX() + npc.getSolidAreaWidth();
            int nTop = npc.getWorldY() + npc.getSolidAreaY();
            int nBottom = npc.getWorldY() + npc.getSolidAreaY() + npc.getSolidAreaHeight();

            // AABB overlap check
            if (eLeft < nRight && eRight > nLeft && eTop < nBottom && eBottom > nTop) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTileCollision(GameSession session, GameEntity entity, int nextX, int nextY) {
        var gameMap = mapService.getMap(session.getCurrentMap());
        if (gameMap == null)
            return false;

        int entityLeftX = nextX + entity.getSolidAreaX();
        int entityRightX = nextX + entity.getSolidAreaX() + entity.getSolidAreaWidth();
        int entityTopY = nextY + entity.getSolidAreaY();
        int entityBottomY = nextY + entity.getSolidAreaY() + entity.getSolidAreaHeight();

        int leftCol = entityLeftX / EFFECTIVE_TILE_SIZE;
        int rightCol = entityRightX / EFFECTIVE_TILE_SIZE;
        int topRow = entityTopY / EFFECTIVE_TILE_SIZE;
        int bottomRow = entityBottomY / EFFECTIVE_TILE_SIZE;

        if (leftCol < 0 || rightCol >= gameMap.getMaxCol() ||
                topRow < 0 || bottomRow >= gameMap.getMaxRow()) {
            return true;
        }

        var tiles = mapService.getTileDataList();
        int t1 = gameMap.getTileAt(leftCol, topRow);
        int t2 = gameMap.getTileAt(rightCol, topRow);
        int t3 = gameMap.getTileAt(leftCol, bottomRow);
        int t4 = gameMap.getTileAt(rightCol, bottomRow);

        if (t1 >= 0 && t1 < tiles.size() && tiles.get(t1).isCollision())
            return true;
        if (t2 >= 0 && t2 < tiles.size() && tiles.get(t2).isCollision())
            return true;
        if (t3 >= 0 && t3 < tiles.size() && tiles.get(t3).isCollision())
            return true;
        if (t4 >= 0 && t4 < tiles.size() && tiles.get(t4).isCollision())
            return true;

        return false;
    }

    /**
     * Check if a position collides with any active map object that has collision.
     */
    private boolean checkObjectCollision(GameSession session, int nextX, int nextY) {
        GameEntity player = session.getPlayer();
        int pLeft = nextX + player.getSolidAreaX();
        int pRight = nextX + player.getSolidAreaX() + player.getSolidAreaWidth();
        int pTop = nextY + player.getSolidAreaY();
        int pBottom = nextY + player.getSolidAreaY() + player.getSolidAreaHeight();

        for (MapObject obj : session.getMapObjects()) {
            if (!obj.isActive() || !obj.isCollision())
                continue;

            // Object occupies a full tile
            int oLeft = obj.getWorldX();
            int oRight = obj.getWorldX() + EFFECTIVE_TILE_SIZE;
            int oTop = obj.getWorldY();
            int oBottom = obj.getWorldY() + EFFECTIVE_TILE_SIZE;

            if (pLeft < oRight && pRight > oLeft && pTop < oBottom && pBottom > oTop) {
                return true;
            }
        }
        return false;
    }

    // ======================== State Broadcasting ========================

    public java.util.Set<String> getAllSessionIds() {
        return sessions.keySet();
    }

    /**
     * Delta state includes player + NPCs + dialogue for efficient broadcasting.
     */
    public Map<String, Object> getDeltaState(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null)
            return null;

        Map<String, Object> state = new HashMap<>();
        state.put("sessionId", sessionId);
        state.put("gameState", session.getGameState().name());
        state.put("inventoryRow", session.getInventorySlotRow());
        state.put("inventoryCol", session.getInventorySlotCol());
        state.put("tick", session.getTick());

        // Transition progress (0.0 → 1.0 for fade effect)
        if (session.getGameState() == GameState.TRANSITION) {
            state.put("transitionProgress", session.getTransitionTimer() / 60.0);
        }

        // Player delta
        GameEntity player = session.getPlayer();
        Map<String, Object> playerDelta = new HashMap<>();
        playerDelta.put("worldX", player.getWorldX());
        playerDelta.put("worldY", player.getWorldY());
        playerDelta.put("direction", player.getDirection().name());
        playerDelta.put("spriteNum", player.getSpriteNum());
        playerDelta.put("attacking", player.isAttacking());
        playerDelta.put("guarding", player.isGuarding());
        playerDelta.put("invincible", player.isInvincible());
        playerDelta.put("life", player.getLife());
        playerDelta.put("maxLife", player.getMaxLife());
        playerDelta.put("mana", player.getMana());
        playerDelta.put("maxMana", player.getMaxMana());
        state.put("player", playerDelta);

        // NPC deltas (position + direction + animation)
        List<Map<String, Object>> npcDeltas = new ArrayList<>();
        for (GameEntity npc : session.getNpcs()) {
            Map<String, Object> nd = new HashMap<>();
            nd.put("id", npc.getId());
            nd.put("worldX", npc.getWorldX());
            nd.put("worldY", npc.getWorldY());
            nd.put("direction", npc.getDirection().name());
            nd.put("spriteNum", npc.getSpriteNum());
            nd.put("spriteKey", npc.getSpriteKey());
            npcDeltas.add(nd);
        }
        state.put("npcs", npcDeltas);

        // Dialogue state
        if (session.isInDialogue()) {
            Map<String, Object> dialogue = new HashMap<>();
            dialogue.put("speaker", session.getDialogueSpeakerName());
            dialogue.put("line", session.getDialogueLines()[session.getDialogueLineIndex()]);
            dialogue.put("hasNext", session.getDialogueLineIndex() < session.getDialogueLines().length - 1);
            state.put("dialogue", dialogue);
        }

        // Include detailed inventory
        state.put("inventory", buildInventoryList(session));

        return state;
    }

    private List<Map<String, Object>> buildInventoryList(GameSession session) {
        List<Map<String, Object>> invList = new ArrayList<>();
        for (var item : session.getInventory()) {
            gtcafe.rpg.model.item.ItemData itemData = gtcafe.rpg.model.item.ItemRegistry.get(item.itemId());
            if (itemData == null)
                continue;

            Map<String, Object> invMap = new HashMap<>();
            invMap.put("id", itemData.id());
            invMap.put("name", itemData.name());
            invMap.put("type", itemData.type().name());
            invMap.put("spriteKey", itemData.spriteKey());
            invMap.put("description", itemData.description());
            invMap.put("quantity", item.quantity());

            // Check if equipped
            boolean equipped = itemData.id().equals(session.getEquippedWeaponId())
                    || itemData.id().equals(session.getEquippedShieldId())
                    || itemData.id().equals(session.getEquippedLightId());
            invMap.put("equipped", equipped);

            invList.add(invMap);
        }
        return invList;
    }

    private Map<String, Object> entityToMap(GameEntity e) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", e.getId());
        map.put("name", e.getName());
        map.put("type", e.getType().name());
        map.put("worldX", e.getWorldX());
        map.put("worldY", e.getWorldY());
        map.put("direction", e.getDirection().name());
        map.put("spriteNum", e.getSpriteNum());
        map.put("life", e.getLife());
        map.put("maxLife", e.getMaxLife());
        map.put("mana", e.getMana());
        map.put("maxMana", e.getMaxMana());
        map.put("level", e.getLevel());
        map.put("exp", e.getExp());
        map.put("coin", e.getCoin());
        map.put("attacking", e.isAttacking());
        map.put("guarding", e.isGuarding());
        map.put("invincible", e.isInvincible());
        map.put("alive", e.isAlive());
        map.put("spriteKey", e.getSpriteKey());
        return map;
    }

    private Map<String, Object> mapObjectToMap(MapObject obj) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", obj.getId());
        map.put("type", obj.getType());
        map.put("spriteKey", obj.getSpriteKey());
        map.put("worldX", obj.getWorldX());
        map.put("worldY", obj.getWorldY());
        map.put("collision", obj.isCollision());
        map.put("active", obj.isActive());
        return map;
    }
}
