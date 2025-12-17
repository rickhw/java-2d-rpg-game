@tool
extends EditorScript
## 世界地圖場景自動生成器
## 在 Godot 編輯器中執行：檔案 → 執行腳本

const TILESET_PATH = "res://tilesets/world_tileset.tres"
const PLAYER_SCENE_PATH = "res://scenes/entities/player.tscn"
const DEBUG_GRID_SCRIPT = "res://scripts/systems/debug_grid.gd"
const MAP_DATA_PATH = "res://resources/maps_v2/worldmap.txt"
const OUTPUT_SCENE = "res://scenes/world/world_map.tscn"

func _run():
	print("========================================")
	print("開始自動生成世界地圖場景...")
	print("========================================")
	
	# 檢查必要檔案是否存在
	if not ResourceLoader.exists(TILESET_PATH):
		print("❌ 錯誤：找不到 TileSet: %s" % TILESET_PATH)
		print("請先執行 generate_tileset.gd 生成 TileSet")
		return
	
	# 建立根節點
	var root = Node2D.new()
	root.name = "WorldMap"
	
	# 添加 MapLoader 腳本
	var script = GDScript.new()
	script.source_code = get_world_map_script()
	var err = script.reload()
	if err != OK:
		print("❌ 錯誤：無法編譯腳本 (錯誤碼: %d)" % err)
		return
	root.set_script(script)
	
	# 建立 TileMap 節點
	var tilemap = TileMap.new()
	tilemap.name = "TileMap"
	
	# 載入並設定 TileSet
	var tileset = load(TILESET_PATH)
	if tileset:
		tilemap.tile_set = tileset
		print("✅ TileSet 已設定")
	else:
		print("❌ 錯誤：無法載入 TileSet")
		return
	
	# 設定 TileMap 屬性
	tilemap.rendering_quadrant_size = 16
	tilemap.collision_animatable = false
	# 不使用 scale，TileSet 會自動處理 16x16 到 64x64 的縮放
	
	# Y-Sort 設定
	root.y_sort_enabled = true
	
	root.add_child(tilemap)
	tilemap.owner = root
	print("✅ TileMap 節點已建立")
	
	# 添加玩家（如果玩家場景存在）
	if ResourceLoader.exists(PLAYER_SCENE_PATH):
		var player_scene = load(PLAYER_SCENE_PATH)
		if player_scene:
			var player = player_scene.instantiate()
			player.name = "Player"
			player.position = Vector2(1472, 1344)  # Tile (23, 21)
			root.add_child(player)
			player.owner = root
			print("✅ 玩家已添加於位置 (1472, 1344)")
	else:
		print("⚠️  警告：找不到玩家場景，跳過添加玩家")
	
	# 添加除錯格線（如果腳本存在）
	if ResourceLoader.exists(DEBUG_GRID_SCRIPT):
		var debug_grid = Node2D.new()
		debug_grid.name = "DebugGrid"
		var grid_script = load(DEBUG_GRID_SCRIPT)
		if grid_script:
			debug_grid.set_script(grid_script)
			debug_grid.z_index = 1000
			root.add_child(debug_grid)
			debug_grid.owner = root
			print("✅ DebugGrid 已添加")
	else:
		print("⚠️  警告：找不到 DebugGrid 腳本，跳過添加")
	
	# 儲存場景
	var packed_scene = PackedScene.new()
	err = packed_scene.pack(root)
	if err != OK:
		print("❌ 錯誤：無法打包場景 (錯誤碼: %d)" % err)
		return
	
	# 確保輸出目錄存在
	var dir = DirAccess.open("res://")
	if not dir.dir_exists("scenes/world"):
		dir.make_dir_recursive("scenes/world")
	
	err = ResourceSaver.save(packed_scene, OUTPUT_SCENE)
	if err == OK:
		print("========================================")
		print("✅ 成功！")
		print("世界地圖場景已儲存到: %s" % OUTPUT_SCENE)
		print("========================================")
		print("")
		print("📝 下一步：")
		print("1. 開啟場景: %s" % OUTPUT_SCENE)
		print("2. 執行遊戲 (F5) 測試")
		print("3. 按 T 鍵顯示格線")
		print("========================================")
	else:
		print("❌ 錯誤：無法儲存場景 (錯誤碼: %d)" % err)

func get_world_map_script() -> String:
	return """extends Node2D

@onready var tilemap = $TileMap
var map_converter = preload(\"res://scripts/systems/map_converter.gd\").new()

func _ready():
	add_child(map_converter)
	load_world_map()

func load_world_map():
	print(\"[WorldMap] 開始載入世界地圖...\")
	
	# 載入地圖資料
	var map_data = map_converter.load_map_from_txt(\"res://resources/maps_v2/worldmap.txt\")
	
	if map_data.is_empty():
		push_error(\"[WorldMap] 無法載入地圖資料\")
		return
	
	# 應用到 TileMap
	map_converter.apply_map_to_tilemap(tilemap, map_data, 0)
	
	print(\"[WorldMap] 世界地圖載入完成！\")
"""
