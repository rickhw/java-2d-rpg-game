# Godot 4.5 遊戲復刻實作計畫

## 目標說明

將現有的 Java 2D 冒險 RPG 遊戲「M/A Legend」完整復刻到 Godot 4.5 遊戲引擎，充分利用 Godot 的內建功能取代原本 Java 手動計算的邏輯。

## 原始專案分析摘要

### 核心架構 (Java)
| 模組 | 說明 |
|------|------|
| `Main.java` | JFrame 視窗設定 |
| `GamePanel.java` | 遊戲主迴圈 (60 FPS)、update/draw 邏輯 |
| `Entity.java` | 所有遊戲物件的基礎類別 (866 行) |
| `Player.java` | 玩家控制、動畫、戰鬥 (727 行) |
| `TileManager.java` | 地圖載入與繪製 |
| `CollisionChecker.java` | 碰撞偵測 |
| `PathFinder.java` | A* 尋路演算法 |
| `UI.java` | 所有 UI 繪製 (1144 行) |

### 遊戲功能
- 12 種遊戲狀態 (Title, Play, Pause, Dialogue, Character, Options, GameOver, Transition, Trade, Sleep, DisplayMap, Cutsense)
- 5 種怪物 (GreenSlime, RedSlime, Orc, Bat, SkeletonLord)
- 3 種 NPC (OldMan, Merchant, BigRock)
- 10+ 種物件 (Door, Chest, Key, Potion, Heart, ManaCrystal, Coin, Tent 等)
- 武器與盾牌系統
- 拋射物系統 (Fireball, Rock)
- 日夜循環與照明效果
- 存檔/讀檔系統
- A* 尋路系統

### 素材資源
- **地圖**: 4 張 (worldmap, indoor01, dungeon01, dungeon02)
- **圖塊**: 38 張 Tile 圖片 + tiledata.txt 碰撞資訊
- **字型**: MaruMonica、Purisa Bold
- **BGM**: 5 首 (BlueBoyAdventure, Dungeon, FinalBattle, Merchant, fanfare)
- **音效**: 18 種
- **精靈圖**: 玩家 (walking/attacking/guarding)、怪物、NPC、物件

---

## Godot 機制對應

| Java 原始實作 | Godot 4.5 對應機制 |
|--------------|-------------------|
| 座標計算 `worldX`, `worldY` | `Node2D.position` |
| 縮放比例 `scale = 4` | `project.godot` 視窗設定 + Stretch Mode |
| 碰撞偵測 `CollisionChecker` | `CharacterBody2D` + `CollisionShape2D` |
| 精靈動畫 `spriteCounter` | `AnimatedSprite2D` 或 `AnimationPlayer` |
| 攝影機跟隨 | `Camera2D` |
| 地圖繪製 `TileManager` | `TileMapLayer` |
| 尋路演算法 `PathFinder` | `NavigationAgent2D` |
| 日夜照明 `Lighting.java` | `CanvasModulate` + `PointLight2D` |
| 遊戲狀態 `GameState.java` | `enum` + 狀態機模式 |
| UI 繪製 `UI.java` | `Control` 節點 + `CanvasLayer` |
| 音效播放 `Sound.java` | `AudioStreamPlayer` / `AudioStreamPlayer2D` |
| 存檔系統 `SaveLoad.java` | `ConfigFile` 或 `JSON` |

---

## 專案結構

```
godot/
├── project.godot              # Godot 專案設定檔
├── resources/                 # 素材資源 (從原始專案複製)
│   ├── fonts/
│   ├── bgm/
│   ├── sfx/
│   ├── tiles/
│   ├── player/
│   ├── monsters/
│   ├── npcs/
│   ├── objects/
│   └── maps/
├── scenes/                    # 場景檔案
│   ├── main.tscn              # 主場景
│   ├── player/
│   │   └── player.tscn
│   ├── entities/
│   │   ├── monsters/
│   │   ├── npcs/
│   │   └── objects/
│   ├── ui/
│   │   ├── title_screen.tscn
│   │   ├── hud.tscn
│   │   ├── dialogue_box.tscn
│   │   ├── inventory.tscn
│   │   └── pause_menu.tscn
│   └── maps/
│       ├── world_map.tscn
│       ├── indoor01.tscn
│       ├── dungeon01.tscn
│       └── dungeon02.tscn
├── scripts/                   # GDScript 腳本
│   ├── autoload/
│   │   ├── game_manager.gd    # 遊戲狀態管理
│   │   ├── audio_manager.gd   # 音效管理
│   │   └── save_manager.gd    # 存檔管理
│   ├── entities/
│   │   ├── entity.gd          # 基礎實體類別
│   │   ├── player.gd
│   │   ├── monster.gd
│   │   └── npc.gd
│   └── ui/
│       └── ...
└── data/                      # 遊戲資料
    ├── items.tres
    └── monsters.tres
```

---

## 階段實作計畫

### Phase 1: 專案初始化與核心架構

#### [NEW] [project.godot](file:///Users/rickhwang/Repos/rickhwang/github/game/godot/project.godot)
- 設定專案名稱: `M/A Legend`
- 設定視窗大小: 1280x768 (20*64 x 12*64)
- 設定 Stretch Mode: `canvas_items`
- 設定預設鍵盤輸入

#### [NEW] [game_manager.gd](file:///Users/rickhwang/Repos/rickhwang/github/game/godot/scripts/autoload/game_manager.gd)
- 定義 `GameState` enum
- 管理當前遊戲狀態
- 管理場景切換

---

### Phase 2: 地圖系統

#### [NEW] TileSet 資源
- 使用 tiledata.txt 設定碰撞
- 38 張 Tile 圖片

#### [NEW] 地圖場景
- 使用 `TileMapLayer` 繪製地圖
- 設定碰撞圖層

---

### Phase 3: 玩家系統

#### [NEW] [player.tscn](file:///Users/rickhwang/Repos/rickhwang/github/game/godot/scenes/player/player.tscn)
```
Player (CharacterBody2D)
├── AnimatedSprite2D
├── CollisionShape2D (SolidArea)
├── AttackArea (Area2D)
│   └── CollisionShape2D
└── Camera2D
```

#### [NEW] [player.gd](file:///Users/rickhwang/Repos/rickhwang/github/game/godot/scripts/entities/player.gd)
- 使用 `move_and_slide()` 處理移動
- 使用 Godot Input Map 處理輸入
- 使用 `AnimatedSprite2D` 處理動畫切換

---

### Phase 4-6: 實體與戰鬥系統

#### [NEW] [entity.gd](file:///Users/rickhwang/Repos/rickhwang/github/game/godot/scripts/entities/entity.gd)
- 基礎屬性: life, maxLife, speed, attack, defense
- 基礎方法: take_damage(), knockback()

#### Monster/NPC 使用相同結構
- `CharacterBody2D` + `AnimatedSprite2D`
- 使用 `NavigationAgent2D` 取代手寫 A* 演算法

---

### Phase 7: UI 系統

使用 Godot 的 `Control` 節點層級:
- `CanvasLayer` (UI 層)
  - `HUD` (Control)
  - `DialogueBox` (Control)
  - `InventoryScreen` (Control)
  - 等...

---

### Phase 8-12: 進階功能

- 使用 `CanvasModulate` 實作日夜循環
- 使用 `PointLight2D` 實作玩家照明
- 使用 `AudioStreamPlayer` 播放音效
- 使用 `ConfigFile` 實作存檔

---

## 驗收計畫

### Phase 1 驗收
- [ ] Godot 專案可以正常開啟
- [ ] 素材資源已正確複製
- [ ] 執行遊戲可看到空白視窗

### Phase 2 驗收
- [ ] TileMap 正確顯示 worldmap
- [ ] 碰撞圖層正確設定

### Phase 3 驗收
- [ ] 玩家可以使用 WASD 移動
- [ ] 玩家精靈動畫正確播放
- [ ] 攝影機正確跟隨玩家
- [ ] 玩家無法穿過碰撞圖塊

### Phase 4-6 驗收
- [ ] 怪物正確生成在地圖上
- [ ] 怪物具有基本 AI 行為
- [ ] 玩家可以攻擊怪物
- [ ] 傷害計算正確

### Phase 7 驗收
- [ ] 標題畫面正確顯示
- [ ] HUD 正確顯示生命值
- [ ] 對話系統正確運作
- [ ] 物品欄可以正常開啟與操作

### 最終驗收
- [ ] 遊戲可以從標題畫面開始
- [ ] 玩家可以在地圖上移動
- [ ] 玩家可以與 NPC 對話
- [ ] 玩家可以戰鬥
- [ ] 遊戲可以存檔與讀檔
- [ ] 遊戲可以完成 (Boss 戰)

---

## 測試方式

### 自動化測試
由於 Godot 專案是新建專案，目前沒有現成的測試框架。建議使用以下方式驗證:

### 手動測試步驟

#### 測試 1: 專案啟動
```bash
cd /Users/rickhwang/Repos/rickhwang/github/game/godot
# 使用 Godot 編輯器開啟專案
# 按 F5 執行遊戲
```
**預期結果**: 遊戲視窗正確開啟，顯示標題畫面

#### 測試 2: 玩家移動
1. 在標題畫面選擇「New Game」
2. 按 W/A/S/D 或方向鍵移動玩家
3. 觀察玩家是否正確移動與動畫播放

**預期結果**: 玩家可以朝四個方向移動，動畫正確播放

#### 測試 3: 碰撞偵測
1. 嘗試讓玩家走向障礙物 (樹木、牆壁)
2. 觀察玩家是否被阻擋

**預期結果**: 玩家無法穿過障礙物

#### 測試 4: 戰鬥系統
1. 找到怪物
2. 按攻擊鍵 (Enter 或 Space)
3. 觀察傷害數值與怪物反應

**預期結果**: 怪物受到傷害，顯示受傷動畫

> [!IMPORTANT]
> 由於這是全新的 Godot 專案，所有功能都需要從零開始實作。建議在每個 Phase 完成後進行手動測試，確認功能正常後再進入下一階段。

---

## 需要用戶確認的事項

1. **Godot 版本**: 確認您已安裝 Godot 4.5 版本
2. **專案結構**: 上述專案結構是否符合您的期望?
3. **優先順序**: 是否有特定功能需要優先實作?
4. **測試方式**: 是否有其他測試需求?
