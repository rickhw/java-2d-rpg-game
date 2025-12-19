extends Node
# GameManager.gd - Autoload
# Main game state manager (corresponds to Java GamePanel.java)

# Preload constants
const Constants = preload("res://scripts/data/constants.gd")

# Game State
var game_state: Constants.GameState = Constants.GameState.TITLE
var current_map: Constants.MapID = Constants.MapID.WORLD_MAP
var current_area: Constants.Area = Constants.Area.OUTSIDE
var next_area: Constants.Area = Constants.Area.OUTSIDE

# Boss Battle
var boss_battle_on: bool = false

# Player reference (set by player when ready)
var player = null

# Entity Arrays (similar to Java's 2D arrays)
var objects: Array = []  # Will be Array[Array[Entity]]
var npcs: Array = []
var monsters: Array = []
var interactive_tiles: Array = []
var particles: Array = []

# Debugging
var debug_mode: bool = false
var god_mode: bool = false

func _ready():
	# Initialize entity arrays for each map
	for i in range(Constants.MAX_MAP):
		objects.append([])
		npcs.append([])
		monsters.append([])
		interactive_tiles.append([])
	
	print("[GameManager] Initialized")

func change_game_state(new_state: Constants.GameState):
	"""Change the current game state"""
	game_state = new_state
	print("[GameManager] Game state changed to: ", Constants.GameState.keys()[new_state])

func change_area(new_area: Constants.Area):
	"""Change the current area (outside, indoor, dungeon)"""
	if new_area != current_area:
		next_area = new_area
		# Music change will be handled by AudioManager
		AudioManager.change_area_music(new_area)
		current_area = new_area
		print("[GameManager] Area changed to: ", Constants.Area.keys()[new_area])

func change_map(new_map: Constants.MapID):
	"""Change the current map"""
	current_map = new_map
	print("[GameManager] Map changed to: ", Constants.MapID.keys()[new_map])

func remove_temp_entities():
	"""Remove temporary entities (used in boss battles, etc.)"""
	for i in range(objects[current_map].size()):
		if objects[current_map][i] != null and objects[current_map][i].has("temp_obj"):
			if objects[current_map][i].temp_obj:
				objects[current_map][i].queue_free()
				objects[current_map][i] = null

func toggle_debug_mode():
	"""Toggle debug mode"""
	debug_mode = !debug_mode
	print("[GameManager] Debug mode: ", debug_mode)

func toggle_god_mode():
	"""Toggle god mode (invincible + always day)"""
	god_mode = !god_mode
	print("[GameManager] God mode: ", god_mode)
