extends Node
# TilemapLoader.gd
# Loads tile data and generates TileSet resources for Godot
# Corresponds to Java TileManager.java

const Constants = preload("res://scripts/data/constants.gd")

# Tile data storage
var tile_collision: Array = []  # Array of bool for collision
var tile_images: Array = []     # Array of String for image paths

# TileSet resource
var tileset: TileSet = null

func _ready():
	print("[TilemapLoader] Initializing")
	load_tile_data()
	generate_tileset()

func load_tile_data():
	"""Load tiledata.txt which contains tile images and collision info"""
	var file_path = "res://assets/maps_v2/tiledata.txt"
	var file = FileAccess.open(file_path, FileAccess.READ)
	
	if not file:
		print("[TilemapLoader] ERROR: Cannot open tiledata.txt")
		return
	
	var line_num = 0
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		
		if line.is_empty():
			continue
		
		# Every pair of lines: first is image name, second is collision
		if line_num % 2 == 0:
			# Image filename
			tile_images.append(line)
		else:
			# Collision status
			var collision = line.to_lower() == "true"
			tile_collision.append(collision)
		
		line_num += 1
	
	file.close()
	print("[TilemapLoader] Loaded %d tiles" % tile_images.size())

func generate_tileset() -> TileSet:
	"""Generate a TileSet resource from tile data"""
	if tile_images.is_empty():
		print("[TilemapLoader] ERROR: No tile data loaded")
		return null
	
	tileset = TileSet.new()
	
	# Set tile size to 64x64 (display size)
	tileset.tile_size = Vector2i(Constants.TILE_SIZE, Constants.TILE_SIZE)
	
	# Add physics layer for collision
	tileset.add_physics_layer()
	
	print("[TilemapLoader] Loading %d tiles..." % tile_images.size())
	
	# Create source for each tile
	for i in range(tile_images.size()):
		var image_name = tile_images[i]
		var image_path = "res://assets/tilesV3/" + image_name
		
		# Load image manually
		var image = Image.new()
		var err = image.load(image_path)
		
		if err != OK:
			print("[TilemapLoader] ERROR: Failed to load image: %s (error: %d)" % [image_path, err])
			continue
		
		# Scale image from 16x16 to 64x64 using nearest neighbor (pixel perfect)
		image.resize(Constants.TILE_SIZE, Constants.TILE_SIZE, Image.INTERPOLATE_NEAREST)
		
		# Create texture from scaled image
		var texture = ImageTexture.create_from_image(image)
		
		if not texture:
			print("[TilemapLoader] ERROR: Failed to create texture from image: %s" % image_name)
			continue
		
		# Create a new TileSetAtlasSource for this tile
		var tile_source = TileSetAtlasSource.new()
		tile_source.texture = texture
		
		# Now the texture is 64x64, so texture_region_size should be 64x64
		tile_source.texture_region_size = Vector2i(Constants.TILE_SIZE, Constants.TILE_SIZE)
		
		# Set margins and separation to 0
		tile_source.margins = Vector2i(0, 0)
		tile_source.separation = Vector2i(0, 0)
		
		# Create atlas coordinates (0,0) since each texture is a single tile
		var atlas_coords = Vector2i(0, 0)
		tile_source.create_tile(atlas_coords)
		
		# Add the source to tileset FIRST
		tileset.add_source(tile_source, i)
		
		# NOW set collision if needed
		if i < tile_collision.size() and tile_collision[i]:
			var tile_data = tile_source.get_tile_data(atlas_coords, 0)
			
			if tile_data:
				# Add collision polygon (full tile rectangle)
				var polygon = PackedVector2Array([
					Vector2(0, 0),
					Vector2(64, 0),
					Vector2(64, 64),
					Vector2(0, 64)
				])
				tile_data.add_collision_polygon(0)
				tile_data.set_collision_polygon_points(0, 0, polygon)
	
	print("[TilemapLoader] TileSet generated with %d tile sources" % tileset.get_source_count())
	return tileset

func load_map(map_file: String) -> Array:
	"""
	Load a map file and return 2D array of tile IDs
	Returns: Array[Array[int]] - 2D array of tile IDs
	"""
	var file_path = "res://assets/maps_v2/" + map_file
	var file = FileAccess.open(file_path, FileAccess.READ)
	
	if not file:
		print("[TilemapLoader] ERROR: Cannot open map file: %s" % map_file)
		return []
	
	var map_data: Array = []
	
	while not file.eof_reached():
		var line = file.get_line().strip_edges()
		
		if line.is_empty():
			continue
		
		# Split by space and convert to integers
		var tile_ids_str = line.split(" ", false)
		var tile_ids: Array = []
		
		for tile_str in tile_ids_str:
			tile_ids.append(int(tile_str))
		
		map_data.append(tile_ids)
	
	file.close()
	print("[TilemapLoader] Loaded map: %s (%d rows)" % [map_file, map_data.size()])
	return map_data

func apply_map_to_tilemap(tilemap: TileMapLayer, map_data: Array):
	"""
	Apply loaded map data to a TileMapLayer
	map_data: Array[Array[int]] - 2D array of tile IDs
	"""
	if map_data.is_empty():
		print("[TilemapLoader] ERROR: Map data is empty")
		return
	
	tilemap.clear()
	
	for row in range(map_data.size()):
		var row_data = map_data[row]
		for col in range(row_data.size()):
			var tile_id = row_data[col]
			
			# Set the tile
			# source_id = tile_id, atlas_coords = (0,0) since each source has one tile
			tilemap.set_cell(Vector2i(col, row), tile_id, Vector2i(0, 0))
	
	print("[TilemapLoader] Applied map data to TileMapLayer: %dx%d" % [map_data[0].size(), map_data.size()])

func get_tileset() -> TileSet:
	"""Get the generated TileSet"""
	return tileset
