package gtcafe.rpg;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, musicPressed;
    GamePanel gp;

    // DEBUG
    boolean showDebugText = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (gp.gameState == GameState.TITLE_STATE) {
            titleState(code);
        }

        // PLAY STATE
        else if (gp.gameState == GameState.PLAY_STATE) {
            playState(code);
        }
        // PAUSE STATE
        else if (gp.gameState == GameState.PAUSE_STATE) {
           pauseState(code);
        }
        // DIALOGUE STATE
        else if (gp.gameState == GameState.DIALOGUE_STATE) {
            dialogueState(code);
        }
        // CHARACTER STATE
        else if (gp.gameState == GameState.CHARACTER_STATE) {
            characterState(code);
        }

        // MUSIC EVENT
        if (code == KeyEvent.VK_M) {
            if (gp.bgmState == true) {
                gp.stopBackgroundMusic();
                gp.bgmState = false;
            } else if (gp.bgmState == false) {
                gp.playBackgroundMusic(Sound.MUSIC__MAIN_THEME);
                gp.bgmState = true;
            }
        }

        // refresh the map data for debugging
        if (code == KeyEvent.VK_R) {
            gp.tileManager.loadMap("/gtcafe/rpg/assets/maps/worldV2.txt");
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }

    private void titleState(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = 3;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 3) {
                gp.ui.commandNum = 0;
            }
        }
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            if (gp.ui.commandNum == 0) {
                System.out.println("Do some fighter specific stuff!");
                gp.gameState = GameState.PLAY_STATE;
                // gp.playMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (gp.ui.commandNum == 1) {
                System.out.println("Do some thief specific stuff!");
                gp.gameState = GameState.PLAY_STATE;
                // gp.playMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (gp.ui.commandNum == 2) {
                System.out.println("Do some sorcerer specific stuff!");
                gp.gameState = GameState.PLAY_STATE;
                // gp.playMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (gp.ui.commandNum == 3) {
                gp.ui.titleScreenState = 0;
            }
        }
    }

    private void playState(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }

        if (code == KeyEvent.VK_C) {
            gp.gameState = GameState.CHARACTER_STATE;
        }

        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            enterPressed = true;
        }

        if (code == KeyEvent.VK_Z) {
            gp.assetSetter.setMonster();
        }

        if (code == KeyEvent.VK_X) {
            gp.player.life = gp.player.maxLife;
        }


        if (code == KeyEvent.VK_P) {
            gp.gameState = GameState.PAUSE_STATE;
            gp.stopBackgroundMusic();
        }

        // DEBUG
        if (code == KeyEvent.VK_T) {
            if(showDebugText == false) {
                showDebugText = true;
                gp.debugMode = true;
            } else if (showDebugText = true) {
                showDebugText = false;
                gp.debugMode = false;
            }
        }
    }

    private void pauseState(int code) {
        if (code == KeyEvent.VK_P) {
            gp.gameState = GameState.PLAY_STATE;
        }
    }

    private void dialogueState(int code) {
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            gp.gameState = GameState.PLAY_STATE;
        }
    }

    private void characterState(int code) {
        if (code == KeyEvent.VK_C) {
            gp.gameState = GameState.PLAY_STATE;
        }
        // Move the cursor in inventory
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (gp.ui.slotRow != 0) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.slotRow--;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (gp.ui.slotRow != 3) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.slotRow++;
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (gp.ui.slotCol != 0) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.slotCol--;
            }
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (gp.ui.slotCol != 4) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.slotCol++;
            }
        }

        if (code == KeyEvent.VK_ENTER) {
            gp.player.selectItem();
        }
   }

}
