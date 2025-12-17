# M/A Legend - Godot 4 Port

## 專案說明

這是原 Java 2D RPG 遊戲的 Godot 4 移植版本，使用 Godot 引擎的內建功能重新實作所有遊戲邏輯。

## 專案設定

- **引擎版本**: Godot 4.3+
- **解析度**: 960x576 (16:9)
- **瓦片大小**: 64x64 像素
- **幀率**: 60 FPS

## 目錄結構

```
godot/
├── project.godot          # Godot 專案設定檔
├── scenes/                # 場景檔案
│   ├── main.tscn         # 主場景（測試用）
│   ├── world/            # 地圖場景
│   ├── entities/         # 實體場景（玩家、怪物、NPC）
│   ├── ui/               # UI 場景
│   └── tiles/            # 瓦片場景
├── scripts/              # GDScript 腳本
│   ├── autoload/         # 自動載入單例
│   │   ├── game_manager.gd    # 遊戲狀態管理
│   │   ├── audio_manager.gd   # 音效管理
│   │   ├── save_manager.gd    # 存檔管理
│   │   └── event_bus.gd       # 事件總線
│   ├── entities/         # 實體腳本
│   ├── systems/          # 系統腳本
│   └── ui/               # UI 腳本
├── resources/            # 資源檔案（從原專案複製）
│   ├── player/           # 玩家精靈圖
│   ├── monsters/         # 怪物精靈圖
│   ├── npcs/             # NPC 精靈圖
│   ├── objects/          # 物品精靈圖
│   ├── tilesV3/          # 瓦片圖
│   ├── bgm/              # 背景音樂
│   ├── sound/            # 音效
│   └── font/             # 字型
└── tilesets/             # Godot TileSet 資源
```

## 自動載入單例 (Autoload)

以下單例在遊戲啟動時自動載入：

1. **GameManager**: 管理遊戲狀態、場景切換、全域變數
2. **AudioManager**: 管理 BGM 和音效播放
3. **SaveManager**: 處理存檔和讀檔
4. **EventBus**: 集中管理遊戲事件訊號

## 輸入映射

| 動作 | 按鍵 |
|------|------|
| 移動 | WASD 或 方向鍵 |
| 攻擊 | Enter |
| 發射 | F 或 J |
| 格擋 | Space |
| 互動 | E |
| 背包 | C |
| 暫停 | P 或 Esc |
| 地圖 | M |
| 小地圖 | V |

## 物理層設定

| 層級 | 用途 |
|------|------|
| Layer 1 | Player（玩家）|
| Layer 2 | Enemies（敵人）|
| Layer 3 | Objects（物件）|
| Layer 4 | Walls（牆壁/障礙物）|
| Layer 5 | Projectiles（拋射物）|
| Layer 6 | InteractiveTiles（互動式瓦片）|
| Layer 7 | NPCs（非玩家角色）|
| Layer 8 | Triggers（觸發區域）|

## 開發階段

### ✅ 第一階段：基礎框架（已完成）
- [x] 專案設定和目錄結構
- [x] 素材複製
- [x] 自動載入單例建立
- [x] 輸入映射設定
- [x] 測試場景

### 🔄 第二階段：玩家系統（進行中）
- [ ] 玩家場景和腳本
- [ ] 移動系統
- [ ] 動畫系統
- [ ] 攻擊系統
- [ ] 屬性系統

### ⏳ 後續階段
- 地圖系統
- 怪物 AI
- NPC 與對話
- UI 系統
- 進階功能
- 音效與存檔
- 測試與優化

## 驗證步驟

1. 用 Godot 4.3+ 開啟 `godot/project.godot`
2. 執行遊戲（F5）
3. 檢查 Output 視窗，應該看到：
   - `[GameManager] 初始化完成`
   - `[AudioManager] 初始化完成`
   - `[SaveManager] 初始化完成`
   - `[EventBus] 事件總線初始化完成`
   - `[Main] 主場景載入完成`
4. 測試按鍵輸入（WASD、Enter 等），應在 Output 看到偵測訊息
5. 按 P 測試暫停功能

## 注意事項

- 本專案使用最近鄰濾波（Nearest Neighbor）保持像素風格
- 所有圖片素材使用原始 16x16 像素，透過 Godot 的 `scale` 放大
- 使用 Godot 內建的物理引擎和碰撞系統，避免手動計算

## 授權

本專案為學習用途，原始素材和遊戲設計保留所有權利。
