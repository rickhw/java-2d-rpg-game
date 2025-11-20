package gtcafe.rpg.object;

import java.io.IOException;

import javax.imageio.ImageIO;

import gtcafe.rpg.GamePanel;

public class OBJ_Heart extends SuperObject {
    public OBJ_Heart(GamePanel gp) {
        super(gp);
        this.name = "Heart";
        initObject();
    }

    protected void initObject() {
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/objects/heart_full.png"));
            this.image2 = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/objects/heart_half.png"));
            this.image3 = ImageIO.read(getClass().getResourceAsStream("/gtcafe/rpg/assets/objects/heart_blank.png"));
            image = uTools.scaleImage(image, gp.tileSize, gp.tileSize);
            image2 = uTools.scaleImage(image2, gp.tileSize, gp.tileSize);
            image3 = uTools.scaleImage(image3, gp.tileSize, gp.tileSize);
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}
