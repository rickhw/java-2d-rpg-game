package gtcafe.rpg.system;

public class GameConfig {
    // Tile Settings
    public static final int originalTileSize = 16; // 16x16 pixel
    public static final int scale = 4;

    // SCREEN SETTINGS
    public static final int tileSize = originalTileSize * scale; // 64x64 pixel

    // public static final int maxScreenCol = 16;
    public static final int maxScreenCol = 20; // for screen size: 16:9
    public static final int maxScreenRow = 12;

    public static final int screenWidth = tileSize * maxScreenCol; // 1280 pixels
    public static final int screenHeight = tileSize * maxScreenRow; // 768 pixels

    // WORLD MAP SETTINGS
    public static final int maxMap = 10;

    // FPS
    public static final int FPS = 60;
}
