# Blue Boy The Venture

本專案是一個名為 **"Blue Boy The Venture"** 的完整 2D 角色扮演遊戲 (RPG)，使用 Java 原生 Swing 函式庫開發。

## 功能特性 (Features)

以下是目前已實作的主要功能列表。

### 核心系統 (Core System)
*   **遊戲引擎 (Game Engine)**: 基於 Java Swing `JPanel` 的自製遊戲循環 (Game Loop)，鎖定 60 FPS。
*   **磚塊系統 (Tile System)**: 16x16 像素基礎磚塊，放大 4 倍顯示。支援碰撞屬性與互動式磚塊 (如可破壞的樹木、寶箱)。
*   **狀態管理 (State Management)**: 支援多種遊戲狀態切換 (標題畫面、遊玩中、暫停、對話、角色選單、交易、地圖、過場動畫、遊戲結束)。
*   **存檔與讀檔 (Save & Load)**: 支援遊戲進度的保存與讀取 (Entity 位置、狀態、背包內容等)。
*   **設定系統 (Config)**: 可調整音效音量、音樂音量、全螢幕模式與按鍵設定，並保存至設定檔。

### 世界與環境 (World & Environment)
*   **多地圖系統 (Multi-Map System)**: 支援多個區域切換 (世界地圖、地牢、室內場景)，並透過傳送點 (Teleport) 連接。
*   **光影系統 (Lighting System)**: 動態光影效果，包含日夜循環 (Day/Night Cycle) 以及光源 (如燈籠) 的範圍渲染。
*   **環境互動 (Interaction)**: 玩家可與環境物件互動，例如砍樹、開門、開啟寶箱。
*   **地圖介面 (Map UI)**: 支援小地圖 (Minimap) 以及全螢幕地圖顯示，並標記玩家位置。
*   **過場動畫 (Cutscenes)**: 支援腳本化的過場動畫系統，用於推動劇情 (如 Boss 戰前對話、結局動畫)。

### 角色與 AI (Entities & AI)
*   **玩家角色 (Player)**:
    *   支援 8 方向移動。
    *   屬性系統：生命值 (Life)、魔力 (Mana)、力量、靈巧、攻擊力、防禦力、經驗值與等級。
    *   裝備系統：主手武器、副手盾牌。
*   **NPC 系統**:
    *   對話系統：支援多段對話、選項分歧。
    *   尋路 AI (Pathfinding): 基於 A* 演算法，NPC 可自動尋找路徑移動到目標點。
*   **怪物系統 (Monsters)**:
    *   多樣化 AI：包含被動、主動攻擊、巡邏模式。
    *   Boss 戰：支援 Boss 血條顯示、戰鬥階段變化。
    *   掉落物系統 (Loot): 怪物死亡後機率掉落物品、金幣或回復球。

### 戰鬥系統 (Combat System)
*   **即時戰鬥 (Real-time Combat)**: 近戰攻擊與投射物 (魔法/箭矢) 攻擊。
*   **受擊判定 (Hit Detection)**: 精確的碰撞判定，包含無敵時間 (Invincibility Frames)。
*   **防禦與格擋 (Guard & Parry)**: 玩家使用盾牌可減少傷害，精準格擋 (Parry) 可造成反擊機會。
*   **擊退效果 (Knockback)**: 攻擊會造成實體位移。
*   **粒子效果 (Particles)**: 攻擊命中、物體破壞時的視覺特效。

### 物品與背包 (Inventory & Items)
*   **背包介面 (Inventory UI)**: 網格狀背包，支援游標操作。
*   **物品分類**:
    *   消耗品 (Consumables): 藥水、食物。
    *   裝備 (Equipment): 劍、斧、盾牌等。
    *   關鍵道具 (Key Items): 開門鑰匙、劇情物品。
*   **物品堆疊 (Stackable Items)**: 相同物品可堆疊顯示。
*   **交易系統 (Trade System)**: 與 NPC 商人進行買賣。

### 使用者介面 (UI/UX)
*   **抬頭顯示器 (HUD)**: 顯示玩家生命 (紅心)、魔力 (水晶) 及當前裝備。
*   **訊息系統 (Message System)**: 畫面下方滾動顯示撿拾物品、升級等資訊。
*   **漂浮文字 (Floating Text)**: 戰鬥時顯示傷害數值。
*   **標題與選單 (Menus)**: 精美的標題畫面、選項選單、遊戲結束畫面。

### 音效 (Audio)
*   **背景音樂 (BGM)**: 隨場景切換背景音樂。
*   **音效 (SFX)**: 攻擊、受傷、選單操作、環境互動等豐富音效。

## 建置與執行 (Build and Run)

### 建置專案 (Build the project)
```bash
./gradlew build
```

### 執行專案 (Run the project)
```bash
./gradlew run
```

### 打包成 JAR 檔案 (Package into a JAR file)
```bash
./gradlew clean jar
```
`clean` 指令會先清除舊的建置檔案。打包完成後，帶有版本號的 JAR 檔案會被建立在 `build/libs/` 目錄下 (例如 `game-1.0.0.jar`)。

### 執行 JAR 檔案 (Run the JAR file)
```bash
java -jar build/libs/game-x.x.x.jar
```
請將 `game-x.x.x.jar` 替換為您當前建置的 JAR 檔案名稱。

