package gtcafe.rpg.model.state;

public enum GameState {
    TITLE("Title Screen"),
    PLAY("Play"),
    PAUSE("Pause"),
    DIALOGUE("Dialogue"),
    CHARACTER("Character"),
    OPTIONS("Options"),
    GAME_OVER("GameOver"),
    TRANSITION("Transition"),
    TRADE("Trade"),
    SLEEP("Sleep"),
    DISPLAY_MAP("Map"),
    CUTSENSE("Cutsense");

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
