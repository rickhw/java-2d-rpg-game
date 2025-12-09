package gtcafe.rpg.entity.projectile;
import gtcafe.rpg.core.GameContext;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.state.Direction;

// 拋射物: 弓箭, 火球, 魔法 ... etc.
public class Projectile extends Entity {
    Entity user;
    public Projectile(GameContext context) {
        super(context);
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
        int mapIndex = context.getCurrentMap().index;

        // check collision
        if (user == context.getPlayer()) {
            // if the projectile hits a monster, it dies (disappers).
            int monsterIndex = context.getCollisionChecker().checkEntity(this, context.getMonster());
            // player attack monster
            if (monsterIndex != 999) {
                context.getPlayer().damageMonster(monsterIndex, this, attack, knockBackPower);
                generateParticle(user.projectile, context.getMonster()[mapIndex][monsterIndex]);
                alive = false;
            }
        } 
        else {
            boolean contacPlayer = context.getCollisionChecker().checkPlayer(this);
            // monster attack player
            if (context.getPlayer().invincible == false && contacPlayer == true) {
                damagePlayer(attack);
                generateParticle(user.projectile, user.projectile);
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
