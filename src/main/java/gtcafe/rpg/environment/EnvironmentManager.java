package gtcafe.rpg.environment;
import gtcafe.rpg.core.GameContext;

import java.awt.Graphics2D;


public class EnvironmentManager {
    GameContext context;
    public Lighting lighting;

    public EnvironmentManager(GameContext context)  {
        this.context = context;
    }

    public void setup() {
        lighting = new Lighting(context);
    }

    public void update() {
        lighting.update();
    }

    public void draw(Graphics2D g2) {
        lighting.draw(g2);
    }
}
