package gtcafe.rpg.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gtcafe.rpg.engine.GameEngine;

/**
 * REST controller for game session management (new game, load, save)
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameEngine gameEngine;

    public GameController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Create a new game session
     */
    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> newGame() {
        String sessionId = gameEngine.createNewSession();
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", "New game session created");
        return ResponseEntity.ok(response);
    }

    /**
     * Get game state for a session
     */
    @GetMapping("/{sessionId}/state")
    public ResponseEntity<Map<String, Object>> getState(@PathVariable String sessionId) {
        Map<String, Object> state = gameEngine.getFullState(sessionId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(state);
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("version", "0.1.0");
        return ResponseEntity.ok(response);
    }
}
