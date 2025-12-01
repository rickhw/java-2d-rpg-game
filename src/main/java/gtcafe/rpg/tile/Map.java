package gtcafe.rpg.tile;

public enum Map {
    WORLD_MAP(0, "World Map"),
    INTERIOR_01(1, "Interior01"),   // // 室內
    ;

    public int value;
    public String name;

    private Map(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
