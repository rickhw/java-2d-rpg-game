

https://www.youtube.com/watch?v=NN0iQZX_51M&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=34

# Monster Projectiles and Mana

- Implementing monster projectiles
- Mana UI and useCost value
- Healing Pool: also recovery mana

---

# Video Summary

### 🎮 物件類別與方法重構
- 將**怪物傷害**處理從更新方法轉移到分開的方法中，類似於玩家類別的實現。
- 方法名稱為`damage player`，並且透過此方法接受**攻擊參數**。

### 🔥 射擊機制實現
- 在射擊項目上處理玩家的**碰撞檢查**，若玩家處於**非無敵狀態**，則會對玩家造成傷害。
- 添加了一種新的**非魔法**射擊項目，稱為**鎖**，用單一圖像來代表所有方向。

### 💎 資源管理
- 對於投射物（如火球），使用**魔法**作為資源，並設置最大魔法值為4。
- 使用**藍水晶**圖像來表示玩家的魔法，並在UI類別中進行繪製。

### 💡 資源檢查與減少
- 創建`have resource`和`subtract resource`方法以處理資源的檢查和減少，以確保玩家在施放魔法時擁有足夠的資源。
- 每個投射物類別可以有不同的資源需求，例如火球需要魔法、而石頭則不需要。

### ⚙️ 系統靈活性
- 實現的資源系統可擴展至其他型態的投射物，例如**箭**和**子彈**，以適應不同的遊戲風格。
- 根據需求可以自由調整這些方法和資源類型以符合遊戲設計。