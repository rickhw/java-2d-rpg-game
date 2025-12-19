extends Node
# SaveManager.gd - Autoload
# Manages game save/load functionality (corresponds to Java SaveLoad.java)

const Constants = preload("res://scripts/data/constants.gd")

const SAVE_FILE_PATH = "user://save.data"
const CONFIG_FILE_PATH = "user://config.cfg"

func _ready():
	print("[SaveManager] Initialized")

func save_game():
	"""Save the current game state"""
	var save_data = {}
	
	# Player stats (from GameManager.player)
	if GameManager.player:
		save_data["player"] = {
			"level": GameManager.player.level,
			"max_life": GameManager.player.max_life,
			"life": GameManager.player.life,
			"max_mana": GameManager.player.max_mana,
			"mana": GameManager.player.mana,
			"strength": GameManager.player.strength,
			"dexterity": GameManager.player.dexterity,
			"exp": GameManager.player.exp,
			"next_level_exp": GameManager.player.next_level_exp,
			"coin": GameManager.player.coin,
			"world_x": GameManager.player.position.x,
			"world_y": GameManager.player.position.y,
		}
		
		# Player inventory
		save_data["inventory"] = []
		for item in GameManager.player.inventory:
			if item != null:
				save_data["inventory"].append({
					"name": item.name,
					"amount": item.amount if "amount" in item else 1
				})
		
		# Current weapon and shield slots
		save_data["current_weapon_slot"] = GameManager.player.current_weapon_slot
		save_data["current_shield_slot"] = GameManager.player.current_shield_slot
	
	# Map objects
	save_data["map_objects"] = []
	for map_idx in range(Constants.MAX_MAP):
		var map_objs = []
		for obj in GameManager.objects[map_idx]:
			if obj != null:
				var obj_data = {
					"name": obj.name,
					"world_x": obj.position.x,
					"world_y": obj.position.y,
					"opened": obj.opened if "opened" in obj else false,
				}
				if "loot" in obj and obj.loot != null:
					obj_data["loot_name"] = obj.loot.name
				map_objs.append(obj_data)
		save_data["map_objects"].append(map_objs)
	
	# Save to file
	var file = FileAccess.open(SAVE_FILE_PATH, FileAccess.WRITE)
	if file:
		file.store_var(save_data)
		file.close()
		print("[SaveManager] Game saved successfully")
		return true
	else:
		print("[SaveManager] Failed to save game")
		return false

func load_game():
	"""Load saved game state"""
	if not FileAccess.file_exists(SAVE_FILE_PATH):
		print("[SaveManager] No save file found")
		return false
	
	var file = FileAccess.open(SAVE_FILE_PATH, FileAccess.READ)
	if not file:
		print("[SaveManager] Failed to open save file")
		return false
	
	var save_data = file.get_var()
	file.close()
	
	# Restore player stats
	if "player" in save_data and GameManager.player:
		var player_data = save_data["player"]
		GameManager.player.level = player_data["level"]
		GameManager.player.max_life = player_data["max_life"]
		GameManager.player.life = player_data["life"]
		GameManager.player.max_mana = player_data["max_mana"]
		GameManager.player.mana = player_data["mana"]
		GameManager.player.strength = player_data["strength"]
		GameManager.player.dexterity = player_data["dexterity"]
		GameManager.player.exp = player_data["exp"]
		GameManager.player.next_level_exp = player_data["next_level_exp"]
		GameManager.player.coin = player_data["coin"]
		GameManager.player.position = Vector2(player_data["world_x"], player_data["world_y"])
	
	# TODO: Restore inventory
	# TODO: Restore map objects
	
	print("[SaveManager] Game loaded successfully")
	return true

func save_config():
	"""Save configuration (volume, fullscreen, etc.)"""
	var config = ConfigFile.new()
	
	config.set_value("display", "fullscreen", DisplayServer.window_get_mode() == DisplayServer.WINDOW_MODE_FULLSCREEN)
	config.set_value("audio", "music_volume", AudioManager.music_volume_scale)
	config.set_value("audio", "sfx_volume", AudioManager.sfx_volume_scale)
	
	var err = config.save(CONFIG_FILE_PATH)
	if err == OK:
		print("[SaveManager] Config saved successfully")
	else:
		print("[SaveManager] Failed to save config")

func load_config():
	"""Load configuration"""
	var config = ConfigFile.new()
	var err = config.load(CONFIG_FILE_PATH)
	
	if err != OK:
		print("[SaveManager] No config file found, using defaults")
		return
	
	# Load fullscreen setting
	var fullscreen = config.get_value("display", "fullscreen", false)
	if fullscreen:
		DisplayServer.window_set_mode(DisplayServer.WINDOW_MODE_FULLSCREEN)
	
	# Load audio settings
	AudioManager.set_music_volume(config.get_value("audio", "music_volume", 3))
	AudioManager.set_sfx_volume(config.get_value("audio", "sfx_volume", 3))
	
	print("[SaveManager] Config loaded successfully")
