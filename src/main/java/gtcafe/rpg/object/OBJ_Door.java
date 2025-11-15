// day7-4-3 start
package gtcafe.rpg.object;

import java.io.IOException;
import javax.imageio.ImageIO;

import gtcafe.rpg.entity.Player;

public class OBJ_Door extends SuperObject{
    public OBJ_Door() {
        name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/objects/door.png"));
        } catch (IOException e ) {
            e.printStackTrace();
        }

        collision = true;
    }

    @Override
    public void interact(Player player) {
        if (player.getKeyCount() > 0) {
            player.decrementKeyCount();
            this.toBeRemoved = true;
        }
    }
}
// day7-4-3 end
