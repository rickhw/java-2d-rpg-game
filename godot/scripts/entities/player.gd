extends Entity
class_name Player
# Player character controller
# Corresponds to Java Player.java

# Node references
@onready var sprite: Sprite2D = $Sprite2D
@onready var player_camera: Camera2D = $Camera2D

# Player-specific properties
var screen_x: int = 640  # Fixed screen position (center of viewport width)
var screen_y: int = 384  # Fixed screen position (center of viewport height)

# Inventory
var inventory: Array = []
var max_inventory_size: int = 20
var current_weapon = null
var current_shield = null

# Actions (attacking is inherited from Entity)
var guarding: bool = false
var attack_canceled: bool = false

# Movement
var key_handler_enabled: bool = true

func _ready():
	super._ready()
	set_default_values()
	load_player_image()
	update_sprite()

func set_default_values():
	"""Initialize player stats - matches Java Player.setDefaultValues()"""
	entity_name = "Player"
	entity_type = Constants.EntityType.PLAYER
	
	# Starting position (world coordinates)
	position = Vector2(23 * Constants.TILE_SIZE, 21 * Constants.TILE_SIZE)
	
	# Movement speed: 4 pixels per frame at 60 FPS = 240 pixels/sec
	speed = 4
	direction = Constants.Direction.DOWN
	
	# Stats
	level = 1
	max_life = 6  # 3 hearts
	life = max_life
	max_mana = 4  # 2 mana crystals
	mana = max_mana
	ammo = 10
	strength = 1
	dexterity = 1
	exp = 0
	next_level_exp = 5
	coin = 0
	
	# Get attack and defense from equipment
	update_attack()
	update_defense()
	
	# Collision area (8, 16, 48, 48) - matching Java
	collision_area = Rect2(8, 16, 48, 48)
	collision_area_default_x = 8
	collision_area_default_y = 16

func load_player_image():
	"""Load player sprite images for all directions"""
	# Load walking animations (2 frames per direction)
	# Scale from 16x16 to 64x64 using nearest neighbor
	sprite_images[Constants.Direction.UP] = [
		scale_image("res://assets/player/walking/boy_up_1.png"),
		scale_image("res://assets/player/walking/boy_up_2.png")
	]
	sprite_images[Constants.Direction.DOWN] = [
		scale_image("res://assets/player/walking/boy_down_1.png"),
		scale_image("res://assets/player/walking/boy_down_2.png")
	]
	sprite_images[Constants.Direction.LEFT] = [
		scale_image("res://assets/player/walking/boy_left_1.png"),
		scale_image("res://assets/player/walking/boy_left_2.png")
	]
	sprite_images[Constants.Direction.RIGHT] = [
		scale_image("res://assets/player/walking/boy_right_1.png"),
		scale_image("res://assets/player/walking/boy_right_2.png")
	]
	
	# TODO: Load attack animations when implementing combat

func scale_image(image_path: String) -> ImageTexture:
	"""Load and scale an image from 16x16 to 64x64"""
	var image = Image.new()
	var err = image.load(image_path)
	
	if err != OK:
		print("[Player] ERROR: Failed to load image: %s" % image_path)
		return null
	
	# Scale image from 16x16 to 64x64 using nearest neighbor (pixel perfect)
	image.resize(Constants.TILE_SIZE, Constants.TILE_SIZE, Image.INTERPOLATE_NEAREST)
	
	# Create texture from scaled image
	var texture = ImageTexture.create_from_image(image)
	return texture

func _physics_process(delta):
	super._physics_process(delta)
	
	if key_handler_enabled:
		handle_input()
	
	# Move the character
	move_and_slide()
	
	# Update sprite
	update_sprite()

func handle_input():
	"""Handle player input"""
	if attacking:
		# Can't move while attacking
		return
	
	# Reset velocity
	velocity = Vector2.ZERO
	
	# Check movement input
	var moving = false
	
	if Input.is_action_pressed("ui_up"):
		direction = Constants.Direction.UP
		velocity.y = -speed * Constants.TILE_SIZE
		moving = true
	elif Input.is_action_pressed("ui_down"):
		direction = Constants.Direction.DOWN
		velocity.y = speed * Constants.TILE_SIZE
		moving = true
	elif Input.is_action_pressed("ui_left"):
		direction = Constants.Direction.LEFT
		velocity.x = -speed * Constants.TILE_SIZE
		moving = true
	elif Input.is_action_pressed("ui_right"):
		direction = Constants.Direction.RIGHT
		velocity.x = speed * Constants.TILE_SIZE
		moving = true
	
	# Update animation
	if moving:
		sprite_counter += 1
		if sprite_counter > 12:  # Change sprite every 12 frames
			sprite_num = 2 if sprite_num == 1 else 1
			sprite_counter = 0
	else:
		# Reset to idle pose
		sprite_num = 1

func update_sprite():
	"""Update the sprite texture based on direction and animation frame"""
	if sprite and direction in sprite_images:
		var frames = sprite_images[direction]
		if frames.size() > 0:
			var frame_index = sprite_num - 1  # sprite_num is 1 or 2
			if frame_index < frames.size():
				sprite.texture = frames[frame_index]

func update_attack():
	"""Calculate total attack power"""
	# Base attack from strength
	var weapon_attack = 0
	if current_weapon:
		weapon_attack = current_weapon.attack_value
	attack = strength + weapon_attack

func update_defense():
	"""Calculate total defense"""
	# Base defense from dexterity  
	var shield_defense = 0
	if current_shield:
		shield_defense = current_shield.defense_value
	defense = dexterity + shield_defense

func get_attack() -> int:
	return attack

func get_defense() -> int:
	return defense
