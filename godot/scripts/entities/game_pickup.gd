extends Area2D
class_name GamePickup
## GamePickup - 可撿取物件基礎類別 (Key, Potion, Heart, Coin 等)

@export var item_name: String = "Item"
@export var description: String = ""
@export var stackable: bool = false
@export var max_stack: int = 1
@export var use_sound: String = "power_up"

var sprite: Sprite2D


func _ready() -> void:
	add_to_group("pickups")
	collision_layer = 8  # Objects layer
	collision_mask = 1   # Player layer
	
	body_entered.connect(_on_body_entered)
	_setup_pickup()


func _setup_pickup() -> void:
	"""子類別覆寫此方法"""
	pass


func _on_body_entered(body: Node2D) -> void:
	"""玩家接觸時觸發"""
	if body.is_in_group("player"):
		pick_up(body)


func pick_up(player: Node2D) -> void:
	"""被撿起"""
	AudioManager.play_sfx(use_sound)
	_on_picked_up(player)
	queue_free()


func _on_picked_up(_player: Node2D) -> void:
	"""子類別覆寫此方法處理撿起邏輯"""
	pass
