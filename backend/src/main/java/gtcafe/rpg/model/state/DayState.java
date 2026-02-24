package gtcafe.rpg.model.state;

public enum DayState {
    DAY("Day"),
    DUSK("Dusk"),
    NIGHT("Night"),
    DAWN("Dawn");

    private final String displayName;

    DayState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
