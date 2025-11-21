
## Event II (Advanced Mechanics) 

https://www.youtube.com/watch?v=ua4B70Xdh3U&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=22


- Refactoring: Add the Direction Enum to replace the string "up/down/left/right"


---
## Video Summary (By Chrome Extension)

### 🎮 簡介
- 本影片聚焦於改善玩家角色與**事件系統**的互動，特別是在遭受傷害時的延續性問題。

### ⚙️ 事件處理器改進
- 在之前的版本中，玩家持續受到傷害，若不變更方向。這次改進使系統更靈活，允許更多選擇。
- 新增一個 **event left** 類，擴展自**矩形**類，能夠在地圖的每個方塊上創建事件。

### 🔧 代碼重構
- 移除了之前的矩形事件，只使用 **event left** 作為二維陣列來組織事件。
- 增加了**boolean**變數以跟蹤事件是否已經發生（單次事件）。

### 🚧 事件觸發邏輯
- 透過記錄角色的過去位置，確保角色遠離事件方塊才能再次觸發事件，避免重複受傷。
- 設置強制距離條件，使角色只有在移動一個方塊距離後，事件才會再度觸發。

### 🆕 更多功能擴展
- 系統現在允許增加多種事件類型，例如**一次性傷害事件**和可以多次觸碰的事件。
- 可以根據需求隨著時間添加其他事件類型以豐富遊戲體驗。

### 🔜 下一步
- 接下來的影片將針對玩家角色的NPC介紹及其他bug進行修正和回覆。