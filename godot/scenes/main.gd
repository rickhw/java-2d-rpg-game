extends Node2D
## Main - 主場景腳本
## 測試場景，驗證專案設置是否正確

func _ready() -> void:
	print("=== M/A Legend - Godot 4 Port ===")
	print("[Main] 主場景載入完成")
	print("[Main] 遊戲狀態: %s" % GameManager.GameState.keys()[GameManager.current_state])
	print("[Main] 當前區域: %s" % GameManager.Area.keys()[GameManager.current_area - 50])
	print("[Main] TileSize: %d" % GameManager.TILE_SIZE)
	
	# 測試音效管理器
	print("[Main] 測試音效管理器...")
	# AudioManager.play_bgm(AudioManager.BGM.MAIN_THEME)  # 暫時註解，等素材確認
	
	# 測試事件總線
	print("[Main] 測試事件總線...")
	EventBus.show_message("歡迎來到 M/A Legend！")
	
	print("=== 第一階段驗證完成 ===")

func _input(event: InputEvent) -> void:
	# 測試輸入映射
	if event.is_action_pressed("move_up"):
		print("[Main] 偵測到輸入: 向上移動 (W 或 ↑)")
	elif event.is_action_pressed("move_down"):
		print("[Main] 偵測到輸入: 向下移動 (S 或 ↓)")
	elif event.is_action_pressed("move_left"):
		print("[Main] 偵測到輸入: 向左移動 (A 或 ←)")
	elif event.is_action_pressed("move_right"):
		print("[Main] 偵測到輸入: 向右移動 (D 或 →)")
	elif event.is_action_pressed("attack"):
		print("[Main] 偵測到輸入: 攻擊 (Enter)")
	elif event.is_action_pressed("shoot"):
		print("[Main] 偵測到輸入: 發射 (F 或 J)")
	elif event.is_action_pressed("guard"):
		print("[Main] 偵測到輸入: 格擋 (Space)")
	elif event.is_action_pressed("pause"):
		print("[Main] 偵測到輸入: 暫停 (P 或 Esc)")
		if GameManager.current_state == GameManager.GameState.PLAY:
			GameManager.change_state(GameManager.GameState.PAUSE)
		else:
			GameManager.change_state(GameManager.GameState.PLAY)
