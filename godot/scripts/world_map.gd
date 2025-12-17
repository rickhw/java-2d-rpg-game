extends Node2D
## WorldMap - 世界地圖場景腳本

const MapLoader = preload("res://scripts/map_loader.gd")

@onready var tilemap: TileMapLayer = $TileMapLayer

var map_data: Array = []
var tileset: TileSet


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
	
	# 通知 GameManager 地圖已載入
	GameManager.change_map(GameManager.MapType.WORLD_MAP)
