# Phase 2 進度報告 - Tilemap System

## 完成項目

### 1. TilemapLoader 腳本 ✅

檔案：`scripts/systems/tilemap_loader.gd`

實作功能：
- **讀取 tiledata.txt**：解析 file containing 38 tiles with collision info
  - 奇數行：tile 圖片檔名（000.png - 037.png）
  - 偶數行：collision 狀態（true/false）
  
- **生成 TileSet**：
  - 為每個 tile 建立 TileSetAtlasSource
  - 設定 tile size = 64x64
  - 自動設置碰撞多邊形（collision = true 的 tiles）
  
- **載入地圖檔案**：
  - 讀取 worldmap.txt, indoor01.txt, dungeon01.txt, dungeon02.txt
  - 解析空格分隔的 tile ID（50x50 grid）
  - 回傳 2D array
  
- **應用地圖到 TileMapLayer**：
  - 將 2D array 數據設置到 TileMapLayer
  - 自動設置每個 tile 的 source_id 和 atlas_coords

### 2. 地圖場景建立 ✅

建立了 4 個地圖場景，每個場景包含：
- TilemapLoader 節點
- TileMapLayer 節點
- Camera2D 節點（位於螢幕中央）

**場景列表：**
1. `scenes/maps/world_map.tscn` - 世界地圖（worldmap.txt）
2. `scenes/maps/indoor.tscn` - 室內/商店（indoor01.txt）
3. `scenes/maps/dungeon_01.tscn` - 地下城 B1（dungeon01.txt）
4. `scenes/maps/dungeon_02.tscn` - 地下城 B2（dungeon02.txt）

### 3. 主場景更新 ✅

`scenes/main.tscn` 已更新：
- 動態載入 world_map 場景進行測試
- 顯示測試訊息
- 可改變載入的場景來測試不同地圖

## 技術要點

### Godot vs Java 對照

| Java (TileManager) | Godot (TilemapLoader) |
|--------------------|-----------------------|
| BufferedImage[] tiles | TileSet with TileSetAtlasSource[] |
| int[][][] mapTileNum | Array[Array[int]] map_data |
| Graphics2D draw() | TileMapLayer 自動繪製 |
| 手動計算 worldX/worldY/screenX/screenY | Godot 自動處理座標 |
| 手動繪製順序 | TileMapLayer 自動 z-index |
| 手動碰撞檢測 | TileSet collision polygons |

### 關鍵差異

**Java 版本**：
```java
// 手動計算並繪製每個 tile
int worldX = worldCol * gp.tileSize;
int worldY = worldRow * gp.tileSize;
int screenX = worldX - gp.player.worldX + gp.player.screenX;
int screenY = worldY - gp.player.worldY + gp.player.screenY;
g2.drawImage(tiles[tileNum].image, screenX, screenY, null);
```

**Godot 版本**：
```gdscript
# 直接設置 tile，Godot 自動處理所有繪製
tilemap.set_cell(Vector2i(col, row), tile_id, Vector2i(0, 0))
```

## 下一步測試

請在 Godot 編輯器中：

1. **運行專案** (F5)
   - 應該會載入世界地圖
   - 查看控制台訊息確認載入成功

2. **檢查地圖顯示**
   - 地圖應正確顯示 50x50 tiles
   - tiles 應該是 64x64 像素

3. **測試其他地圖**
   - 修改 `main.tscn` 的腳本，改成載入其他場景：
     ```gdscript
     var map = load("res://scenes/maps/indoor.tscn").instantiate()
     # 或 dungeon_01.tscn, dungeon_02.tscn
     ```

4. **查看 TileSet Inspector**
   - 選擇 TileMapLayer 節點
   - 在 Inspector 查看 Tile Set 屬性
   - 確認有 38 個 sources
   - 點擊有 collision 的 tiles，確認碰撞形狀存在

## 已知問題與待辦

- [ ] 需要測試碰撞檢測是否正確
- [ ] 需要優化 TileSet 生成（目前每個場景都重新生成）
- [ ] 考慮使用單一 atlas texture 而非 38 個獨立 sources（效能優化）
- [ ] 相機需要跟隨玩家（目前固定在中央）

## 檔案清單

**新增檔案：**
- `scripts/systems/tilemap_loader.gd`
- `scenes/maps/world_map.tscn`
- `scenes/maps/indoor.tscn`
- `scenes/maps/dungeon_01.tscn`
- `scenes/maps/dungeon_02.tscn`

**修改檔案：**
- `scenes/main.tscn`

---

**Phase 2 狀態：部分完成，等待測試驗證**
