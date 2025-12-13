
https://www.youtube.com/watch?v=OSlM1zVKOGY&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=60

# Final Boss

1. Bug fix
    - Collision Bug
    - Invalid Chest status when save the status
    - Fixing the respawn location
2. Add new monster: Bat (蝙蝠)
    - 轉向更新速度快，可以增加 Bat 的特性
3. MON_Skeleton Lord 骷髏領主
    - get images
    - update direction for large entity in draw()
    - add god mode for debugging (X)
    - 因為 boss 太大了，所以 rendering system 要特別處理
        - rendering 根據左上角座標，決定是否畫
        - 所以要更新 camera 的判斷邏輯 Entity.draw()
    - 簡易的跟隨模式
4. Boss Phase 2 (Rage State) 憤怒階段

---

# Video Summary

### 🐛 錯誤修正
- 修正兩個主要錯誤：**碰撞錯誤**和**對話框顯示錯誤**。
- 在檢查物件碰撞時，需考慮物體的方向修改，以免角色在撞擊後被卡住。
- 當中斷開啟寶箱後，必須調用設置方法以顯示對話框文本。

### 👹 增加怪物
- 在地牢中新增怪物並調整他們的基本屬性，例如經驗值。
- 確定不使用路徑尋找功能，以增加怪物的挑戰性。

### 💡 增加光源
- 提升地牢的光線強度，以改善可視度並增強戰鬥體驗。
- 將最大黑暗程度從0.96調整為0.94。

### 🏰 最終BOSS設定
- 創建一個巨大骷髏戰士作為BOSS，並設定其圖像和屬性，如速度和攻擊範圍。
- 確保其行為模式與區域大小相適應，並計算中心點進行攻擊範圍的調整。

### 🕹️ AI行為和狀態管理
- 為BOSS新增簡易的追擊玩家功能，增加其攻擊模式的複雜性。
- 構建一個**狂暴狀態**，讓BOSS在生命值低於一半時提升攻擊力和速度。 

### ⚙️ 系統調整
- 調整物體的尺寸與範圍檢查，確保大物體的行為正常運作。
- 增加檢查頻率以平衡怪物的移動與行動間的時間間隔。

### 🎮 下一步
- 在下個視頻中將實作**過場動畫**以豐富BOSS戰的遊玩體驗。