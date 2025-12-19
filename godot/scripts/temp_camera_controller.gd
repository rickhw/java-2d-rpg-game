extends Camera2D
# Temporary camera controller for testing tilemap
# Will be replaced by player-following camera in Phase 3

var camera_speed: float = 400.0  # pixels per second
var zoom_speed: float = 0.1

func _ready():
	# Center camera at world center initially
	position = Vector2(1600, 1600)  # 50x50 tiles * 64 / 2
	zoom = Vector2(1.0, 1.0)

func _process(delta):
	# Arrow keys to move camera
	var direction = Vector2.ZERO
	
	if Input.is_action_pressed("ui_right"):
		direction.x += 1
	if Input.is_action_pressed("ui_left"):
		direction.x -= 1
	if Input.is_action_pressed("ui_down"):
		direction.y += 1
	if Input.is_action_pressed("ui_up"):
		direction.y -= 1
	
	# Move camera
	position += direction.normalized() * camera_speed * delta
	
	# Zoom in/out with Q/E
	if Input.is_action_just_pressed("ui_page_up"):  # Q key
		zoom += Vector2(zoom_speed, zoom_speed)
		zoom = zoom.clamp(Vector2(0.3, 0.3), Vector2(3.0, 3.0))
	
	if Input.is_action_just_pressed("ui_page_down"):  # E key
		zoom -= Vector2(zoom_speed, zoom_speed)
		zoom = zoom.clamp(Vector2(0.3, 0.3), Vector2(3.0, 3.0))
	
	# Reset camera with R key
	if Input.is_key_pressed(KEY_R):
		position = Vector2(1600, 1600)
		zoom = Vector2(1.0, 1.0)
