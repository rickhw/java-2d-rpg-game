extends Node
# Constants.gd
# Game constants matching Java version values

# Tile and Screen Settings (from Java GamePanel.java)
const ORIGINAL_TILE_SIZE: int = 16
const SCALE: int = 4
const TILE_SIZE: int = ORIGINAL_TILE_SIZE * SCALE  # 64
const SCREEN_COLS: int = 20  # maxScreenCol
const SCREEN_ROWS: int = 12  # maxScreenRow
const SCREEN_WIDTH: int = TILE_SIZE * SCREEN_COLS  # 1280
const SCREEN_HEIGHT: int = TILE_SIZE * SCREEN_ROWS  # 768

# World Settings
const WORLD_COLS: int = 50  # maxWorldCol
const WORLD_ROWS: int = 50  # maxWorldRow
const MAX_MAP: int = 10

# Game Settings
const FPS: int = 60

# Player Settings (from Java Player.java)
const PLAYER_STANDING_ANIMATION_SPEED: int = 30
const PLAYER_WALKING_ANIMATION_SPEED: int = 12

# Areas (from Java GamePanel.java)
enum Area {
	OUTSIDE = 0,
	INDOOR = 1,
	DUNGEON = 2
}

# Game States (from Java GameState.java)
enum GameState {
	TITLE = 0,      # Title Screen
	PLAY = 1,       # Play
	PAUSE = 2,      # Pause
	DIALOGUE = 3,   # Dialogue
	CHARACTER = 4,  # Character
	OPTIONS = 5,    # Options
	GAME_OVER = 6,  # GameOver
	TRANSITION = 7, # Transition
	TRADE = 8,      # Trade
	SLEEP = 9,      # Sleep
	DISPLAY_MAP = 10,  # Map
	CUTSENSE = 11   # Cutsense
}

# Day States (from Java DayState.java)
enum DayState {
	DAY = 0,    # Day
	DUSK = 1,   # Dusk (黃昏)
	NIGHT = 2,  # Night
	DAWN = 3    # Dawn (黎明)
}

# Entity Types (from Java EntityType.java)
enum EntityType {
	PLAYER,
	NPC,
	MONSTER,
	SWORD,
	AXE,
	SHIELD,
	CONSUMABLE,
	PICKUPONLY,
	OBSTACLE,
	LIGHT,
	PICKAXE
}

# Directions (from Java Direction.java)
enum Direction {
	UP,
	DOWN,
	LEFT,
	RIGHT,
	ANY
}

# Map IDs (from Java Map.java)
enum MapID {
	WORLD_MAP = 0,    # World1
	STORE = 1,        # Store (室內)
	DUNGEON01 = 2,    # Dungeon01 (地下城)
	DUNGEON02 = 3     # Dungeon02 (地下城)
}

# Sound IDs (from Java Sound.java)
enum MusicID {
	MAIN_THEME = 1,
	FANFARE = 2,
	MERCHANT = 3,
	DUNGEON = 4,
	FINAL_BATTLE = 5
}

enum SoundFX {
	COIN = 10,
	POWER_UP = 11,
	UNLOCK = 12,
	HIT_MONSTER = 13,
	RECEIVE_DAMAGE = 14,
	SWING_WEAPON = 15,
	LEVEL_UP = 16,
	CURSOR = 17,
	BURNING = 18,
	CUT_TREE = 19,
	GAME_OVER = 20,
	STAIRS = 21,
	SLEEP = 22,
	BLOCKED = 23,
	PARRY = 24,
	SPEAK = 25,
	DOOR_OPEN = 26,
	CHIP_WALL = 27
}
