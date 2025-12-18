extends NPC
class_name NPCOldMan
## NPC Old Man - 老人 NPC

func _setup_entity() -> void:
	super._setup_entity()
	entity_name = "Old Man"
	speed = 32.0
	can_move = true
	_setup_animations()


func _setup_animations() -> void:
	"""設定老人動畫"""
	var sprite_frames = SpriteFrames.new()
	
	# Walk 動畫
	_add_anim(sprite_frames, "walk_down", [
		"res://resources/npcs/oldman_down_1.png",
		"res://resources/npcs/oldman_down_2.png"
	])
	_add_anim(sprite_frames, "walk_up", [
		"res://resources/npcs/oldman_up_1.png",
		"res://resources/npcs/oldman_up_2.png"
	])
	_add_anim(sprite_frames, "walk_left", [
		"res://resources/npcs/oldman_left_1.png",
		"res://resources/npcs/oldman_left_2.png"
	])
	_add_anim(sprite_frames, "walk_right", [
		"res://resources/npcs/oldman_right_1.png",
		"res://resources/npcs/oldman_right_2.png"
	])
	
	# Idle 動畫 (使用第一幀)
	_add_anim(sprite_frames, "idle_down", ["res://resources/npcs/oldman_down_1.png"])
	_add_anim(sprite_frames, "idle_up", ["res://resources/npcs/oldman_up_1.png"])
	_add_anim(sprite_frames, "idle_left", ["res://resources/npcs/oldman_left_1.png"])
	_add_anim(sprite_frames, "idle_right", ["res://resources/npcs/oldman_right_1.png"])
	
	if sprite_frames.has_animation("default"):
		sprite_frames.remove_animation("default")
	
	animated_sprite.sprite_frames = sprite_frames
	animated_sprite.play("idle_down")


func _add_anim(sf: SpriteFrames, anim_name: String, paths: Array) -> void:
	sf.add_animation(anim_name)
	sf.set_animation_speed(anim_name, 5.0)
	sf.set_animation_loop(anim_name, true)
	for path in paths:
		var tex = load(path) as Texture2D
		if tex:
			sf.add_frame(anim_name, tex)
