import type { ClientMessage, GameFullState } from '../../types/game';

/**
 * WebSocket client for real-time game communication with the backend.
 */
export class WebSocketClient {
    private ws: WebSocket | null = null;
    private url: string;
    private onStateUpdate: (state: GameFullState) => void;
    private onConnect: () => void;
    private onDisconnect: () => void;
    private intentionalClose = false;

    constructor(
        url: string,
        onStateUpdate: (state: GameFullState) => void,
        onConnect: () => void = () => { },
        onDisconnect: () => void = () => { }
    ) {
        this.url = url;
        this.onStateUpdate = onStateUpdate;
        this.onConnect = onConnect;
        this.onDisconnect = onDisconnect;
    }

    connect() {
        // Don't connect if intentionally closed
        if (this.intentionalClose) return;

        try {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = protocol + '//' + window.location.host + this.url;
            console.log('[WS] Connecting to ' + wsUrl);

            this.ws = new WebSocket(wsUrl);

            this.ws.onopen = () => {
                console.log('[WS] Connected');
                this.onConnect();
            };

            this.ws.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data) as GameFullState;
                    this.onStateUpdate(data);
                } catch (e) {
                    console.error('[WS] Parse error:', e);
                }
            };

            this.ws.onclose = () => {
                this.onDisconnect();
                // Only reconnect if not intentionally closed
                if (!this.intentionalClose) {
                    console.log('[WS] Disconnected, reconnecting in 2s...');
                    setTimeout(() => this.connect(), 2000);
                }
            };

            this.ws.onerror = () => {
                // Error is followed by onclose, which handles reconnection
            };
        } catch (e) {
            console.error('[WS] Connection failed:', e);
        }
    }

    send(message: ClientMessage) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        }
    }

    disconnect() {
        this.intentionalClose = true; // Prevent reconnection
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
    }

    isConnected(): boolean {
        return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
    }
}
