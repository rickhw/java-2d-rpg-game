package gtcafe.rpg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;

    // status
    public boolean messageOn = false;
    public String message = "";
    public boolean gameFinished = false;
    
    int messageCounter = 0; // for hidden message
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI (GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font(("Arial"), Font.PLAIN, 40);
        arial_80B = new Font(("Arial"), Font.BOLD, 80);
    }
   
    // include game loop: 60 times per second
    // the objects in this method should not inistate each time;
    public void draw(Graphics2D g2) {

        this.g2 = g2;

        g2.setFont(arial_40);
        g2.setColor(Color.white);
        
        if(gp.gameState == GamePanel.STATE_PLAY) {
            // Do playState stuff later
        }

        if (gp.gameState == GamePanel.STATE_PAUSE) {
            drawPauseScreen();
        }
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void drawPauseScreen() {
        String text = "PAUSED";
        int x = getXforCenterText(text);
        int y = gp.screenHight / 2 ;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        g2.drawString(text, x, y);
    }

    public int getXforCenterText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2 ;
        return x;
    }
}
