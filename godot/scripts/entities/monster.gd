extends Entity
class_name Monster
## Monster - 怪物基礎類別

# 怪物特有屬性
@export var exp_reward: int = 2
@export var aggro_range: int = 5  # 追蹤範圍 (tiles)
@export var attack_range: int = 1  # 攻擊範圍 (tiles)
@export var can_shoot: bool = false

# AI 狀態
enum AIState { IDLE, PATROL, CHASE, ATTACK, RETREAT }
var ai_state: AIState = AIState.IDLE

# 計時器
var action_timer: float = 0.0
var action_interval: float = 2.0

# 目標
var target: Node2D = null


func _setup_entity() -> void:
	entity_type = EntityType.MONSTER
	add_to_group("monsters")


func _update_entity(delta: float) -> void:
	# 尋找玩家
	if not target:
		target = get_tree().get_first_node_in_group("player")
	
	# 更新 AI
	action_timer += delta
	if action_timer >= action_interval:
		action_timer = 0.0
		_update_ai()
	
	# 執行當前行為
	match ai_state:
		AIState.IDLE:
			_do_idle()
		AIState.PATROL:
			_do_patrol()
		AIState.CHASE:
			_do_chase()
		AIState.ATTACK:
			_do_attack()
	
	move_and_slide()
	_update_animation()


func _update_ai() -> void:
	"""更新 AI 決策"""
	if not target or not is_instance_valid(target):
		ai_state = AIState.PATROL
		return
	
	var distance = get_tile_distance_to(target)
	
	if distance <= attack_range:
		ai_state = AIState.ATTACK
	elif distance <= aggro_range:
		ai_state = AIState.CHASE
	else:
		ai_state = AIState.PATROL


func _do_idle() -> void:
	velocity = Vector2.ZERO


func _do_patrol() -> void:
	"""隨機巡邏"""
	if randi() % 4 == 0:
		var directions = [Vector2.UP, Vector2.DOWN, Vector2.LEFT, Vector2.RIGHT]
		var dir = directions[randi() % 4]
		velocity = dir * speed
		_set_direction_from_velocity()
	else:
		velocity = Vector2.ZERO


func _do_chase() -> void:
	"""追蹤玩家"""
	if not target:
		return
	
	var direction = (target.global_position - global_position).normalized()
	velocity = direction * speed
	_set_direction_from_velocity()


func _do_attack() -> void:
	"""攻擊玩家"""
	velocity = Vector2.ZERO
	
	if target and target.has_method("take_damage"):
		face_direction(target.global_position)
		# 傷害會在碰撞時由 Player 處理


func _set_direction_from_velocity() -> void:
	"""根據速度設定方向"""
	if abs(velocity.x) > abs(velocity.y):
		current_direction = Direction.RIGHT if velocity.x > 0 else Direction.LEFT
	elif velocity.y != 0:
		current_direction = Direction.DOWN if velocity.y > 0 else Direction.UP


func _update_animation() -> void:
	"""更新怪物動畫"""
	if not animated_sprite:
		return
	
	var anim_name = "walk_" + get_direction_name()
	if animated_sprite.sprite_frames.has_animation(anim_name):
		if animated_sprite.animation != anim_name:
			animated_sprite.play(anim_name)


func get_tile_distance_to(other: Node2D) -> int:
	"""計算到目標的 tile 距離"""
	var my_tile = get_tile_position()
	var other_tile = Vector2i(
		int(other.global_position.x / GameManager.tile_size),
		int(other.global_position.y / GameManager.tile_size)
	)
	return abs(my_tile.x - other_tile.x) + abs(my_tile.y - other_tile.y)


func die() -> void:
	"""怪物死亡 - 可能掉落物品"""
	# TODO: 掉落物品
	super.die()
