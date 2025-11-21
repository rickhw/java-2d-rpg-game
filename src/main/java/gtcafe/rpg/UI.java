package gtcafe.rpg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.object.OBJ_Heart;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    // Font arial_40, arial_80B;
    // Custom Font:
    // - pixel font: https://00ff.booth.pm/items/2958237
    // - https://fontsgeek.com/fonts/purisa-bold
    Font maruMonica, purisaB;
    BufferedImage heart_full, heart_half, heart_blank;

    // status
    public boolean messageOn = false;
    public String message = "";
    public boolean gameFinished = false;
    public String currentDialogue;
    
    int messageCounter = 0; // for hidden message
    public int commandNum = 0;

    // a sub state
    // 0: the first screen
    // 1: the second screen ..
    public int titleScreenState = 0;

    public UI (GamePanel gp) {
        this.gp = gp;
        loadFont();;

        // CREATE HUD OBJECT
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/gtcafe/rpg/assets/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);

            is = getClass().getResourceAsStream("/gtcafe/rpg/assets/font/Purisa Bold.ttf");
            purisaB = Font.createFont(Font.TRUETYPE_FONT, is);
        }  catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    // include game loop: 60 times per second
    // the objects in this method should not inistate each time;
    public void draw(Graphics2D g2) {

        this.g2 = g2;
        g2.setFont(maruMonica);
        // g2.setFont(purisaB);
        g2.setColor(Color.white);
        
        // TITLE STATE
        if (gp.gameState == GameState.TITLE_STATE) {
            drawTitleScreen();
        }

        // PLAY STATE
        if(gp.gameState == GameState.PLAY_STATE) {
            drawPlayerLife();
        }

        // PAUSE STATE
        if (gp.gameState == GameState.PAUSE_STATE) {
            drawPlayerLife();
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (gp.gameState == GameState.DIALOGUE_STATE) {
            drawPlayerLife();
            drawDialogusScreen();
        }
    }

    private void drawPlayerLife() {

        // gp.player.life = 5; // For Test.

        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int i = 0;

        // DRAW MAX HEART
        while (i < gp.player.maxLife / 2 ) {
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += gp.tileSize;
        }

        // RESET
        x = gp.tileSize / 2;
        y = gp.tileSize / 2;
        i = 0;

        // DRAW CURRENT LIFE
        while (i < gp.player.life) {
            g2.drawImage(heart_half, x, y, null);
            i++;

            // replace heart_helf
            if (i < gp.player.life) {
                g2.drawImage(heart_full, x, y, null);
            }
            i++;
            x += gp.tileSize;
        }
    }

    private void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // TITLE NAME
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "Rick Boy Adventure";
            int x = getXforCenterText(text);
            int y = gp.tileSize * 3;

            // SHADOW
            g2.setColor(Color.gray);
            g2.drawString(text, x+5, y+5);

            // MAIN COLOR
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // BLUE BOY IMAGE
            x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2;
            y += gp.tileSize * 2;
            g2.drawImage(gp.player.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);

            // MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F)); 

            text = "NEW GAME";
            x = getXforCenterText(text);
            y += gp.tileSize * 3.5;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "LOAD GAME";
            x = getXforCenterText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "QUIT";
            x = getXforCenterText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x-gp.tileSize, y);
            }
        }
        if (titleScreenState == 1) {

            // CLASS SELECTION SCREEN
            g2.setColor(Color.white);   
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select your class!";
            int x = getXforCenterText(text);
            int y = gp.tileSize * 3;
            g2.drawString(text, x, y);

            text = "Fighter";
            x = getXforCenterText(text);
            y += gp.tileSize * 3;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Thief";
            x = getXforCenterText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Sorcerer";
            x = getXforCenterText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Back";
            x = getXforCenterText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNum == 3) {
                g2.drawString(">", x - gp.tileSize, y);
            }
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
        g2.setFont(purisaB);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        g2.drawString(text, x, y);
    }

    public int getXforCenterText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2 ;
        return x;
    }
}
