package gtcafe.rpg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;

    // status
    public boolean messageOn = false;
    public String message = "";
    public boolean gameFinished = false;
    public String currentDialogue;
    
    int messageCounter = 0; // for hidden message

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
        
        // PLAY STATE
        if(gp.gameState == GamePanel.PLAY_STATE) {
            // Do playState stuff later
        }

        // PAUSE STATE
        if (gp.gameState == GamePanel.PAUSE_STATE) {
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (gp.gameState == GamePanel.DIALOGUE_STATE) {
            drawDialogusScreen();
        }
    }

    public void drawDialogusScreen() {
        // WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;
        drawSubWindow(x, y, width, height);

        // Draw Text on DialogWindow
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        x += gp.tileSize;
        y += gp.tileSize;
        for(String line : currentDialogue.split("\n")) { 
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 180); // Black, alpha value = 200, make the color transparent.
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);  // draw rectangle

        c = new Color(255, 255, 255); // White
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }


    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void drawPauseScreen() {
        String text = "PAUSED";
        int x = getXforCenterText(text);
        int y = gp.screenHeight / 2 ;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        g2.drawString(text, x, y);
    }

    public int getXforCenterText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2 ;
        return x;
    }
}
