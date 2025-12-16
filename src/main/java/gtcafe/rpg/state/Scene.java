package gtcafe.rpg.state;

public enum Scene {
    NA(0, "N/A"),
    SKELETON_LORD(1, "Skeleton Lord"),
    ENDING(2, "Ending"),

    ;

    public String name;
    public int value;
    private Scene(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
