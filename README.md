

## 功能列表 (Feature List)

*   **基礎 2D 遊戲引擎 (Basic 2D Game Engine)**: 一個簡單的 2D 遊戲引擎核心。
*   **玩家角色 (Player Character)**: 可在遊戲世界中移動的玩家角色。
*   **磚塊式世界 (Tile-based World)**: 遊戲世界由磚塊構成，磚塊可以是實體或可穿透的。
*   **地圖加載 (Map Loading)**: 遊戲可以從文字檔案中加載地圖。
*   **碰撞偵測 (Collision Detection)**: 引擎可以偵測玩家與實體磚塊之間的碰撞。
*   **精靈動畫 (Sprite Animation)**: 玩家角色具有簡單的移動精靈動畫。
*   **鍵盤輸入 (Keyboard Input)**: 遊戲接受鍵盤輸入 (WASD 和方向鍵) 來控制玩家移動。
*   **遊戲循環 (Game Loop)**: 經典的遊戲循環，用於處理更新和渲染。
*   **攝影機系統 (Camera System)**: 跟隨玩家的簡易攝影機。


## 版本管理 (Versioning)

每次要發布新版本或增加新功能之前，請依照以下流程：

1.  **更新版本號**：在 `build.gradle` 檔案中，找到 `version = '1.0.0'` 這一行，並將其更新為新的版本號（例如 `1.1.0`）。建議遵循 [語意化版本 (Semantic Versioning)](https://semver.org/lang/zh-TW/) 的規範。
2.  **開發新功能**：完成新功能的開發與測試。
3.  **打包發布**：完成開發後，依照下面的指令打包成 JAR 檔案。

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
`clean` 指令會先清除舊的建置檔案。打包完成後，帶有版本號的 JAR 檔案會被建立在 `build/libs/game-1.0.0.jar` (版本號會根據 `build.gradle` 中的設定而變)。

### 執行 JAR 檔案 (Run the JAR file)
```bash
java -jar build/libs/game-1.0.0.jar
```
請將 `game-1.0.0.jar` 替換為您當前建置的 JAR 檔案名稱。