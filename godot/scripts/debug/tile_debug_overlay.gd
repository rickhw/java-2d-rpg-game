extends Control
# Debug overlay to show tile grid, borders, and coordinates
# Toggle with T key
# NOTE: Using Control with custom _draw instead of CanvasLayer for better compatibility

const Constants = preload("res://scripts/data/constants.gd")

var show_tile_grid: bool = false
var tile_size: int = Constants.TILE_SIZE
var map_cols: int = 50  # WorldMap is 50x50
var map_rows: int = 50

# Camera reference to get viewport
var camera: Camera2D = null

func _ready():
	# Make this control cover the entire viewport
	set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	mouse_filter = Control.MOUSE_FILTER_IGNORE  # Don't block mouse input
	print("[TileDebugOverlay] _ready() called")

func _process(_delta):
	# Toggle with T key
	if Input.is_action_just_pressed("debug_tile_grid"):
		show_tile_grid = !show_tile_grid
		print("[TileDebugOverlay] Tile grid: %s | Camera: %s" % ["ON" if show_tile_grid else "OFF", "null" if not camera else "OK"])
		queue_redraw()  # Request redraw

func _draw():
	if not show_tile_grid:
		return
	
	if not camera:
		print("[TileDebugOverlay] ERROR: Camera is null in _draw()!")
		return
	
	# Get camera position in world space
	var cam_pos = camera.global_position
	var viewport_size = get_viewport_rect().size
	
	# Calculate which tiles are visible in WORLD SPACE
	var world_left = cam_pos.x - viewport_size.x / 2
	var world_right = cam_pos.x + viewport_size.x / 2
	var world_top = cam_pos.y - viewport_size.y / 2
	var world_bottom = cam_pos.y + viewport_size.y / 2
	
	# Convert world coordinates to tile coordinates
	var start_col = max(0, int(world_left / tile_size))
	var end_col = min(map_cols - 1, int(world_right / tile_size))
	var start_row = max(0, int(world_top / tile_size))
	var end_row = min(map_rows - 1, int(world_bottom / tile_size))
	
	# Draw tile grid for all visible tiles
	for row in range(start_row, end_row + 1):
		for col in range(start_col, end_col + 1):
			draw_tile_debug(col, row, cam_pos, viewport_size)
	
	# Draw player current tile in top-left corner
	var player_tile_col = int(cam_pos.x / tile_size)
	var player_tile_row = int(cam_pos.y / tile_size)
	var info_text = "Player Tile: (%d, %d) | Cam: (%.0f, %.0f)" % [player_tile_col, player_tile_row, cam_pos.x, cam_pos.y]
	var font = ThemeDB.fallback_font
	draw_string(font, Vector2(10, 30), info_text, HORIZONTAL_ALIGNMENT_LEFT, -1, 20, Color.YELLOW)
	
	# Request next frame redraw if grid is on
	if show_tile_grid:
		queue_redraw()

func draw_tile_debug(col: int, row: int, cam_pos: Vector2, viewport_size: Vector2):
	"""Draw border and coordinates for a single tile"""
	# Calculate world position of this tile's top-left corner
	var world_x = col * tile_size
	var world_y = row * tile_size
	
	# Convert world position to screen position
	# Screen center is at viewport_size / 2
	# World position relative to camera, then offset to screen center
	var screen_x = world_x - cam_pos.x + viewport_size.x / 2.0
	var screen_y = world_y - cam_pos.y + viewport_size.y / 2.0
	
	# Draw red border (2 pixels)
	var rect = Rect2(screen_x, screen_y, tile_size, tile_size)
	draw_rect(rect, Color(1, 0, 0, 0.7), false, 2.0)
	
	# Draw coordinates text - showing WORLD tile coordinates (col, row)
	var coord_text = "%d,%d" % [col, row]
	var font = ThemeDB.fallback_font
	var font_size = 14
	
	# Center text in tile
	var text_size = font.get_string_size(coord_text, HORIZONTAL_ALIGNMENT_LEFT, -1, font_size)
	var text_x = screen_x + (tile_size - text_size.x) / 2.0
	var text_y = screen_y + (tile_size + text_size.y) / 2.0 - 2
	
	# Draw text with black outline for visibility
	for dx in [-1, 0, 1]:
		for dy in [-1, 0, 1]:
			if dx != 0 or dy != 0:
				draw_string(font, Vector2(text_x + dx, text_y + dy), coord_text, HORIZONTAL_ALIGNMENT_LEFT, -1, font_size, Color.BLACK)
	draw_string(font, Vector2(text_x, text_y), coord_text, HORIZONTAL_ALIGNMENT_LEFT, -1, font_size, Color.RED)

func set_camera(cam: Camera2D):
	"""Set the camera to track for viewport calculations"""
	camera = cam
	print("[TileDebugOverlay] Camera set: %s | Position: %v" % ["null" if not cam else "OK", cam.global_position if cam else Vector2.ZERO])
	
func set_map_size(cols: int, rows: int):
	"""Set the map dimensions"""
	map_cols = cols
	map_rows = rows
	print("[TileDebugOverlay] Map size set: %dx%d" % [cols, rows])
