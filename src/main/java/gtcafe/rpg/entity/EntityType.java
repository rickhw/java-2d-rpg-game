package gtcafe.rpg.entity;

public enum EntityType {
    PLAYER(0),
    NPC(1),
    MONSTER(2),

    // equipable
    SWORD(3),
    AXE(4),
    SHIELD(5),
    LIGHT(9),           // 手持燈具
    SHOE(10),           // 鞋子

    CONSUMABLE(6),      // 一次性消耗
    PICKUPONLY(7),      // Coin, Heart, Mana ... etc
    OBSTACLE(8),        // 障礙

    ;

    int value;

    private EntityType(int value) {
        this.value = value;
    }
}
