package gtcafe.rpg.entity;

public enum EntityType {
    PLAYER(0),          // 玩家
    NPC(1),             // Non-Player Character
    MONSTER(2),         // 怪物

    // equipable
    SWORD(3),           // 劍
    AXE(4),             // 斧頭 
    PICKAXE(11),        // 十字鎬
    SHIELD(5),          // 盾
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
