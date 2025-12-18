extends Node2D
## WorldMap - 世界地圖場景腳本

const MapLoader = preload("res://scripts/map_loader.gd")
const PlayerScene = preload("res://scenes/player/player.tscn")
const OldManScene = preload("res://scenes/entities/npcs/npc_oldman.tscn")
const GreenSlimeScene = preload("res://scenes/entities/monsters/monster_greenslime.tscn")

@onready var tilemap: TileMapLayer = $TileMapLayer
@onready var entities_node: Node2D = $Entities
@onready var player_container: Node2D = $Entities/Player
@onready var npcs_container: Node2D = $Entities/NPCs
@onready var monsters_container: Node2D = $Entities/Monsters

var map_data: Array = []
var tileset: TileSet
var player: CharacterBody2D


func _ready() -> void:
	print("[WorldMap] Initializing...")
	
	# 載入地圖資料
	map_data = MapLoader.load_map_data("res://resources/maps/worldmap.txt")
	
	# 建立 TileSet
	tileset = MapLoader.create_tileset_from_tiles("res://resources/tiles")
	tilemap.tile_set = tileset
	
	# 套用地圖資料到 TileMapLayer
	MapLoader.apply_map_to_tilemap(tilemap, map_data)
	
	print("[WorldMap] Map loaded successfully")
	
	# 生成玩家
	_spawn_player()
	
	# 生成 NPC
	_spawn_npcs()
	
	# 生成怪物
	_spawn_monsters()
	
	# 通知 GameManager 地圖已載入
	GameManager.change_map(GameManager.MapType.WORLD_MAP)


func _spawn_player() -> void:
	"""在起始位置生成玩家"""
	player = PlayerScene.instantiate()
	player.add_to_group("player")
	
	# 設定起始位置 (根據原始 Java 版本: 23*TILE_SIZE, 21*TILE_SIZE)
	var start_tile_x = 23
	var start_tile_y = 21
	player.position = Vector2(
		start_tile_x * GameManager.tile_size + GameManager.tile_size / 2,
		start_tile_y * GameManager.tile_size + GameManager.tile_size / 2
	)
	
	player_container.add_child(player)
	
	# 設定攝影機限制
	var camera = player.get_node("Camera2D") as Camera2D
	if camera:
		var map_width = map_data[0].size() if map_data.size() > 0 else 50
		var map_height = map_data.size()
		
		camera.limit_left = 0
		camera.limit_top = 0
		camera.limit_right = map_width * GameManager.tile_size
		camera.limit_bottom = map_height * GameManager.tile_size
		
		print("[WorldMap] Camera limits set: %d x %d" % [camera.limit_right, camera.limit_bottom])
	
	print("[WorldMap] Player spawned at tile (%d, %d)" % [start_tile_x, start_tile_y])


func _spawn_npcs() -> void:
	"""生成 NPC"""
	# Old Man - 在玩家起始位置附近
	var oldman = OldManScene.instantiate()
	oldman.position = _tile_to_pos(21, 21)
	npcs_container.add_child(oldman)
	print("[WorldMap] NPC Old Man spawned")


func _spawn_monsters() -> void:
	"""生成怪物"""
	# 在道路區域生成幾隻史萊姆
	var slime_positions = [
		Vector2i(26, 20),
		Vector2i(28, 22),
		Vector2i(30, 24),
		Vector2i(25, 26),
	]
	
	for pos in slime_positions:
		var slime = GreenSlimeScene.instantiate()
		slime.position = _tile_to_pos(pos.x, pos.y)
		monsters_container.add_child(slime)
	
	print("[WorldMap] %d Green Slimes spawned" % slime_positions.size())


func _tile_to_pos(tile_x: int, tile_y: int) -> Vector2:
	"""將 Tile 座標轉換為世界座標"""
	return Vector2(
		tile_x * GameManager.tile_size + GameManager.tile_size / 2,
		tile_y * GameManager.tile_size + GameManager.tile_size / 2
	)
