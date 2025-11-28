

https://www.youtube.com/watch?v=d5E_O2N73ZU&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=38

# Full Screen Mode

0. Original: draw everything to screen directly
1. Step#1: draw everything to temp screen as buffered image
2. Step#2: resize buffered image, and put to screen.
    - we also can resize every objects one by one, but it may cause the performance issue.
3. Handle the issues on MacOS Only
    - BufferedImage.TYPE_INT_ARGB_PRE vs BufferedImage.TYPE_INT_ARGB
    - setVisible on setFullScreen(): `Main.window.setVisible(true);`

---

# Video Summary

### 🎮 遊戲改進概述
- 在上一次的教學中，添加了**粒子效果**，當砍樹或施放火球時會顯示。
- 地圖已被編輯，起始位置右側的樹木**阻擋了路徑**，需要找到斧頭才能通過。

### 🔧 錯誤修正
- 在之前的代碼中，對於粒子生成的**方法參數**使用不當，需傳入正確的目標（target）以修正顯示錯亂。

### 🖥️ 全螢幕顯示
- 新增**全螢幕功能**的實現，雖非最初計畫，但因為用戶需求增加而做出的調整。
- 設定**屏幕比例**以避免圖像拉伸，調整為接近**16:9**的比例。

### 🖱️ 渲染過程
- 遊戲畫面需要從**原始面板（JPanel）**改為使用**緩衝影像（BufferedImage）**進行渲染。
- 實現步驟分為**兩個階段**：首先將所有內容繪製到緩衝影像，然後將其顯示於面板上。

### 📏 調整窗口設置
- 在全螢幕模式下，需調整窗口的**位置**；例如角色與庫存的UI使顯示更為合理。
- 利用`GraphicsEnvironment`獲取**顯示設備**的資訊以適應正確的模擬環境。

### ⚙️ 附加功能
- 刪除標題欄會導致**關閉按鈕**消失，因此需額外設置遊戲選項以實現關閉遊戲的功能。
- 可以通過暫停鍵安全退出全螢幕模式。 

以上是影片中的關鍵內容，涵蓋了遊戲改進、錯誤修正及全螢幕顯示的詳細步驟。