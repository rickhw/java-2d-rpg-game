# Godot 4.5 遊戲復刻專案

## 專案階段

### Phase 1: 專案初始化與核心架構
- [x] 分析原始 Java 程式碼架構
- [x] 建立 Godot 專案結構
- [x] 複製素材到 Godot 專案
- [x] 設定專案基本參數 (解析度、縮放)

### Phase 2: 地圖系統 (Tile System)
- [x] 建立 TileSet 資源
- [x] 實作 TileMap 載入機制
- [x] 實作 4 張地圖 (worldmap, indoor01, dungeon01, dungeon02)
- [x] 設定碰撞圖層

### Phase 3: 玩家系統 (Player System)
- [ ] 建立 Player 場景與腳本
- [ ] 實作移動控制 (WASD/方向鍵)
- [ ] 實作精靈動畫 (walking, attacking, guarding)
- [ ] 實作攝影機跟隨

### Phase 4: 實體系統 (Entity System)
- [ ] 建立 Entity 基礎類別
- [ ] 實作 NPC 類別 (OldMan, Merchant, BigRock)
- [ ] 實作 Monster 類別 (GreenSlime, RedSlime, Orc, Bat, SkeletonLord)
- [ ] 實作 Object 類別 (Door, Chest, Key, Potion 等)
- [ ] 實作 Projectile 類別 (Fireball, Rock)

### Phase 5: 碰撞與互動系統
- [ ] 設定 Physics Layers
- [ ] 實作 Tile 碰撞偵測
- [ ] 實作 Entity 碰撞偵測
- [ ] 實作物件互動 (開門、開寶箱)

### Phase 6: 戰鬥系統
- [ ] 實作玩家攻擊
- [ ] 實作怪物 AI (隨機移動、追蹤、攻擊)
- [ ] 實作傷害計算
- [ ] 實作擊退效果 (Knockback)
- [ ] 實作格擋與反擊 (Parry)

### Phase 7: UI 系統
- [ ] 實作標題畫面
- [ ] 實作 HUD (生命值、魔力值)
- [ ] 實作對話系統
- [ ] 實作角色狀態畫面
- [ ] 實作物品欄
- [ ] 實作暫停畫面
- [ ] 實作選項選單
- [ ] 實作交易系統

### Phase 8: AI 與尋路系統
- [ ] 實作 A* 尋路演算法 (或使用 Godot NavigationAgent2D)
- [ ] 實作怪物追蹤行為
- [ ] 實作 NPC 跟隨行為

### Phase 9: 環境系統
- [ ] 實作日夜循環
- [ ] 實作照明效果
- [ ] 實作場景轉換效果

### Phase 10: 音效系統
- [ ] 設定 BGM 播放
- [ ] 設定音效播放
- [ ] 實作音量控制

### Phase 11: 存檔系統
- [ ] 實作存檔機制
- [ ] 實作讀檔機制
- [ ] 設定存檔選項

### Phase 12: 進階功能
- [ ] 實作過場動畫 (Cutsense)
- [ ] 實作世界地圖與小地圖
- [ ] 實作粒子效果
- [ ] 實作 Boss 戰鬥
