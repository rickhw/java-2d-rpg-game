package gtcafe.rpg.state;

import java.awt.Graphics2D;

public interface State {
    public void setup();
    public void update();
    public void draw(Graphics2D g2);
}
