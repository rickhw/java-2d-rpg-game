extends Node
## EventBus - 事件總線
## 集中管理遊戲中的訊號事件，解耦不同系統之間的通訊

# ============================================================================
# 玩家事件
# ============================================================================
signal player_health_changed(current_health: int, max_health: int)
signal player_mana_changed(current_mana: int, max_mana: int)
signal player_exp_changed(current_exp: int, next_level_exp: int)
signal player_level_up(new_level: int)
signal player_coin_changed(coin: int)
signal player_died

# ============================================================================
# 戰鬥事件
# ============================================================================
signal monster_damaged(monster, damage: int)
signal monster_killed(monster, exp: int)
signal player_attacked(attacker, damage: int)

# ============================================================================
# 物品事件
# ============================================================================
signal item_collected(item_name: String)
signal item_used(item_name: String)
signal item_equipped(item_name: String, slot: String)

# ============================================================================
# UI 事件
# ============================================================================
signal message_added(message: String)
signal dialogue_started(npc)
signal dialogue_ended

# ============================================================================
# 遊戲事件
# ============================================================================
signal area_transition(from_area, to_area)
signal map_changed(new_map)

func _ready() -> void:
	print("[EventBus] 事件總線初始化完成")

# ============================================================================
# 輔助方法
# ============================================================================

## 發送訊息到 UI
func show_message(message: String) -> void:
	message_added.emit(message)
	print("[EventBus] 訊息: %s" % message)

## 觸發玩家升級事件
func trigger_level_up(new_level: int) -> void:
	player_level_up.emit(new_level)
	show_message("Level Up! 現在是 Lv.%d" % new_level)
