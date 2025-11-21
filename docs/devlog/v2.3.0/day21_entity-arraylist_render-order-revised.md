
## Entity ArrayList (Render Order Revised) 

https://www.youtube.com/watch?v=eNlcG1v0ofY&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=23

- Render Order (bug fix)
- Draw Player 會在 NPC 之後
    - If an entity A is below an entity B, we draw entity A later.
    - If an entity B is below an entity B, we draw enetiy B later
    - We use their worldY to compare.
    - it's work for single NPC, but if we have multiple NPCs, it doesn't work.
- Switching from SuperObject to Entity. We will not use SupperObject any more.
- Creating an ArrayList of Entity
    - We sort the order of the array. The entity that has the lowest worldY comes in index 0.
    - We draw entities in order of their worldY value.


## Video Summary

### 🔄 影片重發背景
- 這是Freanow的更新影片，針對之前關於**渲染順序**的內容進行修正。
- 原方案僅適用於特定情況，導致需要進行**大幅重構**。

### 💡 問題概述
- 當玩家的某些部分在NPC上方時，可能會發生**重疊顯示**問題。
- 之前的解決方案在只有一個NPC時有效，但當有多個NPC時不再適用。

### 📜 代碼重構
- 將所有物件類別的基礎類別由**super object**改為**entity**，並刪除超類。
- 將**圖像、名稱、碰撞**等變量從超類移至**entity**類。

### 🖼️ 渲染順序調整
- 創建一個**entity list**，包含所有實體（玩家、NPC、物品）。
- 根據**全域y坐標**對實體進行排序，以確保正確的渲染順序。

### ⚙️ 渲染過程
- 在繪製時，依序從**entity list**中提取實體並調用其繪製方法。
- 渲染後重置清單，以準備下一輪繪製。

### 🛠️ 測試與反饋
- 為測試渲染順序效能，添加了更小的solid area進行檢驗。
- 經過測試後，確認新的渲染系統運作正常。

### 🚀 下一步
- 在下一部影片中，將會**實現怪物機制**，並繼續進行其他新內容的開發。
