# 系統設計文件 - 2D RPG 遊戲

本文件旨在說明此 2D RPG 遊戲的系統架構、主要組件及其互動方式。

## 1. 總體架構

此專案是一個使用 Java Swing 技術建構的 2D 角色扮演遊戲。其核心設計模式圍繞一個中央遊戲面板 (`GamePanel`)，由它來統一管理遊戲迴圈、渲染管線以及所有遊戲子系統。

- **進入點**: `Main.java` 是應用程式的起點，負責建立主視窗 (`JFrame`) 並將 `GamePanel` 實例化，從而啟動遊戲。
- **核心引擎**: `GamePanel.java` 是整個遊戲的心臟。它實作了 `Runnable` 介面以建立主遊戲迴圈，並負責初始化和協調所有關鍵組件。
- **渲染機制**: 渲染工作在 `GamePanel` 的 `paintComponent` 方法中完成，遵循固定的繪製順序：`Tiles` -> `Objects` -> `Player`，以此形成 2D 畫面的層次感。
- **實體系統**: `Entity.java` 是所有動態角色的基底類別，而 `Player.java` 繼承自 `Entity`，並加入了處理玩家輸入和互動的特定邏輯。
- **世界與地圖**: `TileManager.java` 負責載入圖塊資源，並從文字檔 (`.txt`) 中解析地圖佈局，最終繪製出遊戲世界。
- **物件與道具**: `SuperObject.java` 是所有可互動道具的基底類別，定義了共同屬性。其子類別（如 `OBJ_Key`）則實作具體的互動行為。

## 2. 主要組件職責

| 類別/檔案 | 主要職責 |
| :--- | :--- |
| `Main.java` | 應用程式進入點，建立主視窗和 `GamePanel`。 |
| `GamePanel.java` | 遊戲核心，管理遊戲迴圈、更新遊戲狀態、協調所有組件的渲染。 |
| `KeyHandler.java` | 處理所有鍵盤輸入事件，透過設定布林旗標來記錄按鍵狀態。 |
| `Entity.java` | 所有動態實體（玩家、NPC）的基底類別，定義了位置、速度、碰撞區等共同屬性。 |
| `Player.java` | 玩家角色的具體實作。根據 `KeyHandler` 的狀態來更新移動、處理與物件的互動。 |
| `TileManager.java` | 管理遊戲地圖的載入與繪製。從文字檔讀取地圖資料，並定義哪些圖塊是固體（不可穿越）。 |
| `SuperObject.java` | 所有遊戲內物件（如鑰匙、門）的抽象基底類別。 |
| `CollisionChecker.java` | 提供碰撞檢測功能。在實體移動前，檢查其是否會與地圖圖塊或其他物件發生碰撞。 |
| `AssetSetter.java` | 在遊戲開始時，負責在世界中初始化並放置各種物件（如道具、寶箱）。 |

## 3. 核心流程

### 3.1. 遊戲迴圈 (Game Loop)

遊戲迴圈位於 `GamePanel.java` 的 `run()` 方法中。它採用了 `delta time` 的策略來維持穩定的 FPS（每秒影格數）。

1.  計算自上次更新以來經過的時間 (`delta`)。
2.  根據 `delta` 累加計時器，當計時器超過 `drawInterval`（即 1/FPS）時，觸發一次更新和渲染。
3.  呼叫 `update()` 方法來更新所有遊戲實體的狀態（如玩家位置）。
4.  呼叫 `repaint()` 方法，觸發 `paintComponent` 的執行，重新繪製整個畫面。
5.  重複此過程，形成連續的動畫效果。

### 3.2. 輸入處理 (Input Handling)

1.  `KeyHandler` 實作了 `KeyListener` 介面，監聽 `keyPressed` 和 `keyReleased` 事件。
2.  當玩家按下或放開按鍵時，`KeyHandler` 會更新對應的布林旗標（例如 `upPressed = true`）。
3.  在遊戲迴圈的 `update()` 階段，`Player` 物件會檢查這些旗標的狀態，以決定是否要進行移動。

這種狀態輪詢（Stateful Polling）的模式將原始的鍵盤事件與遊戲邏輯更新解耦，避免了在事件處理器中直接執行遊戲邏輯。

### 3.3. 渲染管線 (Rendering Pipeline)

1.  `GamePanel` 的 `repaint()` 被呼叫後，`paintComponent(Graphics g)` 方法會被執行。
2.  `TileManager.draw(g2)`：首先繪製地圖背景。`TileManager` 會根據玩家在世界中的位置，計算出畫面上需要顯示哪些圖塊，並將它們繪製出來。
3.  `SuperObject.draw(g2)`：接著遍歷所有遊戲物件，並呼叫它們各自的 `draw` 方法。
4.  `Player.draw(g2)`：最後繪製玩家。玩家始終被繪製在螢幕的中央，以此實現攝影機跟隨的效果。所有其他元素（地圖、物件）的繪製位置都是相對於玩家的世界座標來計算的。

### 3.4. 資源管理 (Asset Management)

- **地圖**: 地圖佈局儲存在 `resources/gtcafe/rpg/assets/maps/` 目錄下的 `.txt` 檔案中。`TileManager` 在 `loadMap()` 方法中讀取這些檔案，將數字轉換為對應的圖塊索引。
- **圖片**: 所有圖片資源（如玩家動畫、物件圖示、圖塊）都存放在 `resources/gtcafe/rpg/assets/` 的相應子目錄下。各個類別（如 `Player`, `TileManager`, `SuperObject`）在其建構函式或初始化方法中，使用 `ImageIO.read()` 來載入所需的圖片。
