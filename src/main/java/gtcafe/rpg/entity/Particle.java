package gtcafe.rpg.entity;
import gtcafe.rpg.core.GameContext;

import java.awt.Color;
import java.awt.Graphics2D;


public class Particle extends Entity {
    
    Entity generator;
    Color color;
    int size;

    // target to move?
    int xd;
    int yd;

    public Particle(GameContext context, Entity generator, Color color, int size, int speed, int maxLife, int xd, int yd) {
        super(context);
        this.generator = generator;
        this.color = color;
        this.size = size;
        this.speed = speed;
        this.maxLife = maxLife;
        this.xd = xd;
        this.yd = yd;

        life = maxLife; // 20 frame
        int offset = (context.getTileSize()/2) - (size/2); // set the partilce in center
        worldX = generator.worldX + offset;
        worldY = generator.worldY + offset;
    }

    public void update() {
        life --;

        // Gravity
        if (life < maxLife/3) {
            yd ++; // go down by change y value
        }

        // change the x/y by frame and speed
        worldX += xd * speed;
        worldY += yd * speed;

        if (life == 0) {
            alive = false;
        }
    }

    public void draw(Graphics2D g2) {
        int screenX = worldX - context.getPlayer().worldX + context.getPlayer().screenX;
        int screenY = worldY - context.getPlayer().worldY + context.getPlayer().screenY;

        g2.setColor(color);
        g2.fillRect(screenX, screenY, size, size);
    }
}
