import React, { useRef, useEffect } from 'react';
import { WebSocketClient } from '../game/network/WebSocketClient';
import { AssetLoader } from '../game/assets/AssetLoader';
import { KeyboardInput } from '../game/input/KeyboardInput';
import { TileRenderer } from '../game/renderer/TileRenderer';
import type {
    GameFullState,
    TileInfo,
} from '../types/game';
import {
    SCREEN_WIDTH,
    SCREEN_HEIGHT,
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
                    renderer.drawPlayer(state);
                    drawHUD(ctx, state);
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
    ctx.fillText('Press ENTER to start', SCREEN_WIDTH / 2, SCREEN_HEIGHT - 60);
    ctx.fillText('WASD / Arrow Keys to move | ENTER to attack | SPACE to guard', SCREEN_WIDTH / 2, SCREEN_HEIGHT - 35);
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

export default GameCanvas;
