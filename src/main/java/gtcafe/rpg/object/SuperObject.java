package gtcafe.rpg.object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Utils;

public class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0,0,48,48); 
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    Utils uTools = new Utils();

    GamePanel gp;

    public SuperObject(GamePanel gp) {
        this.gp = gp;
    }

    // copy from TileManager.java#draw()
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX * gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
        
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }

    protected void initObject(String name, String imagePath) {
        this.name = name;
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream(imagePath));
            uTools.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}