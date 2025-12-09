package gtcafe.rpg.tile;
import gtcafe.rpg.core.GameContext;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Map extends TileManager {
    GameContext context;
    BufferedImage worldMap[];
    public boolean miniMapOn = false;

    public Map(GameContext context) {
        super(context);
        this.context = context;
        createWorldMap();
    }

    public void createWorldMap() {

        worldMap = new BufferedImage[context.getMaxMap()];
        int worldMapWidth = context.getTileSize() * context.getMaxWorldCol();
        int worldMapHeight = context.getTileSize() * context.getMaxWorldRow();

        for(int i=0; i<context.getMaxMap(); i++) {

            worldMap[i] = new BufferedImage(worldMapWidth, worldMapHeight, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g2 = (Graphics2D)worldMap[i].createGraphics();

            int col = 0;
            int row = 0;
            while(col < context.getMaxWorldCol() && row < context.getMaxWorldRow()) {
                int tileNum = mapTileNum[i][col][row];
                int x = context.getTileSize() * col;
                int y = context.getTileSize() * row;

                g2.drawImage(tiles[tileNum].image, x, y, null);

                col++;
                if(col == context.getMaxWorldCol()) {
                    col=0;
                    row++;
                }
            }
            g2.dispose();
        }
    }

    public void drawFullMapScreen(Graphics2D g2) {

        // Background color
        g2.setColor(Color.black);
        g2.fillRect(0, 0, context.getScreenWidth(), context.getScreenHeight());

        // Draw map
        int width = 500;
        int height = 500;
        int x = context.getScreenWidth() / 2 - width / 2;
        int y = context.getScreenHeight()/ 2 - height / 2;
        g2.drawImage(worldMap[context.getCurrentMap().index], x, y, width, height, null);

        // Draw Player
        double scale = (double) (context.getTileSize() * context.getMaxWorldCol()) / width;
        int playerX = (int) (x + context.getPlayer().worldX / scale);
        int playerY = (int) (y + context.getPlayer().worldY / scale);
        int playerSize = (int) (context.getTileSize() / scale);
        g2.drawImage(context.getPlayer().down1, playerX, playerY, playerSize, playerSize, null);

        // Guide message
        g2.setFont(context.getGameUI().maruMonica.deriveFont(34f));
        g2.setColor(Color.white);
        g2.drawString("Press M to close", 750, 550);

    }

    public void drawMiniMap(Graphics2D g2) {

        if (miniMapOn == true) {
            // Draw map
            int width = 200;
            int height = 200;
            int x = context.getScreenWidth() - width - 50;
            int y = 50;

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2.drawImage(worldMap[context.getCurrentMap().index], x, y, width, height, null);

            // Draw Player
            double scale = (double) (context.getTileSize() * context.getMaxWorldCol()) / width;
            int playerX = (int) (x + context.getPlayer().worldX / scale);
            int playerY = (int) (y + context.getPlayer().worldY / scale);
            int playerSize = (int) (context.getTileSize() / 3);
            g2.drawImage(context.getPlayer().down1, playerX-6, playerY-6, playerSize, playerSize, null);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        }
    }
}
