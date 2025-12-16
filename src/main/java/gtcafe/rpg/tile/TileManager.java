package gtcafe.rpg.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Graphics2DUtils;

public class TileManager {

    GamePanel gp;
    public Tile[] tiles;
    public int mapTileNum[][][]; // first dimension to store the map name
    boolean showInfo = false;
    int drawCounter = 0;
    // boolean drawPath = true;
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> collsionStatus = new ArrayList<>();

    public TileManager(GamePanel gp) {
        this.gp = gp;

        // READ TILE DATA File
        InputStream is = getClass().getResourceAsStream("/gtcafe/rpg/assets/maps_v2/tiledata.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // GETTING TILE NAMES AND COLLSION INFO FROM THE FILE
        String line;
        try {
            while ((line = br.readLine()) != null) {
                fileNames.add(line);
                collsionStatus.add(br.readLine());
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tiles = new Tile[fileNames.size()];
        getTileImage();

        // GET THE maxWorldCol & maxWorldRow
        is = getClass().getResourceAsStream("/gtcafe/rpg/assets/maps_v2/worldmap.txt");
        br = new BufferedReader(new InputStreamReader(is));

        try {
            String line2 = br.readLine();
            String maxTile[] = line2.split(" ");

            gp.maxWorldCol = maxTile.length;
            gp.maxWorldRow = maxTile.length;
            mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        loadMap("/gtcafe/rpg/assets/maps_v2/worldmap.txt", Map.WORLD_MAP);
        loadMap("/gtcafe/rpg/assets/maps_v2/indoor01.txt", Map.STORE);
        loadMap("/gtcafe/rpg/assets/maps_v2/dungeon01.txt", Map.DONGEON01);
        loadMap("/gtcafe/rpg/assets/maps_v2/dungeon02.txt", Map.DONGEON02);
    }

    public void getTileImage() {

        for (int i = 0; i < fileNames.size(); i++) {
            String filename = fileNames.get(i);
            boolean collision = collsionStatus.get(i).equals("true") ? true : false;

            setup(i, filename, collision);
        }
    }

    public void setup(int index, String imageName, boolean collision) {
        Graphics2DUtils uTools = new Graphics2DUtils();
        try {
            tiles[index] = new Tile();
            tiles[index].image = ImageIO
                    .read(getClass().getResourceAsStream("/gtcafe/rpg/assets/tilesV3/" + imageName));
            tiles[index].image = uTools.scaleImage(tiles[index].image, gp.tileSize, gp.tileSize);
            tiles[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String mapPath, Map map) {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                while (col < gp.maxWorldCol) {
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[map.index][col][row] = num;
                    col++;
                }
                if (col == gp.maxWorldCol) {
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

    // see:
    // https://www.youtube.com/watch?v=Ny_YHoTYcxo&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=6
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

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[gp.currentMap.index][worldCol][worldRow];

            // 計算 世界地圖 的座標
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            // 計算 攝影機角度 的 螢幕座標
            int screenX = worldX - gp.player.getWorldX() + gp.player.screenX;
            int screenY = worldY - gp.player.getWorldY() + gp.player.screenY;

            // 增加條件, 只畫 screen 的部分, 而不是整個大地圖
            // 畫的時候，往外延伸一格，避免畫面不順的感覺
            if ((worldX + gp.tileSize) > (gp.player.getWorldX() - gp.player.screenX) &&
                    (worldX - gp.tileSize) < (gp.player.getWorldX() + gp.player.screenX) &&
                    (worldY + gp.tileSize) > (gp.player.getWorldY() - gp.player.screenY) &&
                    (worldY - gp.tileSize) < (gp.player.getWorldY() + gp.player.screenY)) {

                // if (showInfo && gp.debugMode) {
                // System.out.printf("tileNum: [%s], worldCol:[%s], worldRow: [%s], screenX:
                // [%s], screenY: [%s]\n", tileNum, worldCol, worldRow, screenX, screenY);
                // }

                g2.drawImage(tiles[tileNum].image, screenX, screenY, null);
            }
            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }

        if (gp.keyHandler.showDebugText == true) {

            // 畫出 PathFinding 的路徑
            g2.setColor(new Color(255, 0, 0, 70));
            for (int i = 0; i < gp.pathFinder.pathList.size(); i++) {
                int worldX = gp.pathFinder.pathList.get(i).col * gp.tileSize;
                int worldY = gp.pathFinder.pathList.get(i).row * gp.tileSize;
                int screenX = worldX - gp.player.getWorldX() + gp.player.screenX;
                int screenY = worldY - gp.player.getWorldY() + gp.player.screenY;

                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }
        }
    }
}
