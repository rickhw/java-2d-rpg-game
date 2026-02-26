package gtcafe.rpg.model.item;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {
    private static final Map<String, ItemData> ITEMS = new HashMap<>();

    static {
        // Weapons (Not stackable)
        register(new ItemData("sword_normal", "Normal Sword", ItemType.WEAPON, "sword_normal",
                "[Normal Sword]\\nAn old sword.\\nAttack: 2", 2, 0, 0, false, 150));
        register(new ItemData("axe", "Axe", ItemType.WEAPON, "axe",
                "[Axe]\\nA rusty axe.\\nAttack: 3", 3, 0, 0, false, 200));
        register(new ItemData("pickaxe", "Pickaxe", ItemType.WEAPON, "pickaxe",
                "[Pickaxe]\\nUsed for mining.\\nAttack: 1", 1, 0, 0, false, 100));

        // Shields (Not stackable)
        register(new ItemData("shield_wood", "Wood Shield", ItemType.SHIELD, "shield_wood",
                "[Wood Shield]\\nAn old wood shield.\\nDefense: 1", 0, 1, 0, false, 100));
        register(new ItemData("shield_blue", "Blue Shield", ItemType.SHIELD, "shield_blue",
                "[Blue Shield]\\nA shining blue shield.\\nDefense: 2", 0, 2, 0, false, 250));

        // Consumables (Stackable)
        register(new ItemData("potion_red", "Red Potion", ItemType.CONSUMABLE, "potion_red",
                "[Red Potion]\\nHeals your life by 5.", 0, 0, 5, true, 40));
        register(new ItemData("blueheart", "Blue Heart", ItemType.CONSUMABLE, "blueheart",
                "[Blue Heart]\\nHeals your life by 5.", 0, 0, 5, true, 40));

        // Tools / Quest Items (Not stackable or Stackable depends)
        register(new ItemData("lantern", "Lantern", ItemType.TOOL, "lantern",
                "[Lantern]\\nIlluminates your surroundings.", 0, 0, 0, false, 200));
        register(new ItemData("tent", "Tent", ItemType.TOOL, "tent",
                "[Tent]\\nYou can sleep to recover HP.", 0, 0, 0, false, 300));
        register(new ItemData("key", "Key", ItemType.KEY_ITEM, "key",
                "[Key]\\nIt opens a door.", 0, 0, 0, true, 100));
    }

    private static void register(ItemData item) {
        ITEMS.put(item.id(), item);
    }

    public static ItemData get(String id) {
        return ITEMS.get(id);
    }
}
