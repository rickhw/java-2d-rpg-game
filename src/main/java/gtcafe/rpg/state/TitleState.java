package gtcafe.rpg.state;

import gtcafe.rpg.core.GameContext;
import java.awt.Graphics2D;

public class TitleState implements State {
    GameContext context;

    public TitleState(GameContext context) {
        this.context = context;
    }

    @Override
    public void setup() {
        // Any specific setup for title screen
    }

    @Override
    public void update() {
        // Currently, title screen logic is mostly in UI and KeyHandler.
        // We will migrate logic here eventually, but for now, 
        // the GamePanel updates nothing for Title State except checking inputs.
        // Ideally, KeyHandler inputs should change state here.
    }

    @Override
    public void draw(Graphics2D g2) {
        if (context.getGameUI() != null) {
            context.getGameUI().draw(g2); // This will handle drawing the title screen based on UI internal state
        }
    }
}
