extends CharacterBody2D
class_name Entity
# Base class for all game entities (Player, NPCs, Monsters, Objects)
# Corresponds to Java Entity.java

const Constants = preload("res://scripts/data/constants.gd")

# Entity properties
@export var entity_name: String = "Entity"
@export var entity_type: Constants.EntityType = Constants.EntityType.PLAYER

# World position and movement
var speed: int = 4  # Tiles per second (will be multiplied by TILE_SIZE)
var direction: Constants.Direction = Constants.Direction.DOWN

# Collision area (relative to entity position)
var collision_area: Rect2 = Rect2(8, 16, 48, 48)  # Standard: 48x48 collision box
var collision_area_default_x: int = 8
var collision_area_default_y: int = 16
var collision_on: bool = false

# Animation
var sprite_counter: int = 0
var sprite_num: int = 1
var sprite_images: Dictionary = {}  # Direction -> Array[Texture2D]

# Combat stats
var life: int = 0
var max_life: int = 0
var mana: int = 0
var max_mana: int = 0
var ammo: int = 0
var level: int = 1
var strength: int = 0
var dexterity: int = 0
var attack: int = 0
var defense: int = 0
var exp: int = 0
var next_level_exp: int = 0
var coin: int = 0

# State
var invincible: bool = false
var invincible_counter: int = 0
var alive: bool = true
var dying: bool = false
var hp_bar_on: bool = false
var hp_bar_counter: int = 0
var action_lock_counter: int = 0

# Attack
var attacking: bool = false
var attack_area: Rect2 = Rect2()

# Dialogue
var dialogues: Array[String] = []
var dialogue_index: int = 0

func _ready():
	# Set up collision shape
	reset_collision_area()

func _physics_process(delta):
	# Called every frame for physics
	update_entity(delta)
	
	# Handle invincibility
	if invincible:
		invincible_counter += 1
		if invincible_counter > 60:  # 1 second at 60 FPS
			invincible = false
			invincible_counter = 0

func update_entity(_delta):
	# Override in child classes
	pass

func reset_collision_area():
	"""Reset collision area to default position"""
	collision_area.position.x = collision_area_default_x
	collision_area.position.y = collision_area_default_y

func set_action(action_index: int):
	"""Set entity behavior - override in child classes"""
	pass

func damage_reaction():
	"""React to taking damage - override in child classes"""
	pass

func speak():
	"""Handle dialogue - override in NPC classes"""
	if dialogues.size() > dialogue_index:
		var current_dialogue = dialogues[dialogue_index]
		dialogue_index += 1
		if dialogue_index >= dialogues.size():
			dialogue_index = 0
		return current_dialogue
	return ""

func use_item(_item):
	"""Use an item - override in child classes"""
	pass

func check_collision():
	"""Check collision with tiles and entities"""
	# Will be implemented when collision checker is created
	pass

func get_detected_item() -> Array:
	"""Detect items in interaction range"""
	return []

func interact_npc():
	"""Interact with NPCs"""
	pass

func get_screen_x() -> int:
	"""Get screen X position (for drawing)"""
	return int(position.x)

func get_screen_y() -> int:
	"""Get screen Y position (for drawing)"""
	return int(position.y)

func get_left_x() -> int:
	return int(position.x + collision_area.position.x)

func get_right_x() -> int:
	return int(position.x + collision_area.position.x + collision_area.size.x)

func get_top_y() -> int:
	return int(position.y + collision_area.position.y)

func get_bottom_y() -> int:
	return int(position.y + collision_area.position.y + collision_area.size.y)

func get_col() -> int:
	"""Get current tile column"""
	return int(position.x / Constants.TILE_SIZE)

func get_row() -> int:
	"""Get current tile row"""
	return int(position.y / Constants.TILE_SIZE)
