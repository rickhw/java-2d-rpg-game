

https://www.youtube.com/watch?v=kkqCeCiFO-8&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=41


# Saving Config to a Text File



---

# Video Summary

### 🛠️ 設定檔創建
- 為了能夠在重新啟動遊戲時恢復設置，創建一個**config**檔案。
- 設定檔需位於資源文件夾之外，以便可以在導出為**JAR**時能夠寫入資料。

### 💾 儲存設置
- 使用**BufferedWriter**來將全螢幕設置及音量等選項寫入config.txt。
- 需要將賦值轉換為字符串（例如，音量值）。

### 🔄 載入設置
- 使用**BufferedReader**來讀取config.txt的內容。
- 根據讀取的值設定遊戲的全螢幕和音量，將字符串轉回整數以便使用。

### 📋 儲存與恢復
- 在選項變更時调用**save config**方法確保設置被持久化。
- 當遊戲啟動時，呼叫**load config**來恢復用戶的設置。

### 🔊 音量控制
- 支援用戶設定的音量，包括音樂音量和聲音控制，並能夠在每次啟動時正確加載這些值。

### ✅ 測試與驗證
- 測試確保設置正確保存與恢復，如全螢幕模式及音量設置正常運作。