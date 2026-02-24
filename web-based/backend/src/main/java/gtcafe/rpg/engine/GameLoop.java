package gtcafe.rpg.engine;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import gtcafe.rpg.model.state.GameState;
import gtcafe.rpg.websocket.GameWebSocketHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Server-side game loop that ticks at a fixed rate (20 ticks/s = 50ms
 * interval).
 *
 * Only active (PLAY state) sessions are ticked and broadcasted.
 * TITLE and other menu-state sessions do NOT receive tick broadcasts -
 * they only receive state updates in response to input (ENTER_KEY, etc.)
 */
@Component
public class GameLoop {

    private static final int TICK_RATE = 60; // Match original game's 60 FPS
    private static final long TICK_INTERVAL_MS = 1000 / TICK_RATE;

    private final GameEngine gameEngine;
    private final GameWebSocketHandler webSocketHandler;
    private ScheduledExecutorService scheduler;

    public GameLoop(GameEngine gameEngine, GameWebSocketHandler webSocketHandler) {
        this.gameEngine = gameEngine;
        this.webSocketHandler = webSocketHandler;
    }

    @PostConstruct
    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "game-loop");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(this::tick, 0, TICK_INTERVAL_MS, TimeUnit.MILLISECONDS);
        System.out.printf("[GameLoop] Started at %d ticks/s (%dms interval)%n", TICK_RATE, TICK_INTERVAL_MS);
    }

    @PreDestroy
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
            System.out.println("[GameLoop] Stopped");
        }
    }

    /**
     * One game tick - only process sessions that are in PLAY state.
     */
    private void tick() {
        try {
            for (String sessionId : gameEngine.getAllSessionIds()) {
                GameSession session = gameEngine.getSession(sessionId);
                if (session == null)
                    continue;

                // Only tick and broadcast for sessions in PLAY state
                if (session.getGameState() != GameState.PLAY)
                    continue;

                // Update game logic
                gameEngine.tick(sessionId);

                // Broadcast delta state to the client
                Map<String, Object> state = gameEngine.getDeltaState(sessionId);
                if (state != null) {
                    state.put("type", "DELTA_STATE");
                    webSocketHandler.sendStateUpdate(sessionId, state);
                }
            }
        } catch (Exception e) {
            System.err.printf("[GameLoop] Error in tick: %s%n", e.getMessage());
        }
    }
}
