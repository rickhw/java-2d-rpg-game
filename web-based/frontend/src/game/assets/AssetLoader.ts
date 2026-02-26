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
        const promises: Promise<any>[] = [];

        // Walking sprites
        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = 'player_' + dir + '_' + i;
                const path = '/api/assets/sprites/player/walking/boy_' + dir + '_' + i + '.png';
                promises.push(this.loadSprite(key, path).catch(() => {
                    console.warn('[AssetLoader] Missing player walking sprite: ' + key);
                }));
            }
        }

        // Sword attack sprites (boy_attack_{dir}_{num})
        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = 'player_attack_sword_' + dir + '_' + i;
                const path = '/api/assets/sprites/player/attacking/sword/boy_attack_' + dir + '_' + i + '.png';
                promises.push(this.loadSprite(key, path).catch(() => { }));
            }
        }

        // Axe attack sprites (boy_axe_{dir}_{num})
        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = 'player_attack_axe_' + dir + '_' + i;
                const path = '/api/assets/sprites/player/attacking/axe/boy_axe_' + dir + '_' + i + '.png';
                promises.push(this.loadSprite(key, path).catch(() => { }));
            }
        }

        // Guard sprites (boy_guard_{dir})
        for (const dir of directions) {
            const key = 'player_guard_' + dir;
            const path = '/api/assets/sprites/player/guarding/boy_guard_' + dir + '.png';
            promises.push(this.loadSprite(key, path).catch(() => { }));
        }

        await Promise.all(promises);
        console.log('[AssetLoader] Loaded ' + this.spriteImages.size + ' sprite images');
    }

    /**
     * Load NPC sprites.
     * File structure: assets/sprites/npc/{npcName}_{dir}_{num}.png
     */
    async loadNpcSprites(): Promise<void> {
        const npcNames = ['oldman'];
        const directions = ['up', 'down', 'left', 'right'];
        const promises: Promise<any>[] = [];

        for (const npcName of npcNames) {
            for (const dir of directions) {
                for (let i = 1; i <= 2; i++) {
                    const key = npcName + '_' + dir + '_' + i;
                    const path = '/api/assets/sprites/npc/' + npcName + '_' + dir + '_' + i + '.png';
                    promises.push(this.loadSprite(key, path).catch(() => { }));
                }
            }
        }
        await Promise.all(promises);
        console.log('[AssetLoader] NPC sprites loaded');
    }

    /**
     * Load monster sprites.
     */
    async loadMonsterSprites(monsterName: string): Promise<void> {
        const directions = ['up', 'down', 'left', 'right'];
        const basePath = '/api/assets/sprites/monster/' + monsterName + '/walking';
        const promises: Promise<any>[] = [];

        for (const dir of directions) {
            for (let i = 1; i <= 2; i++) {
                const key = monsterName + '_' + dir + '_' + i;
                const path = basePath + '/' + monsterName + '_' + dir + '_' + i + '.png';
                promises.push(this.loadSprite(key, path).catch(() => { }));
            }
        }
        await Promise.all(promises);
    }

    /**
     * Load map object sprites.
     * File structure: assets/sprites/objects/{name}.png
     */
    async loadObjectSprites(): Promise<void> {
        const objectNames = [
            'door', 'door_iron', 'chest', 'chest_opened',
            'key', 'axe', 'pickaxe', 'sword_normal',
            'shield_blue', 'shield_wood', 'lantern', 'tent',
            'potion_red', 'blueheart', 'coin_bronze',
            'heart_full', 'heart_half', 'heart_blank',
            'manacrystal_full', 'manacrystal_blank',
            'boots'
        ];
        const promises: Promise<any>[] = [];

        for (const name of objectNames) {
            const key = 'obj_' + name;
            const path = '/api/assets/sprites/objects/' + name + '.png';
            promises.push(this.loadSprite(key, path).catch(() => { }));
        }
        await Promise.all(promises);
        console.log('[AssetLoader] Object sprites loaded');
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
