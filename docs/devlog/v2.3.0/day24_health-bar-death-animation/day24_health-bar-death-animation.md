
## Health Bar & Death Animation

https://www.youtube.com/watch?v=YMFCzAZlqbA&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=27

- Monster death effect
- Sound effect: Attack, Damage
- Monster Health Bar
- Monster react to damage (Additional AI)

## Video Summary

### 🎮 動畫效果與AI改進
- 在遊戲中添加了**怪物死亡動畫**，使用閃爍效果顯示怪物的**生命狀態**，設置怪物的狀態變數為`alive`和`dying`。
- 實現了一種**簡單的AI行為**，當怪物受到傷害時，會根據玩家的方向移動，增加互動性。

### 🔊 音效增強
- 添加了三個新音效，包括**擊中怪物**、**受到傷害**和**揮動武器**，以提升遊戲的沉浸感。
- 在相關事件觸發時，如攻擊和受傷，適當播放相應的音效。

### 🏥 生命條顯示
- 新增怪物的**生命條**顯示，根據怪物的生命值動態調整長度，提供玩家即時的戰鬥資訊。
- 實現了**生命條的消失機制**，若在10秒內不攻擊怪物，生命條會自動隱藏，增強遊戲體驗。

### ⚙️ 參數設置優化
- 透過變數對照來管理生命條的長度，未來調整***時間間隔***等參數變得更加方便。
- 將數字使用變數替代，提升代碼可讀性和靈活性。

### 📅 進一步的計劃
- 下一步將會增加**玩家屬性**系統，包括力量、敏捷、攻擊、防禦、等級等參數，以豐富遊戲的角色扮演元素。