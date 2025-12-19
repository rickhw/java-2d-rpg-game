extends Node
# AudioManager.gd - Autoload
# Manages background music and sound effects (corresponds to Java Sound.java)

const Constants = preload("res://scripts/data/constants.gd")

# Audio players
var music_player: AudioStreamPlayer
var sfx_player: AudioStreamPlayer

# Volume settings (0-5 scale, same as Java)
var music_volume_scale: int = 3
var sfx_volume_scale: int = 3

# Volume conversion (-80 to 6 dB, same as Java)
const VOLUME_DB_VALUES = [-80.0, -20.0, -12.0, -5.0, 1.0, 6.0]

# Music and SFX paths
var music_paths = {
	Constants.MusicID.MAIN_THEME: "res://assets/bgm/Adventure.wav",
	Constants.MusicID.FANFARE: "res://assets/bgm/Fanfare.wav",
	Constants.MusicID.MERCHANT: "res://assets/bgm/MerchantTheme.wav",
	Constants.MusicID.DUNGEON: "res://assets/bgm/Dungeon.wav",
	Constants.MusicID.FINAL_BATTLE: "res://assets/bgm/FinalBattle.wav",
}

var sfx_paths = {
	Constants.SoundFX.COIN: "res://assets/sound/coin.wav",
	Constants.SoundFX.POWER_UP: "res://assets/sound/powerup.wav",
	Constants.SoundFX.UNLOCK: "res://assets/sound/unlock.wav",
	Constants.SoundFX.HIT_MONSTER: "res://assets/sound/hitmonster.wav",
	Constants.SoundFX.RECEIVE_DAMAGE: "res://assets/sound/receivedamage.wav",
	Constants.SoundFX.SWING_WEAPON: "res://assets/sound/swingweapon.wav",
	Constants.SoundFX.LEVEL_UP: "res://assets/sound/levelup.wav",
	Constants.SoundFX.CURSOR: "res://assets/sound/cursor.wav",
	Constants.SoundFX.BURNING: "res://assets/sound/burning.wav",
	Constants.SoundFX.CUT_TREE: "res://assets/sound/cuttree.wav",
	Constants.SoundFX.GAME_OVER: "res://assets/sound/gameover.wav",
	Constants.SoundFX.STAIRS: "res://assets/sound/stairs.wav",
	Constants.SoundFX.SLEEP: "res://assets/sound/sleep.wav",
	Constants.SoundFX.BLOCKED: "res://assets/sound/blocked.wav",
	Constants.SoundFX.PARRY: "res://assets/sound/parry.wav",
	Constants.SoundFX.SPEAK: "res://assets/sound/speak.wav",
	Constants.SoundFX.DOOR_OPEN: "res://assets/sound/dooropen.wav",
	Constants.SoundFX.CHIP_WALL: "res://assets/sound/chipwall.wav",
}

func _ready():
	# Create audio players
	music_player = AudioStreamPlayer.new()
	sfx_player = AudioStreamPlayer.new()
	add_child(music_player)
	add_child(sfx_player)
	
	# Set initial volumes
	update_music_volume()
	update_sfx_volume()
	
	print("[AudioManager] Initialized")

func play_music(music_id: Constants.MusicID):
	"""Play background music with looping"""
	if music_id in music_paths:
		var music_path = music_paths[music_id]
		if ResourceLoader.exists(music_path):
			var stream = load(music_path)
			music_player.stream = stream
			music_player.play()
			# Note: Will need to set loop property on the AudioStream resource
			print("[AudioManager] Playing music: ", music_id)
		else:
			print("[AudioManager] Music file not found: ", music_path)

func stop_music():
	"""Stop background music"""
	music_player.stop()

func play_sfx(sfx_id: Constants.SoundFX):
	"""Play sound effect"""
	if sfx_id in sfx_paths:
		var sfx_path = sfx_paths[sfx_id]
		if ResourceLoader.exists(sfx_path):
			var stream = load(sfx_path)
			sfx_player.stream = stream
			sfx_player.play()
		else:
			print("[AudioManager] SFX file not found: ", sfx_path)

func change_area_music(area: Constants.Area):
	"""Change music based on area"""
	stop_music()
	match area:
		Constants.Area.OUTSIDE:
			play_music(Constants.MusicID.MAIN_THEME)
		Constants.Area.INDOOR:
			play_music(Constants.MusicID.MERCHANT)
		Constants.Area.DUNGEON:
			play_music(Constants.MusicID.DUNGEON)

func set_music_volume(scale: int):
	"""Set music volume (0-5)"""
	music_volume_scale = clamp(scale, 0, 5)
	update_music_volume()

func set_sfx_volume(scale: int):
	"""Set sound effect volume (0-5)"""
	sfx_volume_scale = clamp(scale, 0, 5)
	update_sfx_volume()

func update_music_volume():
	"""Update music player volume"""
	music_player.volume_db = VOLUME_DB_VALUES[music_volume_scale]

func update_sfx_volume():
	"""Update SFX player volume"""
	sfx_player.volume_db = VOLUME_DB_VALUES[sfx_volume_scale]

func increase_music_volume():
	"""Increase music volume by 1"""
	if music_volume_scale < 5:
		set_music_volume(music_volume_scale + 1)

func decrease_music_volume():
	"""Decrease music volume by 1"""
	if music_volume_scale > 0:
		set_music_volume(music_volume_scale - 1)

func increase_sfx_volume():
	"""Increase SFX volume by 1"""
	if sfx_volume_scale < 5:
		set_sfx_volume(sfx_volume_scale + 1)

func decrease_sfx_volume():
	"""Decrease SFX volume by 1"""
	if sfx_volume_scale > 0:
		set_sfx_volume(sfx_volume_scale - 1)
