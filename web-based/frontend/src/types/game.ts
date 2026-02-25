// === Core Game Types ===

export type Direction = 'UP' | 'DOWN' | 'LEFT' | 'RIGHT' | 'ANY';
export type GameStateType = 'TITLE' | 'PLAY' | 'PAUSE' | 'DIALOGUE' | 'CHARACTER' |
    'OPTIONS' | 'GAME_OVER' | 'TRANSITION' | 'TRADE' | 'SLEEP' | 'DISPLAY_MAP' | 'CUTSENSE';
export type DayStateType = 'DAY' | 'DUSK' | 'NIGHT' | 'DAWN';
export type AreaType = 'OUTSIDE' | 'INDOOR' | 'DUNGEON';
export type EntityTypeName = 'PLAYER' | 'NPC' | 'MONSTER' | 'SWORD' | 'AXE' | 'PICKAXE' |
    'SHIELD' | 'LIGHT' | 'SHOE' | 'CONSUMABLE' | 'PICKUPONLY' | 'OBSTACLE';

export interface EntityState {
    id: string;
    name: string;
    type: EntityTypeName;
    worldX: number;
    worldY: number;
    direction: Direction;
    spriteNum: 1 | 2;
    life?: number;
    maxLife?: number;
    mana?: number;
    maxMana?: number;
    level?: number;
    exp?: number;
    coin?: number;
    attacking?: boolean;
    guarding?: boolean;
    invincible?: boolean;
    alive?: boolean;
    dying?: boolean;
    spriteKey?: string;
    solidAreaX?: number;
    solidAreaY?: number;
    solidAreaWidth?: number;
    solidAreaHeight?: number;
}

export interface PlayerState extends EntityState {
    mana: number;
    maxMana: number;
    level: number;
    exp: number;
    coin: number;
    attacking: boolean;
    guarding: boolean;
    invincible: boolean;
    currentWeapon?: string;
    currentShield?: string;
    inventory?: InventoryItem[];
}

export interface InventoryItem {
    name: string;
    amount: number;
    equipped?: boolean;
}

export interface MapData {
    mapId: string;
    name: string;
    maxCol: number;
    maxRow: number;
    tileGrid: number[][];
}

export interface TileInfo {
    index: number;
    fileName: string;
    collision: boolean;
}

export interface NpcState {
    id: string;
    name?: string;
    worldX: number;
    worldY: number;
    direction: Direction;
    spriteNum: 1 | 2;
    spriteKey: string;
}

export interface DialogueState {
    speaker: string;
    line: string;
    hasNext: boolean;
}

export interface GameFullState {
    type: 'FULL_STATE' | 'DELTA_STATE';
    sessionId: string;
    gameState: GameStateType;
    currentMap: string;
    currentArea: AreaType;
    dayState: DayStateType;
    player: PlayerState;
    mapData: MapData;
    npcs?: NpcState[];
    dialogue?: DialogueState;
    entities?: EntityState[];
    projectiles?: EntityState[];
    particles?: any[];
}

export interface ClientMessage {
    type: string;
    [key: string]: any;
}

// Game constants (matching backend)
export const TILE_SIZE = 16;
export const SCALE = 4;
export const EFFECTIVE_TILE_SIZE = TILE_SIZE * SCALE; // 64
export const SCREEN_COLS = 20;
export const SCREEN_ROWS = 12;
export const SCREEN_WIDTH = EFFECTIVE_TILE_SIZE * SCREEN_COLS;   // 1280
export const SCREEN_HEIGHT = EFFECTIVE_TILE_SIZE * SCREEN_ROWS;  // 768
