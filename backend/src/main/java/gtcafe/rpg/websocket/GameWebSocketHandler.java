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
 * Receives player input commands and broadcasts game state updates.
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameEngine gameEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // WebSocket session → Game session ID mapping
    private final ConcurrentHashMap<String, String> wsToGameSession = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, WebSocketSession> gameToWsSession = new ConcurrentHashMap<>();

    public GameWebSocketHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.printf("[WebSocket] Connection established: %s%n", session.getId());

        // Create a new game session for this WebSocket connection
        String gameSessionId = gameEngine.createNewSession();
        wsToGameSession.put(session.getId(), gameSessionId);
        gameToWsSession.put(gameSessionId, session);

        // Send initial state
        try {
            Map<String, Object> state = gameEngine.getFullState(gameSessionId);
            state.put("type", "FULL_STATE");
            sendMessage(session, state);
        } catch (Exception e) {
            System.err.printf("[WebSocket] Error sending initial state: %s%n", e.getMessage());
        }
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

            // Process the input
            gameEngine.processInput(gameSessionId, type, payload);

            // Send updated state back (for now, send full state on each input)
            Map<String, Object> state = gameEngine.getFullState(gameSessionId);
            if (state != null) {
                state.put("type", "DELTA_STATE");
                sendMessage(session, state);
            }
        } catch (Exception e) {
            System.err.printf("[WebSocket] Error handling message: %s%n", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.printf("[WebSocket] Connection closed: %s (status: %s)%n", session.getId(), status);
        String gameSessionId = wsToGameSession.remove(session.getId());
        if (gameSessionId != null) {
            gameToWsSession.remove(gameSessionId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.printf("[WebSocket] Transport error: %s%n", exception.getMessage());
    }

    /**
     * Send a state update to a specific game session's WebSocket.
     */
    public void sendStateUpdate(String gameSessionId, Map<String, Object> state) {
        WebSocketSession wsSession = gameToWsSession.get(gameSessionId);
        if (wsSession != null && wsSession.isOpen()) {
            sendMessage(wsSession, state);
        }
    }

    private void sendMessage(WebSocketSession session, Object data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            System.err.printf("[WebSocket] Error sending message: %s%n", e.getMessage());
        }
    }
}
