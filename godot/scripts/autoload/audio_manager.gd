extends Node
## AudioManager - 音效管理器
## 管理背景音樂和音效播放

# 音效常數（對應 Java Sound.java）
enum BGM {
	MAIN_THEME = 0,
	MERCHANT = 1,
	DUNGEON = 2,
	FINAL_BATTLE = 3,
	FANFARE = 4
}

enum SFX {
	COIN = 0,
	POWERUP = 1,
	UNLOCK = 2,
	FANFARE_SHORT = 3,
	CURSOR = 4,
	BURNING = 5,
	SWING_WEAPON = 6,
	HIT_MONSTER = 7,
	RECEIVE_DAMAGE = 8,
	PARRY = 9,
	BLOCKED = 10,
	SPEAK = 11,
	LEVELUP = 12,
	GAME_OVER = 13,
	STAIRS = 14,
	SLEEP = 15,
	MERCHANT_HELLO = 16,
	MERCHANT_BYE = 17
}

# 音樂播放器
var bgm_player: AudioStreamPlayer
var sfx_player: AudioStreamPlayer

# 音量設定
var bgm_volume: float = 0.7
var sfx_volume: float = 0.7

# BGM 資源路徑
var bgm_paths: Dictionary = {
	BGM.MAIN_THEME: "res://resources/bgm/BlueBoyAdventure.wav",
	BGM.MERCHANT: "res://resources/bgm/Merchant.wav",
	BGM.DUNGEON: "res://resources/bgm/Dungeon.wav",
	BGM.FINAL_BATTLE: "res://resources/bgm/FinalBattle.wav",
	BGM.FANFARE: "res://resources/bgm/fanfare.wav"
}

# SFX 資源路徑
var sfx_paths: Dictionary = {
	SFX.COIN: "res://resources/sound/coin.wav",
	SFX.POWERUP: "res://resources/sound/powerup.wav",
	SFX.UNLOCK: "res://resources/sound/unlock.wav",
	SFX.FANFARE_SHORT: "res://resources/sound/fanfare.wav",
	SFX.CURSOR: "res://resources/sound/cursor.wav",
	SFX.BURNING: "res://resources/sound/burning.wav",
	SFX.SWING_WEAPON: "res://resources/sound/hitmonster.wav",
	SFX.HIT_MONSTER: "res://resources/sound/hitmonster.wav",
	SFX.RECEIVE_DAMAGE: "res://resources/sound/receivedamage.wav",
	SFX.PARRY: "res://resources/sound/parry.wav",
	SFX.BLOCKED: "res://resources/sound/blocked.wav",
	SFX.SPEAK: "res://resources/sound/speak.wav",
	SFX.LEVELUP: "res://resources/sound/levelup.wav",
	SFX.GAME_OVER: "res://resources/sound/gameover.wav",
	SFX.STAIRS: "res://resources/sound/stairs.wav",
	SFX.SLEEP: "res://resources/sound/sleep.wav",
	SFX.MERCHANT_HELLO: "res://resources/sound/Merchant.wav",
	SFX.MERCHANT_BYE: "res://resources/sound/Merchant.wav"
}

func _ready() -> void:
	# 建立 BGM 播放器
	bgm_player = AudioStreamPlayer.new()
	bgm_player.name = "BGMPlayer"
	bgm_player.bus = "Music"
	add_child(bgm_player)
	
	# 建立 SFX 播放器
	sfx_player = AudioStreamPlayer.new()
	sfx_player.name = "SFXPlayer"
	sfx_player.bus = "SFX"
	add_child(sfx_player)
	
	# 設定初始音量
	set_bgm_volume(bgm_volume)
	set_sfx_volume(sfx_volume)
	
	print("[AudioManager] 初始化完成")

## 播放背景音樂
func play_bgm(bgm_id: BGM, loop: bool = true) -> void:
	if bgm_id not in bgm_paths:
		push_error("[AudioManager] BGM ID 不存在: %s" % bgm_id)
		return
	
	var path = bgm_paths[bgm_id]
	if not ResourceLoader.exists(path):
		push_warning("[AudioManager] BGM 檔案不存在: %s" % path)
		return
	
	var stream = load(path)
	if stream:
		bgm_player.stream = stream
		# WAV 檔案需要設定 loop
		if stream is AudioStreamWAV:
			stream.loop_mode = AudioStreamWAV.LOOP_FORWARD if loop else AudioStreamWAV.LOOP_DISABLED
		bgm_player.play()
		print("[AudioManager] 播放 BGM: %s" % BGM.keys()[bgm_id])
	else:
		push_error("[AudioManager] 無法載入 BGM: %s" % path)

## 停止背景音樂
func stop_bgm() -> void:
	bgm_player.stop()
	print("[AudioManager] 停止 BGM")

## 播放音效
func play_sfx(sfx_id: SFX) -> void:
	if sfx_id not in sfx_paths:
		push_error("[AudioManager] SFX ID 不存在: %s" % sfx_id)
		return
	
	var path = sfx_paths[sfx_id]
	if not ResourceLoader.exists(path):
		push_warning("[AudioManager] SFX 檔案不存在: %s" % path)
		return
	
	var stream = load(path)
	if stream:
		sfx_player.stream = stream
		sfx_player.play()
	else:
		push_error("[AudioManager] 無法載入 SFX: %s" % path)

## 設定 BGM 音量
func set_bgm_volume(volume: float) -> void:
	bgm_volume = clamp(volume, 0.0, 1.0)
	bgm_player.volume_db = linear_to_db(bgm_volume)

## 設定 SFX 音量
func set_sfx_volume(volume: float) -> void:
	sfx_volume = clamp(volume, 0.0, 1.0)
	sfx_player.volume_db = linear_to_db(sfx_volume)

## 漸入 BGM
func fade_in_bgm(duration: float = 1.0) -> void:
	var tween = create_tween()
	bgm_player.volume_db = -80
	tween.tween_property(bgm_player, "volume_db", linear_to_db(bgm_volume), duration)

## 漸出 BGM
func fade_out_bgm(duration: float = 1.0) -> void:
	var tween = create_tween()
	tween.tween_property(bgm_player, "volume_db", -80, duration)
	tween.tween_callback(stop_bgm)
