extends Node2D

@onready var map_manager = MapManager.new()
@onready var tile_map_layer = $TileMapLayer

func _ready():
	# 初始化 MapManager
	map_manager._ready()
	
	# 生成主世界地圖
	# 對應 Java: /gtcafe/rpg/assets/maps_v2/worldmap.txt
	map_manager.generate_map_on_layer(tile_map_layer, "res://assets/maps_v2/worldmap.txt")
	
	# 設定 Scale (原本 Java 是 *4，也就是 16 -> 64，但我們這裡設 48x48? 
	# Java: originalTileSize = 16, scale = 4 => tileSize = 64??
	# 等等，Java 程式碼裡 GamePanel.java: 
	# final int originalTileSize = 16; 
	# final int scale = 4;
	# public final int tileSize = originalTileSize * scale; // 應該是 64px
	# 但是註解寫 48x48 pixel，這可能有誤。16*3=48, 16*4=64。
	# 讓我們檢查 Godot 專案設定，視窗是 1280x768。
	# 1280 / 20 cols = 64.  768 / 12 rows = 64.
	# 所以 TileSize 應該是 64px。
	# 而原始圖片是 16px。所以 Scale 應該是 4。
	
	tile_map_layer.scale = Vector2(4, 4)
