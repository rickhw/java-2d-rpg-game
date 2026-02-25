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
                    };
                    if (state.player && current.player) {
                        merged.player = { ...current.player, ...state.player };
                    }
                    // Merge NPCs and dialogue from delta
                    if (state.npcs) {
                        merged.npcs = state.npcs;
                    }
                    merged.dialogue = state.dialogue ?? undefined;
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
                } else if (state.gameState === 'PLAY') {
                    renderer.drawTiles(state);
                    renderer.drawNPCs(state);
                    renderer.drawPlayer(state);
                    if (debugRef.current) {
                        drawDebugOverlay(ctx, state);
                    }
                    drawHUD(ctx, state);
                    if (state.dialogue) {
                        drawDialogueBox(ctx, state.dialogue.speaker, state.dialogue.line, state.dialogue.hasNext);
                    }
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

        // --- Cleanup on unmount ---
        return () => {
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
    ctx.fillText('v1.3.0 / 20260225', SCREEN_WIDTH - 40, SCREEN_HEIGHT - 20);
    ctx.textAlign = 'center';
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
