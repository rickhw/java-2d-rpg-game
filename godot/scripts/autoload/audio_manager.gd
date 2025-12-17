extends Node
## AudioManager Autoload - 管理背景音樂與音效播放

# BGM 檔案路徑
const BGM_PATHS = {
	"adventure": "res://resources/bgm/BlueBoyAdventure.wav",
	"dungeon": "res://resources/bgm/Dungeon.wav",
	"final_battle": "res://resources/bgm/FinalBattle.wav",
	"merchant": "res://resources/bgm/Merchant.wav",
	"fanfare": "res://resources/bgm/fanfare.wav"
}

# SFX 檔案路徑
const SFX_PATHS = {
	"blocked": "res://resources/sfx/blocked.wav",
	"burning": "res://resources/sfx/burning.wav",
	"chip_wall": "res://resources/sfx/chipwall.wav",
	"coin": "res://resources/sfx/coin.wav",
	"cursor": "res://resources/sfx/cursor.wav",
	"cut_tree": "res://resources/sfx/cuttree.wav",
	"door_open": "res://resources/sfx/dooropen.wav",
	"game_over": "res://resources/sfx/gameover.wav",
	"hit_monster": "res://resources/sfx/hitmonster.wav",
	"level_up": "res://resources/sfx/levelup.wav",
	"parry": "res://resources/sfx/parry.wav",
	"power_up": "res://resources/sfx/powerup.wav",
	"receive_damage": "res://resources/sfx/receivedamage.wav",
	"sleep": "res://resources/sfx/sleep.wav",
	"speak": "res://resources/sfx/speak.wav",
	"stairs": "res://resources/sfx/stairs.wav",
	"swing_weapon": "res://resources/sfx/swingweapon.wav",
	"unlock": "res://resources/sfx/unlock.wav"
}

# 音量設定 (0.0 - 1.0)
var bgm_volume: float = 0.8
var sfx_volume: float = 1.0

# BGM 播放器
var bgm_player: AudioStreamPlayer
var current_bgm: String = ""

# SFX 播放器池
var sfx_players: Array[AudioStreamPlayer] = []
const MAX_SFX_PLAYERS = 8


func _ready() -> void:
	_setup_bgm_player()
	_setup_sfx_players()
	print("[AudioManager] Initialized")


func _setup_bgm_player() -> void:
	bgm_player = AudioStreamPlayer.new()
	bgm_player.bus = "Master"
	bgm_player.finished.connect(_on_bgm_finished)
	add_child(bgm_player)


func _setup_sfx_players() -> void:
	for i in MAX_SFX_PLAYERS:
		var player = AudioStreamPlayer.new()
		player.bus = "Master"
		add_child(player)
		sfx_players.append(player)


func play_bgm(bgm_name: String) -> void:
	if bgm_name == current_bgm and bgm_player.playing:
		return
	
	if not BGM_PATHS.has(bgm_name):
		push_warning("[AudioManager] Unknown BGM: %s" % bgm_name)
		return
	
	var stream = load(BGM_PATHS[bgm_name])
	if stream:
		bgm_player.stream = stream
		bgm_player.volume_db = linear_to_db(bgm_volume)
		bgm_player.play()
		current_bgm = bgm_name
		print("[AudioManager] Playing BGM: %s" % bgm_name)


func stop_bgm() -> void:
	bgm_player.stop()
	current_bgm = ""


func play_sfx(sfx_name: String) -> void:
	if not SFX_PATHS.has(sfx_name):
		push_warning("[AudioManager] Unknown SFX: %s" % sfx_name)
		return
	
	var player = _get_available_sfx_player()
	if player:
		var stream = load(SFX_PATHS[sfx_name])
		if stream:
			player.stream = stream
			player.volume_db = linear_to_db(sfx_volume)
			player.play()


func _get_available_sfx_player() -> AudioStreamPlayer:
	for player in sfx_players:
		if not player.playing:
			return player
	# 如果所有播放器都在使用中，返回第一個
	return sfx_players[0]


func set_bgm_volume(volume: float) -> void:
	bgm_volume = clamp(volume, 0.0, 1.0)
	bgm_player.volume_db = linear_to_db(bgm_volume)


func set_sfx_volume(volume: float) -> void:
	sfx_volume = clamp(volume, 0.0, 1.0)


func _on_bgm_finished() -> void:
	"""BGM 播放完畢後重新播放 (循環)"""
	if current_bgm != "":
		bgm_player.play()
