import type { ClientMessage } from '../../types/game';
import { WebSocketClient } from '../network/WebSocketClient';

/**
 * Captures keyboard input and converts it to game commands.
 * Migrated from KeyHandler.java
 */
export class KeyboardInput {
    private pressedKeys: Set<string> = new Set();
    private wsClient: WebSocketClient;

    // Debounce: track sent state to avoid spamming
    private movingState: string | null = null;

    constructor(wsClient: WebSocketClient) {
        this.wsClient = wsClient;
    }

    attach(_element: HTMLElement) {
        // Use window for key events since canvas doesn't naturally receive focus
        window.addEventListener('keydown', this.onKeyDown);
        window.addEventListener('keyup', this.onKeyUp);
    }

    detach() {
        window.removeEventListener('keydown', this.onKeyDown);
        window.removeEventListener('keyup', this.onKeyUp);
    }

    private onKeyDown = (e: KeyboardEvent) => {
        if (this.pressedKeys.has(e.code)) return; // Ignore key repeat
        this.pressedKeys.add(e.code);
        e.preventDefault();

        switch (e.code) {
            // Movement
            case 'KeyW':
            case 'ArrowUp':
                this.sendMove('UP');
                break;
            case 'KeyS':
            case 'ArrowDown':
                this.sendMove('DOWN');
                break;
            case 'KeyA':
            case 'ArrowLeft':
                this.sendMove('LEFT');
                break;
            case 'KeyD':
            case 'ArrowRight':
                this.sendMove('RIGHT');
                break;

            // Enter key - context-dependent (attack in PLAY, select in menus)
            case 'Enter':
                this.send({ type: 'ENTER_KEY' });
                break;

            // Guard
            case 'Space':
                this.send({ type: 'GUARD_START' });
                break;

            // Projectile
            case 'KeyF':
            case 'KeyJ':
                this.send({ type: 'SHOOT' });
                break;

            // Interact
            case 'KeyE':
                this.send({ type: 'INTERACT' });
                break;

            // Debug
            case 'KeyT':
                this.send({ type: 'DEBUG_TOGGLE' });
                break;

            // God Mode
            case 'KeyX':
                this.send({ type: 'GOD_MODE_TOGGLE' });
                break;

            // Minimap
            case 'KeyV':
                this.send({ type: 'MINIMAP_TOGGLE' });
                break;

            // Full Map
            case 'KeyM':
                this.send({ type: 'MAP_TOGGLE' });
                break;

            // Escape / Options
            case 'Escape':
                this.send({ type: 'MENU_TOGGLE' });
                break;
        }
    };

    private onKeyUp = (e: KeyboardEvent) => {
        this.pressedKeys.delete(e.code);
        e.preventDefault();

        switch (e.code) {
            case 'KeyW':
            case 'ArrowUp':
            case 'KeyS':
            case 'ArrowDown':
            case 'KeyA':
            case 'ArrowLeft':
            case 'KeyD':
            case 'ArrowRight':
                // Check if any movement key is still pressed
                if (!this.isAnyMovementKeyPressed()) {
                    this.movingState = null;
                    this.send({ type: 'MOVE_STOP' });
                } else {
                    // Switch to the remaining held direction
                    this.sendCurrentDirection();
                }
                break;
            case 'Space':
                this.send({ type: 'GUARD_STOP' });
                break;
        }
    };

    private isAnyMovementKeyPressed(): boolean {
        return this.pressedKeys.has('KeyW') || this.pressedKeys.has('ArrowUp') ||
            this.pressedKeys.has('KeyS') || this.pressedKeys.has('ArrowDown') ||
            this.pressedKeys.has('KeyA') || this.pressedKeys.has('ArrowLeft') ||
            this.pressedKeys.has('KeyD') || this.pressedKeys.has('ArrowRight');
    }

    private sendCurrentDirection() {
        if (this.pressedKeys.has('KeyW') || this.pressedKeys.has('ArrowUp')) this.sendMove('UP');
        else if (this.pressedKeys.has('KeyS') || this.pressedKeys.has('ArrowDown')) this.sendMove('DOWN');
        else if (this.pressedKeys.has('KeyA') || this.pressedKeys.has('ArrowLeft')) this.sendMove('LEFT');
        else if (this.pressedKeys.has('KeyD') || this.pressedKeys.has('ArrowRight')) this.sendMove('RIGHT');
    }

    private sendMove(direction: string) {
        if (this.movingState === direction) return; // Already moving this direction
        this.movingState = direction;
        this.send({ type: 'MOVE', direction });
    }

    private send(message: ClientMessage) {
        this.wsClient.send(message);
    }
}
