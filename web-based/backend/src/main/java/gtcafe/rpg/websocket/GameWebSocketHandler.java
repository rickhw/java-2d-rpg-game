package gtcafe.rpg.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import gtcafe.rpg.engine.GameEngine;

/**
 * WebSocket handler for real-time game communication.
 *
 * Design:
 * - On connect: create game session → send FULL_STATE (with mapData)
 * - On input: process command → for state-changing actions, send FULL_STATE
 * - Game loop: sends DELTA_STATE at 20 ticks/s (only for PLAY sessions)
 * - On close: remove game session to prevent orphaned sessions
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameEngine gameEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Bidirectional mapping: WS session ID ↔ game session ID
    private final ConcurrentHashMap<String, String> wsToGameSession = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, WebSocketSession> gameToWsSession = new ConcurrentHashMap<>();
    // Per-session lock for thread-safe writes (game loop vs input handler)
    private final ConcurrentHashMap<String, Object> sessionLocks = new ConcurrentHashMap<>();

    public GameWebSocketHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.printf("[WS] Connected: %s%n", session.getId());

        String gameSessionId = gameEngine.createNewSession();
        wsToGameSession.put(session.getId(), gameSessionId);
        gameToWsSession.put(gameSessionId, session);
        sessionLocks.put(session.getId(), new Object());

        // Send initial full state (TITLE screen with mapData)
        sendFullState(session, gameSessionId);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String gameSessionId = wsToGameSession.get(session.getId());
            if (gameSessionId == null)
                return;

            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");
            if (type == null)
                return;

            // Process the input command
            gameEngine.processInput(gameSessionId, type, payload);

            // For state-changing actions, send an immediate FULL_STATE response
            // so the client gets mapData, currentMap, dayState, etc.
            if ("ENTER_KEY".equals(type) || "MENU_SELECT".equals(type)) {
                sendFullState(session, gameSessionId);
            }
        } catch (Exception e) {
            System.err.printf("[WS] Error handling message: %s%n", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.printf("[WS] Closed: %s (status: %s)%n", session.getId(), status);

        String gameSessionId = wsToGameSession.remove(session.getId());
        if (gameSessionId != null) {
            gameToWsSession.remove(gameSessionId);
            // Clean up the game session to prevent orphaned sessions in the game loop
            gameEngine.removeSession(gameSessionId);
        }
        sessionLocks.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.printf("[WS] Transport error: %s%n", exception.getMessage());
    }

    /**
     * Send a state update to a specific game session's WebSocket.
     * Called by the GameLoop at 20 ticks/s for PLAY sessions.
     */
    public void sendStateUpdate(String gameSessionId, Map<String, Object> state) {
        WebSocketSession wsSession = gameToWsSession.get(gameSessionId);
        if (wsSession != null && wsSession.isOpen()) {
            sendMessage(wsSession, state);
        }
    }

    /**
     * Send a FULL_STATE to a specific WebSocket session.
     */
    private void sendFullState(WebSocketSession session, String gameSessionId) {
        Map<String, Object> state = gameEngine.getFullState(gameSessionId);
        if (state != null) {
            state.put("type", "FULL_STATE");
            sendMessage(session, state);
        }
    }

    /**
     * Thread-safe message sending with per-session serialization.
     */
    private void sendMessage(WebSocketSession session, Object data) {
        if (!session.isOpen())
            return;

        Object lock = sessionLocks.get(session.getId());
        if (lock == null)
            return;

        synchronized (lock) {
            try {
                if (session.isOpen()) {
                    String json = objectMapper.writeValueAsString(data);
                    session.sendMessage(new TextMessage(json));
                }
            } catch (IOException e) {
                // Silently handle - session may have closed between check and send
            } catch (IllegalStateException e) {
                // Session closed during send - expected during shutdown
            }
        }
    }
}
