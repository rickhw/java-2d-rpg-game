package gtcafe.rpg;

import java.awt.Graphics2D;

import gtcafe.rpg.entity.PlayerDummy;
import gtcafe.rpg.entity.monster.MON_SkeletonLord;
import gtcafe.rpg.entity.object.OBJ_Door_Iron;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.system.Sound;

public class CutsenseManager {
    GamePanel gp;
    Graphics2D g2;
    public int sceneNum;
    public int scenePhase;

    // Scene Number;
    public final int NA = 0;
    public final int skeletonlord = 1;

    public CutsenseManager(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        // for multiple sense
        switch(sceneNum) {
            case skeletonlord: sense_skeletonLord(); break;
        }
    }

    private void sense_skeletonLord() {
        // Phase 0: Placing Iron Doors
        if (scenePhase == 0) {
            gp.bossBattleOn = true;

            // Shut the iron door
            for(int i=0; i<gp.obj[1].length; i++) {
                if (gp.obj[gp.currentMap.index][i] == null) {
                    gp.obj[gp.currentMap.index][i] = new OBJ_Door_Iron(gp);
                    gp.obj[gp.currentMap.index][i].worldX = gp.tileSize * 25;
                    gp.obj[gp.currentMap.index][i].worldY = gp.tileSize * 28;
                    gp.obj[gp.currentMap.index][i].tempObj = true;  // 暫時的物件，只有在 boss battle 才會出現
                    gp.playSoundEffect(Sound.FX__DOOR_OPEN);
                    break;
                }
            }

            // Search a vacant slot for the dummy
            for(int i=0; i<gp.npc[1].length; i++) {
                if(gp.npc[gp.currentMap.index][i] == null) {
                    gp.npc[gp.currentMap.index][i] = new PlayerDummy(gp);
                    gp.npc[gp.currentMap.index][i].worldX = gp.player.worldX;
                    gp.npc[gp.currentMap.index][i].worldY = gp.player.worldY;
                    gp.npc[gp.currentMap.index][i].direction = gp.player.direction;
                    break;
                }
            }

            gp.player.drawing = false;
            scenePhase++;
            // Music
            gp.stopBackgroundMusic();;
            gp.playBackgroundMusic(Sound.MUSIC__FINAL_BATTLE);

        }
        // Phase 1: Moving the camera
        // 1.1 keep player in fixed postion
        // 1.2 moving the camera
        else if (scenePhase == 1) {

            gp.player.worldY -= 2; // 讓 Player 繼續走, 然後把 Player 影像暫時消失，行程移動 Camera 的效果

            if (gp.player.worldY < gp.tileSize * 16) {
                scenePhase ++;
            }
        }
        // Phase 2: Wake up the boss, and speak
        else if (scenePhase == 2) {
            // Search the boss
            for (int i=0; i<gp.monster[1].length; i++) {
                if (gp.monster[gp.currentMap.index][i] != null
                        && gp.monster[gp.currentMap.index][i].name.equals(MON_SkeletonLord.OBJ_NAME)) {
                    
                    gp.monster[gp.currentMap.index][i].sleep = false;
                    // speak
                    gp.ui.npc = gp.monster[gp.currentMap.index][i];
                    scenePhase++;
                    break;
                }
            }
        }
        // Phase 3: Letting the Boss Speak
        else if (scenePhase == 3) {

            gp.ui.drawDialogusScreen();
        }
        // Phase 4: return the camera to player
        else if (scenePhase == 4) {
            // Search the dummy
            for(int i=0; i<gp.npc[1].length; i++) {
                if(gp.npc[gp.currentMap.index][i] != null 
                    && gp.npc[gp.currentMap.index][i].name.equals(PlayerDummy.OBJ_NAME) ) {
                    // Restore the Player Position
                    gp.player.worldX = gp.npc[gp.currentMap.index][i].worldX;
                    gp.player.worldY = gp.npc[gp.currentMap.index][i].worldY;
                    
                    // Delete the dummy
                    gp.npc[gp.currentMap.index][i] = null;
                    break;
                }
            }

            // Start drawing the player
            gp.player.drawing = true;

            // Reset
            sceneNum = NA;
            scenePhase = 0;
            gp.gameState = GameState.PLAY;

        }
    }
}
