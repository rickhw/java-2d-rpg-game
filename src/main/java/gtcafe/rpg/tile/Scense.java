package gtcafe.rpg.tile;

public enum Scense {
    WORLD_MAP(0, "World1"),
    STORE(1, "Store"),   // 室內
    DONGEON01(2, "Dungeon01"),   // 地下城
    DONGEON02(3, "Dungeon02"),   // 地下城
    ;

    public int index;
    public String name;

    private Scense(int index, String name) {
        this.index = index;
        this.name = name;
    }
}
