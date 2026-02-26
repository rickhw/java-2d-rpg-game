import React, { useRef, useEffect } from 'react';
import { WebSocketClient } from '../game/network/WebSocketClient';
import { AssetLoader } from '../game/assets/AssetLoader';
import { KeyboardInput } from '../game/input/KeyboardInput';
import { TileRenderer } from '../game/renderer/TileRenderer';
import type {
    GameFullState,
    TileInfo,
    NpcState,
} from '../types/game';
import {
    SCREEN_WIDTH,
    SCREEN_HEIGHT,
    EFFECTIVE_TILE_SIZE,
} from '../types/game';

/**
 * Main game canvas component.
 *
 * ZERO React re-renders during gameplay:
 * - All game state is stored in refs, never in React state
 * - The 60 FPS render loop reads refs directly
 * - WebSocket messages update refs without triggering React rendering
 */
const GameCanvas: React.FC = () => {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const gameStateRef = useRef<GameFullState | null>(null);
    const animFrameRef = useRef<number>(0);
    const connectedRef = useRef(false);
    const debugRef = useRef(false);
    const fpsRef = useRef({ frames: 0, lastTime: performance.now(), fps: 0 });

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        if (!ctx) return;
        ctx.imageSmoothingEnabled = false;

        const assetLoader = new AssetLoader();
        const renderer = new TileRenderer(ctx, assetLoader);

        // --- WebSocket client ---
        const wsClient = new WebSocketClient(
            '/ws/game',
            (state) => {
                const current = gameStateRef.current;
                if (state.type === 'FULL_STATE') {
                    // Full state includes mapData, currentMap, player, etc.
                    gameStateRef.current = state;
                } else if (state.type === 'DELTA_STATE' && current) {
                    // Merge delta into existing full state (mapData preserved)
                    const merged: GameFullState = {
                        ...current,
                        gameState: state.gameState ?? current.gameState,
                        inventoryRow: state.inventoryRow ?? current.inventoryRow,
                        inventoryCol: state.inventoryCol ?? current.inventoryCol
                    };
                    if (state.player && current.player) {
                        merged.player = { ...current.player, ...state.player };
                    }
                    // Merge NPCs and dialogue from delta
                    if (state.npcs) {
                        merged.npcs = state.npcs;
                    }
                    merged.dialogue = state.dialogue ?? undefined;
                    merged.transitionProgress = (state as any).transitionProgress;
                    if (state.mapObjects) {
                        merged.mapObjects = state.mapObjects;
                    }
                    if (state.inventory) {
                        merged.inventory = state.inventory;
                    }
                    gameStateRef.current = merged;
                }
                // Ignore DELTA_STATE if no current state yet (wait for FULL_STATE)
            },
            () => { connectedRef.current = true; },
            () => { connectedRef.current = false; }
        );

        // --- Keyboard input ---
        const keyboard = new KeyboardInput(wsClient);
        keyboard.attach(document.body);

        // Debug toggle (T key) — handled client-side only
        const onDebugKey = (e: KeyboardEvent) => {
            if (e.code === 'KeyT' && !e.repeat) {
                debugRef.current = !debugRef.current;
                console.log('[Debug] Mode:', debugRef.current ? 'ON' : 'OFF');
            }
        };
        window.addEventListener('keydown', onDebugKey);

        // --- 60 FPS render loop ---
        const renderLoop = () => {
            const state = gameStateRef.current;

            ctx.fillStyle = '#000000';
            ctx.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            if (state) {
                if (state.gameState === 'TITLE') {
                    drawTitleScreen(ctx);
                } else if (state.gameState === 'PLAY' || state.gameState === 'CHARACTER' || state.gameState === 'DIALOGUE') {
                    renderer.drawTiles(state);
                    renderer.drawObjects(state);
                    renderer.drawNPCs(state);
                    renderer.drawPlayer(state);
                    if (debugRef.current) {
                        drawDebugOverlay(ctx, state);
                    }
                    drawHUD(ctx, state);

                    if (state.dialogue || state.gameState === 'DIALOGUE') {
                        // Backend actually sets PLAY when dialogue happens but we leave the fallback.
                        if (state.dialogue) {
                            drawDialogueBox(ctx, state.dialogue.speaker, state.dialogue.line, state.dialogue.hasNext);
                        }
                    }

                    if (state.gameState === 'CHARACTER') {
                        drawInventory(ctx, state, assetLoader);
                    }
                } else if (state.gameState === 'TRANSITION') {
                    // Draw current scene underneath
                    renderer.drawTiles(state);
                    renderer.drawObjects(state);
                    renderer.drawNPCs(state);
                    renderer.drawPlayer(state);
                    drawHUD(ctx, state);

                    // Fade overlay: 0→1 (fade out) then 1→0 (fade in)
                    const progress = state.transitionProgress ?? 0;
                    const alpha = progress <= 0.5
                        ? progress * 2       // 0 → 1 during first half
                        : (1 - progress) * 2; // 1 → 0 during second half
                    ctx.fillStyle = 'rgba(0, 0, 0, ' + Math.min(1, Math.max(0, alpha)) + ')';
                    ctx.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                }
            } else {
                // Waiting for connection / state
                ctx.fillStyle = '#111';
                ctx.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                ctx.fillStyle = '#888';
                ctx.font = '24px monospace';
                ctx.textAlign = 'center';
                ctx.fillText(
                    connectedRef.current ? 'Loading...' : 'Connecting...',
                    SCREEN_WIDTH / 2,
                    SCREEN_HEIGHT / 2
                );
            }

            // FPS counter
            const now = performance.now();
            fpsRef.current.frames++;
            if (now - fpsRef.current.lastTime >= 1000) {
                fpsRef.current.fps = fpsRef.current.frames;
                fpsRef.current.frames = 0;
                fpsRef.current.lastTime = now;
            }

            animFrameRef.current = requestAnimationFrame(renderLoop);
        };

        // --- Load assets then connect ---
        loadAssets(assetLoader).then(() => {
            wsClient.connect();
            animFrameRef.current = requestAnimationFrame(renderLoop);
        });

        // --- Canvas click (Inventory interaction) ---
        const onCanvasClick = (e: MouseEvent) => {
            const state = gameStateRef.current;
            if (!state || state.gameState !== 'CHARACTER' || !state.inventory) return;

            const rect = canvas.getBoundingClientRect();
            // Calculate scale in case canvas is scaled via CSS
            const scaleX = SCREEN_WIDTH / rect.width;
            const scaleY = SCREEN_HEIGHT / rect.height;

            const mouseX = (e.clientX - rect.left) * scaleX;
            const mouseY = (e.clientY - rect.top) * scaleY;

            // Inventory coordinates
            const panelW = 320;
            const panelH = 280;
            const panelX = (SCREEN_WIDTH - panelW) / 2;
            const panelY = (SCREEN_HEIGHT - panelH) / 2;
            const cols = 5;
            const cellSize = 52;
            const gridStartX = panelX + (panelW - cols * cellSize) / 2;
            const gridStartY = panelY + 60;

            // Check if clicked inside a slot
            for (let i = 0; i < state.inventory.length; i++) {
                const col = i % cols;
                const row = Math.floor(i / cols);
                const cx = gridStartX + col * cellSize;
                const cy = gridStartY + row * cellSize;

                if (mouseX >= cx && mouseX <= cx + cellSize &&
                    mouseY >= cy && mouseY <= cy + cellSize) {
                    // Send equip/use action
                    wsClient.send({ type: 'INVENTORY_ACTION', slot: i });
                    break;
                }
            }
        };
        canvas.addEventListener('mousedown', onCanvasClick);

        // --- Cleanup on unmount ---
        return () => {
            canvas.removeEventListener('mousedown', onCanvasClick);
            cancelAnimationFrame(animFrameRef.current);
            keyboard.detach();
            window.removeEventListener('keydown', onDebugKey);
            wsClient.disconnect();
        };
    }, []); // Empty deps = run once

    return (
        <canvas
            ref={canvasRef}
            width={SCREEN_WIDTH}
            height={SCREEN_HEIGHT}
            style={{
                display: 'block',
                imageRendering: 'pixelated',
                maxWidth: '100%',
                height: 'auto',
            }}
            tabIndex={0}
        />
    );
};

// ==================== Helper Functions ====================

async function loadAssets(assetLoader: AssetLoader) {
    try {
        const response = await fetch('/api/assets/tiles');
        const data = await response.json();
        if (data.tiles) {
            await assetLoader.loadTiles(data.tiles as TileInfo[]);
        }
        await assetLoader.loadPlayerSprites();
        await assetLoader.loadNpcSprites();
        await assetLoader.loadObjectSprites();
        assetLoader.setLoaded(true);
        console.log('[GameCanvas] Assets loaded');
    } catch (e) {
        console.error('[GameCanvas] Failed to load assets:', e);
    }
}

function drawTitleScreen(ctx: CanvasRenderingContext2D) {
    ctx.fillStyle = '#0a0a2e';
    ctx.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    // Stars
    for (let i = 0; i < 60; i++) {
        const x = (Math.sin(i * 127.1 + Date.now() * 0.0001 * (i % 3 + 1)) * 0.5 + 0.5) * SCREEN_WIDTH;
        const y = (Math.cos(i * 311.7 + Date.now() * 0.00005 * (i % 2 + 1)) * 0.5 + 0.5) * SCREEN_HEIGHT;
        const alpha = Math.sin(Date.now() * 0.003 + i) * 0.3 + 0.7;
        const size = (i % 3 === 0) ? 3 : 2;
        ctx.fillStyle = 'rgba(255, 255, 220, ' + alpha + ')';
        ctx.fillRect(x, y, size, size);
    }

    // Title
    ctx.textAlign = 'center';
    ctx.fillStyle = '#ffd700';
    ctx.font = 'bold 56px "Courier New", monospace';
    ctx.shadowColor = '#ff8800';
    ctx.shadowBlur = 20;
    ctx.fillText('Blue Boy Adventure', SCREEN_WIDTH / 2, SCREEN_HEIGHT / 3);
    ctx.shadowBlur = 0;

    ctx.fillStyle = '#aabbcc';
    ctx.font = '20px "Courier New", monospace';
    ctx.fillText('A 2D RPG Game', SCREEN_WIDTH / 2, SCREEN_HEIGHT / 3 + 40);

    // Menu
    const menuY = SCREEN_HEIGHT / 2 + 40;
    ctx.font = '28px "Courier New", monospace';
    const pulse = Math.sin(Date.now() * 0.004) * 0.2 + 0.8;
    ctx.fillStyle = 'rgba(255, 255, 255, ' + pulse + ')';
    ctx.fillText('\u25b6 NEW GAME', SCREEN_WIDTH / 2, menuY);
    ctx.fillStyle = '#888';
    ctx.fillText('  LOAD GAME', SCREEN_WIDTH / 2, menuY + 40);
    ctx.fillText('  QUIT', SCREEN_WIDTH / 2, menuY + 80);

    // Instructions
    ctx.fillStyle = '#556';
    ctx.font = '16px "Courier New", monospace';
    ctx.fillText('Press ENTER to start', SCREEN_WIDTH / 2, SCREEN_HEIGHT - 80);
    ctx.fillText('WASD / Arrow Keys to move | ENTER to attack | SPACE to guard', SCREEN_WIDTH / 2, SCREEN_HEIGHT - 55);

    // Version (bottom-right)
    ctx.fillStyle = '#ffd700';
    ctx.font = 'bold 18px "Courier New", monospace';
    ctx.textAlign = 'right';
    ctx.fillText('v1.4.0 / 20260226', SCREEN_WIDTH - 40, SCREEN_HEIGHT - 20);
    ctx.textAlign = 'center';
}

/**
 * Draw the inventory panel overlay.
 */
function drawInventory(ctx: CanvasRenderingContext2D, state: GameFullState, assetLoader: AssetLoader) {
    const items = state.inventory;
    if (!items) return;

    const panelW = 320;
    const panelH = 280;
    const panelX = (SCREEN_WIDTH - panelW) / 2;
    const panelY = (SCREEN_HEIGHT - panelH) / 2;

    // Semi-transparent background
    ctx.fillStyle = 'rgba(10, 10, 40, 0.92)';
    ctx.fillRect(panelX, panelY, panelW, panelH);

    // Border
    ctx.strokeStyle = '#c8a832';
    ctx.lineWidth = 3;
    ctx.strokeRect(panelX + 2, panelY + 2, panelW - 4, panelH - 4);
    ctx.strokeStyle = '#8a7020';
    ctx.lineWidth = 1;
    ctx.strokeRect(panelX + 6, panelY + 6, panelW - 12, panelH - 12);

    // Title
    ctx.fillStyle = '#c8a832';
    ctx.font = 'bold 18px monospace';
    ctx.textAlign = 'center';
    ctx.fillText('INVENTORY', SCREEN_WIDTH / 2, panelY + 30);

    // Subtitle
    ctx.fillStyle = '#888';
    ctx.font = '12px monospace';
    ctx.fillText('Press I to close', SCREEN_WIDTH / 2, panelY + 48);

    if (items.length === 0) {
        ctx.fillStyle = '#666';
        ctx.font = '14px monospace';
        ctx.fillText('Empty', SCREEN_WIDTH / 2, panelY + panelH / 2 + 10);
        ctx.textAlign = 'left';
        return;
    }

    // Grid layout: 5 columns, 4 rows = 20 max slots
    const cols = 5;
    const maxSlots = 20;
    const cellSize = 52;
    const gridStartX = panelX + (panelW - cols * cellSize) / 2;
    const gridStartY = panelY + 60;

    ctx.textAlign = 'left';

    for (let i = 0; i < maxSlots; i++) {
        const col = i % cols;
        const row = Math.floor(i / cols);
        const cx = gridStartX + col * cellSize;
        const cy = gridStartY + row * cellSize;

        const isCursor = (col === state.inventoryCol && row === state.inventoryRow);
        const item = i < items.length ? items[i] : null;

        // Cell background
        ctx.fillStyle = (item && item.equipped) ? 'rgba(200, 168, 50, 0.25)' : 'rgba(255, 255, 255, 0.06)';
        if (isCursor) ctx.fillStyle = 'rgba(255, 255, 255, 0.2)';

        ctx.fillRect(cx + 2, cy + 2, cellSize - 4, cellSize - 4);
        ctx.strokeStyle = (item && item.equipped) ? 'rgba(255, 215, 0, 0.8)' : 'rgba(200, 168, 50, 0.3)';

        if (isCursor) {
            ctx.strokeStyle = '#fff';
            ctx.lineWidth = 2;
        } else {
            ctx.lineWidth = (item && item.equipped) ? 2 : 1;
        }
        ctx.strokeRect(cx + 2, cy + 2, cellSize - 4, cellSize - 4);

        if (item) {
            // Item sprite
            const spriteKey = 'obj_' + item.spriteKey;
            const img = assetLoader.getSpriteImage(spriteKey);
            if (img) {
                ctx.drawImage(img, cx + 6, cy + 4, 36, 36);
            } else {
                // Fallback
                ctx.fillStyle = '#ccaa22';
                ctx.fillRect(cx + 14, cy + 12, 20, 20);
            }

            // Quantity badge
            if (item.quantity > 1) {
                ctx.fillStyle = '#c8a832';
                ctx.font = 'bold 12px monospace';
                ctx.fillText('×' + item.quantity, cx + 28, cy + 48);
            }

            // Equipped badge "E"
            if (item.equipped) {
                ctx.fillStyle = '#ffd700';
                ctx.font = 'bold 10px monospace';
                ctx.fillText('E', cx + 40, cy + 14);
            }

            // Item name tooltip (small) when cursor is on it
            if (isCursor) {
                ctx.fillStyle = '#fff';
                ctx.font = '10px monospace';
                ctx.fillText(item.name, panelX + 20, panelY + panelH - 20);
            }
        }
    }

    ctx.textAlign = 'left';
}

function drawHUD(ctx: CanvasRenderingContext2D, state: GameFullState) {
    const player = state.player;
    if (!player) return;

    const hudX = 20;
    const hudY = 20;

    // Background
    ctx.fillStyle = 'rgba(0, 0, 0, 0.7)';
    ctx.fillRect(hudX - 8, hudY - 8, 230, 85);
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)';
    ctx.lineWidth = 1;
    ctx.strokeRect(hudX - 8, hudY - 8, 230, 85);

    // Health bar
    const life = player.life ?? 0;
    const maxLife = player.maxLife ?? 1;
    const hp = life / maxLife;
    ctx.fillStyle = '#222';
    ctx.fillRect(hudX + 40, hudY, 170, 18);
    ctx.fillStyle = hp > 0.5 ? '#27ae60' : hp > 0.25 ? '#f39c12' : '#e74c3c';
    ctx.fillRect(hudX + 40, hudY, 170 * hp, 18);
    ctx.strokeStyle = 'rgba(255,255,255,0.3)';
    ctx.strokeRect(hudX + 40, hudY, 170, 18);
    ctx.fillStyle = '#fff';
    ctx.font = 'bold 13px monospace';
    ctx.textAlign = 'left';
    ctx.shadowColor = '#000';
    ctx.shadowBlur = 2;
    ctx.fillText('HP', hudX, hudY + 14);
    ctx.font = '12px monospace';
    ctx.fillText(life + '/' + maxLife, hudX + 48, hudY + 14);
    ctx.shadowBlur = 0;

    // Mana bar
    const mana = player.mana ?? 0;
    const maxMana = player.maxMana ?? 1;
    const mp = mana / maxMana;
    ctx.fillStyle = '#222';
    ctx.fillRect(hudX + 40, hudY + 24, 170, 18);
    ctx.fillStyle = '#2980b9';
    ctx.fillRect(hudX + 40, hudY + 24, 170 * mp, 18);
    ctx.strokeStyle = 'rgba(255,255,255,0.3)';
    ctx.strokeRect(hudX + 40, hudY + 24, 170, 18);
    ctx.fillStyle = '#fff';
    ctx.font = 'bold 13px monospace';
    ctx.shadowColor = '#000';
    ctx.shadowBlur = 2;
    ctx.fillText('MP', hudX, hudY + 38);
    ctx.font = '12px monospace';
    ctx.fillText(mana + '/' + maxMana, hudX + 48, hudY + 38);
    ctx.shadowBlur = 0;

    // Level & Coin
    ctx.fillStyle = '#ffd700';
    ctx.font = 'bold 14px monospace';
    ctx.fillText('Lv.' + (player.level ?? 1), hudX, hudY + 62);
    ctx.fillStyle = '#f0c040';
    ctx.fillText('\u2B50 ' + (player.coin ?? 0), hudX + 90, hudY + 62);

    // Map info (top right)
    ctx.fillStyle = 'rgba(0,0,0,0.5)';
    ctx.fillRect(SCREEN_WIDTH - 200, 8, 190, 24);
    ctx.fillStyle = '#8899aa';
    ctx.font = '12px monospace';
    ctx.textAlign = 'right';
    ctx.fillText((state.currentMap || '') + ' | ' + (state.dayState || ''), SCREEN_WIDTH - 16, 25);
    ctx.textAlign = 'left';
}

function drawDialogueBox(ctx: CanvasRenderingContext2D, speaker: string, line: string, hasNext: boolean) {
    const boxX = 40;
    const boxY = SCREEN_HEIGHT - 220;
    const boxW = SCREEN_WIDTH - 80;
    const boxH = 120;

    // Semi-transparent dark background
    ctx.fillStyle = 'rgba(0, 0, 0, 0.85)';
    ctx.beginPath();
    ctx.roundRect(boxX, boxY, boxW, boxH, 12);
    ctx.fill();

    // Border
    ctx.strokeStyle = '#ffd700';
    ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.roundRect(boxX, boxY, boxW, boxH, 12);
    ctx.stroke();

    // Speaker name tag
    ctx.fillStyle = '#ffd700';
    ctx.font = 'bold 18px "Courier New", monospace';
    ctx.textAlign = 'left';
    ctx.fillText(speaker, boxX + 20, boxY + 28);

    // Separator line
    ctx.strokeStyle = 'rgba(255, 215, 0, 0.3)';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(boxX + 16, boxY + 38);
    ctx.lineTo(boxX + boxW - 16, boxY + 38);
    ctx.stroke();

    // Dialogue text (supports \n line breaks)
    ctx.fillStyle = '#ffffff';
    ctx.font = '16px "Courier New", monospace';
    const lines = line.split('\n');
    for (let i = 0; i < lines.length; i++) {
        ctx.fillText(lines[i], boxX + 24, boxY + 62 + i * 24);
    }

    // Blinking "next" indicator
    const pulse = Math.sin(Date.now() * 0.005) * 0.3 + 0.7;
    ctx.fillStyle = 'rgba(255, 255, 255, ' + pulse + ')';
    ctx.font = '14px monospace';
    ctx.textAlign = 'right';
    ctx.fillText(hasNext ? '▼ ENTER' : '■ CLOSE', boxX + boxW - 20, boxY + boxH - 16);
    ctx.textAlign = 'left';
}

function drawDebugOverlay(ctx: CanvasRenderingContext2D, state: GameFullState) {
    const player = state.player;
    if (!player) return;

    const playerScreenX = SCREEN_WIDTH / 2 - EFFECTIVE_TILE_SIZE / 2;
    const playerScreenY = SCREEN_HEIGHT / 2 - EFFECTIVE_TILE_SIZE / 2;

    // === Player collision rect (red) ===
    ctx.strokeStyle = '#ff0000';
    ctx.lineWidth = 2;
    ctx.strokeRect(
        playerScreenX + (player.solidAreaX ?? 8),
        playerScreenY + (player.solidAreaY ?? 16),
        player.solidAreaWidth ?? 32,
        player.solidAreaHeight ?? 32
    );

    // Player tile coordinates (green)
    const pCol = Math.floor((player.worldX + (player.solidAreaX ?? 8)) / EFFECTIVE_TILE_SIZE);
    const pRow = Math.floor((player.worldY + (player.solidAreaY ?? 16)) / EFFECTIVE_TILE_SIZE);
    ctx.fillStyle = '#00ff00';
    ctx.font = 'bold 14px monospace';
    ctx.textAlign = 'left';
    ctx.fillText(pCol + ',' + pRow, playerScreenX + 10, playerScreenY - 6);

    // === NPC collision rects (green) + coordinates ===
    if (state.npcs) {
        for (const npc of state.npcs as NpcState[]) {
            const sx = npc.worldX - player.worldX + playerScreenX;
            const sy = npc.worldY - player.worldY + playerScreenY;

            // Only draw if on screen
            if (sx > -EFFECTIVE_TILE_SIZE && sx < SCREEN_WIDTH + EFFECTIVE_TILE_SIZE &&
                sy > -EFFECTIVE_TILE_SIZE && sy < SCREEN_HEIGHT + EFFECTIVE_TILE_SIZE) {

                // NPC collision rect (lime green)
                ctx.strokeStyle = '#44ff44';
                ctx.lineWidth = 2;
                ctx.strokeRect(sx + 8, sy + 16, 48, 48);

                // NPC tile coordinates
                const nCol = Math.floor((npc.worldX + 8) / EFFECTIVE_TILE_SIZE);
                const nRow = Math.floor((npc.worldY + 16) / EFFECTIVE_TILE_SIZE);
                ctx.fillStyle = '#44ff44';
                ctx.font = '12px monospace';
                ctx.fillText(nCol + ',' + nRow, sx + 10, sy - 4);
            }
        }
    }

    // === Debug info panel (bottom-left, like original GamePanel) ===
    const panelX = 16;
    const panelY = SCREEN_HEIGHT - 180;
    const lineH = 18;

    ctx.fillStyle = 'rgba(0, 0, 0, 0.75)';
    ctx.fillRect(panelX - 4, panelY - 16, 260, lineH * 7 + 20);

    ctx.fillStyle = '#ffff00';
    ctx.font = 'bold 14px monospace';
    ctx.textAlign = 'left';

    let y = panelY;
    ctx.fillText('Map: ' + (state.currentMap || ''), panelX, y); y += lineH;
    ctx.fillText('State: ' + (state.gameState || ''), panelX, y); y += lineH;
    ctx.fillText('WorldX: ' + player.worldX, panelX, y); y += lineH;
    ctx.fillText('WorldY: ' + player.worldY, panelX, y); y += lineH;
    ctx.fillText('Col: ' + pCol + '  Row: ' + pRow, panelX, y); y += lineH;
    ctx.fillText('Direction: ' + player.direction, panelX, y); y += lineH;
}

export default GameCanvas;
