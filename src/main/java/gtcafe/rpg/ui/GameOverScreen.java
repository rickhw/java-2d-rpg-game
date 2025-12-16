package gtcafe.rpg.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.UI;

public class GameOverScreen {
    
    GamePanel gp;
    UI ui;

    public GameOverScreen(GamePanel gp, UI ui) {
        this.gp = gp;
        this.ui = ui;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0,0,0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

        text = "Game Over";
        // Shadow
        g2.setColor(Color.black);
        x = ui.uiUtil.getXforCenterText(g2, gp, text);
        y = gp.tileSize * 4;
        g2.drawString(text, x, y);
        // Main
        g2.setColor(Color.white);
        g2.drawString(text, x-4, y-4);

        // Option: Retry
        g2.setFont(g2.getFont().deriveFont(50f));
        text = "Retry";
        x = ui.uiUtil.getXforCenterText(g2, gp, text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if (ui.commandNum == 0) {
            g2.drawString(">", x-40, y);
        }

        // Option: Back to Title Screen
        text = "Quit";
        x = ui.uiUtil.getXforCenterText(g2, gp, text);
        y += 55;
        g2.drawString(text, x, y);
        if (ui.commandNum == 1) {
            g2.drawString(">", x-40, y);
        }
    }
}