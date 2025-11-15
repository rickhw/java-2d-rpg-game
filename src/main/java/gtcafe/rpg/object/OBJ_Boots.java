// day9-1 start
package gtcafe.rpg.object;

import java.io.IOException;

import javax.imageio.ImageIO;

// increase player's speed
public class OBJ_Boots extends SuperObject {
    public OBJ_Boots() {
        name = "Boots";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/objects/boots.png"));
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}
// day9-1 end