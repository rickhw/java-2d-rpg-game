
## Event (Damage Pit, Healing Pool, Teleport Tile) 

https://www.youtube.com/watch?v=7GHrldqWDHA&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=21



- EventHandler
    - 碰撞範圍
    - 偵測 Player 與對應的物件 是否碰撞 
- Damage Pit
- Healing Event：到水邊可以補血
    - the event only occurs when you press ENTER key
- Teleport: 傳送


## Video Summary

### 🎮 事件類別的創建與使用  
- **創建事件類別**：設計一個新的事件類別「Event Handler」，該類別用於處理遊戲中的事件。  
- **事件觸發**：利用**矩形**的碰撞來觸發事件，並將觸發點設置在**瓷磚的中心**，以確保玩家需要進一步進入才能觸發事件。  

### ⚙️ 碰撞檢查和事件處理  
- **碰撞檢查方法**：實現**check event**方法來檢查事件的碰撞，類似於物體的碰撞檢查。可以根據玩家的移動方向決定事件是否觸發。  
- **事件響應**：當玩家觸發事件後，可以回復生命或造成傷害，例如：掉入陷阱會使玩家損失生命，而接觸水池可以使生命恢復。  

### 💔 事件效果的實現  
- **傷害設置**：設計一個事件「damage pit」，玩家掉入陷阱時會遭受傷害。此事件根據矩陣位置與玩家的碰撞判斷來激活。  
- **恢復生命事件**：新增一個事件「healing pool」，玩家在接觸水池時，按**Enter鍵**可以恢復生命。  

### 📅 測試與調試  
- **功能測試**：測試事件功能是否正常，包括生命減少和恢復的邏輯。debug過程中發現邏輯錯誤並進行了修正，使事件能正常運行。  

### ✅ 結論與未來方向  
- **事件已成功實現**：目前已成功實現兩個事件，分別是傷害和生命恢復。  
- **未來計劃**：考慮增加新的挑戰，例如與**怪物戰鬥**等，增加遊戲的趣味性與挑戰性。  
