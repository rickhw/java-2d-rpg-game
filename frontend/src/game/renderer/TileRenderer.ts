import { AssetLoader } from '../assets/AssetLoader';
import type {
    GameFullState,
} from '../../types/game';
import {
    EFFECTIVE_TILE_SIZE,
    SCREEN_WIDTH,
    SCREEN_HEIGHT,
} from '../../types/game';

/**
 * Renders the game world onto an HTML5 Canvas.
 * Handles tile rendering, entity sprites, camera, and visual effects.
 */
export class TileRenderer {
    private ctx: CanvasRenderingContext2D;
    private assetLoader: AssetLoader;

    constructor(ctx: CanvasRenderingContext2D, assetLoader: AssetLoader) {
        this.ctx = ctx;
        this.assetLoader = assetLoader;
    }

    /**
     * Draw the tile map visible in the camera viewport.
     */
    drawTiles(state: GameFullState) {
        if (!state.mapData || !state.player) return;

        const { tileGrid, maxCol, maxRow } = state.mapData;
        const player = state.player;

        // Camera: center on player
        const playerScreenX = SCREEN_WIDTH / 2 - EFFECTIVE_TILE_SIZE / 2;
        const playerScreenY = SCREEN_HEIGHT / 2 - EFFECTIVE_TILE_SIZE / 2;

        for (let worldCol = 0; worldCol < maxCol; worldCol++) {
            for (let worldRow = 0; worldRow < maxRow; worldRow++) {
                const tileNum = tileGrid[worldCol]?.[worldRow];
                if (tileNum === undefined || tileNum < 0) continue;

                // Calculate screen position relative to camera
                const worldX = worldCol * EFFECTIVE_TILE_SIZE;
                const worldY = worldRow * EFFECTIVE_TILE_SIZE;
                const screenX = worldX - player.worldX + playerScreenX;
                const screenY = worldY - player.worldY + playerScreenY;

                // Only draw tiles visible on screen (with one tile margin)
                if (
                    screenX + EFFECTIVE_TILE_SIZE > -EFFECTIVE_TILE_SIZE &&
                    screenX < SCREEN_WIDTH + EFFECTIVE_TILE_SIZE &&
                    screenY + EFFECTIVE_TILE_SIZE > -EFFECTIVE_TILE_SIZE &&
                    screenY < SCREEN_HEIGHT + EFFECTIVE_TILE_SIZE
                ) {
                    const img = this.assetLoader.getTileImage(tileNum);
                    if (img) {
                        this.ctx.drawImage(
                            img,
                            screenX,
                            screenY,
                            EFFECTIVE_TILE_SIZE,
                            EFFECTIVE_TILE_SIZE
                        );
                    }
                }
            }
        }
    }

    /**
     * Draw the player sprite.
     */
    drawPlayer(state: GameFullState) {
        if (!state.player) return;

        const player = state.player;
        const dir = player.direction.toLowerCase();
        const num = player.spriteNum;

        let spriteKey: string;

        if (player.guarding) {
            spriteKey = `player_guard_${dir}`;
        } else if (player.attacking) {
            spriteKey = `player_attack_sword_${dir}_${num}`;
        } else {
            spriteKey = `player_${dir}_${num}`;
        }

        const img = this.assetLoader.getSpriteImage(spriteKey);

        // Player is always at screen center
        const screenX = SCREEN_WIDTH / 2 - EFFECTIVE_TILE_SIZE / 2;
        const screenY = SCREEN_HEIGHT / 2 - EFFECTIVE_TILE_SIZE / 2;

        if (img) {
            // Invincible flicker effect
            if (player.invincible) {
                this.ctx.globalAlpha = 0.4;
            }

            this.ctx.drawImage(img, screenX, screenY, EFFECTIVE_TILE_SIZE, EFFECTIVE_TILE_SIZE);
            this.ctx.globalAlpha = 1.0;
        } else {
            // Fallback: draw a colored rectangle
            this.ctx.fillStyle = '#4488ff';
            this.ctx.fillRect(screenX + 8, screenY + 8, EFFECTIVE_TILE_SIZE - 16, EFFECTIVE_TILE_SIZE - 16);
        }
    }
}
