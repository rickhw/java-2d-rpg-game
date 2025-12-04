
https://www.youtube.com/watch?v=8KitXuxcuqI&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=45

# Knockback

- Additional Combat Mechanics (戰鬥機制)
    - Player can cut the projectiles
    - Add Player.damageProjectile()
- KnockBack: 攻擊怪物的時候，怪物受到攻擊後，會退後幾步，增加打擊感
- Setting a KnockBack Power on Weapons
    - 不同武器有不同的 knockBackPower

---

# Video Summary

### 🎮 遊戲改進概述
- **新增戰鬥機制**：影片介紹了兩種新的戰鬥機制，分別是**削減飛行物**和**擊退效果**。

### ⚔️ 削減飛行物
- **功能實現**：通過將**專案列表**類型從**ArrayList**更改為**普通數組**，使得角色可以用武器削減敵人的飛彈。
- **時間控制**：設定削減的時間窗口，玩家需在特定的時機內完成削減，調整時間可增強遊戲的挑戰性。

### 🔄 擊退效果
- **擊退機制**：新增擊退效果，使得玩家的攻擊可以將敵人向後推。這一機制依賴於玩家的攻擊方向和武器的擊退力量。
- **武器多樣性**：每種武器可以設置不同的擊退力量，增強戰鬥策略性，例如劍的擊退力量為2，斧的為10。

### 📊 整體效果
- **策略選擇**：這些新增的戰鬥機制為玩家提供了更多的**戰術選擇**，提升了整體的遊戲體驗。
- **調整靈活性**：數據調整功能讓開發者可根據遊戲需求隨時修改參數，保持遊戲的平衡性和趣味性。