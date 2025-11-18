# 系統設計

## 1. 總覽

本專案是一個使用 Java Swing 技術開發的 2D 角色扮演遊戲（RPG）。系統的核心是一個經典的遊戲循環（Game Loop），它負責以固定的幀率（FPS）更新遊戲狀態和渲染畫面。

## 2. 核心組件

系統由幾個關鍵組件構成，每個組件都有明確的職責。

- **`Main`**: 應用程式的進入點。其主要職責是建立一個 `JFrame` 作為遊戲視窗，並將 `GamePanel` 加入其中，然後啟動遊戲。

- **`GamePanel`**: 遊戲的核心引擎，繼承自 `JPanel` 並實現 `Runnable` 介面。
  - **遊戲循環**: `run()` 方法中包含了一個主循環，以固定的 FPS（預設為 60）不斷調用 `update()` 和 `repaint()`。
  - **組件協調者**: 初始化並持有所有主要遊戲系統的實例，如 `Player`, `TileManager`, `KeyHandler`, `CollisionChecker`, `UI` 等。它負責在遊戲循環中協調這些組件的更新與繪製。

- **`KeyHandler`**: 實現 `KeyListener` 介面，用於處理鍵盤輸入。它使用狀態輪詢（polling）機制，當按鍵被按下或釋放時，更新內部的布林（boolean）狀態旗標。遊戲邏輯（主要在 `Player` 中）會主動檢查這些旗標來決定是否執行相應的動作，從而將輸入事件與遊戲邏輯解耦。

- **`Entity`**: 一個抽象基礎類別，作為遊戲中所有動態實體（如玩家、NPC）的父類別。它定義了共通的屬性（如世界座標 `worldX`/`worldY`、速度 `speed`、方向 `direction`、碰撞區域 `solidArea`）和基礎方法（如 `update`, `draw`）。

- **`Player`**: 代表玩家控制的角色，繼承自 `Entity`。它的 `update()` 方法會連接到 `KeyHandler` 來處理玩家的移動輸入。攝影機總是跟隨玩家，使其保持在螢幕中心。

- **`TileManager`**: 負責載入地圖資料（從文字檔）和繪製遊戲世界的背景瓦片（tiles）。

- **`CollisionChecker`**: 提供檢查實體之間、實體與瓦片之間碰撞的工具方法。

- **`UI`**: 負責在螢幕上繪製使用者介面，例如玩家的生命值、訊息文字、遊戲結束畫面等。

## 3. 遊戲循環（Game Loop）

遊戲循環是 `GamePanel` 中 `gameThread` 的核心。

1.  **初始化**: `startGameThread()` 方法會建立並啟動一個新的執行緒。
2.  **循環**: 執行緒進入 `run()` 方法的循環中。
3.  **更新**: 在每次循環中，調用 `update()` 方法。此方法會更新所有遊戲實體（玩家、NPC）的狀態。
4.  **渲染**: 調用 `repaint()` 方法，這會觸發 AWT 執行緒調用 `paintComponent()`。
5.  **繪製順序**: `paintComponent()` 方法會嚴格按照順序繪製各個圖層，以確保正確的視覺效果：
    1.  地圖瓦片 (`tileManager.draw()`)
    2.  遊戲物件 (`object.draw()`)
    3.  玩家 (`player.draw()`)
    4.  NPC (`npc.draw()`)
    5.  使用者介面 (`ui.draw()`)

## 4. 輸入處理

輸入處理採用輪詢方式，而非事件驅動。

1.  `KeyHandler` 監聽鍵盤事件，並設定 `upPressed`, `downPressed` 等布林旗標。
2.  在遊戲循環的 `update()` 階段，`Player` 物件會讀取這些旗標。
3.  如果某個旗標為 `true`，`Player` 就會更新其方向和位置。

這種設計可以避免因作業系統事件佇列延遲而導致的輸入不穩定問題，並讓遊戲邏輯完全控制玩家的行為。
