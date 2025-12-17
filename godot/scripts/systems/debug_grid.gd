extends Node2D
## DebugGrid - 顯示瓦片格線的除錯工具（世界空間版本）
## 按 T 鍵切換顯示/隱藏

var grid_visible: bool = false
var grid_size: int = 64  # 對應 GameManager.TILE_SIZE
var grid_color: Color = Color(1, 1, 0, 0.8)  # 黃色，較不透明
var grid_line_width: float = 2.0  # 線寬
var grid_range: int = 100  # 繪製範圍（格數）

func _ready() -> void:
	print("[DebugGrid] 格線系統初始化完成 (按 T 切換)")
	z_index = 1000  # 確保格線在最上層

func _input(event: InputEvent) -> void:
	# 檢測 T 鍵 (KeyCode 84)
	if event is InputEventKey:
		if event.pressed and event.keycode == KEY_T and not event.echo:
			toggle_grid()

func toggle_grid() -> void:
	grid_visible = !grid_visible
	queue_redraw()  # 觸發重繪
	
	if grid_visible:
		print("[DebugGrid] 格線顯示：ON")
	else:
		print("[DebugGrid] 格線顯示：OFF")

func _draw() -> void:
	if !grid_visible:
		return
	
	# 繪製大範圍格線（以世界座標繪製）
	var start = -grid_range / 2
	var end = grid_range / 2
	
	# 繪製垂直線
	for i in range(start, end + 1):
		var x = i * grid_size
		draw_line(
			Vector2(x, start * grid_size),
			Vector2(x, end * grid_size),
			grid_color,
			grid_line_width
		)
	
	# 繪製水平線
	for i in range(start, end + 1):
		var y = i * grid_size
		draw_line(
			Vector2(start * grid_size, y),
			Vector2(end * grid_size, y),
			grid_color,
			grid_line_width
		)
	
	# 繪製原點標記
	draw_circle(Vector2.ZERO, 8, Color.RED)
	draw_line(Vector2(-20, 0), Vector2(20, 0), Color.RED, 3)
	draw_line(Vector2(0, -20), Vector2(0, 20), Color.RED, 3)
	
	# 繪製座標文字（每 5 格）
	for i in range(start, end + 1, 5):
		for j in range(start, end + 1, 5):
			var pos = Vector2(i * grid_size + 4, j * grid_size + 16)
			var label_text = "(%d,%d)" % [i, j]
			draw_string(
				ThemeDB.fallback_font,
				pos,
				label_text,
				HORIZONTAL_ALIGNMENT_LEFT,
				-1,
				10,
				Color(1, 1, 0, 0.8)  # 黃色
			)
	
	# 繪製玩家位置標記（如果有玩家）
	if GameManager.player:
		var player_tile_x = int(GameManager.player.global_position.x / grid_size)
		var player_tile_y = int(GameManager.player.global_position.y / grid_size)
		var player_tile_pos = Vector2(player_tile_x * grid_size, player_tile_y * grid_size)
		
		# 高亮玩家所在的格子
		draw_rect(
			Rect2(player_tile_pos, Vector2(grid_size, grid_size)),
			Color(0, 1, 0, 0.3),  # 綠色半透明
			false,
			3.0
		)
		
		# 顯示玩家瓦片座標
		draw_string(
			ThemeDB.fallback_font,
			player_tile_pos + Vector2(4, -8),
			"玩家: (%d,%d)" % [player_tile_x, player_tile_y],
			HORIZONTAL_ALIGNMENT_LEFT,
			-1,
			14,
			Color(0, 1, 0, 1)  # 綠色
		)
