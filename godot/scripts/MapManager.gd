extends Node
class_name MapManager

# 用於將 index (0, 1, 2...) 對應到 atlas 座標 (Vector2i)
# 假設我們的 TileSet 是一個水平排列的長條圖，或者我們會動態建立 TileSource
var tile_id_to_atlas_coords = {}

# TileSet 資源
var tile_set: TileSet

func _ready():
	# 讀取或建立 TileSet
	tile_set = load("res://assets/maps_v2/world_tileset.tres")
	if not tile_set:
		push_error("Failed to load TileSet!")
		return
	
	# 解析 tiledata.txt 來設定 TileSet 的 Source
	parse_tile_data()

func parse_tile_data():
	var file = FileAccess.open("res://assets/maps_v2/tiledata.txt", FileAccess.READ)
	if not file:
		push_error("Failed to open tiledata.txt")
		return

	var tile_index = 0
	
	# 我們需要一個 TileSetAtlasSource 來管理這些圖片
	# 在執行期間動態加入有點複雜，通常建議是在 Editor 裡做好 TileSet
	# 但為了復刻，我們嘗試用程式碼將個別圖片加進去
	
	# 為了簡單起見，我們將每個獨立圖片視為一個新的 Source (ID 從 0 開始遞增)
	# 雖然這不是最高效的方法，但對於 16x16 的小圖來說還行
	
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		if line == "":
			break
			
		var filename = line
		var collision_line = file.get_line().strip_edges()
		var has_collision = (collision_line == "true")
		
		var texture_path = "res://assets/tilesV3/" + filename
		var texture = load(texture_path)
		
		if texture:
			# 建立新的 AtlasSource
			var source = TileSetAtlasSource.new()
			source.texture = texture
			source.texture_region_size = Vector2i(16, 16)
			
			# 因為每個檔案只包含一個 tile，所以我們在 0,0 建立 tile
			source.create_tile(Vector2i(0, 0))
			
			if has_collision:
				# 設定碰撞
				# 建立一個與 tile 大小相同的矩形
				var polygon = [
					Vector2(-8, -8),
					Vector2(8, -8),
					Vector2(8, 8),
					Vector2(-8, 8)
				]
				# 物理層 0
				var tile_data = source.get_tile_data(Vector2i(0, 0), 0)
				tile_data.add_collision_polygon(0)
				tile_data.set_collision_polygon_points(0, 0, polygon)

			# 將 Source 加入 TileSet
			tile_set.add_source(source, tile_index)
			
			tile_index += 1
		else:
			push_warning("Could not load texture: " + texture_path)

func load_map_data(map_path: String) -> Array:
	var map_data = [] # 2D array [col][row] -> int (tile_index)
	var file = FileAccess.open(map_path, FileAccess.READ)
	
	if not file:
		push_error("Failed to open map file: " + map_path)
		return []

	# 原本 Java 是 [col][row]，我們讀入的時候先存為 [row][col] 比較符合直覺，之後再轉
	# Java 程式碼: mapTileNum[col][row] = num
	# Java 讀取邏輯: 
	# while (col < maxWorldCol && row < maxWorldRow) {
	#     line = br.readLine()
	#     numbers = line.split(" ")
	#     ...
	# }
	
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		if line == "":
			continue
			
		var split = line.split(" ", false)
		var row_data = []
		for s in split:
			row_data.append(s.to_int())
		
		if row_data.size() > 0:
			map_data.append(row_data)
			
	return map_data

func generate_map_on_layer(tile_map_layer: TileMapLayer, map_path: String):
	tile_map_layer.tile_set = tile_set
	tile_map_layer.clear()
	
	var map_data = load_map_data(map_path)
	
	# map_data 是 [row][col]
	for y in range(map_data.size()):
		for x in range(map_data[y].size()):
			var tile_id = map_data[y][x]
			
			# source_id = tile_id (因為我們每個圖是一個 source)
			# atlas_coords = (0,0) (因為每個 source 只有一張圖)
			tile_map_layer.set_cell(Vector2i(x, y), tile_id, Vector2i(0, 0))
