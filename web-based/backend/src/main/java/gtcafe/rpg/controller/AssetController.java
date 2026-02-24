package gtcafe.rpg.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gtcafe.rpg.model.map.MapId;
import gtcafe.rpg.service.MapService;

/**
 * REST controller for serving game assets (tiles, sprites, sounds, maps)
 */
@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final MapService mapService;

    public AssetController(MapService mapService) {
        this.mapService = mapService;
    }

    /**
     * Get the asset manifest (tile definitions + map dimensions)
     */
    @GetMapping("/manifest")
    public ResponseEntity<Map<String, Object>> getManifest() {
        Map<String, Object> manifest = new HashMap<>();
        manifest.put("tileData", mapService.getTileManifest());
        manifest.put("maps", getMapList());
        manifest.put("sprites", getSpriteManifest());
        return ResponseEntity.ok(manifest);
    }

    /**
     * Get tile data definitions
     */
    @GetMapping("/tiles")
    public ResponseEntity<Map<String, Object>> getTileData() {
        return ResponseEntity.ok(mapService.getTileManifest());
    }

    /**
     * Get a specific tile image
     */
    @GetMapping("/tiles/images/{fileName}")
    public ResponseEntity<Resource> getTileImage(@PathVariable String fileName) {
        return serveImage("assets/tiles/" + fileName);
    }

    /**
     * Get map data (tile grid) for a specific map
     */
    @GetMapping("/maps/{mapName}")
    public ResponseEntity<Map<String, Object>> getMapData(@PathVariable String mapName) {
        try {
            MapId mapId = MapId.valueOf(mapName.toUpperCase());
            Map<String, Object> data = mapService.getMapDataForClient(mapId);
            if (data == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get sprite images - supports deep paths like
     * /sprites/player/walking/boy_down_1.png
     */
    @GetMapping("/sprites/**")
    public ResponseEntity<Resource> getSpriteImage(HttpServletRequest request) {
        String fullPath = request.getRequestURI();
        // Extract the path after /api/assets/sprites/
        String spritePath = fullPath.substring(fullPath.indexOf("/sprites/") + "/sprites/".length());
        return serveImage("assets/sprites/" + spritePath);
    }

    /**
     * Get sound files
     */
    @GetMapping("/sounds/{name}")
    public ResponseEntity<Resource> getSound(@PathVariable String name) {
        try {
            Resource resource = new ClassPathResource("assets/sounds/" + name);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = "audio/wav";
            if (name.endsWith(".mp3"))
                contentType = "audio/mpeg";
            if (name.endsWith(".ogg"))
                contentType = "audio/ogg";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // === Helper methods ===

    private ResponseEntity<Resource> serveImage(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private List<Map<String, String>> getMapList() {
        List<Map<String, String>> maps = new ArrayList<>();
        for (MapId mapId : MapId.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("id", mapId.name());
            map.put("name", mapId.getName());
            maps.add(map);
        }
        return maps;
    }

    private Map<String, Object> getSpriteManifest() {
        Map<String, Object> sprites = new HashMap<>();

        // Player sprites
        Map<String, Object> player = new HashMap<>();
        player.put("walking", List.of(
                "player/walking/boy_up_1.png", "player/walking/boy_up_2.png",
                "player/walking/boy_down_1.png", "player/walking/boy_down_2.png",
                "player/walking/boy_left_1.png", "player/walking/boy_left_2.png",
                "player/walking/boy_right_1.png", "player/walking/boy_right_2.png"));
        sprites.put("player", player);

        // Monster sprites
        sprites.put("monsters", List.of("greenslime", "redslime", "bat", "orc", "skeletonlord"));

        // NPC sprites
        sprites.put("npcs", List.of("oldman", "merchant"));

        return sprites;
    }
}
