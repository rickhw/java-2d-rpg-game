package gtcafe.rpg.environment;

import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;

public class EnvironmentManager {
    GamePanel gp;
    Lighting lighting;

    public EnvironmentManager(GamePanel gp)  {
        this.gp = gp;
    }

    public void setup() {
        // circleSize < screenHight
        lighting = new Lighting(gp, 550);
    }

    public void draw(Graphics2D g2) {
        lighting.draw(g2);
    }
}
