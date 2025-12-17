extends Node
## MapConverter - 地圖資料轉換工具
## 將 .txt 地圖檔案轉換為 Godot TileMap

## 讀取 .txt 地圖檔案
func load_map_from_txt(file_path: String) -> Array:
	print("[MapConverter] 載入地圖: %s" % file_path)
	
	if not FileAccess.file_exists(file_path):
		push_error("[MapConverter] 檔案不存在: %s" % file_path)
		return []
	
	var file = FileAccess.open(file_path, FileAccess.READ)
	if not file:
		push_error("[MapConverter] 無法開啟檔案: %s" % file_path)
		return []
	
	var map_data = []
	var line_number = 0
	
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		line_number += 1
		
		# 跳過空行
		if line.is_empty():
			continue
		
		# 分割瓦片 ID
		var tiles_str = line.split(" ")
		var row = []
		
		for tile_str in tiles_str:
			if not tile_str.is_empty():
				var tile_id = int(tile_str)
				row.append(tile_id)
		
		if row.size() > 0:
			map_data.append(row)
	
	file.close()
	
	var height = map_data.size()
	var width = map_data[0].size() if height > 0 else 0
	print("[MapConverter] 地圖尺寸: %d x %d" % [width, height])
	
	return map_data

## 將地圖資料應用到 TileMap
func apply_map_to_tilemap(tilemap: TileMap, map_data: Array, layer: int = 0) -> void:
	if map_data.is_empty():
		push_warning("[MapConverter] 地圖資料為空")
		return
	
	print("[MapConverter] 開始填充 TileMap...")
	
	var tile_count = 0
	for y in range(map_data.size()):
		for x in range(map_data[y].size()):
			var tile_id = map_data[y][x]
			
			# Godot 4.x TileMap API
			# set_cell(layer, coords, source_id, atlas_coords)
			# 因為我們為每個瓦片建立了獨立的 TileSetAtlasSource
			# source_id = tile_id (瓦片 ID 就是 source ID)
			# atlas_coords = Vector2i(0, 0) - 每個 source 只有一個瓦片
			tilemap.set_cell(layer, Vector2i(x, y), tile_id, Vector2i(0, 0))
			tile_count += 1
	
	print("[MapConverter] 完成！填充了 %d 個瓦片" % tile_count)

## 讀取瓦片碰撞資料
func load_tile_collision_data(file_path: String) -> Dictionary:
	print("[MapConverter] 載入碰撞資料: %s" % file_path)
	
	if not FileAccess.file_exists(file_path):
		push_error("[MapConverter] 檔案不存在: %s" % file_path)
		return {}
	
	var file = FileAccess.open(file_path, FileAccess.READ)
	if not file:
		push_error("[MapConverter] 無法開啟檔案: %s" % file_path)
		return {}
	
	var collision_data = {}
	var tile_id = 0
	var line_number = 0
	
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		line_number += 1
		
		# 偶數行是檔名，奇數行是碰撞資料
		if line_number % 2 == 0:
			# 碰撞資料行
			var has_collision = (line.to_lower() == "true")
			collision_data[tile_id] = has_collision
			tile_id += 1
	
	file.close()
	
	print("[MapConverter] 載入 %d 個瓦片的碰撞資料" % collision_data.size())
	return collision_data

## 輔助函數：取得地圖尺寸
func get_map_size(map_data: Array) -> Vector2i:
	if map_data.is_empty():
		return Vector2i.ZERO
	
	var height = map_data.size()
	var width = map_data[0].size()
	return Vector2i(width, height)

## 輔助函數：檢查座標是否在地圖範圍內
func is_valid_position(map_data: Array, x: int, y: int) -> bool:
	if map_data.is_empty():
		return false
	
	var height = map_data.size()
	var width = map_data[0].size()
	
	return x >= 0 and x < width and y >= 0 and y < height

## 輔助函數：取得指定位置的瓦片 ID
func get_tile_at(map_data: Array, x: int, y: int) -> int:
	if not is_valid_position(map_data, x, y):
		return -1
	
	return map_data[y][x]
