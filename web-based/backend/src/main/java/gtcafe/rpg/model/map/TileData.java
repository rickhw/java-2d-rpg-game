package gtcafe.rpg.model.map;

/**
 * Represents tile data - collision info loaded from tiledata.txt
 */
public class TileData {
    private int index;
    private String fileName;
    private boolean collision;

    public TileData() {
    }

    public TileData(int index, String fileName, boolean collision) {
        this.index = index;
        this.fileName = fileName;
        this.collision = collision;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }
}
