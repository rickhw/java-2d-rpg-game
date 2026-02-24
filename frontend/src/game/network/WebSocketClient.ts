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
    private reconnectAttempts = 0;
    private maxReconnectAttempts = 5;

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
        try {
            // Use relative WebSocket URL via Vite proxy
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${protocol}//${window.location.host}${this.url}`;
            console.log(`[WebSocket] Connecting to ${wsUrl}`);

            this.ws = new WebSocket(wsUrl);

            this.ws.onopen = () => {
                console.log('[WebSocket] Connected');
                this.reconnectAttempts = 0;
                this.onConnect();
            };

            this.ws.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data) as GameFullState;
                    this.onStateUpdate(data);
                } catch (e) {
                    console.error('[WebSocket] Failed to parse message:', e);
                }
            };

            this.ws.onclose = (event) => {
                console.log(`[WebSocket] Disconnected (code: ${event.code})`);
                this.onDisconnect();
                this.tryReconnect();
            };

            this.ws.onerror = (error) => {
                console.error('[WebSocket] Error:', error);
            };
        } catch (e) {
            console.error('[WebSocket] Connection failed:', e);
            this.tryReconnect();
        }
    }

    send(message: ClientMessage) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        }
    }

    disconnect() {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
    }

    isConnected(): boolean {
        return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
    }

    private tryReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 10000);
            console.log(`[WebSocket] Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts})`);
            setTimeout(() => this.connect(), delay);
        }
    }
}
