extends Entity
class_name NPC
## NPC - 非玩家角色基礎類別

# NPC 特有屬性
@export var dialogue_lines: Array[String] = []
@export var dialogue_set: int = 0

# 移動行為
@export var can_move: bool = true
@export var move_interval: float = 2.0  # 每隔幾秒移動一次
var move_timer: float = 0.0
var is_moving: bool = false


func _setup_entity() -> void:
	entity_type = EntityType.NPC
	add_to_group("npcs")


func _update_entity(delta: float) -> void:
	if not can_move:
		return
	
	# 隨機移動計時器
	move_timer += delta
	if move_timer >= move_interval:
		move_timer = 0.0
		_random_move()
	
	# 執行移動
	if is_moving:
		move_and_slide()
		_update_animation()
		
		# 碰撞檢查
		if get_slide_collision_count() > 0 or velocity == Vector2.ZERO:
			is_moving = false
			velocity = Vector2.ZERO


func _random_move() -> void:
	"""隨機選擇方向移動"""
	var random_action = randi() % 5  # 0-3 移動, 4 停止
	
	match random_action:
		0:  # 上
			current_direction = Direction.UP
			velocity = Vector2(0, -speed)
			is_moving = true
		1:  # 下
			current_direction = Direction.DOWN
			velocity = Vector2(0, speed)
			is_moving = true
		2:  # 左
			current_direction = Direction.LEFT
			velocity = Vector2(-speed, 0)
			is_moving = true
		3:  # 右
			current_direction = Direction.RIGHT
			velocity = Vector2(speed, 0)
			is_moving = true
		_:  # 停止
			is_moving = false
			velocity = Vector2.ZERO


func _update_animation() -> void:
	"""更新 NPC 動畫"""
	if not animated_sprite:
		return
	
	var anim_name = "walk_" + get_direction_name() if is_moving else "idle_" + get_direction_name()
	
	if animated_sprite.sprite_frames.has_animation(anim_name):
		if animated_sprite.animation != anim_name:
			animated_sprite.play(anim_name)


func speak() -> void:
	"""開始對話"""
	# 面向玩家
	var player = get_tree().get_first_node_in_group("player")
	if player:
		face_direction(player.global_position)
	
	# 停止移動
	is_moving = false
	velocity = Vector2.ZERO
	
	# 觸發對話 (由 UI 系統處理)
	GameManager.change_state(GameManager.GameState.DIALOGUE)


func get_dialogue() -> Array[String]:
	"""取得當前對話內容"""
	return dialogue_lines
