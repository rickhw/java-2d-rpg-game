extends Node
## GameManager - 遊戲狀態管理器
## 管理遊戲狀態、場景切換、全域變數

# 遊戲狀態列舉
enum GameState {
	TITLE,        # 標題畫面
	PLAY,         # 遊戲進行中
	PAUSE,        # 暫停
	DIALOGUE,     # 對話中
	CHARACTER,    # 角色介面（背包）
	TRADE,        # 交易
	OPTIONS,      # 選項設定
	GAME_OVER,    # 遊戲結束
	DISPLAY_MAP,  # 顯示地圖
	CUTSENSE,     # 過場動畫
	SLEEP         # 睡眠（使用帳篷）
}

# 地圖/區域列舉
enum Area {
	OUTSIDE = 50,   # 室外
	INDOOR = 51,    # 室內
	DUNGEON = 52    # 地下城
}

enum Map {
	WORLD_MAP = 0,
	STORE = 1,
	DUNGEON_B1 = 2,
	DUNGEON_B2 = 3
}

# 全域狀態
var current_state: GameState = GameState.TITLE
var current_area: Area = Area.OUTSIDE
var current_map: Map = Map.WORLD_MAP

# 遊戲設定
const TILE_SIZE: int = 64  # 對應 Java 的 tileSize = 48 * scale(4/3)
const ORIGINAL_TILE_SIZE: int = 16
const SCALE: int = 4

# Boss 戰鬥標記
var boss_battle_on: bool = false

# 場景引用（會在運行時設定）
var player: CharacterBody2D = null
var current_world: Node2D = null

# 訊號
signal state_changed(new_state: GameState)
signal area_changed(new_area: Area)
signal map_changed(new_map: Map)
signal boss_battle_started
signal boss_battle_ended

func _ready() -> void:
	print("[GameManager] 初始化完成")
	process_mode = Node.PROCESS_MODE_ALWAYS  # 即使暫停也繼續運行

## 改變遊戲狀態
func change_state(new_state: GameState) -> void:
	if current_state != new_state:
		var old_state = current_state
		current_state = new_state
		print("[GameManager] 狀態改變: %s -> %s" % [GameState.keys()[old_state], GameState.keys()[new_state]])
		state_changed.emit(new_state)
		
		# 根據狀態處理暫停
		match new_state:
			GameState.PAUSE, GameState.OPTIONS, GameState.CHARACTER, GameState.TRADE:
				get_tree().paused = true
			_:
				get_tree().paused = false

## 改變區域（會影響 BGM）
func change_area(new_area: Area) -> void:
	if current_area != new_area:
		print("[GameManager] 區域改變: %s -> %s" % [Area.keys()[current_area - 50], Area.keys()[new_area - 50]])
		current_area = new_area
		area_changed.emit(new_area)

## 改變地圖
func change_map(new_map: Map) -> void:
	if current_map != new_map:
		print("[GameManager] 地圖改變: %s -> %s" % [Map.keys()[current_map], Map.keys()[new_map]])
		current_map = new_map
		map_changed.emit(new_map)

## 重置遊戲（回到標題或重試）
func reset_game(restart: bool = false) -> void:
	print("[GameManager] 重置遊戲 (restart=%s)" % restart)
	boss_battle_on = false
	current_area = Area.OUTSIDE
	
	if player:
		player.restore_status()
		player.reset_counters()
	
	if restart:
		current_state = GameState.TITLE
	else:
		current_state = GameState.PLAY

## 開始 Boss 戰
func start_boss_battle() -> void:
	boss_battle_on = true
	boss_battle_started.emit()
	print("[GameManager] Boss 戰開始！")

## 結束 Boss 戰
func end_boss_battle() -> void:
	boss_battle_on = false
	boss_battle_ended.emit()
	print("[GameManager] Boss 戰結束！")
