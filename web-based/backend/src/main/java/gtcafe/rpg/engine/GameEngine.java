package gtcafe.rpg.engine;

import java.util.HashMap;
import java.util.Map;
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
 * Migrated from GamePanel.java (the hub of the original game).
 */
@Component
public class GameEngine {

    private static final int TILE_SIZE = 16;
    private static final int SCALE = 4;
    private static final int EFFECTIVE_TILE_SIZE = TILE_SIZE * SCALE; // 64

    private final MapService mapService;
    private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();

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

        // Initialize player (from Player.setDefaultValues())
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
        player.setAttack(2); // strength + weapon attack
        player.setDefense(1); // dexterity + shield defense
        player.setSpriteKey("player");

        // Solid area (from original Player.java)
        player.setSolidAreaX(8);
        player.setSolidAreaY(16);
        player.setSolidAreaWidth(32);
        player.setSolidAreaHeight(32);
        player.setSolidAreaDefaultX(8);
        player.setSolidAreaDefaultY(16);

        // Attack area
        player.setAttackAreaWidth(36);
        player.setAttackAreaHeight(36);

        // Weapon timing (Sword)
        player.setMotion1Duration(5);
        player.setMotion2Duration(25);

        session.setPlayer(player);

        sessions.put(sessionId, session);
        System.out.printf("[GameEngine] New session created: %s%n", sessionId);
        return sessionId;
    }

    /**
     * Get a session by ID.
     */
    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Remove a session (called when WebSocket connection closes).
     */
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

        // Include map tile data
        state.put("mapData", mapService.getMapDataForClient(session.getCurrentMap()));

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
                String dirStr = (String) data.get("direction");
                Direction dir = Direction.valueOf(dirStr);
                session.setMoving(true);
                session.setMoveDirection(dir);
            }
            case "MOVE_STOP" -> {
                session.setMoving(false);
            }
            case "ENTER_KEY" -> {
                // Context-dependent: acts differently based on game state
                switch (session.getGameState()) {
                    case TITLE -> {
                        session.setGameState(GameState.PLAY);
                        System.out.printf("[GameEngine] Session %s: Starting new game%n", session.getId());
                    }
                    case PLAY -> {
                        player.setAttacking(true);
                        player.setSpriteNum(1);
                        player.setSpriteCounter(0);
                    }
                    case DIALOGUE -> {
                        // Advance dialogue (TODO)
                    }
                    default -> {
                    }
                }
            }
            case "ATTACK" -> {
                if (session.getGameState() == GameState.PLAY) {
                    player.setAttacking(true);
                    player.setSpriteNum(1);
                    player.setSpriteCounter(0);
                }
            }
            case "GUARD_START" -> {
                if (session.getGameState() == GameState.PLAY) {
                    player.setGuarding(true);
                    player.setGuardCounter(0);
                }
            }
            case "GUARD_STOP" -> {
                player.setGuarding(false);
            }
            case "INTERACT" -> {
                // Trigger interaction based on game state
                processInteraction(session);
            }
            case "MENU_SELECT" -> {
                String action = (String) data.get("action");
                processMenuAction(session, action);
            }
            case "DEBUG_TOGGLE" -> {
                session.setDebugMode(!session.isDebugMode());
            }
            default -> {
                System.out.printf("[GameEngine] Unknown input type: %s%n", type);
            }
        }
    }

    private void processInteraction(GameSession session) {
        switch (session.getGameState()) {
            case TITLE -> {
                // Start game handled by MENU_SELECT
            }
            case DIALOGUE -> {
                // Advance dialogue
            }
            default -> {
            }
        }
    }

    private void processMenuAction(GameSession session, String action) {
        switch (action) {
            case "NEW_GAME" -> {
                session.setGameState(GameState.PLAY);
                System.out.printf("[GameEngine] Session %s: Starting new game%n", session.getId());
            }
            case "LOAD_GAME" -> {
                // TODO: Load game implementation
                session.setGameState(GameState.PLAY);
            }
            case "QUIT" -> {
                sessions.remove(session.getId());
            }
            default -> {
            }
        }
    }

    /**
     * Perform one game tick for a session. Called by the server game loop.
     */
    public void tick(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null)
            return;

        session.incrementTick();

        if (session.getGameState() == GameState.PLAY) {
            updatePlayer(session);
            // TODO: updateNPCs, updateMonsters, updateProjectiles, etc.
        }
    }

    private void updatePlayer(GameSession session) {
        GameEntity player = session.getPlayer();

        // Attack animation processing
        if (player.isAttacking()) {
            int counter = player.getSpriteCounter() + 1;
            player.setSpriteCounter(counter);
            if (counter <= player.getMotion1Duration()) {
                player.setSpriteNum(1);
            } else if (counter <= player.getMotion2Duration()) {
                player.setSpriteNum(2);
            } else {
                // Attack finished
                player.setAttacking(false);
                player.setSpriteNum(1);
                player.setSpriteCounter(0);
            }
        }
        // Movement processing
        else if (session.isMoving() && !player.isGuarding()) {
            Direction dir = session.getMoveDirection();
            player.setDirection(dir);

            // Simple collision check with tiles
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

            // Check tile collision before moving
            if (!checkTileCollision(session, player, nextX, nextY)) {
                player.setWorldX(nextX);
                player.setWorldY(nextY);
            }

            // Walking animation
            int counter = player.getSpriteCounter() + 1;
            player.setSpriteCounter(counter);
            if (counter > 12) { // walking animation speed
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
     * Basic tile collision check. Migrated from CollisionChecker.checkTile()
     */
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

        // Bounds check
        if (leftCol < 0 || rightCol >= gameMap.getMaxCol() ||
                topRow < 0 || bottomRow >= gameMap.getMaxRow()) {
            return true; // Out of bounds = collision
        }

        var tiles = mapService.getTileDataList();

        // Check the two tiles the entity would be touching based on direction
        int tileNum1 = gameMap.getTileAt(leftCol, topRow);
        int tileNum2 = gameMap.getTileAt(rightCol, topRow);
        int tileNum3 = gameMap.getTileAt(leftCol, bottomRow);
        int tileNum4 = gameMap.getTileAt(rightCol, bottomRow);

        if (tileNum1 >= 0 && tileNum1 < tiles.size() && tiles.get(tileNum1).isCollision())
            return true;
        if (tileNum2 >= 0 && tileNum2 < tiles.size() && tiles.get(tileNum2).isCollision())
            return true;
        if (tileNum3 >= 0 && tileNum3 < tiles.size() && tiles.get(tileNum3).isCollision())
            return true;
        if (tileNum4 >= 0 && tileNum4 < tiles.size() && tiles.get(tileNum4).isCollision())
            return true;

        return false;
    }

    // === Session management for game loop ===

    /**
     * Get all active session IDs.
     */
    public java.util.Set<String> getAllSessionIds() {
        return sessions.keySet();
    }

    /**
     * Get a compact delta state suitable for broadcasting each tick.
     * Only includes data that changes frequently (position, direction, animation,
     * combat state).
     */
    public Map<String, Object> getDeltaState(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null)
            return null;

        // Only send delta when in PLAY state (or TITLE for initial state)
        Map<String, Object> state = new HashMap<>();
        state.put("sessionId", sessionId);
        state.put("gameState", session.getGameState().name());
        state.put("tick", session.getTick());

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
