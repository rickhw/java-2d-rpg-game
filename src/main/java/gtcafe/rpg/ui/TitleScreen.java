package gtcafe.rpg.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.Main;
import gtcafe.rpg.UI;

public class TitleScreen {
    
    GamePanel gp;
    UI ui;

    public TitleScreen(GamePanel gp, UI ui) {
        this.gp = gp;
        this.ui = ui;
    }

    public void draw(Graphics2D g2) {
        if (ui.titleScreenState == 0) {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // TITLE NAME
            g2.setFont(ui.purisaB);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = Main.GAME_TITLE;
            int x = ui.uiUtil.getXforCenterText(g2, gp, text);
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
            x = ui.uiUtil.getXforCenterText(g2, gp, text);
            y += gp.tileSize * 3.5;
            g2.drawString(text, x, y);
            if (ui.commandNum == 0) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "LOAD GAME";
            x = ui.uiUtil.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (ui.commandNum == 1) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "QUIT";
            x = ui.uiUtil.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (ui.commandNum == 2) {
                g2.drawString(">", x-gp.tileSize, y);
            }
        }
        if (ui.titleScreenState == 1) {

            // CLASS SELECTION SCREEN
            g2.setColor(Color.white);   
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select your class!";
            int x = ui.uiUtil.getXforCenterText(g2, gp, text);
            int y = gp.tileSize * 3;
            g2.drawString(text, x, y);

            text = "Fighter";
            x = ui.uiUtil.getXforCenterText(g2, gp, text);
            y += gp.tileSize * 3;
            g2.drawString(text, x, y);
            if (ui.commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Thief";
            x = ui.uiUtil.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (ui.commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Sorcerer";
            x = ui.uiUtil.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (ui.commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Back";
            x = ui.uiUtil.getXforCenterText(g2, gp, text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (ui.commandNum == 3) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        }
    }
}