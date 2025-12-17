extends Node
## GameManager Autoload - 管理遊戲狀態與全域設定

# 遊戲狀態枚舉
enum GameState {
	TITLE,
	PLAY,
	PAUSE,
	DIALOGUE,
	CHARACTER,
	OPTIONS,
	GAME_OVER,
	TRANSITION,
	TRADE,
	SLEEP,
	DISPLAY_MAP,
	CUTSENSE
}

# 日夜狀態枚舉
enum DayState {
	DAY,
	DUSK,
	NIGHT,
	DAWN
}

# 場景/地圖枚舉
enum MapType {
	WORLD_MAP,
	STORE,
	DUNGEON01,
	DUNGEON02
}

# 當前遊戲狀態
var current_state: GameState = GameState.TITLE
var previous_state: GameState = GameState.TITLE

# 當前地圖
var current_map: MapType = MapType.WORLD_MAP

# 日夜狀態
var current_day_state: DayState = DayState.DAY

# 遊戲設定
var tile_size: int = 64  # 原始 16 * 縮放 4
var max_screen_col: int = 20
var max_screen_row: int = 12

# Debug 模式
var debug_mode: bool = false
var god_mode: bool = false

# 全螢幕設定
var full_screen: bool = false

# 訊號
signal state_changed(new_state: GameState)
signal map_changed(new_map: MapType)
signal day_state_changed(new_day_state: DayState)


func _ready() -> void:
	print("[GameManager] Initialized")


func change_state(new_state: GameState) -> void:
	previous_state = current_state
	current_state = new_state
	state_changed.emit(new_state)
	print("[GameManager] State changed: %s -> %s" % [GameState.keys()[previous_state], GameState.keys()[new_state]])


func change_map(new_map: MapType) -> void:
	current_map = new_map
	map_changed.emit(new_map)
	print("[GameManager] Map changed to: %s" % MapType.keys()[new_map])


func change_day_state(new_day_state: DayState) -> void:
	current_day_state = new_day_state
	day_state_changed.emit(new_day_state)
	print("[GameManager] Day state changed to: %s" % DayState.keys()[new_day_state])


func toggle_debug_mode() -> void:
	debug_mode = not debug_mode
	print("[GameManager] Debug mode: %s" % debug_mode)


func toggle_god_mode() -> void:
	god_mode = not god_mode
	print("[GameManager] God mode: %s" % god_mode)


func is_playing() -> bool:
	return current_state == GameState.PLAY


func is_paused() -> bool:
	return current_state == GameState.PAUSE


func can_player_move() -> bool:
	return current_state == GameState.PLAY
