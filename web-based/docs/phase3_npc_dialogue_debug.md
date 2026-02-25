# Phase 3: NPC 系統 + 對話系統 + Debug Mode

> 完成日期: 2026-02-25
> 狀態: ✅ 已完成

## 1. 概述

Phase 3 實作了 NPC 系統、對話系統、以及 Debug Mode。NPC 擁有隨機移動 AI，能與地圖磚和玩家產生碰撞。玩家面對 NPC 按下 Enter 可觸發對話，對話期間所有實體凍結。Debug Mode（按 T 切換）顯示碰撞框、實體座標和遊戲狀態資訊，匹配原版 Java 遊戲的 debug 功能。

### 核心成果

| 功能 | 說明 |
|------|------|
| NPC 放置 | OldMan "Steve" 放在 tile (18, 20)，匹配原版 AssetSetter |
| NPC 隨機移動 AI | 每 120 ticks (~2秒) 隨機換方向，speed=1 |
| NPC-Tile 碰撞 | NPC 無法穿過碰撞磚（樹木、水域等） |
| NPC-Player 碰撞 | 雙向碰撞：玩家不穿 NPC、NPC 不穿玩家 |
| NPC 精靈圖 | 載入 oldman 8 方向行走精靈圖 (up/down/left/right × 2) |
| 對話系統 | 3 組多行對話，隨機選擇，Enter 推進/關閉 |
| 對話凍結 | 對話中 NPC 停止移動、玩家停止移動 |
| NPC 面向玩家 | 對話時 NPC 自動面轉向玩家 |
| Debug Mode | 按 T 顯示碰撞框、座標、遊戲狀態面板 |
| Delta 同步 | NPC 位置 + 對話狀態每 tick 透過 DELTA_STATE 廣播 |

---

## 2. 架構設計

### 2.1 NPC 資料流

```
┌─────────────────────────────────────────────────────────────┐
│  Client                                                     │
│  ┌────────────┐     ┌──────────────┐    ┌────────────────┐  │
│  │ Keyboard   │ T → │ debugRef     │    │ TileRenderer   │  │
│  │ Input      │     │ (client-side)│    │ .drawNPCs()    │  │
│  └────┬───────┘     └──────────────┘    │ .drawPlayer()  │  │
│       │ ENTER_KEY                       └───────┬────────┘  │
│       │                                         │           │
│  ┌────┼──── GameCanvas.tsx ─────────────────────┘           │
│  │    │  gameStateRef.current.npcs[]                        │
│  │    │  gameStateRef.current.dialogue{}                    │
│  │    │  drawDialogueBox() / drawDebugOverlay()             │
│  └────┼─────────────────────────────────────────────────────┘
│       │ WebSocket
├───────┼─────────────────────────────────────────────────────┤
│  ┌────▼────────────────────────────────┐                    │
│  │ GameEngine                          │                    │
│  │  - createWorldMapNPCs()             │                    │
│  │  - updateNPCs()  ← 隨機移動 AI      │                    │
│  │  - findNpcInFront()  ← 互動偵測     │                    │
│  │  - startDialogue() / advanceDialogue│                    │
│  │  - checkEntityCollision()           │                    │
│  └─────────────────────────────────────┘                    │
│  Server (Spring Boot 3.4)                                   │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 訊息協議擴充

#### Server → Client（DELTA_STATE 新增欄位）

```json
{
  "type": "DELTA_STATE",
  "gameState": "PLAY",
  "tick": 1234,
  "player": { "worldX": 1472, "worldY": 1340, ... },
  "npcs": [
    {
      "id": "npc_oldman_1",
      "worldX": 1152, "worldY": 1280,
      "direction": "DOWN", "spriteNum": 1,
      "spriteKey": "oldman"
    }
  ],
  "dialogue": {
    "speaker": "Steve",
    "line": "Hello, lad! I used to be an\nadventurer like you...",
    "hasNext": true
  }
}
```

`dialogue` 欄位僅在對話中出現，否則不包含。

---

## 3. 後端實作細節

### 3.1 GameSession.java（修改）

新增欄位：

| 欄位 | 型別 | 說明 |
|------|------|------|
| `npcs` | `List<GameEntity>` | 當前地圖上的 NPC 列表 |
| `inDialogue` | `boolean` | 是否正在對話中 |
| `dialogueSpeakerName` | `String` | 當前對話者名稱 |
| `dialogueLines` | `String[]` | 當前對話組的所有台詞 |
| `dialogueLineIndex` | `int` | 正在顯示的台詞索引 |

### 3.2 GameEngine.java（修改）

#### NPC 建立

```java
private List<GameEntity> createWorldMapNPCs() {
    GameEntity oldMan = new GameEntity("npc_oldman_1", "Steve", EntityType.NPC);
    oldMan.setWorldX(18 * EFFECTIVE_TILE_SIZE);  // tile (18, 20)
    oldMan.setWorldY(20 * EFFECTIVE_TILE_SIZE);
    oldMan.setSpeed(1);
    oldMan.setSpriteKey("oldman");
    oldMan.setSolidAreaX(8);  oldMan.setSolidAreaY(16);
    oldMan.setSolidAreaWidth(48);  oldMan.setSolidAreaHeight(48);
    // ...
}
```

#### NPC 隨機移動 AI（from `NPC_OldMan.setAction()`）

```java
private void updateNPCs(GameSession session) {
    for (GameEntity npc : session.getNpcs()) {
        if (session.isInDialogue()) continue;  // 對話中凍結
        
        // 每 120 ticks 隨機換方向
        if (npc.getActionLockCounter() >= 120) {
            int r = random.nextInt(100) + 1;
            if (r <= 25) npc.setDirection(Direction.UP);
            else if (r <= 50) npc.setDirection(Direction.DOWN);
            // ...
        }

        // 碰撞：tile + player
        if (!checkTileCollision(...) && !checkEntityCollision(player, npc, ...)) {
            npc.setWorldX(nextX);
            npc.setWorldY(nextY);
        }
    }
}
```

#### NPC 互動偵測

```java
private GameEntity findNpcInFront(GameSession session) {
    // 根據玩家面向方向，檢查前方一格 (EFFECTIVE_TILE_SIZE) 是否有 NPC
    // 使用 AABB 距離判定
}
```

#### 對話系統

| 方法 | 說明 |
|------|------|
| `startDialogue(session, npc)` | NPC 面向玩家、隨機選對話組、設定 session 對話狀態 |
| `advanceDialogue(session)` | 推進到下一句，最後一句後關閉對話 |
| `findNpcInFront(session)` | 偵測玩家面前一格是否有 NPC |

#### 碰撞系統

| 方法 | 說明 |
|------|------|
| `checkNpcCollision()` | 檢查玩家移動時是否撞到 NPC |
| `checkEntityCollision()` | 通用 AABB 碰撞偵測（NPC 移動時檢查是否撞到玩家） |

#### ENTER_KEY 行為擴充

```java
case "ENTER_KEY" -> {
    case PLAY -> {
        if (session.isInDialogue()) {
            advanceDialogue(session);  // 推進對話
        } else {
            GameEntity nearbyNpc = findNpcInFront(session);
            if (nearbyNpc != null) {
                startDialogue(session, nearbyNpc);  // 面前有 NPC → 對話
            } else {
                player.setAttacking(true);  // 沒有 NPC → 攻擊
            }
        }
    }
}
```

### 3.3 Direction.java（修改）

新增 `opposite()` 方法，用於對話時 NPC 面向玩家：

```java
public enum Direction {
    UP, DOWN, LEFT, RIGHT, ANY;

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case ANY -> ANY;
        };
    }
}
```

### 3.4 對話台詞資料

靜態定義在 `GameEngine` 中（from `NPC_OldMan.setDialogue()`）：

```java
NPC_DIALOGUES.put("oldman", new String[][] {
    {   // 對話組 0
        "Hello, lad! I used to be an\nadventurer like you...",
        "So you've come to this island\nto find the treasure?",
        "I used to be a great wizard but\nnow... I'm a bit too old.",
        "Well, good luck to you."
    },
    {   // 對話組 1
        "If you become tired, rest at\nthe water.",
        "However, the monsters reappear\nif you rest.",
        "In any case, don't push\nyourself too hard."
    },
    {   // 對話組 2
        "I wonder how to open\nthat door..."
    }
});
```

---

## 4. 前端實作細節

### 4.1 TypeScript 型別（types/game.ts）

新增介面：

```typescript
export interface NpcState {
    id: string;
    name?: string;
    worldX: number;
    worldY: number;
    direction: Direction;
    spriteNum: 1 | 2;
    spriteKey: string;
}

export interface DialogueState {
    speaker: string;
    line: string;
    hasNext: boolean;
}
```

`GameFullState` 新增：
- `npcs?: NpcState[]`
- `dialogue?: DialogueState`

`EntityState` 新增：
- `solidAreaX/Y/Width/Height` — 供 debug 碰撞框渲染

### 4.2 AssetLoader.ts（修改）

新增 `loadNpcSprites()` 方法：

```typescript
async loadNpcSprites(): Promise<void> {
    const npcNames = ['oldman'];
    const directions = ['up', 'down', 'left', 'right'];
    for (const npcName of npcNames) {
        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                // key: "oldman_down_1", path: "/api/assets/sprites/npc/oldman_down_1.png"
                await this.loadSprite(key, path);
            }
        }
    }
}
```

### 4.3 TileRenderer.ts（修改）

新增 `drawNPCs()` 方法：

```typescript
drawNPCs(state: GameFullState) {
    for (const npc of state.npcs) {
        // 計算 NPC 相對攝影機的螢幕座標
        const screenX = npc.worldX - player.worldX + playerScreenX;
        const screenY = npc.worldY - player.worldY + playerScreenY;

        // 根據 spriteKey + direction + spriteNum 查找精靈圖
        const spriteKey = npc.spriteKey + '_' + dir + '_' + npc.spriteNum;
        const img = this.assetLoader.getSpriteImage(spriteKey);
        ctx.drawImage(img, screenX, screenY, EFFECTIVE_TILE_SIZE, EFFECTIVE_TILE_SIZE);
    }
}
```

**渲染順序**：`drawTiles()` → `drawNPCs()` → `drawPlayer()` → `drawHUD()` → `drawDialogueBox()`

### 4.4 GameCanvas.tsx（修改）

#### Delta 合併新增 NPC 和 Dialogue

```typescript
if (state.type === 'DELTA_STATE' && current) {
    const merged = { ...current, gameState: state.gameState };
    merged.player = { ...current.player, ...state.player };
    if (state.npcs) merged.npcs = state.npcs;      // 新增
    merged.dialogue = state.dialogue ?? undefined;  // 新增
    gameStateRef.current = merged;
}
```

#### 對話框 UI

RPG 風格對話框，繪製在畫面底部：

| 元素 | 樣式 |
|------|------|
| 背景 | 半透明黑 (`rgba(0,0,0,0.85)`)，圓角 12px |
| 邊框 | 金色 (`#ffd700`)，3px 寬 |
| 講者名稱 | 金色粗體 18px |
| 分隔線 | 金色半透明 |
| 台詞文字 | 白色 16px，支援 `\n` 換行 |
| 提示符號 | 右下角閃爍：`▼ ENTER` (有下一句) / `■ CLOSE` (最後一句) |

```
╭────────────────────────────────────────────────╮
│ Steve                                          │
│ ────────────────────────────────────────────    │
│ Hello, lad! I used to be an                    │
│ adventurer like you...                         │
│                                     ▼ ENTER    │
╰────────────────────────────────────────────────╯
```

### 4.5 Debug Mode（新增）

按 `T` 切換，純前端實作（不需 WebSocket roundtrip）。

| 元素 | 顏色 | 說明 |
|------|------|------|
| Player 碰撞框 | 🔴 `#ff0000` | 紅色方框顯示 solidArea |
| Player 座標 | 🟢 `#00ff00` | 精靈圖上方顯示 col,row |
| NPC 碰撞框 | 🟢 `#44ff44` | 淺綠方框顯示 solidArea |
| NPC 座標 | 🟢 `#44ff44` | 精靈圖上方顯示 col,row |
| Debug 面板 | 🟡 `#ffff00` | 左下角黑底面板 |

Debug 面板顯示資訊（匹配原版 `GamePanel.java`）：

```
Map: WORLD_MAP
State: PLAY
WorldX: 1472
WorldY: 1344
Col: 23  Row: 21
Direction: DOWN
```

---

## 5. 開發過程中的 Bug 修正

### Bug 1: NPC 穿過 Player

**症狀**：NPC 隨機行走時直接穿過玩家身體

**根因**：`updateNPCs()` 只檢查 tile 碰撞，沒有檢查 player 碰撞

**修正**：新增 `checkEntityCollision()` AABB 碰撞偵測，NPC 移動前同時檢查 tile 和 player

### Bug 2: 對話中 NPC 繼續移動

**症狀**：打開對話框後 NPC 仍在背景隨機走動

**根因**：`updateNPCs()` 沒有判斷對話狀態

**修正**：在 `updateNPCs()` 開頭加入 `if (session.isInDialogue()) continue;` 凍結所有 NPC

### Bug 3: 對話框底部超出畫面

**症狀**：對話框下緣跑到 Canvas 可見區域之外

**根因**：`boxY` 和 `boxH` 數值設定未考慮 Canvas 實際可見高度

**修正**：經多次調整，最終 `boxY = SCREEN_HEIGHT - 220`、`boxH = 120`，保證 100px 底部餘裕

### Bug 4: Player solidArea 偏左

**症狀**：Debug Mode 顯示碰撞框偏向 Player 精靈圖左側

**根因**：`solidAreaX = 8`（原版值），在 64px tile 中不置中

**修正**：`solidAreaX` 從 8 調整為 16，碰撞框水平置中 (16~48)

---

## 6. 檔案變更清單

### Backend 修改

| 檔案 | 變更 |
|------|------|
| `engine/GameSession.java` | 新增 `npcs` 列表、對話狀態欄位 (`inDialogue`, `dialogueSpeakerName`, `dialogueLines`, `dialogueLineIndex`) |
| `engine/GameEngine.java` | 新增 NPC 建立 (`createWorldMapNPCs`)、隨機移動 AI (`updateNPCs`)、互動偵測 (`findNpcInFront`)、對話系統 (`startDialogue`, `advanceDialogue`)、碰撞偵測 (`checkEntityCollision`, `checkNpcCollision`)、NPC+對話 delta 廣播 |
| `model/state/Direction.java` | 新增 `opposite()` 方法 |

### Frontend 修改

| 檔案 | 變更 |
|------|------|
| `types/game.ts` | 新增 `NpcState`, `DialogueState` 介面；`GameFullState` 新增 `npcs`, `dialogue`；`EntityState` 新增 `solidArea*` 欄位 |
| `game/assets/AssetLoader.ts` | 新增 `loadNpcSprites()` 載入 oldman 8 方向精靈圖 |
| `game/renderer/TileRenderer.ts` | 新增 `drawNPCs()` 方法（攝影機相對座標渲染 NPC） |
| `game/GameCanvas.tsx` | Delta 合併 NPC+對話、渲染 NPC、`drawDialogueBox()` 對話框 UI、`drawDebugOverlay()` Debug 模式、`debugRef` + `fpsRef` + T 鍵監聽 |

### 無變更

| 檔案 | 說明 |
|------|------|
| `game/input/KeyboardInput.ts` | `KeyT → DEBUG_TOGGLE` 已在 Phase 2 存在 |
| `game/network/WebSocketClient.ts` | 無變更 |

---

## 7. 下一步：Phase 4

Phase 4 預計方向（尚未決定）：

- **地圖切換系統**：門 / 入口觸發場景轉換（World Map ↔ Store ↔ Dungeon）
- **物件系統**：地圖上的可互動物件（寶箱、鑰匙、門）
- **怪物系統**：Slime/Bat 怪物 AI + 碰撞傷害
