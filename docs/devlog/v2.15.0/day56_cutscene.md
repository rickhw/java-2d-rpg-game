https://www.youtube.com/watch?v=9czCgoBstn8&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=62

---
# Cutscene (過場動畫)

- Phase 0: Placing Iron Doors
- Phase 1: Moving the camera
- Phase 2: Wake up the boss
- Phase 3: Letting the Boss Speak
- Phase 4: return the camera to player

---
# Summary

### 🎬 創建過場動畫
- 使用 **cutscene manager** 來管理遊戲中的過場動畫。
- 可設定 **boss** 進入休眠狀態，直到過場動畫結束，確保其不會移動。

### 🎤 對話設定
- 設定 **對話框** 以顯示間接對話，讓 **boss** 說出指定的台詞，例如「沒有人能偷走我的寶藏」。

### 📽️ 動畫分段
- 過場動畫通過 **多個階段** 進行，每個階段控制不同的事件和效果，如觸發 **boss** 的出現。
- 使用 **switch 語句** 來根據不同的場景編號調用不同的動畫方法。

### 🎮 遊戲狀態管理
- 在遊戲狀態變更時，需檢查當前的 **boss battle** 狀態以防重複開始。
- 當玩家擊敗 **boss** 後，通過設定狀態變數來避免重啟同一個過場動畫。

### 🔄 物體管理
- 在過場動畫中，需要建立 **虛擬物體** 以避免玩家模型被移動時出現視覺錯覺。
- 使用相關的遊戲方法來 **移除臨時物体**，以防止在下一次進入時產生阻礙。

### 🎵 音效控制
- 在不同階段中設置和控制音樂播放，例如在 **boss** 戰開始和結束時播放對應的音樂，增強遊戲體驗。

### ✅ 測試和調整
- 在完成前，進行一系列測試以確保每個功能正常運作，包括 **擊敗 boss** 後相應效果的觸發。