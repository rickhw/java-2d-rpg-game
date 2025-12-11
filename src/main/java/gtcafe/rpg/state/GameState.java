package gtcafe.rpg.state;

public enum GameState {
    TITLE(0, "Title Screen"),
    PLAY(1, "Playing"),
    PAUSE(2, "Pause"),
    DIALOGUE(3, "Dialogue"),
    CHARACTER(4, "Character"),
    OPTIONS(5, "Options"),
    GAME_OVER(6, "GameOver"),
    TRANSITION(7, "Transition"),
    TRADE(8, "Trade"),
    SLEEP(9, "Sleep"),
    DISPLAY_MAP(10, "Map"),
    
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
