extends CharacterBody2D
class_name Entity
## Entity - 所有遊戲實體的基礎類別 (NPC, Monster, Object)

# 實體類型
enum EntityType { NPC, MONSTER, OBJECT, PROJECTILE }

# 基礎屬性
@export var entity_name: String = "Entity"
@export var entity_type: EntityType = EntityType.NPC
@export var max_life: int = 10
@export var life: int = 10
@export var speed: float = 64.0
@export var attack_value: int = 1
@export var defense_value: int = 0

# 方向
enum Direction { DOWN, UP, LEFT, RIGHT }
var current_direction: Direction = Direction.DOWN

# 狀態
var is_alive: bool = true
var is_invincible: bool = false
var invincible_counter: float = 0.0
const INVINCIBLE_DURATION: float = 1.0

# 擊退
var knockback_active: bool = false
var knockback_direction: Vector2 = Vector2.ZERO
var knockback_counter: float = 0.0
const KNOCKBACK_DURATION: float = 0.2
const KNOCKBACK_SPEED: float = 300.0

# 動畫
@onready var animated_sprite: AnimatedSprite2D = $AnimatedSprite2D if has_node("AnimatedSprite2D") else null

# 訊號
signal died
signal took_damage(amount: int)


func _ready() -> void:
	life = max_life
	_setup_entity()


func _setup_entity() -> void:
	"""子類別覆寫此方法進行初始化"""
	pass


func _physics_process(delta: float) -> void:
	if not is_alive:
		return
	
	# 處理無敵時間
	if is_invincible:
		invincible_counter += delta
		if invincible_counter >= INVINCIBLE_DURATION:
			is_invincible = false
			invincible_counter = 0.0
			_set_sprite_visible(true)
		else:
			# 閃爍效果
			_set_sprite_visible(int(invincible_counter * 10) % 2 == 0)
	
	# 處理擊退
	if knockback_active:
		knockback_counter += delta
		if knockback_counter >= KNOCKBACK_DURATION:
			knockback_active = false
			knockback_counter = 0.0
			velocity = Vector2.ZERO
		else:
			velocity = knockback_direction * KNOCKBACK_SPEED
			move_and_slide()
			return
	
	# 正常更新
	_update_entity(delta)


func _update_entity(_delta: float) -> void:
	"""子類別覆寫此方法進行更新"""
	pass


func take_damage(amount: int, attacker: Node2D = null) -> void:
	"""受到傷害"""
	if is_invincible or not is_alive:
		return
	
	var actual_damage = max(1, amount - defense_value)
	life -= actual_damage
	
	took_damage.emit(actual_damage)
	AudioManager.play_sfx("hit_monster")
	
	# 擊退效果
	if attacker:
		apply_knockback(attacker.global_position)
	
	# 無敵時間
	is_invincible = true
	invincible_counter = 0.0
	
	# 檢查死亡
	if life <= 0:
		die()


func apply_knockback(from_position: Vector2) -> void:
	"""應用擊退效果"""
	knockback_direction = (global_position - from_position).normalized()
	knockback_active = true
	knockback_counter = 0.0


func die() -> void:
	"""死亡處理"""
	is_alive = false
	died.emit()
	
	# 播放死亡動畫 (如果有)
	if animated_sprite and animated_sprite.sprite_frames.has_animation("die"):
		animated_sprite.play("die")
		await animated_sprite.animation_finished
	
	queue_free()


func heal(amount: int) -> void:
	"""恢復生命"""
	life = min(life + amount, max_life)


func _set_sprite_visible(visible: bool) -> void:
	"""設定精靈可見性"""
	if animated_sprite:
		animated_sprite.visible = visible


func get_direction_name() -> String:
	"""取得方向名稱"""
	match current_direction:
		Direction.DOWN: return "down"
		Direction.UP: return "up"
		Direction.LEFT: return "left"
		Direction.RIGHT: return "right"
	return "down"


func face_direction(target_position: Vector2) -> void:
	"""面向目標位置"""
	var direction = target_position - global_position
	
	if abs(direction.x) > abs(direction.y):
		current_direction = Direction.RIGHT if direction.x > 0 else Direction.LEFT
	else:
		current_direction = Direction.DOWN if direction.y > 0 else Direction.UP


func get_tile_position() -> Vector2i:
	"""取得目前所在的 Tile 座標"""
	return Vector2i(
		int(global_position.x / GameManager.tile_size),
		int(global_position.y / GameManager.tile_size)
	)
