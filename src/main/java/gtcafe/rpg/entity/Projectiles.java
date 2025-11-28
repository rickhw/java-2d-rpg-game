package gtcafe.rpg.entity;

import gtcafe.rpg.Direction;
import gtcafe.rpg.GamePanel;

// 拋射物: 弓箭, 火球, 魔法 ... etc.
public class Projectiles extends Entity {
    Entity user;
    public Projectiles(GamePanel gp) {
        super(gp);
    } 

    // reset the life to the max value every time your shoot it.
    public void set(int worldX, int worldY, Direction direction, boolean alive, Entity user) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.alive = alive;
        this.user = user;
        this.life = this.maxLife;
    }

    public void update() {

        // check collision
        if (user == gp.player) {
            // if the projectile hits a monster, it dies (disappers).
            int monsterIndex = gp.collisionChecker.checkEntity(this, gp.monster);
            // player attack monster
            if (monsterIndex != 999) {
                gp.player.damageMonster(monsterIndex, attack);
                generateParticle(user.projectiles, gp.monster[monsterIndex]);
                alive = false;
            }
        }
        if (user != gp.player) {
            boolean contacPlayer = gp.collisionChecker.checkPlayer(this);
            // monster attack player
            if (gp.player.invincible == false && contacPlayer == true) {
                damagePlayer(attack);
                generateParticle(user.projectiles, gp.player);
                alive = false;
            }
        }

        switch (direction) {
            case UP -> worldY -= speed;
            case DOWN -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }

        life--;
        if (life <= 0) {
            alive = false;
        }

        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1 ? 2 : 1);
            spriteCounter = 0;
        }
    }

    // 判斷是否有足夠的魔力值可以使用
    // overwrite by subclass
    public boolean haveResource(Entity user) {
        boolean haveResource = false;
        return haveResource;
    }

    // 減去使用者的魔力值
    // overwrite by subclass
    public void subtractResource(Entity user) {

    }
}
