package gtcafe.rpg;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import gtcafe.rpg.entity.PlayerDummy;
import gtcafe.rpg.entity.monster.MON_SkeletonLord;
import gtcafe.rpg.entity.object.OBJ_BlueHeart;
import gtcafe.rpg.entity.object.OBJ_Door_Iron;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.state.Scene;
import gtcafe.rpg.system.Sound;
import gtcafe.rpg.entity.Entity;

public class CutsenseManager {
    GamePanel gp;
    Graphics2D g2;
    Graphics2DUtils g2utils = new Graphics2DUtils();
    public Scene sceneNum = Scene.NA;
    public int scenePhase;
    int counter = 0;
    float alpha = 0f;
    int y = 0;
    String endCredit;

    public CutsenseManager(GamePanel gp) {
        this.gp = gp;

        endCredit = "Program/Music/Art/Story\n"
                + "RyiSnow"
                + "\n\n\n\n\n\n\n\n\n\n\n"
                + "Special Thanks\n"
                + "Someone\n"
                + "Someone\n"
                + "Someone\n"
                + "Thank you for playing!";
        // + "\n\n\n\n\n\n\n\n\n\n\n"
        // + Main.GAME_TITLE;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        // for multiple sense
        switch (sceneNum) {
            case SKELETON_LORD -> scene_skeletonLord();
            case ENDING -> scene_ending();
            default -> {
            }
        }
    }

    private void scene_skeletonLord() {
        // Phase 0: Placing Iron Doors
        if (scenePhase == 0) {
            gp.bossBattleOn = true;

            // Shut the iron door
            OBJ_Door_Iron door = new OBJ_Door_Iron(gp);
            door.setWorldX(gp.tileSize * 25);
            door.setWorldY(gp.tileSize * 28);
            door.tempObj = true;
            gp.obj[gp.currentMap.index].add(door);
            gp.playSoundEffect(Sound.FX__DOOR_OPEN);

            // Add dummy player
            PlayerDummy dummy = new PlayerDummy(gp);
            dummy.setWorldX(gp.player.getWorldX());
            dummy.setWorldY(gp.player.getWorldY());
            dummy.direction = gp.player.direction;
            gp.npc[gp.currentMap.index].add(dummy);

            gp.player.drawing = false;
            scenePhase++;
            // Music
            gp.stopBackgroundMusic();
            ;
            gp.playBackgroundMusic(Sound.MUSIC__FINAL_BATTLE);

        }
        // Phase 1: Moving the camera
        // 1.1 keep player in fixed postion
        // 1.2 moving the camera
        else if (scenePhase == 1) {

            gp.player.setWorldY(gp.player.getWorldY() - 2); // 讓他 Player 繼續走, 然後把 Player 影像暫時消失，行程移動 Camera 的效果

            if (gp.player.getWorldY() < gp.tileSize * 16) {
                scenePhase++;
            }
        }
        // Phase 2: Wake up the boss, and speak
        else if (scenePhase == 2) {
            // Search the boss
            for (int i = 0; i < gp.monster[gp.currentMap.index].size(); i++) {
                Entity monster = gp.monster[gp.currentMap.index].get(i);
                if (monster != null
                        && monster.name.equals(MON_SkeletonLord.OBJ_NAME)) {

                    monster.sleep = false;
                    // speak
                    gp.ui.npc = monster;
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
            for (int i = 0; i < gp.npc[gp.currentMap.index].size(); i++) {
                Entity npc = gp.npc[gp.currentMap.index].get(i);
                if (npc != null
                        && npc.name.equals(PlayerDummy.OBJ_NAME)) {
                    // Restore the Player Position
                    gp.player.setWorldX(npc.getWorldX());
                    gp.player.setWorldY(npc.getWorldY());

                    // Delete the dummy
                    gp.npc[gp.currentMap.index].remove(i);
                    break;
                }
            }

            // Start drawing the player
            gp.player.drawing = true;

            // Reset
            sceneNum = Scene.NA;
            scenePhase = 0;
            gp.gameState = GameState.PLAY;

        }
    }

    private void scene_ending() {
        if (scenePhase == 0) {
            gp.stopBackgroundMusic();
            gp.ui.npc = new OBJ_BlueHeart(gp);
            scenePhase++;
        } else if (scenePhase == 1) {
            // Display dialogues
            gp.ui.drawDialogusScreen();
        } else if (scenePhase == 2) {
            // Play the fanfare
            gp.playSoundEffect(Sound.MUSIC__FANFARE);
            scenePhase++;
        } else if (scenePhase == 3) {

            // Wait until the seound effect ends
            // 300 fps = 5 s
            if (counterReached(300) == true) {
                scenePhase++;
            }
        } else if (scenePhase == 4) {
            // The screen gets darker

            alpha += 0.005f;
            if (alpha > 1f) {
                alpha = 1f;
            }
            drawBlackBackground(alpha);

            if (alpha == 1f) {
                alpha = 0;
                scenePhase++;
            }
        } else if (scenePhase == 5) {
            drawBlackBackground(1f);

            alpha += 0.005f;
            if (alpha > 1f) {
                alpha = 1f;
            }

            String text = "After the fierce battle with the Skeleton Lord,\n"
                    + "the Blue Boy finally found the legendary treasure.\n"
                    + "Bue this is not the end of his joureny.\n"
                    + "The Blue Boy's adventure has just begun.";
            drawString(alpha, 38f, 200, text, 70);

            // Waiting for 10 second
            if (counterReached(600) == true) {
                gp.playBackgroundMusic(Sound.MUSIC__MAIN_THEME);
                scenePhase++;
            }
        } else if (scenePhase == 6) {
            // Diaply the game title
            drawBlackBackground(1f);
            drawString(1f, 120f, gp.screenHeight / 2, Main.GAME_TITLE, 40);

            // Waiting for 8 second
            if (counterReached(480) == true) {
                scenePhase++;
            }
        } else if (scenePhase == 7) {
            drawBlackBackground(1f);

            y = gp.screenHeight / 2;
            drawString(1f, 38f, y, endCredit, 40);

            // Waiting for 8 second
            if (counterReached(480) == true) {
                scenePhase++;
            }
        } else if (scenePhase == 8) {
            drawBlackBackground(1f);

            // Scrolling the credit
            y--;
            drawString(1f, 38f, y, endCredit, 40);

        }
    }

    // Waiting for N second
    private boolean counterReached(int target) {
        boolean counterReached = false;
        counter++;
        if (counter > target) {
            counterReached = true;
            counter = 0;
        }

        return counterReached;
    }

    // Make the screen go to dark
    private void drawBlackBackground(float alpha) {

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawString(float alpha, float fontSize, int y, String text, int lineHeight) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(fontSize));

        for (String line : text.split("\n")) {
            int x = g2utils.getXforCenterText(g2, gp, line);
            g2.drawString(line, x, y);
            y += lineHeight;
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

}
