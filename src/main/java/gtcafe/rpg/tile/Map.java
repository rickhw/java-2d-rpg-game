package gtcafe.rpg.tile;

public enum Map {
    WORLD_MAP(0, "World Map"),
    INTERIOR_01(1, "Interior01"),   // // 室內
    ;

    public int index;
    public String name;

    private Map(int index, String name) {
        this.index = index;
        this.name = name;
    }
}
