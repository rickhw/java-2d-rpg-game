

https://www.youtube.com/watch?v=RoNr6opGjWc&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=35

# Item Drops

- Add new OBJ_COIN, and EntityType.PICKUPONLY
- For OBJ_Heart/Mana/Coin
    - Set type as PICKUPONLY, set Value
    - Add use() to calc the logic
- Reuse OBJ_Heart/Mana to draw to map via AssetSetter.java
- Drop the items when monster die.
    - checkDrop() and dropItem() in Entity
    - Monster checkDrop() by random
    - dropItem in monster position
---

# Video Summary

### 🚀 新增火球系統
- 實施了**投射物系統**，允許玩家消耗**法力**發射火球。
- 殺死怪物後，隨機掉落物品，提高遊戲挑戰性。

### 🎮 物品掉落機制
- 加入了新物品，包括**銅幣**，用於提升玩家的貨幣數量，但不存入背包。
- 每個怪物的掉落物品是隨機的，設定了不同掉落機率。

### 🔧 除錯與優化
- 移除**實體類別**中不必要的圖片寬度和高度參數，提高代碼整潔性。
- 更新物品類別，移動共用方法至**玩家類別**，減少冗餘代碼。

### 🍬 餵食物品與效果
- 物品如**全心**和**法力水晶**可放置在地圖上，玩家可以拾取並恢復生命或法力值。
- 新增**立即效果**的物品類型，提升玩家互動性。

### 🐉 怪物掉落設定
- 每個怪物死亡時會隨機決定掉落物品，根據設定的機率分配不同物品。
- 透過**隨機數生成器**實現掉落的多樣性，讓玩家在探索中獲得驚喜。

### 🏆 未來發展
- 計劃進一步擴展掉落物品的範圍，包括稀有裝備，以增加遊戲的深度和趣味性。
