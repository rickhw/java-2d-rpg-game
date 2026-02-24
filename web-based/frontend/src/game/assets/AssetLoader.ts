import type { TileInfo } from '../../types/game';

/**
 * Loads and manages game assets (tile images, sprite sheets, sounds).
 */
export class AssetLoader {
    private tileImages: Map<number, HTMLImageElement> = new Map();
    private spriteImages: Map<string, HTMLImageElement> = new Map();
    private loaded = false;

    /**
     * Load all tile images from the backend.
     */
    async loadTiles(tileData: TileInfo[]): Promise<void> {
        const promises = tileData.map((tile) => {
            return new Promise<void>((resolve) => {
                const img = new Image();
                img.onload = () => {
                    this.tileImages.set(tile.index, img);
                    resolve();
                };
                img.onerror = () => {
                    console.warn('[AssetLoader] Failed to load tile: ' + tile.fileName);
                    resolve(); // Don't fail the whole load
                };
                img.src = '/api/assets/tiles/images/' + tile.fileName;
            });
        });

        await Promise.all(promises);
        console.log('[AssetLoader] Loaded ' + this.tileImages.size + '/' + tileData.length + ' tile images');
    }

    /**
     * Load a single sprite image.
     */
    async loadSprite(key: string, path: string): Promise<HTMLImageElement> {
        return new Promise((resolve, reject) => {
            const img = new Image();
            img.onload = () => {
                this.spriteImages.set(key, img);
                resolve(img);
            };
            img.onerror = () => {
                reject(new Error('Failed to load sprite: ' + path));
            };
            img.src = path;
        });
    }

    /**
     * Load all player sprites.
     * File structure: assets/sprites/player/walking/boy_{dir}_{num}.png
     *                 assets/sprites/player/attacking/{weapon}/boy_attack_{dir}_{num}.png
     *                 assets/sprites/player/guarding/boy_guard_{dir}.png
     */
    async loadPlayerSprites(): Promise<void> {
        const directions = ['up', 'down', 'left', 'right'];

        // Walking sprites
        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = 'player_' + dir + '_' + i;
                const path = '/api/assets/sprites/player/walking/boy_' + dir + '_' + i + '.png';
                try {
                    await this.loadSprite(key, path);
                } catch (_e) {
                    console.warn('[AssetLoader] Missing player walking sprite: ' + key);
                }
            }
        }

        // Sword attack sprites (boy_attack_{dir}_{num})
        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = 'player_attack_sword_' + dir + '_' + i;
                const path = '/api/assets/sprites/player/attacking/sword/boy_attack_' + dir + '_' + i + '.png';
                try {
                    await this.loadSprite(key, path);
                } catch (_e) {
                    // OK - may not exist
                }
            }
        }

        // Axe attack sprites (boy_axe_{dir}_{num})
        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = 'player_attack_axe_' + dir + '_' + i;
                const path = '/api/assets/sprites/player/attacking/axe/boy_axe_' + dir + '_' + i + '.png';
                try {
                    await this.loadSprite(key, path);
                } catch (_e) {
                    // OK
                }
            }
        }

        // Guard sprites (boy_guard_{dir})
        for (const dir of directions) {
            const key = 'player_guard_' + dir;
            const path = '/api/assets/sprites/player/guarding/boy_guard_' + dir + '.png';
            try {
                await this.loadSprite(key, path);
            } catch (_e) {
                // OK
            }
        }

        console.log('[AssetLoader] Loaded ' + this.spriteImages.size + ' sprite images');
    }

    /**
     * Load monster sprites.
     */
    async loadMonsterSprites(monsterName: string): Promise<void> {
        const directions = ['up', 'down', 'left', 'right'];
        const basePath = '/api/assets/sprites/monster/' + monsterName + '/walking';

        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = monsterName + '_' + dir + '_' + i;
                const path = basePath + '/' + monsterName + '_' + dir + '_' + i + '.png';
                try {
                    await this.loadSprite(key, path);
                } catch (_e) {
                    // OK
                }
            }
        }
    }

    getTileImage(index: number): HTMLImageElement | undefined {
        return this.tileImages.get(index);
    }

    getSpriteImage(key: string): HTMLImageElement | undefined {
        return this.spriteImages.get(key);
    }

    isLoaded(): boolean {
        return this.loaded;
    }

    setLoaded(loaded: boolean) {
        this.loaded = loaded;
    }
}
