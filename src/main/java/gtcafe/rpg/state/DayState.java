package gtcafe.rpg.state;

public enum DayState {
    DAY(0, "Day"),
    DUSK(1, "Dusk"),    // 黃昏
    NIGHT(2, "Night"),
    DAWN(3, "Dawn"),    // 黎明

    ;

    public String name;
    public int value;
    private DayState(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
