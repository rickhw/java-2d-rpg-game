package gtcafe.rpg.entity;

public enum EntityType {
    PLAYER(0),
    NPC(1),
    MONSTER(2),
    SWORD(3),
    AXE(4),
    SHIELD(5),
    CONSUMABLE(6),
    PICKUPONLY(7)   // Coin
    ;

    int value;

    private EntityType(int value) {
        this.value = value;
    }
}
