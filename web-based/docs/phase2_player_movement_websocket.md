# Phase 2: 玩家移動 + WebSocket 即時通訊

> 完成日期: 2026-02-24
> 狀態: ✅ 已完成

## 1. 概述

Phase 2 將原本 Java Swing 的單體遊戲循環，重構為 **前後端分離架構**。後端 Spring Boot 運行 60 ticks/s 的遊戲引擎，透過 WebSocket 即時推送狀態差異（Delta State）給前端。前端 React + Canvas 以 60 FPS 渲染，完全繞過 React 渲染週期，實現與原版遊戲相同的流暢度。

### 核心成果

| 功能 | 說明 |
|------|------|
| Server-side Game Loop | 60 ticks/s，ScheduledExecutorService 驅動 |
| Delta State Broadcasting | 每 tick 僅推送位置/動畫/戰鬥變更的差異 |
| Player Sprite Loading | 正確載入 walking/attacking/guarding 精靈圖 |
| Attack Animation | 根據攻擊方向動態調整精靈尺寸（16×32 / 32×16） |
| Camera Following | 攝影機中心跟蹤玩家 |
| Tile Collision | 伺服器端碰撞偵測（樹木、水域阻擋移動） |
| Zero React Re-render | 遊戲狀態全部用 Ref 管理，60 FPS 零 React 開銷 |

---

## 2. 架構設計

### 2.1 系統流程

```
┌─────────────────────────────────────────────────────────┐
│  Client (React + Canvas)         60 FPS render loop     │
│  ┌──────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │ Keyboard │  │ WebSocket    │  │ Canvas Renderer  │  │
│  │ Input    │──│ Client       │──│ (TileRenderer)   │  │
│  └────┬─────┘  └──────┬───────┘  └──────────────────┘  │
│       │               │ gameStateRef (no React state)   │
└───────┼───────────────┼─────────────────────────────────┘
        │ MOVE/ENTER_KEY│ FULL_STATE / DELTA_STATE
        │               │
┌───────┼───────────────┼─────────────────────────────────┐
│       ▼               ▼                                 │
│  ┌──────────────────────────────┐                       │
│  │ GameWebSocketHandler         │                       │
│  │  - processInput()            │                       │
│  │  - sendFullState() on ENTER  │                       │
│  └──────────────┬───────────────┘                       │
│                 │                                       │
│  ┌──────────────▼───────────────┐                       │
│  │ GameEngine                    │  ┌────────────────┐  │
│  │  - tick() → updatePlayer()   │◀─│ GameLoop        │  │
│  │  - getDeltaState()           │  │ 60 ticks/s      │  │
│  │  - checkTileCollision()      │  │ broadcast delta │  │
│  └──────────────────────────────┘  └────────────────┘  │
│  Server (Spring Boot 3.4)                               │
└─────────────────────────────────────────────────────────┘
```

### 2.2 訊息協議

#### Client → Server

```json
{ "type": "MOVE", "direction": "UP" }
{ "type": "MOVE_STOP" }
{ "type": "ENTER_KEY" }
{ "type": "GUARD_START" }
{ "type": "GUARD_STOP" }
```

#### Server → Client

**FULL_STATE** (連線時 + 狀態切換時)
```json
{
  "type": "FULL_STATE",
  "sessionId": "uuid",
  "gameState": "PLAY",
  "currentMap": "WORLD_MAP",
  "currentArea": "OUTSIDE",
  "dayState": "DAY",
  "player": { "worldX": 1472, "worldY": 1344, "direction": "DOWN", ... },
  "mapData": { "tileGrid": [[...]], "maxCol": 50, "maxRow": 50 }
}
```

**DELTA_STATE** (每 tick, 約 16.67ms)
```json
{
  "type": "DELTA_STATE",
  "gameState": "PLAY",
  "tick": 1234,
  "player": {
    "worldX": 1472, "worldY": 1340,
    "direction": "UP", "spriteNum": 2,
    "attacking": false, "guarding": false
  }
}
```

---

## 3. 後端實作細節

### 3.1 GameLoop.java（新增）

伺服器端遊戲迴圈，使用 `ScheduledExecutorService` 以 60 ticks/s 運行。

**設計決策**：
- 只對 `PLAY` 狀態的 session 執行 tick 和廣播，避免在 TITLE 畫面產生不必要的 DELTA_STATE（此為 Phase 2 中發現的 Bug 修正）
- 使用 daemon thread 確保 JVM 正常關閉

```java
@Component
public class GameLoop {
    private static final int TICK_RATE = 60; // 匹配原版遊戲 60 FPS
    private static final long TICK_INTERVAL_MS = 1000 / TICK_RATE; // ~16ms

    private void tick() {
        for (String sessionId : gameEngine.getAllSessionIds()) {
            GameSession session = gameEngine.getSession(sessionId);
            if (session.getGameState() != GameState.PLAY) continue; // 只 tick PLAY

            gameEngine.tick(sessionId);
            Map<String, Object> state = gameEngine.getDeltaState(sessionId);
            state.put("type", "DELTA_STATE");
            webSocketHandler.sendStateUpdate(sessionId, state);
        }
    }
}
```

### 3.2 GameEngine.java（修改）

新增方法：

| 方法 | 說明 |
|------|------|
| `getAllSessionIds()` | 回傳所有活躍 session ID（供 GameLoop 遍歷） |
| `getDeltaState(id)` | 回傳精簡的差異狀態（位置、動畫、戰鬥） |
| `removeSession(id)` | 清除 session（WebSocket 關閉時呼叫） |
| `incrementTick()` | 遞增 session 的 tick 計數器 |

**攻擊動畫邏輯**：
```java
if (player.isAttacking()) {
    int counter = player.getSpriteCounter() + 1;
    if (counter <= player.getMotion1Duration()) {
        player.setSpriteNum(1); // 揮起武器
    } else if (counter <= player.getMotion2Duration()) {
        player.setSpriteNum(2); // 揮下武器
    } else {
        player.setAttacking(false); // 攻擊結束
    }
}
```

### 3.3 GameWebSocketHandler.java（修改）

**關鍵修正**：

1. **Per-session 同步鎖**：防止 Game Loop 和 Input Handler 同時寫入同一 WebSocket session，解決 `TEXT_PARTIAL_WRITING` 錯誤
2. **Session 清理**：WebSocket 關閉時呼叫 `gameEngine.removeSession()` 防止孤兒 session
3. **ENTER_KEY 回覆 FULL_STATE**：狀態切換時（TITLE → PLAY）發送完整狀態（含 mapData），而非 DELTA

### 3.4 AssetController.java（修改）

精靈圖 API 端點從 `/sprites/{category}/{name}` 改為 `/sprites/**`（wildcard），支援深層路徑如：
```
GET /api/assets/sprites/player/walking/boy_down_1.png
GET /api/assets/sprites/player/attacking/sword/boy_attack_up_2.png
```

---

## 4. 前端實作細節

### 4.1 GameCanvas.tsx（重寫）

核心設計：**Zero React Re-render**

```
傳統 React 方式（每秒 60 次 re-render ❌）：
  WebSocket→setState()→React re-render→Canvas draw

我們的方式（零 re-render ✅）：
  WebSocket→gameStateRef.current=state  (ref, 不觸發 render)
  requestAnimationFrame→讀 ref→Canvas draw  (獨立 60 FPS loop)
```

**狀態合併策略**：
| 訊息類型 | 處理方式 |
|----------|----------|
| `FULL_STATE` | 完整替換 `gameStateRef.current`（含 mapData） |
| `DELTA_STATE` | 合併 player 變更到現有 state，保留 mapData |

### 4.2 TileRenderer.ts（修改）

攻擊精靈圖渲染根據方向設定不同尺寸：

| 方向 | 原始圖尺寸 | 渲染尺寸 | 偏移量 |
|------|-----------|----------|--------|
| 上攻擊 | 16×32 px | 64×128 | offsetY = -64（武器向上延伸） |
| 下攻擊 | 16×32 px | 64×128 | offsetY = 0（武器向下延伸） |
| 左攻擊 | 32×16 px | 128×64 | offsetX = -64（武器向左延伸） |
| 右攻擊 | 32×16 px | 128×64 | offsetX = 0（武器向右延伸） |
| 行走 | 16×16 px | 64×64 | 無偏移 |
| 防禦 | 16×16 px | 64×64 | 無偏移 |

### 4.3 AssetLoader.ts（修改）

修正精靈圖路徑以匹配實際檔案結構：
```
assets/sprites/player/walking/boy_{direction}_{1|2}.png
assets/sprites/player/attacking/sword/boy_attack_{direction}_{1|2}.png
assets/sprites/player/attacking/axe/boy_axe_{direction}_{1|2}.png
assets/sprites/player/guarding/boy_guard_{direction}.png
```

### 4.4 WebSocketClient.ts（修改）

新增 `intentionalClose` 旗標：
- `disconnect()` 設定 `intentionalClose = true`
- `onclose` callback 檢查旗標，若為 intentional 則**不自動重連**
- 解決 React StrictMode 雙重 mount 導致的 zombie 重連問題

### 4.5 KeyboardInput.ts（修改）

`Enter` 鍵改發送 `ENTER_KEY`（取代 `ATTACK`），由後端根據遊戲狀態決定行為：
| 狀態 | ENTER_KEY 行為 |
|------|---------------|
| TITLE | 開始新遊戲（→ PLAY） |
| PLAY | 攻擊動作 |
| DIALOGUE | 推進對話（TODO） |

---

## 5. 開發過程中的 Bug 修正

### Bug 1: 標題畫面與地圖之間閃爍

**症狀**：進入遊戲後畫面不定時閃爍，在標題畫面和地圖之間跳躍

**根因**：
1. Game Loop 對 TITLE 狀態的 session 也發送 DELTA_STATE（不含 mapData）
2. React StrictMode 雙重 mount 產生多個 WebSocket 連線
3. `DELTA_STATE` 可能在 `FULL_STATE` 之前到達，導致 mapData 遺失

**修正**：`GameLoop` 只對 `PLAY` 狀態的 session 執行 tick 和廣播

### Bug 2: Enter 鍵無法進入遊戲

**症狀**：標題畫面按 Enter 沒有反應

**根因**：React StrictMode cleanup 呼叫 `wsClient.disconnect()`（設 ws=null），但 `onclose` 仍觸發並呼叫 `tryReconnect()`，創建 zombie 連線

**修正**：新增 `intentionalClose` 旗標，`disconnect()` 後不再自動重連

### Bug 3: 攻擊時精靈圖被壓縮

**症狀**：按 Enter 攻擊時，玩家角色被壓扁成一半大小

**根因**：攻擊精靈圖為 16×32 (上下) / 32×16 (左右)，但統一以 64×64 渲染

**修正**：TileRenderer 根據攻擊方向設定不同的 drawWidth/drawHeight 和 offset

### Bug 4: 動畫過慢（慢動作感）

**症狀**：走路、攻擊、防禦都像慢動作，FPS 感覺在 20 以下

**根因**：Server tick rate = 20/s，但原版遊戲跑 60 FPS

**修正**：Tick rate 從 20 提升到 60，速度從 8 調回原始值 4（60 ticks/s × 4 px = 240 px/s）

---

## 6. 檔案變更清單

### Backend 新增

| 檔案 | 說明 |
|------|------|
| `engine/GameLoop.java` | 60 ticks/s 伺服器端遊戲迴圈 |

### Backend 修改

| 檔案 | 變更 |
|------|------|
| `engine/GameEngine.java` | 新增 `getAllSessionIds()`, `getDeltaState()`, `removeSession()`, 攻擊動畫邏輯 |
| `engine/GameSession.java` | 新增 tick 計數器 |
| `controller/AssetController.java` | Sprite endpoint 支援 wildcard deep path |
| `websocket/GameWebSocketHandler.java` | Per-session 同步鎖 + session 清理 + ENTER_KEY FULL_STATE 回覆 |

### Frontend 修改

| 檔案 | 變更 |
|------|------|
| `game/GameCanvas.tsx` | 完全重寫：零 React state、ref-only、FULL/DELTA 合併 |
| `game/renderer/TileRenderer.ts` | 攻擊精靈圖動態尺寸渲染 |
| `game/assets/AssetLoader.ts` | 修正精靈圖路徑 + 載入全部 walking/attacking/guarding |
| `game/network/WebSocketClient.ts` | intentionalClose 防 zombie 重連 |
| `game/input/KeyboardInput.ts` | Enter → ENTER_KEY（context-dependent） |
| `App.tsx` | 簡化為靜態外框，移除所有 state 管理 |

---

## 7. 下一步：Phase 3

Phase 3 預計實作 **NPC 系統 + 對話系統**：
- 後端：NPC 實體管理、隨機移動 AI、對話資料結構
- 前端：NPC 精靈圖渲染、對話框 UI
- WebSocket：NPC 位置同步、對話觸發/推進
