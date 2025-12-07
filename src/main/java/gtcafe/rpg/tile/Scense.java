package gtcafe.rpg.tile;

public enum Scense {
    WORLD_MAP(0, "World1"),
    INTERIOR_01(1, "Interior01"),   // // 室內
    ;

    public int index;
    public String name;

    private Scense(int index, String name) {
        this.index = index;
        this.name = name;
    }
}
