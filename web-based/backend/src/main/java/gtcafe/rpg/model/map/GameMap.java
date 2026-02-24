package gtcafe.rpg.model.map;

/**
 * Represents a loaded game map with tile indices.
 */
public class GameMap {
    private MapId mapId;
    private int[][] tileGrid; // [col][row] = tileIndex
    private int maxCol;
    private int maxRow;

    public GameMap() {
    }

    public GameMap(MapId mapId, int[][] tileGrid, int maxCol, int maxRow) {
        this.mapId = mapId;
        this.tileGrid = tileGrid;
        this.maxCol = maxCol;
        this.maxRow = maxRow;
    }

    public MapId getMapId() {
        return mapId;
    }

    public void setMapId(MapId mapId) {
        this.mapId = mapId;
    }

    public int[][] getTileGrid() {
        return tileGrid;
    }

    public void setTileGrid(int[][] tileGrid) {
        this.tileGrid = tileGrid;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public void setMaxCol(int maxCol) {
        this.maxCol = maxCol;
    }

    public int getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public int getTileAt(int col, int row) {
        if (col >= 0 && col < maxCol && row >= 0 && row < maxRow) {
            return tileGrid[col][row];
        }
        return -1;
    }
}
