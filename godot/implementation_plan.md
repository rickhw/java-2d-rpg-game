# Godot v4.5 遊戲復刻實作計劃

## 專案概述

本計劃旨在將現有的 Java 2D RPG 遊戲完整復刻到 Godot v4.5 遊戲引擎。原 Java 遊戲包含豐富的功能，包括：

- **遊戲規模**：216 張圖片資源、23 個音效/音樂檔案
- **地圖系統**：4 個地圖（世界地圖、商店、地下城B1、地下城B2）
- **瓦片系統**：38 種瓦片，包含碰撞檢測資料
- **實體系統**：玩家、怪物、NPC、物品、拋射物、互動式瓦片
- **遊戲狀態**：11 種遊戲狀態（標題、遊戲中、暫停、對話、角色、選項、遊戲結束、轉場、交易、睡眠、地圖顯示、過場動畫）
- **戰鬥系統**：近戰攻擊、遠程攻擊、格擋、擊退效果
- **AI 系統**：A* 尋路演算法用於怪物追蹤和 NPC 移動
- **環境系統**：日夜循環、照明效果、粒子效果
- **存檔系統**：玩家狀態、物品欄、地圖物件狀態

## 核心設計原則

1. **利用 Godot 內建功能**：避免重複實作圖形計算、碰撞檢測、座標轉換等，使用 Godot 的 Node 系統、Area2D、CharacterBody2D 等
2. **保持遊戲邏輯一致**：確保遊戲玩法、數值、行為與原 Java 版本完全一致
3. **模組化設計**：使用 Godot 的場景系統，將不同功能分離成獨立場景
4. **資源重用**：所有素材直接從原專案的 `src/main/resources/gtcafe/rpg/assets/` 目錄複製使用

---

## Proposed Changes

### 專案結構 (Project Structure)

```
godot/
├── project.godot                 # Godot 專案配置檔
├── assets/                       # 資源目錄（從 Java 專案複製）
│   ├── bgm/                     # 背景音樂
│   ├── sound/                   # 音效
│   ├── font/                    # 字型
│   ├── player/                  # 玩家圖片
│   ├── monster/                 # 怪物圖片
│   ├── npc/                     # NPC 圖片
│   ├── objects/                 # 物品圖片
│   ├── projectiles/             # 拋射物圖片
│   ├── tilesV3/                 # 瓦片圖片
│   ├── tiles_interactive/       # 互動式瓦片圖片
│   └── maps_v2/                 # 地圖數據
│       ├── worldmap.txt
│       ├── indoor01.txt
│       ├── dungeon01.txt
│       ├── dungeon02.txt
│       └── tiledata.txt
├── scenes/                       # 場景目錄
│   ├── main.tscn                # 主場景
│   ├── entities/                # 實體場景
│   │   ├── player.tscn
│   │   ├── monsters/
│   │   ├── npcs/
│   │   └── objects/
│   ├── ui/                      # UI 場景
│   │   ├── title_screen.tscn
│   │   ├── hud.tscn
│   │   ├── inventory.tscn
│   │   ├── dialogue.tscn
│   │   └── pause_menu.tscn
│   └── maps/                    # 地圖場景
│       ├── world_map.tscn
│       ├── indoor.tscn
│       ├── dungeon_01.tscn
│       └── dungeon_02.tscn
├── scripts/                      # GDScript 腳本目錄
│   ├── autoload/                # 自動載入（全域）腳本
│   │   ├── game_manager.gd      # 遊戲狀態管理
│   │   ├── audio_manager.gd     # 音效/音樂管理
│   │   └── save_manager.gd      # 存檔管理
│   ├── entities/                # 實體腳本
│   │   ├── entity.gd            # 基礎實體類別
│   │   ├── player.gd
│   │   ├── monster.gd
│   │   └── npc.gd
│   ├── systems/                 # 系統腳本
│   │   ├── collision_checker.gd
│   │   ├── pathfinder.gd        # A* 尋路
│   │   ├── particle_generator.gd
│   │   └── lighting_system.gd
│   └── ui/                      # UI 腳本
│       ├── inventory_ui.gd
│       ├── dialogue_ui.gd
│       └── hud.gd
└── data/                         # 資料目錄
    ├── entity_data.gd           # 實體資料定義
    ├── item_data.gd             # 物品資料定義
    └── constants.gd             # 常數定義
```

---

### Component 1: 專案基礎設置 (Project Foundation)

#### [NEW] [project.godot](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/project.godot)

Godot 專案配置檔，設定：
- 視窗大小：1280x768 (對應 Java 的 20x12 瓦片，每瓦片 64px)
- FPS: 60
- 2D 渲染設置
- 自動載入腳本

#### [NEW] [data/constants.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/data/constants.gd)

定義遊戲常數：
```gdscript
const TILE_SIZE = 64                    # 原 Java: tileSize = 16 * 4
const SCREEN_COLS = 20                  # 原 Java: maxScreenCol
const SCREEN_ROWS = 12                  # 原 Java: maxScreenRow  
const WORLD_COLS = 50                   # 原 Java: maxWorldCol
const WORLD_ROWS = 50                   # 原 Java: maxWorldRow
const FPS = 60
```

---

### Component 2: 自動載入系統 (Autoload Systems)

#### [NEW] [autoload/game_manager.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/autoload/game_manager.gd)

遊戲狀態管理器，對應 Java 的 `GameState` enum：
- 11 種遊戲狀態切換
- 當前地圖追蹤
- 全域事件管理

```gdscript
enum GameState {
    TITLE, PLAY, PAUSE, DIALOGUE, CHARACTER, 
    OPTIONS, GAME_OVER, TRANSITION, TRADE, 
    SLEEP, DISPLAY_MAP, CUTSENSE
}
```

#### [NEW] [autoload/audio_manager.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/autoload/audio_manager.gd)

音效與音樂管理器，對應 Java 的 `Sound` 類別：
- 背景音樂播放與循環
- 音效播放
- 音量控制（5 級音量）
- 音樂/音效切換（主題、商店、地下城、BOSS 戰）

#### [NEW] [autoload/save_manager.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/autoload/save_manager.gd)

存檔管理器，對應 Java 的 `SaveLoad` 類別：
- 玩家狀態存檔（等級、生命、魔力、經驗值、金幣）
- 物品欄存檔
- 地圖物件狀態存檔
- 使用 Godot 的 `ConfigFile` 或 JSON 格式

---

### Component 3: 瓦片地圖系統 (Tilemap System)

#### [NEW] [scripts/systems/tilemap_loader.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/systems/tilemap_loader.gd)

地圖載入器，對應 Java 的 `TileManager`:
- 讀取 `tiledata.txt`，生成 TileSet 資源
- 讀取地圖檔案（worldmap.txt, indoor01.txt, dungeon01.txt, dungeon02.txt）
- 使用 Godot 的 `TileMapLayer` 和 `TileSet` 系統
- 自動設置碰撞形狀（對應 Java 的 `collision = true/false`）

> [!IMPORTANT]
> Godot 的 TileSet 系統會自動處理瓦片的繪製順序和碰撞檢測，不需要手動計算座標和碰撞盒。

#### [NEW] 地圖場景

為每個地圖創建獨立場景：
- `maps/world_map.tscn`：使用 `worldmap.txt`
- `maps/indoor.tscn`：使用 `indoor01.txt`
- `maps/dungeon_01.tscn`：使用 `dungeon01.txt`
- `maps/dungeon_02.tscn`：使用 `dungeon02.txt`

每個場景包含：
- TileMapLayer (瓦片圖層)
- 實體位置標記 (Marker2D 節點)
- 區域觸發器 (Area2D 節點，用於事件觸發)

---

### Component 4: 實體系統 (Entity System)

#### [NEW] [scripts/entities/entity.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/entities/entity.gd)

基礎實體類別，對應 Java 的 `Entity` 類別：
- 使用 `CharacterBody2D` 作為基底
- 屬性：生命值、魔力、攻擊力、防禦力、速度
- 動畫狀態：上、下、左、右、攻擊、睡眠
- 碰撞處理：使用 Godot 的 `CollisionShape2D`
- 擊退效果：使用 `velocity` 實現

```gdscript
class_name Entity extends CharacterBody2D

var max_life: int
var life: int
var max_mana: int
var mana: int
var attack: int
var defense: int
var speed: int
var direction: Vector2
var invincible: bool = false
var alive: bool = true
```

#### [NEW] [scripts/entities/player.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/entities/player.gd)

玩家控制器，對應 Java 的 `Player` 類別：
- 鍵盤輸入處理 (WASD/方向鍵)
- 攻擊系統（近戰、遠程）
- 格擋系統（按 Space 鍵）
- 物品欄系統（20 格，4x5）
- 等級提升系統
- 裝備系統（武器、盾牌）
- 相機跟隨（使用 `Camera2D` 節點）

> [!IMPORTANT]
> 使用 Godot 的 `Camera2D` 實現相機跟隨，不需要手動計算 `screenX` 和 `screenY`。

#### [NEW] Monster 腳本

為每種怪物創建獨立腳本，繼承自 `monster.gd`：
- `green_slime.gd`：對應 Java 的 `MON_GreenSlime`
- `red_slime.gd`：對應 Java 的 `MON_RedSlime`
- `orc.gd`：對應 Java 的 `MON_Orc`
- `bat.gd`：對應 Java 的 `MON_Bat`
- `skeleton_lord.gd`：對應 Java 的 `MON_SkeletonLord`（BOSS）

每個怪物有獨特的 AI 行為：
- 巡邏模式
- 追蹤玩家（使用 A* 尋路）
- 攻擊模式
- 遠程攻擊（部分怪物）
- 掉落物品

#### [NEW] NPC 腳本

- `npc_old_man.gd`：對應 Java 的 `NPC_OldMan`
- `npc_merchant.gd`：對應 Java 的 `NPC_Merchant`
- `npc_big_rock.gd`：對應 Java 的 `NPC_BigRock`（解謎元素）

---

### Component 5: 碰撞與物理系統 (Collision & Physics)

#### [NEW] [scripts/systems/collision_checker.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/systems/collision_checker.gd)

> [!TIP]
> Godot 的物理引擎會自動處理大部分碰撞檢測。此腳本主要處理遊戲邏輯相關的碰撞（例如拾取物品、觸發事件）。

對應 Java 的 `CollisionChecker`，但使用 Godot 的物理系統：
- 瓦片碰撞：由 `TileMapLayer` 自動處理
- 實體間碰撞：使用 `Area2D` 的 `body_entered` 信號
- 物品拾取：使用 `Area2D` 檢測玩家範圍
- 攻擊判定：使用動態生成的 `Area2D`

---

### Component 6: AI 與尋路系統 (AI & Pathfinding)

#### [NEW] [scripts/systems/pathfinder.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/systems/pathfinder.gd)

A* 尋路實作，對應 Java 的 `PathFinder`：
- 使用 Godot 的 `AStar2D` 或 `AStarGrid2D` 類別
- 節點成本計算（g_cost, h_cost, f_cost）
- 動態障礙物檢測（瓦片、實體）
- 路徑平滑處理

> [!NOTE]
> Godot 提供內建的 `AStarGrid2D` 類別，可以大幅簡化尋路實作。

---

### Component 7: UI 系統 (UI System)

#### [NEW] UI 場景

所有 UI 使用 Godot 的 `Control` 節點：

1. **標題畫面** (`title_screen.tscn`)：
   - 新遊戲、載入遊戲、離開選項
   
2. **HUD** (`hud.tscn`)：
   - 生命條、魔力條
   - 經驗值顯示
   - 金幣顯示
   - 訊息視窗

3. **物品欄** (`inventory.tscn`)：
   - 4x5 格子系統
   - 物品描述視窗
   - 裝備欄位

4. **對話系統** (`dialogue.tscn`)：
   - 多行對話支援
   - 逐字顯示效果
   - NPC 頭像顯示

5. **交易系統** (`trade_ui.tscn`)：
   - 買賣介面
   - 價格顯示

6. **選項選單** (`options_menu.tscn`)：
   - 全螢幕切換
   - 音量控制
   - 返回標題確認

7. **角色狀態** (`character_status.tscn`)：
   - 等級、經驗值
   - 屬性顯示
   - 裝備顯示

8. **地圖顯示** (`map_display.tscn`)：
   - 小地圖 (按 V 鍵)
   - 全地圖 (按 M 鍵)
   - 玩家位置標記

---

### Component 8: 物品與裝備系統 (Items & Equipment)

#### [NEW] [data/item_data.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/data/item_data.gd)

物品資料定義，對應 Java 的各種物品類別：

**武器 (Weapons)**：
- 劍 (`OBJ_Sword_Normal`)
- 斧頭 (`OBJ_Axe`)
- 十字鎬 (`OBJ_Pickaxe`)

**盾牌 (Shields)**：
- 木盾 (`OBJ_Shield_Wood`)
- 藍盾 (`OBJ_Shield_Blue`)

**消耗品 (Consumables)**：
- 紅藥水 (`OBJ_Postion_Red`)
- 帳篷 (`OBJ_Tent`)
- 鑰匙 (`OBJ_Key`)

**拋射物 (Projectiles)**：
- 火球 (`OBJ_Fireball`)
- 石頭 (`OBJ_Rock`)

**裝備道具 (Equipables)**：
- 靴子 (`OBJ_Boots`，增加速度）
- 燈籠 (`OBJ_Lantern`，照明效果）

**貨幣與恢復**：
- 銅幣 (`OBJ_Coin_Bronze`)
- 愛心 (`OBJ_Heart`)
- 魔力水晶 (`OBJ_ManaCrystal`)

---

### Component 9: 環境與特效系統 (Environment & Effects)

#### [NEW] [scripts/systems/lighting_system.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/systems/lighting_system.gd)

日夜循環與照明系統，對應 Java 的 `EnvironmentManager` 和 `Lighting`：
- 4 種時段：白天 (Day)、黃昏 (Dusk)、夜晚 (Night)、黎明 (Dawn)
- 使用 Godot 的 `CanvasModulate` 節點控制整體色調
- 使用 `PointLight2D` 實現燈籠效果
- 地下城與室內不受日夜影響

#### [NEW] [scripts/systems/particle_generator.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/systems/particle_generator.gd)

粒子效果生成器，對應 Java 的 `Particle` 類別：
- 使用 Godot 的 `GPUParticles2D` 或 `CPUParticles2D`
- 火球擊中效果（紅色粒子）
- 石頭擊中效果（灰色粒子）
- 砍樹效果（褐色粒子）
- 敲牆效果（灰色粒子）
- 重力效果

---

### Component 10: 事件系統 (Event System)

#### [NEW] [scripts/systems/event_handler.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/systems/event_handler.gd)

事件處理器，對應 Java 的 `EventHandler`：
- 傷害陷阱
- 治療池
- 傳送點（地圖切換）
- 使用 `Area2D` 節點在地圖上佈置事件觸發區域

#### [NEW] [scripts/systems/cutscene_manager.gd](file:///Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game/godot/scripts/systems/cutscene_manager.gd)

過場動畫管理器，對應 Java 的 `CutsenseManager`：
- 骷髏領主 BOSS 戰過場
- 結局過場（片尾字幕）
- 使用 `AnimationPlayer` 控制過場流程

---

### Component 11: 互動式瓦片 (Interactive Tiles)

#### [NEW] 互動式瓦片腳本

對應 Java 的 `InteractiveTile` 類別：
- `IT_DryTree`：可砍的乾樹（需斧頭）
- `IT_DestructibleWall`：可敲的牆壁（需十字鎬）
- `IT_MetalPlate`：金屬板（解謎元素，配合大石頭）

使用 `TileMapLayer` 的自定義數據或獨立的 `StaticBody2D` 節點實現。

---

### Component 12: 解謎元素 (Puzzle Elements)

對應 Java 的解謎關卡：
- 大石頭推動系統（`NPC_BigRock`）
- 金屬板機關（`IT_MetalPlate`）
- 鐵門開啟條件（`OBJ_Door_Iron`）
- 使用 Godot 的物理系統實現推動邏輯

---

## Verification Plan

### Phase 1: 專案設置驗收 (Project Setup)

**目標**：確認 Godot 專案成功建立，資源正確複製

**驗收標準**：
1. 在 Godot 編輯器中開啟專案不出現錯誤
2. 所有資源檔案正確顯示在 FileSystem 面板中
3. 自動載入腳本成功載入（檢查 Project Settings > Autoload）
4. 專案設定正確：
   - 視窗大小 1280x768
   - FPS 設定為 60
   - 2D 渲染模式

**測試步驟**：
```bash
# 1. 建立 godot 目錄
cd /Users/rickhwang/Repos/rickhwang/github/java-2d-rpg-game
mkdir -p godot

# 2. 複製資源
cp -r src/main/resources/gtcafe/rpg/assets godot/assets

# 3. 在 Godot 編輯器中開啟專案
# 手動操作：開啟 Godot 4.5，選擇 Import 並選擇 godot/project.godot

# 4. 檢查專案設定
# 手動操作：Project > Project Settings 確認設定正確
```

---

### Phase 2: 瓦片地圖系統驗收 (Tilemap System)

**目標**：成功載入所有地圖，瓦片碰撞正確設置

**驗收標準**：
1. 4 個地圖場景成功創建並可在編輯器中開啟
2. 瓦片圖片正確顯示
3. 碰撞形狀正確設置（對應 tiledata.txt）
4. 可在場景中測試玩家移動，碰撞檢測正常

**測試步驟**：
```gdscript
# 建立測試場景 test_tilemap.tscn
# 包含：
# - TileMapLayer 節點
# - 簡單的 CharacterBody2D 玩家節點（用於測試碰撞）
# 
# 運行場景，測試：
# 1. 玩家可在可通行瓦片上移動
# 2. 玩家無法穿過碰撞瓦片
# 3. 地圖顯示正確
```

**手動測試**：
- 在 Godot 編輯器中逐一開啟 4 個地圖場景
- 檢查地圖是否與 Java 版本視覺上一致
- 按 F6 運行當前場景，測試碰撞

---

### Phase 3: 玩家系統驗收 (Player System)

**目標**：玩家移動、動畫、攻擊、物品欄功能正常

**驗收標準**：
1. WASD/方向鍵控制移動流暢
2. 8 方向動畫正確播放（上下左右x2幀）
3. 攻擊動畫與判定正確
4. 物品欄可開啟、關閉、選擇物品
5. 相機正確跟隨玩家

**測試步驟**：
```gdscript
# 建立測試場景 test_player.tscn
# 運行場景，測試：
# 1. 移動：按 WASD 可移動
# 2. 動畫：移動時動畫播放，停止時靜止
# 3. 攻擊：按 J/F 鍵攻擊，攻擊動畫播放
# 4. 物品欄：按 C 鍵開啟/關閉物品欄
# 5. 相機：移動時相機跟隨
```

**手動測試**：
- 按下所有控制按鍵，確認回應正確
- 檢查相機邊界處理（地圖邊緣）
- 檢查碰撞檢測（不能穿牆）

---

### Phase 4: 實體與 AI 系統驗收 (Entity & AI)

**目標**：怪物、NPC 正常生成與運作，AI 行為正確

**驗收標準**：
1. 怪物可在地圖上生成
2. 怪物巡邏行為正常（隨機方向移動）
3. 怪物追蹤玩家（A* 尋路）
4. 怪物攻擊玩家
5. NPC 對話系統正常
6. 商人交易系統正常

**測試步驟**：
```gdscript
# 在地圖場景中添加怪物節點
# 運行場景，測試：
# 1. 怪物巡邏：遠離玩家時隨機移動
# 2. 怪物追蹤：靠近玩家時追蹤
# 3. 怪物攻擊：接觸玩家時造成傷害
# 4. NPC 對話：按 Enter 與 NPC 對話
# 5. 商人交易：與商人對話可開啟交易介面
```

**手動測試**：
- 開啟 Godot 的遠程場景樹 (Remote tab)
- 觀察怪物的路徑（可用 debug draw 繪製）
- 測試每種怪物的特殊行為：
  - Green Slime：基本追蹤
  - Red Slime：發射石頭
  - Orc：近戰攻擊
  - Bat：高速移動
  - Skeleton Lord：BOSS 戰，狂暴模式

---

### Phase 5: 戰鬥系統驗收 (Combat System)

**目標**：近戰、遠程、格擋、擊退功能正常

**驗收標準**：
1. 玩家近戰攻擊命中怪物，造成傷害
2. 玩家遠程攻擊（火球）正常發射與命中
3. 怪物遠程攻擊（石頭）正常發射
4. 格擋系統：按 Space 可格擋怪物攻擊
5. 擊退效果：攻擊命中時目標被擊退
6. 無敵時間：受傷後短暫無敵

**測試步驟**：
```gdscript
# 在測試場景中添加玩家與怪物
# 運行場景，測試：
# 1. 按 J/F 鍵近戰攻擊怪物，觀察傷害數值
# 2. 按 J/F 鍵遠程攻擊（裝備火球），觀察發射與命中
# 3. 怪物攻擊玩家時按 Space，觀察格擋效果
# 4. 攻擊怪物時觀察擊退效果
# 5. 受傷後觀察無敵時間（閃爍效果）
```

**驗收數值對照**：
| 實體 | 生命值 | 攻擊力 | 防禦力 | Java 數值來源 |
|------|--------|--------|--------|---------------|
| Player (Lv1) | 6 | 1 | 0 | Player.setDefaultValues() |
| Green Slime | 10 | 2 | 0 | MON_GreenSlime |
| Red Slime | 20 | 4 | 0 | MON_RedSlime |
| Orc | 10 | 8 | 2 | MON_Orc |
| Bat | 10 | 1 | 0 | MON_Bat |
| Skeleton Lord | 50 | 10 | 2 | MON_SkeletonLord |

---

### Phase 6: UI 系統驗收 (UI System)

**目標**：所有 UI 畫面正常顯示與互動

**驗收標準**：
1. 標題畫面：新遊戲、載入、離開按鈕可點擊
2. HUD：生命條、魔力條、經驗值、金幣正確顯示
3. 物品欄：可開啟、選擇、使用物品
4. 對話系統：多行、逐字顯示正常
5. 交易系統：買賣功能正常
6. 選項選單：音量、全螢幕切換正常
7. 地圖顯示：小地圖、全地圖正常

**測試步驟**：
```gdscript
# 運行主場景，測試：
# 1. 標題畫面：點擊各按鈕
# 2. HUD：觀察數值變化
# 3. 物品欄：按 C 開啟，WASD 移動游標，Enter 使用物品
# 4. 對話：與 NPC 對話，觀察逐字顯示
# 5. 交易：與商人對話，測試買賣
# 6. 選項：按 ESC 開啟，調整音量
# 7. 地圖：按 M 開啟全地圖，按 V 開啟小地圖
```

**手動測試**：
- 逐一點擊所有 UI 元素
- 檢查字型是否正確（使用原 Java 的像素字型）
- 檢查 UI 位置與 Java 版本對照

---

### Phase 7: 音效與音樂驗收 (Audio System)

**目標**：背景音樂、音效正常播放

**驗收標準**：
1. 背景音樂循環播放
2. 切換地圖時音樂切換（世界→商店→地下城）
3. 音效正確觸發（攻擊、受傷、拾取物品、開門等）
4. 音量控制正常（5 級音量）
5. 設定可儲存

**測試步驟**：
```bash
# 運行遊戲，測試：
# 1. 在世界地圖：播放主題音樂
# 2. 進入商店：播放商店音樂
# 3. 進入地下城：播放地下城音樂
# 4. BOSS 戰：播放 BOSS 音樂
# 5. 觸發各種音效：
#    - 攻擊怪物
#    - 受傷
#    - 拾取物品
#    - 開門
#    - UI 選單移動
#    - 等級提升
# 6. 調整音量：開啟選項選單，調整音樂與音效音量
```

**音效清單對照** (對應 Java 的 Sound.java)：
- 背景音樂：MAIN_THEME, MERCHANT, DUNGEON, FINAL_BATTLE, FANFARE
- 音效：COIN, POWER_UP, UNLOCK, HIT_MONSTER, CURSOR, BURNING, PARRY, 等

---

### Phase 8: 環境與特效驗收 (Environment & Effects)

**目標**：日夜循環、照明、粒子效果正常

**驗收標準**：
1. 日夜循環：白天→黃昏→夜晚→黎明
2. 燈籠效果：裝備後周圍有光圈
3. 粒子效果：
   - 火球擊中產生紅色粒子
   - 石頭擊中產生灰色粒子
   - 砍樹產生褐色粒子
   - 敲牆產生灰色粒子
4. 室內與地下城不受日夜影響

**測試步驟**：
```gdscript
# 運行遊戲，測試：
# 1. 在世界地圖等待，觀察日夜變化
# 2. 裝備燈籠，觀察照明效果
# 3. 使用火球攻擊，觀察粒子效果
# 4. 砍樹，觀察粒子效果
# 5. 進入室內，確認不受日夜影響
```

**手動測試**：
- 加速時間流逝（開發模式）
- 檢查各時段的色調是否與 Java 版本一致
- 檢查粒子數量、顏色、重力效果

---

### Phase 9: 存檔系統驗收 (Save System)

**目標**：遊戲存檔與讀檔功能正常

**驗收標準**：
1. 存檔成功：玩家狀態、物品欄、地圖物件狀態儲存
2. 讀檔成功：所有狀態正確恢復
3. 設定檔儲存：音量、全螢幕設定儲存
4. 多存檔支援（如果實作）

**測試步驟**：
```gdscript
# 運行遊戲，測試：
# 1. 進行遊戲，改變狀態：
#    - 提升等級
#    - 拾取物品
#    - 開啟寶箱
# 2. 在治療池存檔
# 3. 關閉遊戲
# 4. 重新開啟遊戲，選擇「載入遊戲」
# 5. 檢查所有狀態是否正確恢復：
#    - 玩家位置
#    - 等級、經驗值
#    - 物品欄內容
#    - 寶箱已開啟
```

**檔案檢查**：
```bash
# 檢查存檔檔案是否生成
ls -la godot/save.data  # 或 save.json，視實作方式
```

---

### Phase 10: 完整遊戲流程驗收 (Full Game Flow)

**目標**：從標題畫面到結局的完整流程可正常進行

**驗收標準**：
1. 新遊戲可開始
2. 地圖切換正常（世界→室內→地下城）
3. 怪物戰鬥正常
4. BOSS 戰正常（含過場動畫）
5. 結局過場正常（片尾字幕）
6. 遊戲結束可返回標題

**測試步驟**：
```
完整流程測試（預計 30 分鐘）：

1. 標題畫面
   - 選擇「新遊戲」

2. 世界地圖
   - 移動玩家
   - 戰鬥綠史萊姆
   - 拾取掉落物品
   - 提升等級

3. 進入商店
   - 與商人對話
   - 購買物品

4. 返回世界地圖
   - 前往地下城入口

5. 地下城 B1
   - 戰鬥獸人
   - 開啟寶箱
   - 使用鑰匙開門

6. 地下城 B2
   - 推動大石頭解謎
   - 觸發 BOSS 戰過場
   - 擊敗骷髏領主
   - 觀看結局過場

7. 返回標題
```

**效能檢查**：
- FPS 應維持在 60
- 無明顯卡頓
- 記憶體使用合理

---

### Phase 11: 除錯與最佳化 (Debug & Optimization)

**目標**：修復所有已知問題，優化效能

**驗收標準**：
1. 無崩潰或錯誤訊息
2. FPS 穩定在 60
3. 所有功能與 Java 版本行為一致
4. 代碼結構清晰，易於維護

**測試工具**：
- Godot 的 Profiler (底部 Debugger > Profiler)
- Godot 的 Visual Profiler (底部 Debugger > Visual Profiler)
- Godot 的 Remote 場景樹 (底部 Scene > Remote)

**對照測試**：
同時運行 Java 版本與 Godot 版本，逐一對照：
- 移動速度
- 攻擊傷害
- 怪物 AI 行為
- UI 顯示
- 音效觸發時機

---

## 風險與限制

1. **資源格式轉換**：Java 使用的音效格式可能需要轉換（Godot 支援 WAV, OGG）
2. **字型渲染**：像素字型在 Godot 中的渲染需要特別設定（Filter 關閉）
3. **效能差異**：Godot 的物理引擎與 Java 手工實作的差異可能導致細微行為不同
4. **存檔格式**：需要設計新的存檔格式（JSON 或 Godot 的 ConfigFile）

---

## 時程估計

| 階段 | 預計時間 | 累計時間 |
|------|----------|----------|
| Phase 1: 專案設置 | 2 小時 | 2 小時 |
| Phase 2: 瓦片地圖 | 4 小時 | 6 小時 |
| Phase 3: 玩家系統 | 6 小時 | 12 小時 |
| Phase 4: 實體與 AI | 8 小時 | 20 小時 |
| Phase 5: 戰鬥系統 | 6 小時 | 26 小時 |
| Phase 6: UI 系統 | 8 小時 | 34 小時 |
| Phase 7: 音效音樂 | 3 小時 | 37 小時 |
| Phase 8: 環境特效 | 4 小時 | 41 小時 |
| Phase 9: 存檔系統 | 3 小時 | 44 小時 |
| Phase 10: 完整流程測試 | 4 小時 | 48 小時 |
| Phase 11: 除錯最佳化 | 8 小時 | 56 小時 |

**總計：約 56 小時（7 個工作天）**

---

## 附錄：關鍵對應表

### Java 類別 → Godot 腳本

| Java 類別 | Godot 腳本 | 說明 |
|-----------|------------|------|
| GamePanel | game_manager.gd (Autoload) | 遊戲主控制器 |
| Entity | entity.gd (extends CharacterBody2D) | 實體基礎類別 |
| Player | player.gd | 玩家控制器 |
| CollisionChecker | 使用 Godot 物理引擎 + collision_checker.gd | 碰撞檢測 |
| TileManager | tilemap_loader.gd | 瓦片地圖載入 |
| PathFinder | pathfinder.gd (使用 AStarGrid2D) | A* 尋路 |
| Sound | audio_manager.gd (Autoload) | 音效管理 |
| UI | 多個 UI 場景 (.tscn) | 使用者介面 |
| SaveLoad | save_manager.gd (Autoload) | 存檔管理 |
| EnvironmentManager | lighting_system.gd | 環境管理 |
| Particle | particle_generator.gd (使用 CPUParticles2D) | 粒子效果 |

### 遊戲常數對應

| Java 常數 | 值 | Godot 常數 |
|-----------|-----|------------|
| originalTileSize | 16 | - |
| scale | 4 | - |
| tileSize | 64 | Constants.TILE_SIZE |
| maxScreenCol | 20 | Constants.SCREEN_COLS |
| maxScreenRow | 12 | Constants.SCREEN_ROWS |
| maxWorldCol | 50 | Constants.WORLD_COLS |
| maxWorldRow | 50 | Constants.WORLD_ROWS |
| FPS | 60 | Constants.FPS |

### 輸入對應

| Java 按鍵 | 原功能 | Godot 輸入動作 |
|-----------|--------|----------------|
| W / UP | 向上移動 | ui_up |
| S / DOWN | 向下移動 | ui_down |
| A / LEFT | 向左移動 | ui_left |
| D / RIGHT | 向右移動 | ui_right |
| ENTER | 確認 / 對話 | ui_accept |
| F / J | 攻擊 | attack |
| SPACE | 格擋 | guard |
| C | 開啟角色狀態 | character |
| M | 開啟全地圖 | map |
| V | 開啟小地圖 | minimap |
| ESC | 暫停 / 返回 | ui_cancel |

---

## 結語

本實作計劃提供了將 Java 2D RPG 遊戲復刻到 Godot v4.5 的完整藍圖。通過善用 Godot 的內建功能（物理引擎、場景系統、動畫系統、音效系統等），可以大幅簡化開發流程，同時保持與原遊戲的完全一致性。

每個階段都有清晰的驗收標準和測試步驟，確保復刻品質。在實作過程中，應隨時對照原 Java 代碼，確保遊戲邏輯、數值、行為的準確性。
