package gtcafe.rpg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import gtcafe.rpg.object.OBJ_Key;

public class UI {
    GamePanel gp;
    Font arial_40, arial_80B;
    BufferedImage keyImage;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0; // for hidden message
    public boolean gameFinished = false;

    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI (GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font(("Arial"), Font.PLAIN, 40);
        arial_80B = new Font(("Arial"), Font.BOLD, 80);
        OBJ_Key key = new OBJ_Key();
        keyImage = key.image;
    }
   
    // include game loop: 60 times per second
    // the objects in this method should not inistate each time;
    public void draw(Graphics2D g2) {

        // CHECK GAME STATUS
        if (gameFinished) {
            String text;
            int textLength;
            int x, y ;

            // draw
            g2.setFont(arial_40);
            g2.setColor(Color.white);

            text = "You found the treasure!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth(); // 取得套用自行後的長度
            x = gp.screenWidth / 2 - textLength/2;
            y = gp.screenHight / 2 - (gp.tileSize*3);
            g2.drawString(text, x, y);

            text = "You Time is :" + dFormat.format(playTime) + "!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth / 2 - textLength/2;
            y = gp.screenHight / 2 - (gp.tileSize*4);
            g2.drawString(text, x, y);


            // 
            g2.setFont(arial_80B);
            g2.setColor(Color.yellow);
            text = "Confgratulation!!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth(); // 取得套用自行後的長度
            x = gp.screenWidth / 2 - textLength/2;
            y = gp.screenHight / 2 - (gp.tileSize*2);            
            g2.drawString(text, x, y);

            // stop the game
            gp.gameThread = null;
            gp.stopMusic();;

        } else {
            g2.setFont(arial_40);
            g2.setColor(Color.white);
            g2.drawImage(keyImage, gp.tileSize/2, gp.tileSize/2, gp.tileSize/2, gp.tileSize/2, null);
            // g2.drawString("Key = " + gp.player.hasKey, 25, 50);
            g2.drawString("x " + gp.player.hasKey, 74, 65);

            // Time
            playTime += (double)1/60;
            g2.drawString("Time: " + dFormat.format(playTime), gp.tileSize*11, 65);

            // MESSAGE
            if (messageOn) {
                g2.setFont(g2.getFont().deriveFont(30F));
                g2.drawString(message, gp.tileSize/2, gp.tileSize*5);
                messageCounter++;

                if (messageCounter > 120) {
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }

    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }
}
