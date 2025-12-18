extends StaticBody2D
class_name GameObject
## GameObject - 靜態互動物件基礎類別 (Door, Chest 等)

@export var object_name: String = "Object"
@export var is_solid: bool = true
@export var interact_sound: String = "door_open"

var sprite: Sprite2D
var interaction_area: Area2D


func _ready() -> void:
	add_to_group("objects")
	_setup_object()


func _setup_object() -> void:
	"""子類別覆寫此方法"""
	pass


func interact(player: Node2D) -> void:
	"""玩家互動"""
	AudioManager.play_sfx(interact_sound)
	_on_interact(player)


func _on_interact(_player: Node2D) -> void:
	"""子類別覆寫此方法處理互動邏輯"""
	pass
