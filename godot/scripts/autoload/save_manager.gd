extends Node
## SaveManager - 存檔管理器
## 處理遊戲存檔和讀檔

const SAVE_FILE_PATH = "user://savegame.save"

# 存檔資料結構
var save_data: Dictionary = {
	"player": {},
	"game_state": {},
	"world_state": {}
}

func _ready() -> void:
	print("[SaveManager] 初始化完成")

## 儲存遊戲
func save_game() -> bool:
	print("[SaveManager] 開始存檔...")
	
	# 收集玩家資料
	if GameManager.player:
		save_data["player"] = _get_player_data()
	
	# 收集遊戲狀態
	save_data["game_state"] = _get_game_state_data()
	
	# 收集世界狀態（物件、怪物等）
	save_data["world_state"] = _get_world_state_data()
	
	# 寫入檔案
	var file = FileAccess.open(SAVE_FILE_PATH, FileAccess.WRITE)
	if file:
		var json_string = JSON.stringify(save_data)
		file.store_string(json_string)
		file.close()
		print("[SaveManager] 存檔成功: %s" % SAVE_FILE_PATH)
		return true
	else:
		push_error("[SaveManager] 存檔失敗: 無法開啟檔案")
		return false

## 讀取遊戲
func load_game() -> bool:
	print("[SaveManager] 開始讀檔...")
	
	if not FileAccess.file_exists(SAVE_FILE_PATH):
		push_warning("[SaveManager] 存檔檔案不存在")
		return false
	
	var file = FileAccess.open(SAVE_FILE_PATH, FileAccess.READ)
	if file:
		var json_string = file.get_as_text()
		file.close()
		
		var json = JSON.new()
		var parse_result = json.parse(json_string)
		
		if parse_result == OK:
			save_data = json.data
			_apply_save_data()
			print("[SaveManager] 讀檔成功")
			return true
		else:
			push_error("[SaveManager] 讀檔失敗: JSON 解析錯誤")
			return false
	else:
		push_error("[SaveManager] 讀檔失敗: 無法開啟檔案")
		return false

## 刪除存檔
func delete_save() -> void:
	if FileAccess.file_exists(SAVE_FILE_PATH):
		DirAccess.remove_absolute(SAVE_FILE_PATH)
		print("[SaveManager] 存檔已刪除")

## 檢查是否有存檔
func has_save() -> bool:
	return FileAccess.file_exists(SAVE_FILE_PATH)

# ============================================================================
# 私有方法
# ============================================================================

## 取得玩家資料
func _get_player_data() -> Dictionary:
	var player = GameManager.player
	return {
		"position": {
			"x": player.global_position.x,
			"y": player.global_position.y
		},
		"stats": {
			"level": player.level,
			"life": player.life,
			"max_life": player.max_life,
			"mana": player.mana,
			"max_mana": player.max_mana,
			"exp": player.exp,
			"next_level_exp": player.next_level_exp,
			"coin": player.coin,
			"strength": player.strength,
			"dexterity": player.dexterity
		},
		"inventory": _get_inventory_data(player)
	}

## 取得遊戲狀態
func _get_game_state_data() -> Dictionary:
	return {
		"current_map": GameManager.current_map,
		"current_area": GameManager.current_area,
		"boss_battle_on": GameManager.boss_battle_on
	}

## 取得世界狀態
func _get_world_state_data() -> Dictionary:
	# TODO: 實作世界物件狀態保存
	return {}

## 取得背包資料
func _get_inventory_data(player) -> Array:
	var inventory_data = []
	# TODO: 遍歷玩家背包並序列化物品
	return inventory_data

## 應用存檔資料
func _apply_save_data() -> void:
	# 應用玩家資料
	if "player" in save_data and GameManager.player:
		_apply_player_data(save_data["player"])
	
	# 應用遊戲狀態
	if "game_state" in save_data:
		_apply_game_state_data(save_data["game_state"])

## 應用玩家資料
func _apply_player_data(data: Dictionary) -> void:
	var player = GameManager.player
	
	# 位置
	if "position" in data:
		player.global_position = Vector2(data["position"]["x"], data["position"]["y"])
	
	# 屬性
	if "stats" in data:
		var stats = data["stats"]
		player.level = stats.get("level", 1)
		player.life = stats.get("life", 6)
		player.max_life = stats.get("max_life", 6)
		player.mana = stats.get("mana", 4)
		player.max_mana = stats.get("max_mana", 4)
		player.exp = stats.get("exp", 0)
		player.next_level_exp = stats.get("next_level_exp", 5)
		player.coin = stats.get("coin", 0)
		player.strength = stats.get("strength", 1)
		player.dexterity = stats.get("dexterity", 1)
	
	print("[SaveManager] 玩家資料已套用")

## 應用遊戲狀態
func _apply_game_state_data(data: Dictionary) -> void:
	if "current_map" in data:
		GameManager.current_map = data["current_map"]
	if "current_area" in data:
		GameManager.current_area = data["current_area"]
	if "boss_battle_on" in data:
		GameManager.boss_battle_on = data["boss_battle_on"]
	
	print("[SaveManager] 遊戲狀態已套用")
