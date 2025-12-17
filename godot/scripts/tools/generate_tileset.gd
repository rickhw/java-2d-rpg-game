@tool
extends EditorScript
## TileSet 自動生成器 v2 - 正確的尺寸設定
## 在 Godot 編輯器中執行：檔案 → 執行腳本

const TILES_DIR = "res://resources/tilesV3/"
const TILEDATA_FILE = "res://resources/maps_v2/tiledata.txt"
const OUTPUT_FILE = "res://tilesets/world_tileset.tres"
const TILE_SIZE = 64  # 遊戲中的瓦片大小
const TEXTURE_SIZE = 16  # 原始圖片大小

func _run():
	print("========================================")
	print("TileSet 自動生成器 v2")
	print("========================================")
	
	# 建立 TileSet
	var tileset = TileSet.new()
	# 重要：tile_size 設為 64x64（遊戲世界的邏輯大小）
	tileset.tile_size = Vector2i(TILE_SIZE, TILE_SIZE)
	
	print("✅ TileSet tile_size: %dx%d" % [TILE_SIZE, TILE_SIZE])
	
	# 先建立物理層
	tileset.add_physics_layer()
	tileset.set_physics_layer_collision_layer(0, 16)  # Layer 4
	print("✅ 物理層已建立")
	
	# 讀取碰撞資料
	var collision_data = load_collision_data()
	
	# 第一階段：添加所有瓦片
	print("\n--- 添加瓦片 ---")
	var tile_count = 0
	
	for i in range(38):
		var tile_id = i
		var filename = "%03d.png" % i
		var filepath = TILES_DIR + filename
		
		if not ResourceLoader.exists(filepath):
			print("⚠️  跳過: %s (檔案不存在)" % filename)
			continue
		
		var texture = load(filepath)
		if not texture:
			print("❌ 無法載入: %s" % filename)
			continue
		
		# 建立 Atlas Source（每個瓦片一個）
		var atlas = TileSetAtlasSource.new()
		atlas.texture = texture
		
		# 關鍵設定：texture_region_size 設為實際紋理大小
		atlas.texture_region_size = Vector2i(TEXTURE_SIZE, TEXTURE_SIZE)
		
		# 建立瓦片 at (0, 0)
		atlas.create_tile(Vector2i(0, 0))
		
		# 設定瓦片的大小覆蓋（讓 16x16 填滿 64x64 的空間）
		var tile_data = atlas.get_tile_data(Vector2i(0, 0), 0)
		if tile_data:
			# 設定 texture 原點為中心，並縮放
			tile_data.texture_origin = Vector2i(-24, -24)  # 將 16x16 置中於 64x64 (-24 = (16-64)/2)
		
		# 添加到 TileSet
		tileset.add_source(atlas, tile_id)
		tile_count += 1
		
		if (tile_count % 10 == 0):
			print("  已添加 %d 個瓦片..." % tile_count)
	
	print("✅ 共添加 %d 個瓦片" % tile_count)
	
	# 第二階段：設定碰撞
	print("\n--- 設定碰撞 ---")
	var collision_count = 0
	
	for tile_id in collision_data:
		if collision_data[tile_id]:
			var source = tileset.get_source(tile_id)
			if source and source is TileSetAtlasSource:
				var tile_data = source.get_tile_data(Vector2i(0, 0), 0)
				if tile_data:
					# 碰撞多邊形應該是 64x64 的整個瓦片
					var polygon = PackedVector2Array([
						Vector2(0, 0),
						Vector2(64, 0),
						Vector2(64, 64),
						Vector2(0, 64)
					])
					tile_data.set_collision_polygons_count(0, 1)
					tile_data.set_collision_polygon_points(0, 0, polygon)
					collision_count += 1
	
	print("✅ 設定 %d 個瓦片的碰撞" % collision_count)
	
	# 儲存
	var err = ResourceSaver.save(tileset, OUTPUT_FILE)
	if err == OK:
		print("\n========================================")
		print("✅ 成功！")
		print("TileSet 已儲存: %s" % OUTPUT_FILE)
		print("瓦片數量: %d (碰撞: %d)" % [tile_count, collision_count])
		print("========================================")
	else:
		print("❌ 儲存失敗 (錯誤碼: %d)" % err)

func load_collision_data() -> Dictionary:
	var collision_data = {}
	
	if not FileAccess.file_exists(TILEDATA_FILE):
		print("⚠️  找不到碰撞資料: %s" % TILEDATA_FILE)
		return collision_data
	
	var file = FileAccess.open(TILEDATA_FILE, FileAccess.READ)
	if not file:
		return collision_data
	
	var tile_id = 0
	var line_number = 0
	
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		line_number += 1
		
		if line_number % 2 == 0:
			collision_data[tile_id] = (line.to_lower() == "true")
			tile_id += 1
	
	file.close()
	print("載入 %d 個瓦片的碰撞資料" % collision_data.size())
	return collision_data
