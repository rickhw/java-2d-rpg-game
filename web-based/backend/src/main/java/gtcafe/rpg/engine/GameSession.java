package gtcafe.rpg.engine;

import java.util.ArrayList;
import java.util.List;

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

    // NPCs on the current map
    private List<GameEntity> npcs = new ArrayList<>();

    // Dialogue state
    private boolean inDialogue = false;
    private String dialogueSpeakerName;
    private String[] dialogueLines; // All lines in the current dialogue set
    private int dialogueLineIndex; // Current line being shown

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
}
