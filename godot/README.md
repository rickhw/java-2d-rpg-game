# Godot Game Replication - README

## 專案說明

這是原 Java 2D RPG 遊戲的 Godot v4.5 復刻版本。

### 原始專案

- **語言**: Java
- **位置**: `/src/main/java/gtcafe/rpg/`
- **資源**: `/src/main/resources/gtcafe/rpg/assets/`

### Godot 復刻專案

- **引擎版本**: Godot 4.3+ (相容 4.5)
- **位置**: `/godot/`
- **開發文件**: `implementation_plan.md`

## 目錄結構

```
godot/
├── project.godot              # Godot 專案配置
├── implementation_plan.md     # 完整實作計劃
├── assets/                    # 遊戲資源（從 Java 專案複製）
│   ├── bgm/                  # 背景音樂
│   ├── sound/                # 音效
│   ├── font/                 # 字型
│   ├── player/               # 玩家圖片
│   ├── monster/              # 怪物圖片
│   ├── npc/                  # NPC 圖片
│   ├── objects/              # 物品圖片
│   ├── projectiles/          # 拋射物圖片
│   ├── tilesV3/              # 瓦片圖片
│   ├── tiles_interactive/    # 互動式瓦片
│   └── maps_v2/              # 地圖數據檔案
├── scenes/                    # Godot 場景
│   ├── main.tscn             # 主場景
│   ├── entities/             # 實體場景
│   ├── ui/                   # UI 場景
│   └── maps/                 # 地圖場景
├── scripts/                   # GDScript 腳本
│   ├── autoload/             # 自動載入腳本
│   │   ├── game_manager.gd   # 遊戲狀態管理
│   │   ├── audio_manager.gd  # 音效管理
│   │   └── save_manager.gd   # 存檔管理
│   ├── entities/             # 實體腳本
│   ├── systems/              # 系統腳本
│   └── ui/                   # UI 腳本
└── data/                      # 資料定義
    └── constants.gd          # 遊戲常數
```

## 開發進度

請參閱 `implementation_plan.md` 中的詳細實作計劃和驗收標準。

### Phase 1: 專案設置 ✅ (已完成)

- [x] 建立目錄結構
- [x] 複製資源檔案（216 張圖片，23 個音效）
- [x] 建立 `project.godot` 配置檔
- [x] 建立自動載入腳本（GameManager, AudioManager, SaveManager）
- [x] 建立常數定義（Constants.gd）

### Phase 2-11: 實作中

詳見 `implementation_plan.md`

## 如何開啟專案

1. 安裝 [Godot 4.3+](https://godotengine.org/download/)
2. 開啟 Godot 編輯器
3. 選擇 "Import"
4. 選擇此目錄中的 `project.godot` 檔案
5. 點擊 "Import & Edit"

## 遊戲控制

| 按鍵 | 功能 |
|------|------|
| WASD / 方向鍵 | 移動 |
| F / J | 攻擊 |
| Space | 格擋 |
| C | 角色狀態 |
| M | 全地圖 |
| V | 小地圖 |
| Enter | 對話/確認 |
| ESC | 暫停/返回 |

## 技術特點

- 使用 Godot 內建的 `TileMapLayer` 系統處理瓦片地圖
- 使用 `CharacterBody2D` 處理實體物理與碰撞
- 使用 `AStarGrid2D` 實作怪物 AI 尋路
- 使用 `CPUParticles2D` 實作粒子效果
- 使用 `Camera2D` 實作相機跟隨
- 使用 Autoload 模式管理全域狀態

## 參考資料

- [Godot 官方文件](https://docs.godotengine.org/)
- [原始 Java 專案](../src/main/java/gtcafe/rpg/)
- [實作計劃](implementation_plan.md)
