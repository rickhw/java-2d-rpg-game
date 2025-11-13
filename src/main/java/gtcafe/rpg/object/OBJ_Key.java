// day7-2-2 start
package gtcafe.rpg.object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_Key extends SuperObject {
    public OBJ_Key() {
        name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/objects/key.png"));
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}
// day7-2-2 end