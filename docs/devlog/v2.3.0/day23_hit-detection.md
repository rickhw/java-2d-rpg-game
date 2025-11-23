
## Hit Detection

https://www.youtube.com/watch?v=HL39xRzPpm4&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=26

- Finetine the code
    - GameLoop: Clear EntityArray
    - Fix the starting dialogues by pressing ENTER alone + WASD
- Attack Animation
    - Load and scale attack images
    - Player attack animation
        - for up and left, due to the image position, need the excpetion handle the positions.
- Hit Detection
    - invisible


## Video Summary

### 🎮 更新代碼精簡
- 使用 `nttList.clear()` 簡化清空實體列表的過程，避免因為刪除元素導致 loop 環境錯誤。
- 玩家現在可以僅用 Enter 鍵來打開 NPC 對話框，簡化操作。

### ⚔️ 玩家攻擊動畫實作
- 為玩家攻擊創建兩張圖片，分別用於不同方向，但需確保圖片尺寸設定及合理縮放。
- 使用新方法來載入與設置攻擊圖片，以便於未來武器更換時的調整。

### 🔄 攻擊判斷與動畫邏輯
- 當玩家按下 Enter 鍵時，若無 NPC 在附近，則觸發攻擊動作。
- 添加額外變數來控制攻擊狀態，確保在攻擊期間不會因再次按 Enter 而觸發對話框。

### 🔍 碰撞檢測實作
- 定義 **攻擊範圍矩形**，以確保攻擊時能正確檢測到敵人。
- 在攻擊方法中，暫時修改玩家座標與碰撞區域，以檢查攻擊是否命中敵人。

### 💥 受傷與死亡邏輯
- 加入攻擊後的無敵狀態，以防止連續受傷，增強遊戲的可玩性。
- 檢查敵人生命值，實作HP減少邏輯，並在生命值為零時刪除敵人。

### ⚡ 效果與後續改進
- 計劃新增敵人被殺死時的動畫效果，以增強遊戲的視覺體驗。