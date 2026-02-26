# Phase 4: 地圖切換系統 + 物件系統

> 開始日期: 2026-02-25
> 完成日期: 2026-02-26
> 狀態: ✅ 完成
> 版本: v1.4.0

## 1. 概述

Phase 4 實作兩個緊密耦合的系統：**地圖切換**和**地圖物件**。玩家踩到傳送點時，觸發轉場動畫並切換地圖。物件系統讓地圖上放置可拾取/互動的物品（鑰匙、寶箱、門、回復道具等）。

### 核心目標

| 功能 | 說明 | 優先級 | 狀態 |
|------|------|--------|------|
| 地圖傳送事件 | 踩到特定 tile 觸發地圖切換 | P0 | ✅ |
| 轉場動畫 | TRANSITION 狀態 + 黑幕漸變 | P0 | ✅ |
| 多地圖 NPC | 切換地圖時重載 NPC | P1 | ✅ |
| 地圖物件放置 | Door、Key、Chest、Potion 等 | P1 | ✅ |
| 物件拾取/互動 | 玩家接觸自動拾取 / 按 Enter 開寶箱 | P1 | ✅ |
| 治療池事件 | 水邊面朝 UP 自動回復 HP/MP | P2 | ✅ |

---

## 2. 實作摘要

### 2.1 地圖切換系統

**傳送點定義：**

| 來源地圖 | 座標 | 目標地圖 | 座標 | 區域類型 |
|----------|------|----------|------|----------|
| World Map | (10, 39) | Store | (12, 13) | INDOOR |
| Store | (12, 13) | World Map | (10, 39) | OUTSIDE |
| World Map | (12, 9) | Dungeon B1 | (9, 41) | DUNGEON |
| Dungeon B1 | (9, 41) | World Map | (12, 9) | OUTSIDE |
| Dungeon B1 | (8, 7) | Dungeon B2 | (26, 41) | DUNGEON |
| Dungeon B2 | (26, 41) | Dungeon B1 | (8, 7) | DUNGEON |

**轉場流程：**
1. 玩家踩到傳送點 tile → `checkTeleportEvents()`
2. 設定 `GameState.TRANSITION`，開始 60 tick 計時器
3. Tick 0-29：前端黑幕漸入 (alpha 0→1)
4. Tick 30：後端切換地圖、移動玩家、重載 NPC/物件、發送 `FULL_STATE`
5. Tick 31-59：前端黑幕漸出 (alpha 1→0)
6. Tick 60：恢復 `GameState.PLAY`，設定 `lastTeleportMap/Col/Row` 防止迴圈

**Bug 修復：**
- 位置偵測防止傳送門迴圈（`lastTeleportMap/Col/Row`）
- `FULL_STATE` 包含 `transitionProgress` 防止畫面閃爍

### 2.2 物件系統

**MapObject 資料模型：**
- `id`, `type`, `spriteKey`, `worldX`, `worldY`
- `collision`（門/寶箱擋住玩家）
- `pickupable`（接觸自動拾取）
- `interactable`（按 Enter 互動，如寶箱）
- `lootType`, `lootSpriteKey`（寶箱內容物）

**World Map 物件：**
| 物件 | 座標 | 類型 |
|------|------|------|
| Door × 2 | (14,28), (12,12) | collision |
| Chest (Key) | (30,29) | collision + interactable |
| Axe | (33,7) | pickupable |
| Shield Blue | (10,34) | pickupable |
| Lantern | (27,16) | pickupable |
| Tent | (30,12) | pickupable |

**Dungeon B1 物件：**
| 物件 | 座標 | 類型 |
|------|------|------|
| Chest (Pickaxe) | (40,41) | collision + interactable |
| Chest (Potion) × 3 | (13,16), (26,34), (27,15) | collision + interactable |
| Iron Door | (18,23) | collision |

**Dungeon B2 物件：**
| 物件 | 座標 | 類型 |
|------|------|------|
| Blue Heart | (25,8) | pickupable |
| Iron Door | (25,15) | collision |

### 2.3 治療池

World Map (23, 12) 方向 UP → HP/MP 不滿時自動回復至最大值，顯示對話框。

---

## 3. 檔案變更

### Backend 新增
| 檔案 | 說明 |
|------|------|
| `model/entity/MapObject.java` | 地圖物件資料模型 |

### Backend 修改
| 檔案 | 變更 |
|------|------|
| `engine/GameEngine.java` | TeleportEvent、checkTeleportEvents、updateTransition、createMapObjects、checkObjectCollision、checkPickupObjects、findObjectInFront、interactWithObject、checkHealingPool、transitionProgress in delta/full state |
| `engine/GameSession.java` | transitionTimer、targetMap/Col/Row/Area、needsFullState、lastTeleportMap/Col/Row、mapObjects list |
| `engine/GameLoop.java` | TRANSITION 狀態 tick + needsFullState → FULL_STATE |

### Frontend 修改
| 檔案 | 變更 |
|------|------|
| `types/game.ts` | MapObjectState 介面、transitionProgress、mapObjects |
| `game/assets/AssetLoader.ts` | loadObjectSprites() (21 種物件精靈圖) |
| `game/renderer/TileRenderer.ts` | drawObjects() |
| `game/GameCanvas.tsx` | TRANSITION 渲染（黑幕漸變）、物件繪製、delta 合併 mapObjects |
| `App.tsx` | 版本 v1.4.0 |
