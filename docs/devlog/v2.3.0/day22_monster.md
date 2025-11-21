
## Monster

https://www.youtube.com/watch?v=Fb9wjyG01eQ&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=24

- Implement Monsters
- Handle collision
    - Checking
    - collision between Entities
    
- Handle Damage
    - Player.update() handle 60 time per second, to avoid quick damage, creating invincible time (暫時無敵).
    - Player touch monster
    - Monster attack player
    - Visual effect to invincible state if player is attacking

## Video Summary

### 🐉 主要更新內容
- **怪物實作**: 在遊戲中新增了綠色史萊姆作為怪物，並設置影像用於所有方向的呈現。
- **碰撞檢測**: 玩家現在當接觸到怪物時會受損，並新增了碰撞檢測機制以確保怪物和玩家之間可以互動。

### ⚙️ 程式設計流程
- **類別與包管理**: 在源文件夾中創建了名為`monster`的包，並在其中建立了`Monster`類以處理怪物屬性與行為。
- **行為設置功能**: 使用隨機AI來設定怪物的初步行為，並設計了一個魚眼檢測機制以生成與顯示不同的怪物。

### ⚔️ 受傷與無敵狀態
- **受傷邏輯**: 當玩家碰到怪物時，其生命值減少，並在碰撞時實施了損傷計算。
- **無敵時間**: 引入無敵時間以避免玩家在受傷後瞬間失去生命，併設計了一個計時器來管理這個設定。

### 🖥️ 可視化效果
- **狀態視覺化**: 玩家在無敵狀態下變得半透明，以便視覺上更容易辨認其狀態，提升遊戲體驗。

### 🔄 代碼優化
- **簡化重複程式碼**: 將多次出現的碰撞檢查程式碼進行統一管理，減少冗餘，提升效能。 

### ⚡ 下一步計畫
- **攻擊機制**: 下一次將實作玩家的攻擊功能，以便玩家能夠反擊怪物，增加遊戲互動性。