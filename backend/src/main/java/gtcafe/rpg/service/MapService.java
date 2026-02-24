package gtcafe.rpg.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import gtcafe.rpg.model.map.GameMap;
import gtcafe.rpg.model.map.MapId;
import gtcafe.rpg.model.map.TileData;
import jakarta.annotation.PostConstruct;

/**
 * Loads and manages map data and tile definitions.
 * Migrated from TileManager.java
 */
@Service
public class MapService {

    private static final String ASSETS_BASE = "assets/";
    private static final String MAPS_DIR = ASSETS_BASE + "maps/";
    private static final String TILES_DIR = ASSETS_BASE + "tiles/";

    private int maxWorldCol;
    private int maxWorldRow;

    private List<TileData> tileDataList = new ArrayList<>();
    private Map<MapId, GameMap> maps = new HashMap<>();

    @PostConstruct
    public void init() {
        loadTileData();
        loadAllMaps();
    }

    /**
     * Load tile definitions from tiledata.txt
     * Format: filename\ncollision\n (alternating lines)
     */
    private void loadTileData() {
        try {
            InputStream is = getResourceStream(MAPS_DIR + "tiledata.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                String collisionStr = br.readLine();
                boolean collision = "true".equals(collisionStr);
                tileDataList.add(new TileData(index, line, collision));
                index++;
            }
            br.close();
            System.out.printf("[MapService] Loaded %d tile definitions%n", tileDataList.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tile data", e);
        }
    }

    /**
     * Load all maps
     */
    private void loadAllMaps() {
        // First, determine grid dimensions from worldmap
        determineMapDimensions();

        loadMap(MapId.WORLD_MAP, MAPS_DIR + "worldmap.txt");
        loadMap(MapId.STORE, MAPS_DIR + "indoor01.txt");
        loadMap(MapId.DUNGEON01, MAPS_DIR + "dungeon01.txt");
        loadMap(MapId.DUNGEON02, MAPS_DIR + "dungeon02.txt");
    }

    private void determineMapDimensions() {
        try {
            InputStream is = getResourceStream(MAPS_DIR + "worldmap.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String firstLine = br.readLine();
            String[] tokens = firstLine.split(" ");
            maxWorldCol = tokens.length;
            maxWorldRow = tokens.length; // Square maps
            br.close();
            System.out.printf("[MapService] Map dimensions: %dx%d%n", maxWorldCol, maxWorldRow);
        } catch (IOException e) {
            throw new RuntimeException("Failed to determine map dimensions", e);
        }
    }

    private void loadMap(MapId mapId, String resourcePath) {
        try {
            InputStream is = getResourceStream(resourcePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int[][] grid = new int[maxWorldCol][maxWorldRow];
            int col = 0;
            int row = 0;

            while (col < maxWorldCol && row < maxWorldRow) {
                String line = br.readLine();
                if (line == null)
                    break;

                String[] numbers = line.split(" ");
                for (col = 0; col < maxWorldCol && col < numbers.length; col++) {
                    grid[col][row] = Integer.parseInt(numbers[col]);
                }
                col = 0;
                row++;
            }
            br.close();

            maps.put(mapId, new GameMap(mapId, grid, maxWorldCol, maxWorldRow));
            System.out.printf("[MapService] Loaded map: [%s]%n", mapId.getName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load map: " + mapId.getName(), e);
        }
    }

    private InputStream getResourceStream(String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        return resource.getInputStream();
    }

    // === Public API ===

    public List<TileData> getTileDataList() {
        return tileDataList;
    }

    public GameMap getMap(MapId mapId) {
        return maps.get(mapId);
    }

    public Map<MapId, GameMap> getAllMaps() {
        return maps;
    }

    public int getMaxWorldCol() {
        return maxWorldCol;
    }

    public int getMaxWorldRow() {
        return maxWorldRow;
    }

    /**
     * Returns the map data as a flat format suitable for sending to the frontend.
     */
    public Map<String, Object> getMapDataForClient(MapId mapId) {
        GameMap map = maps.get(mapId);
        if (map == null)
            return null;

        Map<String, Object> result = new HashMap<>();
        result.put("mapId", mapId.name());
        result.put("name", mapId.getName());
        result.put("maxCol", map.getMaxCol());
        result.put("maxRow", map.getMaxRow());
        result.put("tileGrid", map.getTileGrid());
        return result;
    }

    public Map<String, Object> getTileManifest() {
        Map<String, Object> manifest = new HashMap<>();
        List<Map<String, Object>> tiles = new ArrayList<>();

        for (TileData td : tileDataList) {
            Map<String, Object> tile = new HashMap<>();
            tile.put("index", td.getIndex());
            tile.put("fileName", td.getFileName());
            tile.put("collision", td.isCollision());
            tiles.add(tile);
        }

        manifest.put("tiles", tiles);
        manifest.put("maxWorldCol", maxWorldCol);
        manifest.put("maxWorldRow", maxWorldRow);
        return manifest;
    }
}
