import React, { useRef, useEffect, useState } from 'react';
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

interface GameCanvasProps {
    onStateUpdate?: (state: GameFullState) => void;
}

/**
 * Main game canvas component.
 * Manages the WebSocket connection, asset loading, game loop, and rendering.
 */
const GameCanvas: React.FC<GameCanvasProps> = ({ onStateUpdate }) => {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const wsClientRef = useRef<WebSocketClient | null>(null);
    const assetLoaderRef = useRef<AssetLoader>(new AssetLoader());
    const keyboardRef = useRef<KeyboardInput | null>(null);
    const rendererRef = useRef<TileRenderer | null>(null);
    const gameStateRef = useRef<GameFullState | null>(null);
    const animFrameRef = useRef<number>(0);

    const [connected, setConnected] = useState(false);
    const [loading, setLoading] = useState(true);
    const [_gameState, setGameState] = useState<GameFullState | null>(null);

    // Initialize WebSocket and assets
    useEffect(() => {
        const assetLoader = assetLoaderRef.current;

        // Create WebSocket client
        const wsClient = new WebSocketClient(
            '/ws/game',
            (state) => {
                gameStateRef.current = state;
                setGameState(state);
                onStateUpdate?.(state);
            },
            () => setConnected(true),
            () => setConnected(false)
        );
        wsClientRef.current = wsClient;

        // Create keyboard input handler
        const keyboard = new KeyboardInput(wsClient);
        keyboardRef.current = keyboard;
        keyboard.attach(document.body);

        // Load assets then connect
        loadAssets(assetLoader).then(() => {
            setLoading(false);
            wsClient.connect();
        });

        return () => {
            keyboard.detach();
            wsClient.disconnect();
            cancelAnimationFrame(animFrameRef.current);
        };
    }, []);

    // Set up canvas renderer and game loop
    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas || loading) return;

        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        // Disable image smoothing for pixel art
        ctx.imageSmoothingEnabled = false;

        const renderer = new TileRenderer(ctx, assetLoaderRef.current);
        rendererRef.current = renderer;

        // Game render loop (60 FPS)
        const renderLoop = () => {
            const state = gameStateRef.current;
            if (state) {
                // Clear
                ctx.fillStyle = '#000000';
                ctx.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                if (state.gameState === 'TITLE') {
                    drawTitleScreen(ctx);
                } else if (state.gameState === 'PLAY') {
                    renderer.drawTiles(state);
                    renderer.drawPlayer(state);
                    drawHUD(ctx, state);
                }
            } else {
                // Loading or no state yet
                ctx.fillStyle = '#111';
                ctx.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                ctx.fillStyle = '#888';
                ctx.font = '24px monospace';
                ctx.textAlign = 'center';
                ctx.fillText(
                    connected ? 'Waiting for game state...' : 'Connecting...',
                    SCREEN_WIDTH / 2,
                    SCREEN_HEIGHT / 2
                );
            }

            animFrameRef.current = requestAnimationFrame(renderLoop);
        };

        renderLoop();

        return () => {
            cancelAnimationFrame(animFrameRef.current);
        };
    }, [loading, connected]);

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

/**
 * Load all game assets from the backend API.
 */
async function loadAssets(assetLoader: AssetLoader) {
    try {
        // Fetch tile manifest
        const response = await fetch('/api/assets/tiles');
        const data = await response.json();

        if (data.tiles) {
            await assetLoader.loadTiles(data.tiles as TileInfo[]);
        }

        // Load player sprites
        await assetLoader.loadPlayerSprites();

        assetLoader.setLoaded(true);
        console.log('[GameCanvas] All assets loaded');
    } catch (e) {
        console.error('[GameCanvas] Failed to load assets:', e);
    }
}

/**
 * Draw the title screen.
 */
function drawTitleScreen(ctx: CanvasRenderingContext2D) {
    // Background
    ctx.fillStyle = '#0a0a2e';
    ctx.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    // Stars effect
    for (let i = 0; i < 50; i++) {
        const x = (Math.sin(i * 127.1 + Date.now() * 0.0001 * (i % 3 + 1)) * 0.5 + 0.5) * SCREEN_WIDTH;
        const y = (Math.cos(i * 311.7 + Date.now() * 0.00005 * (i % 2 + 1)) * 0.5 + 0.5) * SCREEN_HEIGHT;
        const alpha = Math.sin(Date.now() * 0.003 + i) * 0.3 + 0.7;
        ctx.fillStyle = 'rgba(255, 255, 220, ' + alpha + ')';
        ctx.fillRect(x, y, 2, 2);
    }

    // Title
    ctx.textAlign = 'center';
    ctx.fillStyle = '#ffd700';
    ctx.font = 'bold 56px "Courier New", monospace';
    ctx.shadowColor = '#ff8800';
    ctx.shadowBlur = 20;
    ctx.fillText('Blue Boy Adventure', SCREEN_WIDTH / 2, SCREEN_HEIGHT / 3);
    ctx.shadowBlur = 0;

    // Subtitle
    ctx.fillStyle = '#aabbcc';
    ctx.font = '20px "Courier New", monospace';
    ctx.fillText('A 2D RPG Game', SCREEN_WIDTH / 2, SCREEN_HEIGHT / 3 + 40);

    // Menu items
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
    ctx.fillText('WASD / Arrow Keys to move  |  ENTER to attack  |  SPACE to guard', SCREEN_WIDTH / 2, SCREEN_HEIGHT - 35);
}

/**
 * Draw the in-game HUD (Health, Mana, etc.)
 */
function drawHUD(ctx: CanvasRenderingContext2D, state: GameFullState) {
    const player = state.player;
    if (!player) return;

    const hudX = 20;
    const hudY = 20;

    // Semi-transparent background
    ctx.fillStyle = 'rgba(0, 0, 0, 0.6)';
    ctx.fillRect(hudX - 5, hudY - 5, 220, 80);

    // Health bar
    ctx.fillStyle = '#333';
    ctx.fillRect(hudX + 40, hudY, 160, 16);
    const playerLife = player.life ?? 0;
    const playerMaxLife = player.maxLife ?? 1;
    const healthPercent = playerLife / playerMaxLife;
    ctx.fillStyle = healthPercent > 0.3 ? '#e74c3c' : '#ff0000';
    ctx.fillRect(hudX + 40, hudY, 160 * healthPercent, 16);
    ctx.strokeStyle = '#fff';
    ctx.lineWidth = 1;
    ctx.strokeRect(hudX + 40, hudY, 160, 16);
    ctx.fillStyle = '#fff';
    ctx.font = '12px monospace';
    ctx.textAlign = 'left';
    ctx.fillText('HP', hudX, hudY + 13);
    ctx.fillText(playerLife + '/' + playerMaxLife, hudX + 45, hudY + 13);

    // Mana bar
    ctx.fillStyle = '#333';
    ctx.fillRect(hudX + 40, hudY + 22, 160, 16);
    const playerMana = player.mana ?? 0;
    const playerMaxMana = player.maxMana ?? 1;
    const manaPercent = playerMana / playerMaxMana;
    ctx.fillStyle = '#3498db';
    ctx.fillRect(hudX + 40, hudY + 22, 160 * manaPercent, 16);
    ctx.strokeStyle = '#fff';
    ctx.strokeRect(hudX + 40, hudY + 22, 160, 16);
    ctx.fillStyle = '#fff';
    ctx.fillText('MP', hudX, hudY + 35);
    ctx.fillText(playerMana + '/' + playerMaxMana, hudX + 45, hudY + 35);

    // Level & Coin
    ctx.fillStyle = '#ffd700';
    ctx.font = '14px monospace';
    ctx.fillText('Lv.' + (player.level ?? 1), hudX, hudY + 56);
    ctx.fillStyle = '#ffaa00';
    ctx.fillText('Coin: ' + (player.coin ?? 0), hudX + 80, hudY + 56);

    // Game state indicator (debug)
    ctx.fillStyle = '#556';
    ctx.font = '12px monospace';
    ctx.textAlign = 'right';
    ctx.fillText('Map: ' + state.currentMap + ' | ' + state.dayState, SCREEN_WIDTH - 20, 20);
    ctx.textAlign = 'left';
}

export default GameCanvas;
