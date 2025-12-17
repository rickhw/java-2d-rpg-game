extends Node
class_name MapLoader
## MapLoader - 從文字檔案載入地圖資料到 TileMapLayer

const TILE_SIZE = 64
const TILE_TEXTURE_SIZE = 16  # 原始圖片大小

# 碰撞資訊 (從 tiledata.txt 解析)
# true = 有碰撞, false = 可穿越
const TILE_COLLISION = {
	0: false, 1: false, 2: false, 3: false, 4: false,
	5: false, 6: false, 7: false, 8: false, 9: false,
	10: false, 11: false, 12: false, 13: false, 14: false,
	15: false, 16: true, 17: false, 18: true, 19: true,
	20: true, 21: true, 22: true, 23: true, 24: true,
	25: true, 26: true, 27: true, 28: true, 29: true,
	30: true, 31: true, 32: true, 33: false, 34: false,
	35: true, 36: false, 37: false
}


static func load_map_data(map_path: String) -> Array:
	"""載入地圖文字檔案並返回 2D 陣列"""
	var map_data: Array = []
	
	var file = FileAccess.open(map_path, FileAccess.READ)
	if not file:
		push_error("[MapLoader] Failed to open map file: %s" % map_path)
		return map_data
	
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		if line.is_empty():
			continue
		
		var row: Array = []
		var tiles = line.split(" ")
		for tile_str in tiles:
			if tile_str.is_valid_int():
				row.append(int(tile_str))
		
		if row.size() > 0:
			map_data.append(row)
	
	file.close()
	print("[MapLoader] Loaded map: %s (%d x %d)" % [map_path, map_data[0].size() if map_data.size() > 0 else 0, map_data.size()])
	return map_data


static func create_tileset_from_tiles(tile_dir: String) -> TileSet:
	"""從 tiles 目錄建立 TileSet"""
	var tileset = TileSet.new()
	tileset.tile_size = Vector2i(TILE_SIZE, TILE_SIZE)
	
	# 建立物理層 (用於碰撞)
	tileset.add_physics_layer()
	tileset.set_physics_layer_collision_layer(0, 1 << 4)  # tiles layer (layer 5)
	tileset.set_physics_layer_collision_mask(0, 0)
	
	# 建立 Atlas Source
	var atlas_source = TileSetAtlasSource.new()
	
	# 找到所有 tile 圖片並建立 atlas
	var tile_images: Array[Image] = []
	for i in range(38):
		var path = "%s/%03d.png" % [tile_dir, i]
		var texture = load(path) as Texture2D
		if texture:
			var img = texture.get_image()
			# 縮放圖片到 TILE_SIZE
			img.resize(TILE_SIZE, TILE_SIZE, Image.INTERPOLATE_NEAREST)
			tile_images.append(img)
		else:
			push_warning("[MapLoader] Tile not found: %s" % path)
			# 建立空白圖片作為佔位
			var empty_img = Image.create(TILE_SIZE, TILE_SIZE, false, Image.FORMAT_RGBA8)
			empty_img.fill(Color.MAGENTA)
			tile_images.append(empty_img)
	
	# 建立合併的 atlas 圖片 (橫向排列，已縮放)
	var atlas_width = TILE_SIZE * tile_images.size()
	var atlas_height = TILE_SIZE
	var atlas_image = Image.create(atlas_width, atlas_height, false, Image.FORMAT_RGBA8)
	
	for i in tile_images.size():
		var src_rect = Rect2i(0, 0, TILE_SIZE, TILE_SIZE)
		var dst_pos = Vector2i(i * TILE_SIZE, 0)
		atlas_image.blit_rect(tile_images[i], src_rect, dst_pos)
	
	var atlas_texture = ImageTexture.create_from_image(atlas_image)
	atlas_source.texture = atlas_texture
	atlas_source.texture_region_size = Vector2i(TILE_SIZE, TILE_SIZE)
	
	# 為每個 tile 建立 atlas 座標
	for i in range(38):
		var atlas_coords = Vector2i(i, 0)
		atlas_source.create_tile(atlas_coords)
		
		# 設定碰撞
		if TILE_COLLISION.get(i, false):
			var tile_data = atlas_source.get_tile_data(atlas_coords, 0)
			if tile_data:
				# 建立碰撞多邊形 (整個 tile，大小為 TILE_SIZE)
				var polygon = PackedVector2Array([
					Vector2(0, 0),
					Vector2(TILE_SIZE, 0),
					Vector2(TILE_SIZE, TILE_SIZE),
					Vector2(0, TILE_SIZE)
				])
				tile_data.add_collision_polygon(0)
				tile_data.set_collision_polygon_points(0, 0, polygon)
	
	tileset.add_source(atlas_source)
	print("[MapLoader] Created TileSet with %d tiles (size: %dx%d)" % [tile_images.size(), TILE_SIZE, TILE_SIZE])
	return tileset


static func apply_map_to_tilemap(tilemap: TileMapLayer, map_data: Array, source_id: int = 0) -> void:
	"""將地圖資料套用到 TileMapLayer"""
	tilemap.clear()
	
	for row in range(map_data.size()):
		for col in range(map_data[row].size()):
			var tile_id = map_data[row][col]
			var atlas_coords = Vector2i(tile_id, 0)
			tilemap.set_cell(Vector2i(col, row), source_id, atlas_coords)
	
	print("[MapLoader] Applied map data to TileMapLayer (%d x %d)" % [map_data[0].size() if map_data.size() > 0 else 0, map_data.size()])
