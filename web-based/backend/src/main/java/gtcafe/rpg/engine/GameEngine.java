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
import gtcafe.rpg.model.map.MapId;
import gtcafe.rpg.model.state.AreaType;
import gtcafe.rpg.model.state.DayState;
import gtcafe.rpg.model.state.Direction;
import gtcafe.rpg.model.state.GameState;
import gtcafe.rpg.service.MapService;

/**
 * Core game engine - manages game sessions and the server-side game loop.
 * Phase 3: Added NPC system with random movement AI and dialogue.
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
        state.put("currentMap", session.getCurrentMap().name());
        state.put("currentArea", session.getCurrentArea().name());
        state.put("dayState", session.getDayState().name());
        state.put("player", entityToMap(session.getPlayer()));
        state.put("mapData", mapService.getMapDataForClient(session.getCurrentMap()));

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
            case "MOVE" -> {
                if (session.getGameState() == GameState.PLAY && !session.isInDialogue()) {
                    String dirStr = (String) data.get("direction");
                    Direction dir = Direction.valueOf(dirStr);
                    session.setMoving(true);
                    session.setMoveDirection(dir);
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
                                // Attack
                                player.setAttacking(true);
                                player.setSpriteNum(1);
                                player.setSpriteCounter(0);
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
            default -> {
                System.out.printf("[GameEngine] Unknown input type: %s%n", type);
            }
        }
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
     * Start a dialogue with an NPC.
     */
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

            // Check tile collision AND NPC collision
            if (!checkTileCollision(session, player, nextX, nextY)
                    && !checkNpcCollision(session, player, nextX, nextY)) {
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
        state.put("tick", session.getTick());

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

        return state;
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
}
