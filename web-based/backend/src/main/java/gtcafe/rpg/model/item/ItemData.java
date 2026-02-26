package gtcafe.rpg.model.item;

public record ItemData(
        String id,
        String name,
        ItemType type,
        String spriteKey,
        String description,
        int attackValue,
        int defenseValue,
        int healValue,
        boolean stackable,
        int price) {
}
