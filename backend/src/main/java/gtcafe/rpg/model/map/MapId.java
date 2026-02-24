package gtcafe.rpg.model.map;

public enum MapId {
    WORLD_MAP(0, "World1"),
    STORE(1, "Store"),
    DUNGEON01(2, "Dungeon01"),
    DUNGEON02(3, "Dungeon02");

    private final int index;
    private final String name;

    MapId(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
