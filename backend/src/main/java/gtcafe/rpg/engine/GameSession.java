package gtcafe.rpg.engine;

import gtcafe.rpg.model.entity.GameEntity;
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
}
