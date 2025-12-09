package gtcafe.rpg.tile;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.util.Graphics2DUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;


public class TileManager {

    GameContext context;
    public Tile[] tiles;
    public int mapTileNum[][][];    // first dimension to store the map name
    boolean showInfo = false;
    int drawCounter = 0;
    // boolean drawPath = true;

    public TileManager(GameContext context) {
        this.context = context;
        tiles = new Tile[50];
        mapTileNum = new int[context.getMaxMap()][context.getMaxWorldCol()][context.getMaxWorldRow()];
        

        getTileImage();
        loadMap("/gtcafe/rpg/assets/maps/worldV3.txt", Scense.WORLD_MAP);
        loadMap("/gtcafe/rpg/assets/maps/interior01.txt", Scense.STORE);
    }

    public void getTileImage() {
        // we don't use the tile 0 to 9, but I've set a placeholder image so we can
        // prevent NullPointerException happens when we scan tils the array.
        setup(0, "grass00", false);
        setup(1, "grass00", false);
        setup(2, "grass00", false);
        setup(3, "grass00", false);
        setup(4, "grass00", false);
        setup(5, "grass00", false);
        setup(6, "grass00", false);
        setup(7, "grass00", false);
        setup(8, "grass00", false);
        setup(9, "grass00", false);
        // end of PLACEHOLDER

        setup(10, "grass00", false);
        setup(11, "grass01", false);
        setup(12, "water00", true);
        setup(13, "water01", true);
        setup(14, "water02", true);
        setup(15, "water03", true);
        setup(16, "water04", true);
        setup(17, "water05", true);
        setup(18, "water06", true);
        setup(19, "water07", true);

        setup(20, "water08", true);
        setup(21, "water09", true);
        setup(22, "water10", true);
        setup(23, "water11", true);
        setup(24, "water12", true);
        setup(25, "water13", true);
        setup(26, "road00", false);
        setup(27, "road01", false);
        setup(28, "road02", false);
        setup(29, "road03", false);

        setup(30, "road04", false);
        setup(31, "road05", false);
        setup(32, "road06", false);
        setup(33, "road07", false);
        setup(34, "road08", false);
        setup(35, "road09", false);
        setup(36, "road10", false);
        setup(37, "road11", false);
        setup(38, "road12", false);
        setup(39, "earth", false);

        setup(40, "wall", true);
        setup(41, "tree", true);

        setup(42, "hut", false); // 小屋
        setup(43, "floor01", false);
        setup(44, "table01", true);
    }

    public void setup(int index, String imageName, boolean collision) {
        Graphics2DUtils uTools = new Graphics2DUtils();
        try {
            tiles[index] = new Tile();
            tiles[index].image = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/tilesV2/" + imageName + ".png"));
            tiles[index].image = uTools.scaleImage(tiles[index].image, context.getTileSize(), context.getTileSize());
            tiles[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String mapPath, Scense map) {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < context.getMaxWorldCol() && row < context.getMaxWorldRow()) {
                String line = br.readLine();
                while (col < context.getMaxWorldCol()) {
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[map.index][col][row] = num;
                    col++;
                }
                if(col == context.getMaxWorldCol()) {
                    col = 0;
                    row++;
                }
            }
            br.close();
            System.out.printf("[TileManager#loadMap] finished to load map: [%s], index: [%s]\n", map.name, map.index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // see: https://www.youtube.com/watch?v=Ny_YHoTYcxo&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=6
    public void draw(Graphics2D g2) {

        int worldCol = 0;
        int worldRow = 0;

        // For Debugging
        if (drawCounter < 60) {
            drawCounter++;
            showInfo = false;
        } else {
            drawCounter = 0;
            showInfo = true;
        }

        while (worldCol < context.getMaxWorldCol() && worldRow < context.getMaxWorldRow()) {
            int tileNum = mapTileNum[context.getCurrentMap().index][worldCol][worldRow];

            // 計算 世界地圖 的座標
            int worldX = worldCol * context.getTileSize();
            int worldY = worldRow * context.getTileSize();
            // 計算 攝影機角度 的 螢幕座標
            int screenX = worldX - context.getPlayer().worldX + context.getPlayer().screenX;
            int screenY = worldY - context.getPlayer().worldY + context.getPlayer().screenY;

            // 增加條件, 只畫 screen 的部分, 而不是整個大地圖
            // 畫的時候，往外延伸一格，避免畫面不順的感覺
            if ((worldX + context.getTileSize()) > (context.getPlayer().worldX - context.getPlayer().screenX) &&
                (worldX - context.getTileSize()) < (context.getPlayer().worldX + context.getPlayer().screenX) &&
                (worldY + context.getTileSize()) > (context.getPlayer().worldY - context.getPlayer().screenY) &&
                (worldY - context.getTileSize()) < (context.getPlayer().worldY + context.getPlayer().screenY)) {

                // if (showInfo && gp.debugMode) {
                //     System.out.printf("tileNum: [%s], worldCol:[%s], worldRow: [%s], screenX: [%s], screenY: [%s]\n", tileNum, worldCol, worldRow, screenX, screenY);
                // }

                g2.drawImage(tiles[tileNum].image, screenX, screenY, null);
            }
            worldCol++;

            if (worldCol == context.getMaxWorldCol()) {
                worldCol = 0;
                worldRow++;
            }
        }

        // 畫出 PathFinding 的路徑
        if (context.getKeyHandler().showDebugText == true) {
            g2.setColor(new Color(255, 0,0, 70));
            for(int i=0; i<context.getPathFinder().pathList.size(); i++) {
                int worldX = context.getPathFinder().pathList.get(i).col * context.getTileSize();
                int worldY = context.getPathFinder().pathList.get(i).row * context.getTileSize();
                int screenX = worldX - context.getPlayer().worldX + context.getPlayer().screenX;
                int screenY = worldY - context.getPlayer().worldY + context.getPlayer().screenY;

                g2.fillRect(screenX, screenY, context.getTileSize(), context.getTileSize());
            }
        }
    }
}
