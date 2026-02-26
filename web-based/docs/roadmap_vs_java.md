# Web-based 2D RPG 發展藍圖與差異對照 (Roadmap vs Java)

這份文件總結了目前 Web 版本與原本 Java 實作（GT Cafe RPG）的進度差異，並且將尚未開發的功能分拆為後續的開發階段 (Phase) 規劃。

---

## 🟢 已經完成的開發階段

目前專案已經順利將地圖、角色、碰撞、對話與道具系統實作完畢，並完整支援前後端 WebSocket 的同步連線。

- **Phase 1: 基礎遊戲引擎與地圖宣染**
  - 後端遊戲迴圈 (Tick 系統, 60 FPS)。
  - Tiled Map 渲染 (Layer, Tile Size 解析)。
  - 玩家 WASD 基本移動同步。
- **Phase 2: 碰撞與精靈動畫**
  - 伺服器端的 Tile Collision 碰撞偵測 (防止走入牆壁/水裡)。
  - 精靈圖 (Sprites) 序列對應不同的 Walking Animation。
  - Player 基礎攻擊與防禦的按鍵狀態廣播。
- **Phase 3: NPC 系統與對話狀態**
  - NPC 實體與亂數閒晃邏輯。
  - `DIALOGUE` 遊戲狀態切換。
  - 前端對話框 (Dialogue Box) UI 與 Enter 換行推進機制。
  - 基本的玩家狀態列 (HUD) 顯示 HP/MP。
- **Phase 4: 傳送系統與物件、背包實作**
  - 跨地圖傳送 (Teleport) 與轉場漸變黑幕 (Transition)。
  - 掉落物/可互動地圖物件 (Chests, Doors, Keys, Potions)。
  - `CHARACTER` 背包介面 (Inventory UI) 的展開與游標同步。
  - 道具效果實作 (裝備武器盾牌、喝水補血、自動消耗數量)。

---

## 🔴 尚待開發的功能 (Java 版現有功能)

基於原本的 Java 遊戲生態，目前 Web 版本仍缺乏戰鬥細節、怪物 AI、光影、存檔、商店交易... 等機制。以下將這些未開發功能依賴性高低拆分為未來的 Phase 5 到 Phase 8 規劃：

### 📌 Phase 5: 戰鬥系統強化與怪物 AI (Combat & Monster AI)
這階段著重於將「遊戲」變得好玩，也是 RPG 核心。
- **怪物行為實作 (Monster AI)**: 如 Slime, Orc, Red Slime 等，實作追蹤玩家的尋路 (Pathfinding) 或亂數移動。
- **進階戰鬥與傷害公式**:
  - 套用真正的攻擊力 (STR + Weapon) 與防禦力 (DEX + Shield) 公式。
  - 被攻擊時的無敵時間幀 (Invincible State) 及閃爍特效。
  - 原 Java 版被攻擊時的擊退效果 (Knockback)。
- **投射物 (Projectiles)**:
  - 實現玩家射出火球 (Fireball) 需消耗 Mana 系統。
  - 怪物丟石塊 (Rock) 等遠程攻擊。
- **等級經驗系統**: 怪物死亡掉落 EXP、Coin 與隨機道具；實作 Level Up 升級提示框與屬性成長。

### 📌 Phase 6: 特殊系統：環境光影與交易 (Environment, Lighting & Trade)
實作遊戲深度的進階邏輯與系統。
- **商店系統 (Trade Menu)**:
  - 實作向商人 NPC 買、賣道具的切換介面 `GameState.TRADE`。
- **日夜循環與睡眠 (Day/Night & Sleep)**:
  - 背景晝夜交替機制。
  - 實作玩家使用帳篷 (Tent) 睡覺後跳轉至白天的機制。
- **光影系統 (Lighting System)**:
  - 將 Dungeon 地下城設為黑暗，實作使用燈籠 (Lantern) 時照亮周圍的遮罩演算 (Light Circle)。
- **環境互動效果 (Environment / Particle)**:
  - 實作揮劍破壞地圖上的 草叢 (Grass)、罐子 等互動。
  - 物件破壞與受擊時產生的粒子噴灑特效 (Particle Generator)。

### 📌 Phase 7: 總體 UI 與系統流程 (Main Menu, Save/Load & Maps)
將遊戲首尾串接，變得可以保存進度。
- **主畫面與遊戲中斷控制**: 
  - 讓 Title Screen 上的 NEW GAME, LOAD GAME, QUIT 真正擁有邏輯並切換 API 狀態。
  - 實作 Game Over 畫面與 Retry 機制。
- **存檔/讀檔系統 (Save/Load)**:
  - 後端存取玩家屬性、背包、在地圖的座標、是否開過寶箱/打倒過 Boss 等。
  - 使用者重整網頁時可以找回進度。
- **導航地圖介面**:
  - 右上角的迷你地圖 (Minimap) 視窗。
  - 按鍵叫出的全螢幕大地圖 (Full Map) `GameState.DISPLAY_MAP`。

### 📌 Phase 8: 音效、過場與潤飾 (Audio, Cutscenes & Polish)
最後完善遊戲的氛圍與特效。
- **音樂與音效 (BGM / SE)**:
  - 前端建立 AudioContext 或簡單的 Audio 元素管理。
  - 對應各個行為 (攻擊、受傷、升級、吃金幣、開門) 播放音效。
  - 依據不同地圖區塊切換背景音樂。
- **過場動畫 (Cutscenes)**:
  - 原版 Java 戰勝魔王或特殊事件時的 Cutscene 機制（鎖定玩家操作、運鏡與特定對話強制播放）。
  - 對應 `GameState.CUTSCENE` 處理。
