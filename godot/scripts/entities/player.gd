extends CharacterBody2D
class_name Player
## Player - 玩家控制腳本

# 常數
const TILE_SIZE = 64
const SPEED = 256.0  # 4 tiles per second

# 方向枚舉
enum Direction { DOWN, UP, LEFT, RIGHT }

# 狀態
var current_direction: Direction = Direction.DOWN
var is_moving: bool = false
var is_attacking: bool = false
var is_guarding: bool = false

# 動畫
@onready var animated_sprite: AnimatedSprite2D = $AnimatedSprite2D
@onready var attack_area: Area2D = $AttackArea
@onready var attack_collision: CollisionShape2D = $AttackArea/CollisionShape2D

# 精靈動畫幀 (動態載入)
var sprite_frames: SpriteFrames


func _ready() -> void:
	print("[Player] Initializing...")
	_setup_animations()
	_update_animation()
	print("[Player] Ready at position: %s" % position)


func _setup_animations() -> void:
	"""設定所有動畫幀"""
	sprite_frames = SpriteFrames.new()
	
	# Walking 動畫 (8 FPS)
	_add_animation("walk_down", [
		"res://resources/player/walking/boy_down_1.png",
		"res://resources/player/walking/boy_down_2.png"
	], 8.0)
	_add_animation("walk_up", [
		"res://resources/player/walking/boy_up_1.png",
		"res://resources/player/walking/boy_up_2.png"
	], 8.0)
	_add_animation("walk_left", [
		"res://resources/player/walking/boy_left_1.png",
		"res://resources/player/walking/boy_left_2.png"
	], 8.0)
	_add_animation("walk_right", [
		"res://resources/player/walking/boy_right_1.png",
		"res://resources/player/walking/boy_right_2.png"
	], 8.0)
	
	# Idle 動畫 (站立使用 walk 的第一幀)
	_add_animation("idle_down", ["res://resources/player/walking/boy_down_1.png"], 1.0)
	_add_animation("idle_up", ["res://resources/player/walking/boy_up_1.png"], 1.0)
	_add_animation("idle_left", ["res://resources/player/walking/boy_left_1.png"], 1.0)
	_add_animation("idle_right", ["res://resources/player/walking/boy_right_1.png"], 1.0)
	
	# Guard 動畫
	_add_animation("guard_down", ["res://resources/player/guarding/boy_guard_down.png"], 1.0)
	_add_animation("guard_up", ["res://resources/player/guarding/boy_guard_up.png"], 1.0)
	_add_animation("guard_left", ["res://resources/player/guarding/boy_guard_left.png"], 1.0)
	_add_animation("guard_right", ["res://resources/player/guarding/boy_guard_right.png"], 1.0)
	
	# Attack 動畫 (劍) - 不循環
	_add_animation("attack_down", [
		"res://resources/player/attacking/sword/boy_attack_down_1.png",
		"res://resources/player/attacking/sword/boy_attack_down_2.png"
	], 10.0, false)
	_add_animation("attack_up", [
		"res://resources/player/attacking/sword/boy_attack_up_1.png",
		"res://resources/player/attacking/sword/boy_attack_up_2.png"
	], 10.0, false)
	_add_animation("attack_left", [
		"res://resources/player/attacking/sword/boy_attack_left_1.png",
		"res://resources/player/attacking/sword/boy_attack_left_2.png"
	], 10.0, false)
	_add_animation("attack_right", [
		"res://resources/player/attacking/sword/boy_attack_right_1.png",
		"res://resources/player/attacking/sword/boy_attack_right_2.png"
	], 10.0, false)
	
	# 移除預設動畫
	if sprite_frames.has_animation("default"):
		sprite_frames.remove_animation("default")
	
	animated_sprite.sprite_frames = sprite_frames
	print("[Player] Animations loaded")


func _add_animation(anim_name: String, paths: Array, fps: float, loop: bool = true) -> void:
	"""新增動畫"""
	sprite_frames.add_animation(anim_name)
	sprite_frames.set_animation_speed(anim_name, fps)
	sprite_frames.set_animation_loop(anim_name, loop)
	
	for path in paths:
		var texture = load(path) as Texture2D
		if texture:
			sprite_frames.add_frame(anim_name, texture)
		else:
			push_warning("[Player] Failed to load texture: %s" % path)


func _physics_process(delta: float) -> void:
	if not GameManager.can_player_move():
		velocity = Vector2.ZERO
		return
	
	# 處理攻擊
	if Input.is_action_just_pressed("attack") and not is_attacking:
		_start_attack()
		return
	
	# 處理格擋
	is_guarding = Input.is_action_pressed("guard")
	
	if is_attacking:
		velocity = Vector2.ZERO
	elif is_guarding:
		velocity = Vector2.ZERO
	else:
		_handle_movement()
	
	move_and_slide()
	_update_animation()


func _handle_movement() -> void:
	"""處理移動輸入"""
	var input_direction = Vector2.ZERO
	
	if Input.is_action_pressed("move_up"):
		input_direction.y -= 1
		current_direction = Direction.UP
	elif Input.is_action_pressed("move_down"):
		input_direction.y += 1
		current_direction = Direction.DOWN
	
	if Input.is_action_pressed("move_left"):
		input_direction.x -= 1
		current_direction = Direction.LEFT
	elif Input.is_action_pressed("move_right"):
		input_direction.x += 1
		current_direction = Direction.RIGHT
	
	is_moving = input_direction != Vector2.ZERO
	velocity = input_direction.normalized() * SPEED


func _start_attack() -> void:
	"""開始攻擊"""
	is_attacking = true
	AudioManager.play_sfx("swing_weapon")
	
	# 播放攻擊動畫
	var anim_name = "attack_" + _get_direction_name()
	animated_sprite.play(anim_name)
	
	# 啟用攻擊碰撞區域
	_update_attack_area()
	attack_collision.disabled = false
	
	# 等待動畫結束
	await animated_sprite.animation_finished
	
	# 結束攻擊
	attack_collision.disabled = true
	is_attacking = false


func _update_attack_area() -> void:
	"""根據方向更新攻擊區域位置"""
	match current_direction:
		Direction.DOWN:
			attack_collision.position = Vector2(0, 48)
		Direction.UP:
			attack_collision.position = Vector2(0, -48)
		Direction.LEFT:
			attack_collision.position = Vector2(-48, 0)
		Direction.RIGHT:
			attack_collision.position = Vector2(48, 0)


func _update_animation() -> void:
	"""更新動畫狀態"""
	if is_attacking:
		return  # 攻擊動畫由 _start_attack 控制
	
	var anim_name: String
	if is_guarding:
		anim_name = "guard_" + _get_direction_name()
	elif is_moving:
		anim_name = "walk_" + _get_direction_name()
	else:
		anim_name = "idle_" + _get_direction_name()
	
	if animated_sprite.animation != anim_name:
		animated_sprite.play(anim_name)


func _get_direction_name() -> String:
	"""取得方向名稱字串"""
	match current_direction:
		Direction.DOWN: return "down"
		Direction.UP: return "up"
		Direction.LEFT: return "left"
		Direction.RIGHT: return "right"
	return "down"
