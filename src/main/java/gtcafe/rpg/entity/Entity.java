package gtcafe.rpg.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gtcafe.rpg.Direction;

public class Entity {
    // Position
    public int worldX, worldY;
    public int speed;

    // Animation
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public Direction direction;

    // Sprite animation
    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY; // day8-1
    public boolean collisionOn = false;
}
