# Java 2D RPG Game — 前後端分離架構改寫：系統設計文件

> **文件版本**: v1.0  
> **建立日期**: 2026-02-24  
> **目標**: 將現有 Java Swing RPG 遊戲改寫為前後端分離架構 (React + Spring Boot 3.4 + Java 21 + Gradle)

---

## 一、現有架構分析

### 1.1 原始碼結構 (66 個 Java 檔案)

```
src/main/java/gtcafe/rpg/
├── Main.java                    # 程式入口 (JFrame)
├── GamePanel.java               # 核心：遊戲面板 (JPanel + Runnable, Game Loop)
├── UI.java                      # 所有 UI 繪製 (HUD, 選單, 對話框, 交易)
├── AssetSetter.java             # 地圖物件/NPC/怪物/互動磚塊 初始化
├── CutsenseManager.java         # 過場動畫管理
├── Graphics2DUtils.java         # 圖形工具 (縮放, 透明度)
├── ai/
│   ├── Node.java                # A* 路徑節點
│   └── PathFinder.java          # A* 搜尋算法
├── data/
│   ├── DataStorage.java         # 存檔結構 (Serializable)
│   ├── Progress.java            # 遊戲進度旗標
│   └── SaveLoad.java            # 序列化存讀檔
├── entity/
│   ├── Entity.java              # 實體基類 (866 行, 核心)
│   ├── EntityGenerator.java     # 實體工廠
│   ├── EntityType.java          # 實體類型 Enum
│   ├── Particle.java            # 粒子效果
│   ├── Player.java              # 玩家 (727 行)
│   ├── PlayerDummy.java         # 過場用假玩家
│   ├── equipable/               # 裝備 (Boots, Lantern)
│   ├── monster/                 # 怪物 (Bat, GreenSlime, Orc, RedSlime, SkeletonLord)
│   ├── npc/                     # NPC (BigRock, Merchant, OldMan)
│   ├── object/                  # 物件 (BlueHeart, Chest, Coin, Door, Heart, Key, ManaCrystal, Potion, Tent)
│   ├── projectile/              # 拋射物 (Fireball, Rock, Projectile)
│   ├── shield/                  # 盾牌 (Blue, Wood)
│   └── weapon/                  # 武器 (Axe, Pickaxe, Sword)
├── environment/
│   ├── EnvironmentManager.java  # 環境管理
│   └── Lighting.java            # 日夜循環 + 照明
├── state/
│   ├── DayState.java            # 日夜狀態 Enum
│   ├── Direction.java           # 方向 Enum
│   ├── GameState.java           # 遊戲狀態 Enum (12 種狀態)
│   └── Scene.java               # 場景 Enum (過場)
├── system/
│   ├── CollisionChecker.java    # 碰撞偵測
│   ├── Config.java              # 設定檔讀寫
│   ├── EventHandler.java        # 事件處理 (傳送門, 治療池, Boss)
│   ├── EventRect.java           # 事件矩形
│   ├── KeyHandler.java          # 鍵盤事件 (442 行, 狀態機)
│   └── Sound.java               # 音效管理
└── tile/
    ├── Map.java                 # 地圖 Enum (4 張地圖)
    ├── MapManager.java          # 小地圖/大地圖顯示
    ├── Tile.java                # 磁磚
    ├── TileManager.java         # 磁磚載入/繪製
    └── interactive/
        ├── InteractiveTile.java # 可互動磁磚基類
        ├── IT_DestructibleWall.java
        ├── IT_DryTree.java
        └── IT_MetalPlate.java
```

### 1.2 遊戲資源 (Resources)

```
src/main/resources/gtcafe/rpg/assets/
├── bgm/                # 背景音樂 (WAV)
├── font/               # 像素字型
├── maps_v2/            # 地圖數據 (worldmap.txt, indoor01.txt, dungeon01/02.txt, tiledata.txt)
├── monster/            # 怪物精靈圖 (bat, greenslime, orc, redslime, skeletonlord)
├── npc/                # NPC 精靈圖
├── objects/            # 物件圖片
├── player/             # 玩家精靈圖 (walking, attacking, guarding)
├── projectiles/        # 拋射物圖片
├── sound/              # 音效 (WAV)
├── tilesV3/            # 磁磚圖片
└── tiles_interactive/  # 互動磁磚圖片
```

### 1.3 核心遊戲機制

| 機制 | 說明 | 原始實作位置 |
|------|------|-------------|
| **Game Loop** | 60 FPS, Delta/Accumulator | `GamePanel.run()` |
| **Tile Map** | 16x16 原始尺寸, x4 縮放 = 64px | `TileManager` |
| **碰撞偵測** | 矩形碰撞 (SolidArea) | `CollisionChecker` |
| **A* 路徑搜尋** | NPC/怪物追蹤玩家 | `PathFinder` |
| **戰鬥系統** | 近戰 + 遠程(拋射物) + 防禦 + 格擋 | `Entity.attacking()`, `Player.damageMonster()` |
| **裝備系統** | 武器/盾牌/燈具/鞋子 | `Player`, Entity 子類 |
| **物品系統** | 可堆疊物品, 20 格背包 | `Player.inventory`, `Entity` |
| **交易系統** | 買賣 NPC | `UI.drawTradeScreen()` |
| **日夜循環** | Day → Dusk → Night → Dawn | `Lighting` |
| **地圖傳送** | 4 張地圖, 區域轉場效果 | `EventHandler.teleport()` |
| **存檔/讀檔** | Java 序列化 | `SaveLoad`, `DataStorage` |
| **Boss 戰** | SkeletonLord, 過場動畫 | `CutsenseManager` |
| **狀態機** | 12 種遊戲狀態切換 | `GameState` enum |

### 1.4 遊戲參數

- **Tile Size**: 16x16 (原始), Scale: 4x → 64x64 (螢幕)
- **螢幕格數**: 20 x 12 tiles
- **螢幕解析度**: 1280 x 768 px
- **FPS**: 60
- **地圖數量**: 4 (World, Store, Dungeon B1, Dungeon B2)
- **Players**: 單人

---

## 二、目標架構設計

### 2.1 架構概覽

```
                    ┌──────────────────────────────────────────┐
                    │            Frontend (React)              │
                    │                                          │
                    │  ┌─────────────────────────────────────┐ │
                    │  │        HTML5 Canvas / PixiJS         │ │
                    │  │   (Game Rendering, 60 FPS Loop)      │ │
                    │  └────────────────┬────────────────────┘ │
                    │                   │                      │
                    │  ┌────────────────▼────────────────────┐ │
                    │  │      React UI Layer (HUD/Menus)      │ │
                    │  │  Status Bar, Inventory, Dialogue,    │ │
                    │  │  Trade, Options, Title Screen        │ │
                    │  └────────────────┬────────────────────┘ │
                    │                   │                      │
                    │  ┌────────────────▼────────────────────┐ │
                    │  │        WebSocket Client              │ │
                    │  │   (Real-time Game State Sync)        │ │
                    │  └────────────────┬────────────────────┘ │
                    └───────────────────┼──────────────────────┘
                                        │
                              WebSocket + REST API
                                        │
                    ┌───────────────────┼──────────────────────┐
                    │            Backend (Spring Boot)          │
                    │                                          │
                    │  ┌────────────────▼────────────────────┐ │
                    │  │       WebSocket Handler              │ │
                    │  │   (Input Commands, State Updates)    │ │
                    │  └────────────────┬────────────────────┘ │
                    │                   │                      │
                    │  ┌────────────────▼────────────────────┐ │
                    │  │         Game Engine (Core)           │ │
                    │  │                                      │ │
                    │  │  ┌──────────┐  ┌──────────────────┐ │ │
                    │  │  │ Game Loop│  │ Collision Engine  │ │ │
                    │  │  └──────────┘  └──────────────────┘ │ │
                    │  │  ┌──────────┐  ┌──────────────────┐ │ │
                    │  │  │PathFinder│  │ Combat System    │ │ │
                    │  │  └──────────┘  └──────────────────┘ │ │
                    │  │  ┌──────────┐  ┌──────────────────┐ │ │
                    │  │  │Map System│  │ Entity System    │ │ │
                    │  │  └──────────┘  └──────────────────┘ │ │
                    │  └────────────────┬────────────────────┘ │
                    │                   │                      │
                    │  ┌────────────────▼────────────────────┐ │
                    │  │  REST Controllers (Assets, Save)     │ │
                    │  └─────────────────────────────────────┘ │
                    └──────────────────────────────────────────┘
```

### 2.2 技術選型

| 層級 | 技術 | 用途 |
|------|------|------|
| **前端框架** | React 19 + Vite | SPA, 快速開發 |
| **遊戲渲染** | HTML5 Canvas (原生) | 2D 精靈圖繪製, Game Loop |
| **音效** | Web Audio API (Howler.js) | 背景音樂 + 音效 |
| **通訊** | WebSocket (STOMP) + REST | 即時遊戲同步 + 靜態資源 |
| **後端框架** | Spring Boot 3.4 + Java 21 | RESTful API + WebSocket |
| **構建工具** | Gradle (Kotlin DSL) | 後端構建 |
| **資料儲存** | H2 (開發) / SQLite / JSON | 存檔系統 |

### 2.3 前後端職責分離

#### **後端 (Server-Authoritative 模式)**

後端是**遊戲邏輯的權威端**，負責：

1. **Game Loop**: 伺服器端以固定 tick rate (20 ticks/s) 運行遊戲邏輯
2. **Entity 管理**: 所有 Entity (Player, NPC, Monster) 的狀態與行為
3. **碰撞偵測**: 所有碰撞計算
4. **戰鬥結算**: 傷害計算、擊退效果、等級提升
5. **地圖/事件**: 傳送門、治療池、Boss 觸發
6. **AI**: A* 路徑搜尋、怪物行為
7. **存檔/讀檔**: 資料持久化
8. **資源服務**: 提供 Sprite / Tile / Sound 等資源

#### **前端 (Client-Side Prediction)**

前端負責**展示與輸入**：

1. **渲染**: 60 FPS Canvas 繪製 (tiles, sprites, particles, lighting)
2. **輸入捕捉**: 鍵盤事件 → 透過 WebSocket 傳送命令
3. **插值/預測**: 在兩次 Server Tick 之間做平滑動畫
4. **UI 層**: React 組件負責 HUD、選單、背包、對話框
5. **音效**: 本地播放 BGM + SFX
6. **資源快取**: 預載入 + 快取圖片/音效

---

## 三、API 設計

### 3.1 REST API

```
GET  /api/assets/sprites/{category}/{name}    # 取得精靈圖
GET  /api/assets/tiles                        # 取得所有磁磚數據
GET  /api/assets/maps/{mapName}               # 取得地圖數據
GET  /api/assets/sounds/{name}                # 取得音效檔案
GET  /api/assets/manifest                     # 資源清單 (用於預載入)

POST /api/game/new                            # 開始新遊戲 → 回傳 sessionId
POST /api/game/load                           # 讀取存檔 → 回傳 sessionId
POST /api/game/save                           # 儲存遊戲
GET  /api/game/{sessionId}/state              # 取得完整遊戲狀態 (初始用)
```

### 3.2 WebSocket 通訊

**Endpoint**: `ws://localhost:8080/ws/game`

#### Client → Server (Input Commands)

```json
// 移動
{ "type": "MOVE", "direction": "UP" }
{ "type": "MOVE_STOP" }

// 攻擊
{ "type": "ATTACK" }

// 防禦
{ "type": "GUARD_START" }
{ "type": "GUARD_STOP" }

// 拋射物
{ "type": "SHOOT" }

// 互動 (Enter)
{ "type": "INTERACT" }

// 選單操作
{ "type": "MENU_SELECT", "action": "NEW_GAME" }
{ "type": "MENU_SELECT", "action": "LOAD_GAME" }
{ "type": "MENU_NAVIGATE", "direction": "UP" }

// 背包操作
{ "type": "INVENTORY_OPEN" }
{ "type": "INVENTORY_SELECT", "slot": 3 }
{ "type": "INVENTORY_CLOSE" }

// 交易操作
{ "type": "TRADE_BUY", "slot": 0 }
{ "type": "TRADE_SELL", "slot": 2 }

// 對話
{ "type": "DIALOGUE_NEXT" }

// Debug
{ "type": "DEBUG_TOGGLE" }
{ "type": "GOD_MODE_TOGGLE" }
```

#### Server → Client (Game State Updates)

```json
// 完整狀態快照 (初始化 + 地圖切換時)
{
  "type": "FULL_STATE",
  "gameState": "PLAY",
  "currentMap": "WORLD_MAP",
  "currentArea": "OUTSIDE",
  "dayState": "DAY",
  "player": {
    "worldX": 1472, "worldY": 1344,
    "direction": "DOWN", "spriteNum": 1,
    "life": 6, "maxLife": 6,
    "mana": 4, "maxMana": 4,
    "level": 1, "exp": 0, "coin": 0,
    "attacking": false, "guarding": false, "invincible": false,
    "currentWeapon": "Sword_Normal", "currentShield": "Shield_Wood",
    "inventory": [
      { "name": "Sword_Normal", "amount": 1, "equipped": true },
      { "name": "Shield_Wood", "amount": 1, "equipped": true }
    ]
  },
  "entities": [
    {
      "id": "npc_0", "type": "NPC", "name": "OldMan",
      "worldX": 1280, "worldY": 1024,
      "direction": "DOWN", "spriteNum": 1
    },
    {
      "id": "mon_0", "type": "MONSTER", "name": "GreenSlime",
      "worldX": 2048, "worldY": 1536,
      "direction": "LEFT", "spriteNum": 2, 
      "life": 4, "maxLife": 4, "alive": true
    }
  ],
  "objects": [
    { "id": "obj_0", "name": "Key", "worldX": 1600, "worldY": 1280, "opened": false }
  ],
  "interactiveTiles": [
    { "id": "it_0", "name": "DryTree", "worldX": 1408, "worldY": 1152 }
  ],
  "projectiles": [],
  "particles": []
}

// 差量更新 (每個 Server Tick, 50ms)
{
  "type": "DELTA_STATE",
  "tick": 1234,
  "updates": [
    { "id": "player", "worldX": 1476, "worldY": 1344, "direction": "RIGHT", "spriteNum": 2 },
    { "id": "mon_0", "worldX": 2044, "direction": "LEFT" }
  ],
  "events": [
    { "event": "SOUND", "sound": "COIN" },
    { "event": "MESSAGE", "text": "You found a key!" },
    { "event": "PARTICLE", "x": 2048, "y": 1536, "color": "#00FF00", "count": 4 }
  ],
  "removed": ["obj_2"]
}

// UI 事件
{ "type": "UI_EVENT", "event": "DIALOGUE_OPEN", "npcName": "OldMan", "text": "Hello, adventurer!" }
{ "type": "UI_EVENT", "event": "LEVEL_UP", "level": 2 }
{ "type": "UI_EVENT", "event": "GAME_OVER" }
{ "type": "UI_EVENT", "event": "TRADE_OPEN", "merchantInventory": [...] }
```

---

## 四、資料模型設計

### 4.1 後端核心資料模型

```java
// === Entity 體系 ===

public record Position(int worldX, int worldY) {}
public record SolidArea(int x, int y, int width, int height) {}
public record AttackArea(int x, int y, int width, int height) {}

public enum Direction { UP, DOWN, LEFT, RIGHT, ANY }
public enum GameState { TITLE, PLAY, PAUSE, DIALOGUE, CHARACTER, OPTIONS, GAME_OVER, TRANSITION, TRADE, SLEEP, DISPLAY_MAP, CUTSENSE }
public enum EntityType { PLAYER, NPC, MONSTER, SWORD, AXE, PICKAXE, SHIELD, LIGHT, SHOE, CONSUMABLE, PICKUPONLY, OBSTACLE }
public enum MapId { WORLD_MAP, STORE, DUNGEON01, DUNGEON02 }
public enum DayState { DAY, DUSK, NIGHT, DAWN }
public enum AreaType { OUTSIDE, INDOOR, DUNGEON }

// 遊戲實體基類
public class GameEntity {
    String id;
    String name;
    EntityType type;
    Position position;
    Direction direction;
    SolidArea solidArea;
    int speed, defaultSpeed;
    int maxLife, life;
    boolean alive, dying, invincible;
    int spriteNum;
    // ... 其他屬性
}

// 玩家
public class PlayerEntity extends GameEntity {
    int maxMana, mana;
    int level, exp, nextLevelExp, coin;
    int strength, dexterity, attack, defense;
    String currentWeaponName, currentShieldName, currentLightName;
    List<InventoryItem> inventory;
    boolean attacking, guarding, knockBack;
}

// 怪物
public class MonsterEntity extends GameEntity {
    int attack, defense;
    boolean boss, inRage, sleep, onPath;
    String projectileType;
    List<String> dropTable;
}

// 存檔數據
public record SaveData(
    PlayerEntity player,
    Map<MapId, List<MapObjectState>> mapObjects
) {}
```

### 4.2 前端資料模型

```typescript
// === 前端型別定義 ===

interface GameState {
  state: 'TITLE' | 'PLAY' | 'PAUSE' | 'DIALOGUE' | 'CHARACTER' | 
         'OPTIONS' | 'GAME_OVER' | 'TRANSITION' | 'TRADE' | 'SLEEP' | 'MAP' | 'CUTSENSE';
  currentMap: MapId;
  currentArea: AreaType;
  dayState: DayState;
  player: PlayerState;
  entities: EntityState[];       // NPC + Monster + Object
  projectiles: ProjectileState[];
  particles: ParticleState[];
}

interface PlayerState {
  worldX: number; worldY: number;
  direction: Direction;
  spriteNum: 1 | 2;
  life: number; maxLife: number;
  mana: number; maxMana: number;
  level: number; exp: number; coin: number;
  attacking: boolean; guarding: boolean; invincible: boolean;
  currentWeapon: string; currentShield: string;
  inventory: InventoryItem[];
}

interface EntityState {
  id: string;
  type: 'NPC' | 'MONSTER' | 'OBJECT' | 'INTERACTIVE_TILE';
  name: string;
  worldX: number; worldY: number;
  direction: Direction;
  spriteNum: 1 | 2;
  life?: number; maxLife?: number;
  alive?: boolean; dying?: boolean;
  hpBarOn?: boolean;
  onPath?: boolean;
}

interface SpriteSheet {
  name: string;
  image: HTMLImageElement;
  frames: {
    up: [HTMLImageElement, HTMLImageElement];
    down: [HTMLImageElement, HTMLImageElement];
    left: [HTMLImageElement, HTMLImageElement];
    right: [HTMLImageElement, HTMLImageElement];
  };
  attackFrames?: { ... };
  guardFrames?: { ... };
}
```

---

## 五、目錄結構

### 5.1 後端 (backend)

```
backend/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── wrapper/
├── src/
│   ├── main/
│   │   ├── java/gtcafe/rpg/
│   │   │   ├── RpgApplication.java              # Spring Boot 入口
│   │   │   ├── config/
│   │   │   │   ├── WebSocketConfig.java          # WebSocket 設定
│   │   │   │   └── CorsConfig.java               # CORS 設定
│   │   │   ├── controller/
│   │   │   │   ├── AssetController.java          # 資源 API
│   │   │   │   └── GameController.java           # 遊戲管理 API
│   │   │   ├── websocket/
│   │   │   │   ├── GameWebSocketHandler.java     # WebSocket 處理器
│   │   │   │   └── message/                      # 訊息 DTO
│   │   │   │       ├── ClientMessage.java
│   │   │   │       └── ServerMessage.java
│   │   │   ├── engine/
│   │   │   │   ├── GameEngine.java               # Game Loop + 總協調
│   │   │   │   ├── GameSession.java              # 單一遊戲 Session
│   │   │   │   ├── CollisionEngine.java          # 碰撞偵測 (從 CollisionChecker 遷移)
│   │   │   │   ├── CombatEngine.java             # 戰鬥系統
│   │   │   │   ├── EventEngine.java              # 事件系統 (傳送門等)
│   │   │   │   └── PathFinder.java               # A* (從 ai/ 遷移)
│   │   │   ├── model/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── GameEntity.java           # Entity 基類 (純邏輯, 無 Graphics)
│   │   │   │   │   ├── PlayerEntity.java
│   │   │   │   │   ├── MonsterEntity.java
│   │   │   │   │   ├── NpcEntity.java
│   │   │   │   │   ├── ObjectEntity.java
│   │   │   │   │   └── ProjectileEntity.java
│   │   │   │   ├── map/
│   │   │   │   │   ├── GameMap.java
│   │   │   │   │   ├── TileData.java
│   │   │   │   │   └── MapId.java
│   │   │   │   ├── state/
│   │   │   │   │   ├── GameState.java
│   │   │   │   │   ├── Direction.java
│   │   │   │   │   ├── DayState.java
│   │   │   │   │   └── AreaType.java
│   │   │   │   └── item/
│   │   │   │       ├── ItemDefinition.java
│   │   │   │       └── ItemRegistry.java
│   │   │   ├── factory/
│   │   │   │   ├── MonsterFactory.java           # 怪物工廠
│   │   │   │   ├── NpcFactory.java
│   │   │   │   └── ObjectFactory.java
│   │   │   └── service/
│   │   │       ├── AssetService.java             # 資源管理
│   │   │       ├── SaveService.java              # 存讀檔
│   │   │       └── MapService.java               # 地圖載入
│   │   └── resources/
│   │       ├── application.yml
│   │       └── assets/                           # 從原版複製的遊戲資源
│   │           ├── maps/
│   │           ├── tiles/
│   │           ├── sprites/
│   │           └── sounds/
│   └── test/
│       └── java/gtcafe/rpg/
│           ├── engine/
│           └── model/
└── gradlew / gradlew.bat
```

### 5.2 前端 (frontend)

```
frontend/
├── package.json
├── vite.config.ts
├── index.html
├── tsconfig.json
├── public/
│   └── favicon.ico
├── src/
│   ├── main.tsx                              # React 入口
│   ├── App.tsx                               # 主應用
│   ├── index.css                             # 全域樣式
│   ├── game/
│   │   ├── GameCanvas.tsx                    # Canvas 組件 (遊戲渲染)
│   │   ├── GameLoop.ts                       # 前端 Game Loop (60 FPS 渲染)
│   │   ├── renderer/
│   │   │   ├── TileRenderer.ts              # 磁磚渲染
│   │   │   ├── EntityRenderer.ts            # Entity 精靈渲染
│   │   │   ├── ParticleRenderer.ts          # 粒子效果渲染
│   │   │   ├── LightingRenderer.ts          # 日夜 + 燈光渲染
│   │   │   └── DebugRenderer.ts             # Debug 資訊覆蓋層
│   │   ├── input/
│   │   │   └── KeyboardInput.ts             # 鍵盤處理 / 命令轉換
│   │   ├── assets/
│   │   │   ├── AssetLoader.ts               # 資源預載入
│   │   │   ├── SpriteManager.ts             # 精靈圖管理 + 切割
│   │   │   └── SoundManager.ts              # 音效管理 (Howler.js)
│   │   ├── state/
│   │   │   ├── GameStateManager.ts          # 遊戲狀態本地管理
│   │   │   └── Interpolator.ts              # 座標插值 (平滑動畫)
│   │   └── network/
│   │       ├── WebSocketClient.ts           # WebSocket 連線管理
│   │       └── ApiClient.ts                 # REST API 客戶端
│   ├── ui/
│   │   ├── HUD.tsx                          # 生命/魔力/訊息列
│   │   ├── TitleScreen.tsx                  # 標題畫面
│   │   ├── DialogueBox.tsx                  # 對話框
│   │   ├── InventoryPanel.tsx               # 背包介面
│   │   ├── CharacterPanel.tsx               # 角色狀態
│   │   ├── TradePanel.tsx                   # 交易介面
│   │   ├── OptionsPanel.tsx                 # 設定選單
│   │   ├── GameOverScreen.tsx               # Game Over
│   │   ├── MiniMap.tsx                      # 小地圖
│   │   └── BossHealthBar.tsx                # Boss 血條
│   ├── hooks/
│   │   ├── useGameState.ts                  # 遊戲狀態 Hook
│   │   ├── useWebSocket.ts                  # WebSocket Hook
│   │   └── useKeyboard.ts                   # 鍵盤 Hook
│   └── types/
│       └── game.ts                          # TypeScript 類型定義
└── tsconfig.node.json
```

---

## 六、實作階段計畫

### Phase 0: 專案初始化 (估計: 1 session)

**目標**: 建立 backend + frontend 專案骨架

- [ ] 用 Gradle 初始化 Spring Boot 3.4 + Java 21 專案 (backend/)
- [ ] 用 Vite + React + TypeScript 初始化前端 (frontend/)
- [ ] 設定 CORS、WebSocket 基礎配置
- [ ] 複製遊戲資源到 backend/src/main/resources/assets/
- [ ] 驗收: 前後端可各自啟動、前端能 ping 後端

### Phase 1: 資源服務 + 地圖渲染 (估計: 1-2 sessions)

**目標**: 後端提供靜態資源 API，前端能載入 + 渲染地圖

**後端**:
- [ ] AssetController: 提供 tiles, sprites, maps 的 REST API
- [ ] MapService: 讀取 worldmap.txt, tiledata.txt
- [ ] 資源清單 API (manifest)

**前端**:
- [ ] AssetLoader: 從後端載入所有 tile 圖片
- [ ] TileRenderer: Canvas 繪製磁磚地圖
- [ ] Camera 系統: 以地圖中心點模擬攝影機

- 驗收: 在瀏覽器中可看到完整的世界地圖

### Phase 2: 玩家移動 + WebSocket (估計: 1-2 sessions)

**目標**: 玩家可在地圖上移動，前後端雙向通訊

**後端**:
- [ ] GameEngine: 基礎 game loop (20 ticks/s)
- [ ] GameSession: 管理單一遊戲會話
- [ ] PlayerEntity: 位置、方向、速度
- [ ] WebSocket 端點: 接收輸入、廣播狀態
- [ ] 基礎碰撞偵測 (Tile collision)

**前端**:
- [ ] WebSocketClient: 連線 + 訊息收發
- [ ] KeyboardInput: 擷取 WASD/方向鍵 → 發送 MOVE
- [ ] EntityRenderer: 繪製玩家精靈 (walking animation)
- [ ] Interpolator: 座標插值 (平滑移動)
- [ ] Camera: 追隨玩家

- 驗收: 按方向鍵，玩家在地圖上平滑移動，碰撞正常

### Phase 3: NPC + 物件 + 對話 (估計: 1 session)

**目標**: NPC 能顯示與互動、物件可拾取

**後端**:
- [ ] NpcEntity + NpcFactory: OldMan, Merchant
- [ ] ObjectEntity + ObjectFactory: Key, Coin, Heart, Potion
- [ ] Entity 碰撞偵測 (Entity ↔ Entity)
- [ ] 對話系統: 按 Enter 觸發對話

**前端**:
- [ ] EntityRenderer: 繪製 NPC + 物件精靈
- [ ] DialogueBox: React 對話框組件
- [ ] HUD: 滾動訊息列

- 驗收: 走到 NPC 旁按 Enter 看到對話、撿起物件

### Phase 4: 戰鬥系統 (估計: 2 sessions)

**目標**: 近戰 + 遠程攻擊、怪物 AI

**後端**:
- [ ] MonsterEntity + MonsterFactory: GreenSlime, RedSlime, Bat, Orc
- [ ] CombatEngine: 近戰攻擊計算
- [ ] 防禦 + 格擋 (Guard / Parry)
- [ ] 擊退 (KnockBack)
- [ ] ProjectileEntity: Fireball, Rock
- [ ] 怪物 AI: 隨機移動、追蹤 (A*)、攻擊
- [ ] 怪物掉落物品
- [ ] 粒子效果事件

**前端**:
- [ ] 攻擊動畫渲染 (attack sprites)
- [ ] 防禦動畫
- [ ] ParticleRenderer: 粒子效果
- [ ] HUD: 怪物血條
- [ ] 受傷閃爍效果、死亡動畫

- 驗收: 玩家可攻擊怪物、怪物會追蹤並攻擊玩家

### Phase 5: 裝備 + 背包 + 交易 (估計: 1 session)

**目標**: 完整的物品系統

**後端**:
- [ ] ItemRegistry: 所有物品定義
- [ ] 裝備切換邏輯
- [ ] 交易系統 (Buy/Sell)
- [ ] 可堆疊物品
- [ ] 等級提升

**前端**:
- [ ] InventoryPanel: 背包 UI (4x5 grid)
- [ ] CharacterPanel: 角色狀態面板
- [ ] TradePanel: 交易 UI
- [ ] 等級提升提示

- 驗收: 完整的背包 + 裝備功能、可與商人交易

### Phase 6: 地圖系統 + 事件 (估計: 1 session)

**目標**: 多地圖 + 傳送 + 特殊地圖事件

**後端**:
- [ ] 多地圖管理: World, Store, Dungeon B1, B2
- [ ] EventEngine: 傳送門、治療池、陷阱
- [ ] InteractiveTile: DryTree, DestructibleWall, MetalPlate, BigRock
- [ ] 區域音樂切換事件

**前端**:
- [ ] 地圖轉場動畫 (fadeout/fadein)
- [ ] MiniMap 組件 + 全地圖顯示
- [ ] 互動磁磚渲染

- 驗收: 能在 4 張地圖間傳送，互動磁磚可破壞

### Phase 7: 環境 + 音效 + 效果 (估計: 1 session)

**目標**: 日夜循環、照明、音效、過場

**後端**:
- [ ] DayNightCycle: Day → Dusk → Night → Dawn
- [ ] Lighting 狀態 (filterAlpha)

**前端**:
- [ ] LightingRenderer: 日夜濾鏡 + 提燈照明
- [ ] SoundManager: BGM + SFX 播放
- [ ] Sleep 過場動畫
- [ ] 轉場動畫效果

- 驗收: 日夜循環正常、提燈照明、音效播放

### Phase 8: Boss 戰 + 存檔 + 完善 (估計: 1-2 sessions)

**目標**: Boss 戰、存讀檔、Title Screen

**後端**:
- [ ] SkeletonLord Boss (Rage mode)
- [ ] CutsenseManager → 過場事件
- [ ] SaveService: JSON 存檔/讀檔
- [ ] 推石頭解謎

**前端**:
- [ ] TitleScreen: 遊戲標題畫面
- [ ] BossHealthBar 
- [ ] GameOverScreen
- [ ] 存檔/讀檔 UI
- [ ] Cutscene 渲染 (字幕 + 場景動畫)

- 驗收: 完整的遊戲流程：標題 → 遊戲 → Boss → 結局

### Phase 9: 打磨 + 優化 (估計: 1 session)

- [ ] 效能優化 (Canvas off-screen rendering)
- [ ] 資源壓縮 + CDN
- [ ] 響應式設計 (螢幕大小適配)
- [ ] 全螢幕支援
- [ ] Error handling + 斷線重連
- [ ] Debug 模式 (座標/碰撞框/FPS)

---

## 七、原始檔案對應說明

| 原始 Java 檔案 | 後端對應 | 前端對應 |
|---------------|---------|---------|
| `Main.java` | `RpgApplication.java` | `main.tsx` |
| `GamePanel.java` | `GameEngine.java` + `GameSession.java` | `GameCanvas.tsx` + `GameLoop.ts` |
| `UI.java` | — (UI 全移前端) | `HUD.tsx`, `InventoryPanel.tsx`, ... |
| `Entity.java` | `GameEntity.java` | `EntityRenderer.ts` |
| `Player.java` | `PlayerEntity.java` | `EntityRenderer.ts` (渲染) |
| `Monster subclasses` | `MonsterEntity.java` + `MonsterFactory.java` | `EntityRenderer.ts` |
| `NPC subclasses` | `NpcEntity.java` + `NpcFactory.java` | `EntityRenderer.ts` |
| `Object subclasses` | `ObjectEntity.java` + `ObjectFactory.java` | `EntityRenderer.ts` |
| `CollisionChecker.java` | `CollisionEngine.java` | — (伺服器端計算) |
| `KeyHandler.java` | — | `KeyboardInput.ts` |
| `EventHandler.java` | `EventEngine.java` | — |
| `PathFinder.java` | `PathFinder.java` | — |
| `TileManager.java` | `MapService.java` | `TileRenderer.ts` |
| `SaveLoad.java` | `SaveService.java` | — (透過 API) |
| `Sound.java` | — (資源 API) | `SoundManager.ts` |
| `Lighting.java` | DayNightCycle (tick 數據) | `LightingRenderer.ts` |
| `CutsenseManager.java` | 過場事件邏輯 | Cutscene 渲染 |

---

## 八、關鍵設計決策

### Q1: 為什麼用 Server-Authoritative 架構?
原本的 Java Swing 版本所有邏輯都在本地端，改為前後端分離後，遊戲邏輯放在後端可以確保：
1. 未來可以支持多人遊戲
2. 防止作弊
3. 遊戲邏輯與渲染完全分離，容易維護

### Q2: Server Tick Rate 為什麼是 20?
- 這是一個 tile-based RPG，不是 FPS 遊戲，20 ticks/s 足夠
- 前端用 60 FPS 渲染 + 插值，視覺上完全平滑
- 降低伺服器與網路負擔

### Q3: 為什麼用原生 Canvas 而非 PixiJS/Phaser?
- 這是一個相對簡單的 2D tile-based 遊戲
- 原生 Canvas 足以應付需求，不增加額外依賴
- 可以更精確控制渲染流程，與原版邏輯對應

### Q4: 保留原始 Tile 16x16 嗎？
- 是的，後端仍使用原始 16x16 tile 作為邏輯單位
- 前端根據螢幕大小動態計算 scale (預設 4x = 64px)
- 這樣保持與原版一致的遊戲感受

---

> **下一步**: 從 Phase 0 開始，建立專案骨架。
