extends CharacterBody2D
class_name Player
## Player - 玩家角色
## 使用 Godot 物理引擎處理移動和碰撞

# ============================================================================
# 常數
# ============================================================================
const WALKING_SPEED = 300.0  # 對應 Java 的 speed = 5 * (60FPS) = 300 pixel/s
const STANDING_ANIMATION_SPEED = 0.5  # 站立動畫速度
const WALKING_ANIMATION_SPEED = 0.15  # 走路動畫速度
const ATTACK_DURATION = 0.4  # 攻擊動畫時長

# ============================================================================
# 節點引用
# ============================================================================
@onready var animated_sprite = $AnimatedSprite2D
@onready var collision_shape = $CollisionShape2D
@onready var attack_area = $AttackArea
@onready var attack_collision = $AttackArea/CollisionShape2D
@onready var camera = $Camera2D

# ============================================================================
# 玩家屬性
# ============================================================================
var level: int = 1
var life: int = 6
var max_life: int = 6
var mana: int = 4
var max_mana: int = 4
var exp: int = 0
var next_level_exp: int = 5
var coin: int = 500

var strength: int = 1      # 力量
var dexterity: int = 1     # 敏捷
var attack_power: int = 1  # 攻擊力 (strength * weapon)
var defense: int = 1       # 防禦力 (dexterity * shield)

# ============================================================================
# 狀態
# ============================================================================
var current_direction: String = "down"  # up, down, left, right
var is_attacking: bool = false
var is_guarding: bool = false
var is_invincible: bool = false
var invincible_timer: float = 0.0
const INVINCIBLE_DURATION: float = 1.0  # 60 frames / 60 FPS = 1 second

# 拋射物
var can_shoot: bool = true
var shoot_cooldown: float = 0.5  # 30 frames / 60 FPS = 0.5 second
var shoot_timer: float = 0.0

# 背包
var inventory: Array = []
const MAX_INVENTORY_SIZE: int = 20

# 裝備（暫時為 null，後續實作）
var current_weapon = null
var current_shield = null
var current_light = null

# ============================================================================
# Godot 回調
# ============================================================================
func _ready() -> void:
	# 設定玩家引用給 GameManager
	GameManager.player = self
	
	# 設定初始位置（對應 Java 的 setDefaultPosition）
	set_default_position()
	
	# 設定初始動畫
	animated_sprite.play("idle_down")
	
	# 禁用攻擊判定
	attack_collision.disabled = true
	
	# 設定碰撞層
	collision_layer = 1  # Player layer
	collision_mask = 0b11111110  # 與除了自己以外的所有層碰撞
	
	# 設定攻擊區域
	attack_area.collision_layer = 0
	attack_area.collision_mask = 0b00000010  # 只偵測 Enemies (Layer 2)
	
	print("[Player] 玩家初始化完成")
	print("[Player] 初始位置: %s" % global_position)
	print("[Player] 屬性: Lv.%d HP:%d/%d MP:%d/%d" % [level, life, max_life, mana, max_mana])

func _physics_process(delta: float) -> void:
	# 更新無敵狀態
	if is_invincible:
		invincible_timer += delta
		if invincible_timer >= INVINCIBLE_DURATION:
			is_invincible = false
			invincible_timer = 0.0
			modulate.a = 1.0
		else:
			# 閃爍效果
			modulate.a = 0.5 if int(invincible_timer * 10) % 2 == 0 else 1.0
	
	# 更新發射冷卻
	if !can_shoot:
		shoot_timer += delta
		if shoot_timer >= shoot_cooldown:
			can_shoot = true
			shoot_timer = 0.0
	
	# 攻擊中不能移動
	if is_attacking:
		velocity = Vector2.ZERO
		move_and_slide()
		update_animation()  # 更新攻擊動畫
		return
	
	# 格擋中速度減半
	var speed = WALKING_SPEED
	if is_guarding:
		speed *= 0.5
	
	# 取得輸入方向
	var input_direction = Input.get_vector("move_left", "move_right", "move_up", "move_down")
	
	# 設定速度
	velocity = input_direction * speed
	
	# 更新方向
	if input_direction != Vector2.ZERO:
		update_direction(input_direction)
	
	# 使用 Godot 物理引擎移動（自動處理碰撞）
	var old_position = global_position
	move_and_slide()
	
	# 除錯：顯示位置變化
	if velocity.length() > 0 and global_position != old_position:
		print("[Player] 位置: %s -> %s (移動了 %s)" % [old_position, global_position, global_position - old_position])
	
	# 更新動畫
	update_animation()

func _input(event: InputEvent) -> void:
	# 攻擊
	if event.is_action_pressed("attack") and !is_attacking:
		attack()
	
	# 格擋
	if event.is_action_pressed("guard"):
		is_guarding = true
	elif event.is_action_released("guard"):
		is_guarding = false
	
	# 發射拋射物
	if event.is_action_pressed("shoot") and can_shoot and mana > 0:
		shoot_projectile()

# ============================================================================
# 移動與方向
# ============================================================================
func update_direction(input_dir: Vector2) -> void:
	# 根據輸入更新面向
	if abs(input_dir.x) > abs(input_dir.y):
		# 水平移動優先
		if input_dir.x > 0:
			current_direction = "right"
		else:
			current_direction = "left"
	else:
		# 垂直移動
		if input_dir.y > 0:
			current_direction = "down"
		else:
			current_direction = "up"

func set_default_position() -> void:
	# 對應 Java 的 worldX = gp.tileSize * 23, worldY = gp.tileSize * 21
	global_position = Vector2(
		GameManager.TILE_SIZE * 23,
		GameManager.TILE_SIZE * 21
	)

# ============================================================================
# 動畫
# ============================================================================
func update_animation() -> void:
	var new_animation = ""
	
	if is_attacking:
		# 攻擊動畫
		new_animation = "attack_" + current_direction
		
		# 調整 offset 以補償攻擊圖片較大的問題
		# 攻擊圖上下是 64x128，左右是 128x64
		match current_direction:
			"up":
				# 向上攻擊時，圖片高度是2倍，需要向上偏移半個 tile (32像素)
				animated_sprite.offset = Vector2(0, -8)
			"down":
				# 向下攻擊時不需要偏移（因為錨點在上方）
				animated_sprite.offset = Vector2(0, +8)
			"left":
				# 向左攻擊時，圖片寬度是2倍，需要向左偏移半個 tile (32像素)
				animated_sprite.offset = Vector2(-8, 0)
			"right":
				# 向右攻擊時不需要偏移（因為錨點在左方）
				animated_sprite.offset = Vector2(+8, 0)
	else:
		# 非攻擊狀態，重置 offset
		animated_sprite.offset = Vector2.ZERO
		
		if is_guarding:
			# 格擋動畫
			new_animation = "guard_" + current_direction
		elif velocity.length() > 0:
			# 移動動畫
			new_animation = "walk_" + current_direction
		else:
			# 站立動畫
			new_animation = "idle_" + current_direction
	
	#print("new_animation: %s" % new_animation)
	
	# 只在動畫改變時才播放，避免重複重置動畫
	if animated_sprite.animation != new_animation:
		animated_sprite.play(new_animation)

# ============================================================================
# 戰鬥系統
# ============================================================================
func attack() -> void:
	is_attacking = true
	attack_collision.disabled = false
	
	# 播放攻擊音效
	AudioManager.play_sfx(AudioManager.SFX.SWING_WEAPON)
	
	# 調整攻擊區域位置
	match current_direction:
		"up":
			attack_area.position = Vector2(0, -32)
			attack_area.rotation_degrees = -90
		"down":
			attack_area.position = Vector2(0, 32)
			attack_area.rotation_degrees = 90
		"left":
			attack_area.position = Vector2(-32, 0)
			attack_area.rotation_degrees = 180
		"right":
			attack_area.position = Vector2(32, 0)
			attack_area.rotation_degrees = 0
	
	# 檢測攻擊範圍內的敵人
	var enemies = attack_area.get_overlapping_bodies()
	for enemy in enemies:
		if enemy.is_in_group("enemies"):
			print("[Player] 攻擊擊中: %s" % enemy.name)
			# TODO: 呼叫敵人的 take_damage 方法
	
	# 設定計時器結束攻擊
	await get_tree().create_timer(ATTACK_DURATION).timeout
	is_attacking = false
	attack_collision.disabled = true

## 發射拋射物
func shoot_projectile() -> void:
	if mana <= 0:
		print("[Player] 魔力不足，無法發射")
		return
	
	# 消耗魔力
	mana -= 1
	EventBus.player_mana_changed.emit(mana, max_mana)
	
	# 播放音效
	AudioManager.play_sfx(AudioManager.SFX.BURNING)
	
	# TODO: 實例化火球拋射物
	print("[Player] 發射火球！方向: %s" % current_direction)
	EventBus.show_message("發射火球！")
	
	# 設定冷卻
	can_shoot = false

## 受到傷害
func take_damage(damage: int) -> void:
	if is_invincible:
		return
	
	# 計算實際傷害（考慮防禦）
	var actual_damage = max(1, damage - defense)
	
	# 格擋減少傷害
	if is_guarding:
		actual_damage = int(actual_damage / 3.0)
		AudioManager.play_sfx(AudioManager.SFX.BLOCKED)
		EventBus.show_message("格擋！")
	else:
		AudioManager.play_sfx(AudioManager.SFX.RECEIVE_DAMAGE)
	
	life -= actual_damage
	life = max(0, life)
	
	EventBus.player_health_changed.emit(life, max_life)
	EventBus.show_message("受到 %d 點傷害！" % actual_damage)
	
	# 設定無敵時間
	is_invincible = true
	invincible_timer = 0.0
	
	# 檢查是否死亡
	if life <= 0:
		die()

## 玩家死亡
func die() -> void:
	print("[Player] 玩家死亡")
	EventBus.player_died.emit()
	GameManager.change_state(GameManager.GameState.GAME_OVER)
	# TODO: 播放死亡動畫

## 恢復狀態
func restore_status() -> void:
	life = max_life
	mana = max_mana
	is_invincible = false
	is_attacking = false
	is_guarding = false
	modulate.a = 1.0
	print("[Player] 狀態已恢復")

## 重置計數器
func reset_counters() -> void:
	invincible_timer = 0.0
	shoot_timer = 0.0
	can_shoot = true

# ============================================================================
# 升級系統
# ============================================================================
func gain_exp(amount: int) -> void:
	exp += amount
	EventBus.player_exp_changed.emit(exp, next_level_exp)
	
	# 檢查是否升級
	if exp >= next_level_exp:
		level_up()

func level_up() -> void:
	level += 1
	next_level_exp *= 2
	max_life += 2
	max_mana += 1
	strength += 1
	dexterity += 1
	
	# 恢復滿血滿魔
	life = max_life
	mana = max_mana
	
	# 觸發事件
	EventBus.trigger_level_up(level)
	AudioManager.play_sfx(AudioManager.SFX.LEVELUP)
	
	print("[Player] 升級！等級: %d" % level)

# ============================================================================
# 物品系統（簡化版，後續擴展）
# ============================================================================
func add_item(item) -> bool:
	if inventory.size() >= MAX_INVENTORY_SIZE:
		EventBus.show_message("背包已滿！")
		return false
	
	inventory.append(item)
	EventBus.item_collected.emit(item.name if typeof(item) == TYPE_OBJECT else str(item))
	return true

func add_coin(amount: int) -> void:
	coin += amount
	EventBus.player_coin_changed.emit(coin)
	EventBus.show_message("獲得 %d 金幣！" % amount)
