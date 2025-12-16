package gtcafe.rpg.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.UI;

public class PauseScreen {
    
    GamePanel gp;
    UI ui;
    Hud hud;

    public PauseScreen(GamePanel gp, UI ui, Hud hud) {
        this.gp = gp;
        this.ui = ui;
        this.hud = hud;
    }

    public void draw(Graphics2D g2) {
        hud.drawPlayerLife(g2);
        
        g2.setFont(ui.purisaB);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));

        String text = "PAUSED";
        int x = ui.uiUtil.getXforCenterText(g2, gp, text);
        int y = gp.screenHeight / 2 ;
        
        g2.drawString(text, x, y);
    }
}