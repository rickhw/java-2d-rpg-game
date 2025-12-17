extends Node2D
## DungeonMap - 地下城地圖場景腳本

const MapLoader = preload("res://scripts/map_loader.gd")

@export var map_file: String = "res://resources/maps/dungeon01.txt"
@export var map_type: int = 2  # 2 = DUNGEON01, 3 = DUNGEON02

@onready var tilemap: TileMapLayer = $TileMapLayer

var map_data: Array = []
var tileset: TileSet


func _ready() -> void:
	print("[DungeonMap] Initializing: %s" % map_file)
	
	# 載入地圖資料
	map_data = MapLoader.load_map_data(map_file)
	
	# 建立 TileSet
	tileset = MapLoader.create_tileset_from_tiles("res://resources/tiles")
	tilemap.tile_set = tileset
	
	# 套用地圖資料到 TileMapLayer
	MapLoader.apply_map_to_tilemap(tilemap, map_data)
	
	print("[DungeonMap] Map loaded successfully")
	
	# 通知 GameManager 地圖已載入
	var godot_map_type = GameManager.MapType.DUNGEON01 if map_type == 2 else GameManager.MapType.DUNGEON02
	GameManager.change_map(godot_map_type)
