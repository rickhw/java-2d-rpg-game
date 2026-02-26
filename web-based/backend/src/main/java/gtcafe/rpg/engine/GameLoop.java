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
     * One game tick - process sessions in PLAY or TRANSITION state.
     */
    private void tick() {
        try {
            for (String sessionId : gameEngine.getAllSessionIds()) {
                GameSession session = gameEngine.getSession(sessionId);
                if (session == null)
                    continue;

                GameState state = session.getGameState();

                // Only tick PLAY, TRANSITION, CHARACTER, and DIALOGUE states
                if (state != GameState.PLAY && state != GameState.TRANSITION && state != GameState.CHARACTER
                        && state != GameState.DIALOGUE)
                    continue;

                // Update game logic
                gameEngine.tick(sessionId);

                // Check if we need to send FULL_STATE (after map switch)
                if (session.isNeedsFullState()) {
                    session.setNeedsFullState(false);
                    Map<String, Object> fullState = gameEngine.getFullState(sessionId);
                    if (fullState != null) {
                        fullState.put("type", "FULL_STATE");
                        webSocketHandler.sendStateUpdate(sessionId, fullState);
                    }
                } else {
                    // Broadcast delta state to the client
                    Map<String, Object> delta = gameEngine.getDeltaState(sessionId);
                    if (delta != null) {
                        delta.put("type", "DELTA_STATE");
                        webSocketHandler.sendStateUpdate(sessionId, delta);
                    }
                }
            }
        } catch (Exception e) {
            System.err.printf("[GameLoop] Error in tick: %s%n", e.getMessage());
        }
    }
}
