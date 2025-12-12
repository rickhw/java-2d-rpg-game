
https://www.youtube.com/watch?v=isawYK_HJ5k&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=59

# Dungeon Puzzle/Gimmick

1. Refactoring the image structure for monster and player
2. New Entity:
    - New weapon: Pickaxe (十字鎬/鋤頭)
    - New Interactive Tile: DestructibleWall (可以敲打的牆壁)
        - Define the object
        - 粒子效果 (particles)
        - Set the Asset
        - 可以讓打磚塊時候，隨機掉落一些物件 (像是打怪那樣): 實作 Entity.checkDrop() 即可
    - New Obj: OBJ_Door_Iron
        - 不能透過鑰匙打開的門
        - 需要透過特定的行為
    - New Iteractive Tile: IT_MetalPlate
    - New NPC: BigRock
        - disable update(), 避免他自己亂跑
        - Player 可以推動
3. Pickaxe 和 Destructible Wall 的互動
    - 在地下城放入可以敲打的牆壁 (Destructible Wall)，引導玩家去尋找十字鎬 (Pickaxe)
4. 推磚塊 解謎小關卡的設計：Detecing the rock on the plate
    - 計算 BigRock 和 MetalPlate 的交互
    - 計算交互後，DoorIron 是否打開
8. Reset rock location
    - BigRock 卡住了，切換場景可以重來


---

# Video Summary

### 🏰 地牢內容添加
- 在地牢中添加了一個**簡單的謎題**，更新了地牢地圖的走廊寬度。
- 創建了新的資源文件，包括**新怪物圖片、可破壞牆壁、互動瓷磚**和物品（如大鎬和金屬板）。

### ⚒️ 新物品與互動
- 添加了**大鎬**作為新武器，能夠用於攻擊可破壞物體。
- 破壞牆壁後可掉落物品，包括做為**暫時占位符的藥水**。

### 🔐 地牢謎題設計
- 創建了**特殊鐵門**，只能通過完成特定任務打開。
- 玩家需要將三個**大鎬**推動至三個**金屬板**上才能開啟鐵門。

### 🔧 程式邏輯
- 通過修改對象的移動邏輯和檢查距離，以確保大鎬的正確放置和互動。
- 設置條件以檢查連結狀態，並在所有大鎬到位時打開鐵門。

### ✉️ 其他功能優化
- 在退出地牢後，可以重置大鎬的位置，避免玩家卡住。
- 謝謝觀眾，期待在未來的視頻中加入**怪物與最終Boss**。