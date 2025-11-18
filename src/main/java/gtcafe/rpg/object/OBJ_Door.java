package gtcafe.rpg.object;

import gtcafe.rpg.GamePanel;

public class OBJ_Door extends SuperObject{
    public OBJ_Door(GamePanel gp) {
        super(gp);
        initObject("Door","/gtcafe/rpg/assets/objects/door.png"); 
        collision = true;
    }
}
