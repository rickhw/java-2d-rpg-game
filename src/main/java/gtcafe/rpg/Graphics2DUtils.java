package gtcafe.rpg;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class Graphics2DUtils {
   public BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();

        return scaledImage;
   } 

   public void changeAlpha(Graphics2D g2, float alphaValue) {
       g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue)); 
   }

   public void drawSubWindow(Graphics2D g2, int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 180); // Black, alpha value = 200, make the color transparent.
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);  // draw rectangle

        c = new Color(255, 255, 255); // White
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    public int getXforCenterText(Graphics2D g2, GamePanel gp, String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2 ;
        return x;
    }
    public int getXforAlignToRightText(Graphics2D g2, String text, int tailX) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - length;
        return x;
    }
}
