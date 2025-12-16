package gtcafe.rpg.ui;

import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;

public class PlayScreen {
    
    GamePanel gp;
    Hud hud;

    public PlayScreen(GamePanel gp, Hud hud) {
        this.gp = gp;
        this.hud = hud;
    }

    public void draw(Graphics2D g2) {
        hud.drawPlayerLife(g2);
        hud.drawMonsterLife(g2);
        hud.drawMessage(g2);
    }
}
