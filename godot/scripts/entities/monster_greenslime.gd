extends Monster
class_name MonsterGreenSlime
## Green Slime - 綠色史萊姆

func _setup_entity() -> void:
	super._setup_entity()
	entity_name = "Green Slime"
	max_life = 4
	life = max_life
	speed = 48.0
	attack_value = 1
	exp_reward = 2
	aggro_range = 4
	action_interval = 1.5
	_setup_animations()


func _setup_animations() -> void:
	"""設定史萊姆動畫 (只有一個方向)"""
	var sprite_frames = SpriteFrames.new()
	
	# 史萊姆只有一個方向的動畫，所有方向都使用相同的
	var paths = [
		"res://resources/monsters/greenslime/walking/greenslime_down_1.png",
		"res://resources/monsters/greenslime/walking/greenslime_down_2.png"
	]
	
	for dir in ["down", "up", "left", "right"]:
		var anim_name = "walk_" + dir
		sprite_frames.add_animation(anim_name)
		sprite_frames.set_animation_speed(anim_name, 5.0)
		sprite_frames.set_animation_loop(anim_name, true)
		for path in paths:
			var tex = load(path) as Texture2D
			if tex:
				sprite_frames.add_frame(anim_name, tex)
	
	if sprite_frames.has_animation("default"):
		sprite_frames.remove_animation("default")
	
	animated_sprite.sprite_frames = sprite_frames
	animated_sprite.play("walk_down")
