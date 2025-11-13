// day7-4-3 start
package gtcafe.rpg.object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_Chest extends SuperObject {
    public OBJ_Chest() {
        name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/objects/chest.png"));
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}
// day7-4-3 end
