package gtcafe.rpg.state;

public enum GameState {
    TITLE_STATE(0, "Title Screen"),
    PLAY_STATE(1, "Playing Mode"),
    PAUSE_STATE(2, "Pause Mode"),
    DIALOGUE_STATE(3, "Dialogue Mode"),
    CHARACTER_STATE(4, "Character State"),
    OPTIONS_STATE(5, "Options State"),
    GAME_OVER_STATE(6, "Game Over State"),
    TRANSITION_STATE(7, "Transition State"),
    TRADE_STATE(8, "Trade State"),
    
    ;

    int state;
    public String name;

    private GameState(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public int getState() {
        return this.state;
    }
}
