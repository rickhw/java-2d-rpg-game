# 循序圖

此圖表展示了遊戲主循環（Game Loop）期間，各個核心物件之間的互動順序。

```mermaid
sequenceDiagram
    participant Main
    participant GamePanel
    participant Player
    participant KeyHandler
    participant TileManager
    participant UI

    Main->>GamePanel: 1. 建立 GamePanel 物件
    Main->>GamePanel: 2. 呼叫 startGameThread()
    
    Note over GamePanel: gameThread 開始執行 run() 方法

    loop 遊戲主循環 (每 1/60 秒)
        
        %% Update Phase %%
        GamePanel->>GamePanel: 3. update()
        GamePanel->>Player: 4. 呼叫 player.update()
        Player->>KeyHandler: 5. 檢查按鍵狀態 (e.g., upPressed)
        Note over GamePanel: (同時更新所有 NPC 和物件)

        %% Render Phase %%
        GamePanel->>GamePanel: 6. repaint()
    end

    Note right of GamePanel: Swing/AWT 執行緒非同步呼叫 paintComponent()
    
    participant AWT_Thread as Swing/AWT 執行緒
    AWT_Thread->>GamePanel: 7. paintComponent(g)
    
    GamePanel->>TileManager: 8. 呼叫 tileM.draw(g2)
    Note over GamePanel: (依序繪製物件、NPC...)
    GamePanel->>Player: 9. 呼叫 player.draw(g2)
    GamePanel->>UI: 10. 呼叫 ui.draw(g2)

```

### 流程說明：

1.  **初始化**: `Main` 類別建立 `GamePanel` 的實例並啟動其主執行緒 `gameThread`。
2.  **遊戲循環**: `gameThread` 進入一個無限循環。
3.  **更新邏輯**: 在循環的每一輪，`GamePanel` 的 `update()` 方法被呼叫，它會觸發所有遊戲實體（如 `Player`）的更新。
4.  **輸入處理**: `Player` 在其 `update()` 方法中，會向 `KeyHandler` 查詢當前的按鍵狀態來決定如何移動。
5.  **請求渲染**: `update()` 完成後，`GamePanel` 呼叫 `repaint()`，向 Swing/AWT 系統請求重新繪製畫面。
6.  **執行渲染**: Swing/AWT 的繪圖執行緒（EDT）在適當的時機呼叫 `GamePanel` 的 `paintComponent()` 方法。
7.  **繪製畫面**: `paintComponent()` 按照固定的層次順序（背景、物件、玩家、UI）呼叫各個元件的 `draw()` 方法，最終將完整的遊戲畫面渲染到螢幕上。
