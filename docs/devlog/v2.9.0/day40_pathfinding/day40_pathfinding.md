
https://www.youtube.com/watch?v=Hd0D68guFKg&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=44

# Pathfinding

The algorithm: A-star search algorithm (https://www.youtube.com/watch?v=2JNEme00ZFA)

1. Guide the NPC to a specific location
2. NPC follows the player
3. Monsters get aggro and start chasing the player

---

# Video Summary

### 🗺️ 路徑尋找演算法
- 本視頻將展示如何實現**路徑尋找演算法**，使角色能在避免固體瓦片的情況下移動。
- 使用的演算法為**A-Star搜索**（A*），視頻中不會詳細解釋該算法，建議觀看之前的基礎教學視頻。

### 👴 NPC行為範例
- 提供了三個範例： 
  - 角色（例如老爺爺）移動至地圖上的特定目標點。
  - 角色跟隨玩家角色移動。
  - 怪物追逐玩家角色。

### 🚧 結構設計
- 創建名為**Node**的類來定義路徑中的各個節點，包含父節點、成本、固體狀態等屬性。
- 創建**PathFinder**類來管理路徑尋找的邏輯，包括節點的重置和搜索。

### 🏃 NPC行動設計
- NPC在玩家觸發對話後開始向特定位置移動，根據目標位置設置路徑。
- NPC能夠追隨玩家並在玩家接近時觸發行為。

### 🧟 怪物行為設計
- 通過檢查玩家距離來決定怪物是否變得**攻擊性（Aggro）**，如在玩家攻擊或接近時開始追逐。
- 可設置隨機機率，使追逐行為不那麼機械化。

### 🎮 增進遊戲體驗
- 經由**A-Star路徑尋找演算法**的實施，NPC和怪物能夠有效地尋找路徑，增強了遊戲的互動性和挑戰性。
- 鼓勵開發者利用該技術以創造更有趣的遊戲機制。