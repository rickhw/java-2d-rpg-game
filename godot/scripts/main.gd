extends Node2D
## Main 場景腳本 - 遊戲入口點

# 標題畫面選單項目
var menu_items: Array[Label] = []
var current_menu_index: int = 0
var title_screen: Control

func _ready() -> void:
	print("[Main] Game started")
	
	# 獲取標題畫面節點
	title_screen = $CanvasLayer/TitleScreen
	
	# 獲取選單項目
	var menu_container = $CanvasLayer/TitleScreen/MenuContainer
	for child in menu_container.get_children():
		if child is Label:
			menu_items.append(child)
	
	# 設定初始選單狀態
	update_menu_selection()
	
	# 播放標題畫面 BGM
	AudioManager.play_bgm("adventure")


func _process(_delta: float) -> void:
	# 只在標題畫面處理輸入
	if GameManager.current_state != GameManager.GameState.TITLE:
		return
	
	# 選單導航
	if Input.is_action_just_pressed("move_up"):
		current_menu_index = (current_menu_index - 1 + menu_items.size()) % menu_items.size()
		update_menu_selection()
		AudioManager.play_sfx("cursor")
	
	if Input.is_action_just_pressed("move_down"):
		current_menu_index = (current_menu_index + 1) % menu_items.size()
		update_menu_selection()
		AudioManager.play_sfx("cursor")
	
	# 確認選擇
	if Input.is_action_just_pressed("confirm"):
		select_menu_item()


func update_menu_selection() -> void:
	for i in menu_items.size():
		var label = menu_items[i]
		if i == current_menu_index:
			label.text = "> " + get_menu_text(i)
		else:
			label.text = "  " + get_menu_text(i)


func get_menu_text(index: int) -> String:
	match index:
		0: return "NEW GAME"
		1: return "LOAD GAME"
		2: return "QUIT"
		_: return ""


func select_menu_item() -> void:
	match current_menu_index:
		0:  # New Game
			start_new_game()
		1:  # Load Game
			load_game()
		2:  # Quit
			quit_game()


func start_new_game() -> void:
	print("[Main] Starting new game...")
	AudioManager.play_sfx("power_up")
	
	# 隱藏標題畫面
	title_screen.visible = false
	
	# 切換到遊玩狀態
	GameManager.change_state(GameManager.GameState.PLAY)
	
	# 載入世界地圖場景
	var world_map_scene = load("res://scenes/maps/world_map.tscn")
	var world_map = world_map_scene.instantiate()
	add_child(world_map)
	
	print("[Main] World map loaded")


func load_game() -> void:
	print("[Main] Load game - Not implemented yet")
	AudioManager.play_sfx("blocked")


func quit_game() -> void:
	print("[Main] Quitting game...")
	get_tree().quit()
