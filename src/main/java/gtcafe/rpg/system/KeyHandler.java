package gtcafe.rpg.system;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.tile.Map;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, musicPressed;
    public boolean shotKeyPressed; // means shot projectiles
    public boolean spacePressed;
    public boolean godModeOn = false;
    public GamePanel gp;

    // DEBUG
    public boolean showDebugText = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (gp.gameState == GameState.TITLE) {
            titleState(code);
        }

        // PLAY STATE
        else if (gp.gameState == GameState.PLAY) {
            playState(code);
        }
        // PAUSE STATE
        else if (gp.gameState == GameState.PAUSE) {
           pauseState(code);
        }
        // DIALOGUE STATE
        else if (gp.gameState == GameState.DIALOGUE || gp.gameState == GameState.CUTSENSE) {
            dialogueState(code);
        }
        // CHARACTER STATE
        else if (gp.gameState == GameState.CHARACTER) {
            characterState(code);
        }

        // OPTIONS STATE
        else if (gp.gameState == GameState.OPTIONS) {
            optionsState(code);
        }

        // Trade STATE
        else if (gp.gameState == GameState.TRADE) {
            tradeState(code);
        }

        // GAME OVER STATE
        else if (gp.gameState == GameState.GAME_OVER) {
            gameOverState(code);
        }

        // MAP STATE
        else if (gp.gameState == GameState.DISPLAY_MAP) {
            mapState(code);
        }

        // For Debugging
        // refresh the map data
        if (code == KeyEvent.VK_R) {
            switch(gp.currentMap) {
                case WORLD_MAP:
                    gp.tileManager.loadMap("/gtcafe/rpg/assets/maps/worldV3.txt", Map.WORLD_MAP);
                    break;
                case STORE:
                    gp.tileManager.loadMap("/gtcafe/rpg/assets/maps/interior01.txt", Map.STORE); 
                    break;
            }
        }
    }


    private void mapState(int code) {
        if (code == KeyEvent.VK_M) {
            gp.gameState = GameState.PLAY;
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

        if (code == KeyEvent.VK_F || code == KeyEvent.VK_J) {
            shotKeyPressed = false;
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }

        if (code == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

    private void titleState(int code) {
        // Control the Cursor
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.commandNum--;
            gp.playSoundEffect(Sound.FX__CURSOR);
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = 2;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.commandNum++;
            gp.playSoundEffect(Sound.FX__CURSOR);
            if (gp.ui.commandNum > 2) {
                gp.ui.commandNum = 0;
            }
        }

        // Handle the options
        if (code == KeyEvent.VK_ENTER) {
            if (gp.ui.commandNum == 0) {
                gp.gameState = GameState.PLAY;
                gp.playBackgroundMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (gp.ui.commandNum == 1) {
                gp.saveLoad.load();
                gp.gameState = GameState.PLAY;
                gp.playBackgroundMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (gp.ui.commandNum == 2) {
                gp.gameState = GameState.PLAY;
                System.exit(0);
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

        if (code == KeyEvent.VK_F || code == KeyEvent.VK_J) {
            shotKeyPressed = true;
        }

        if (code == KeyEvent.VK_C) {
            gp.gameState = GameState.CHARACTER;
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        if (code == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }

        if (code == KeyEvent.VK_P) {
            gp.gameState = GameState.PAUSE;
            gp.stopBackgroundMusic();
        }

        if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GameState.OPTIONS;
        }

        if (code == KeyEvent.VK_M) {
            gp.gameState = GameState.DISPLAY_MAP;
        }
        if (code == KeyEvent.VK_V) {
            if(gp.map.miniMapOn == false) {
                gp.map.miniMapOn = true;
            } else {
                gp.map.miniMapOn = false;
            }
        }

        // DEBUG
        if (code == KeyEvent.VK_T) {
            if(showDebugText == false) {
                showDebugText = true;
                // gp.debugMode = true;
            } else if (showDebugText = true) {
                showDebugText = false;
                // gp.debugMode = false;
            }
        }

        // For Debugging
        if (code == KeyEvent.VK_Z) {
            gp.assetSetter.setMonster();
        }

        if (code == KeyEvent.VK_X) {
            // gp.player.life = gp.player.maxLife;
            // gp.player.mana = gp.player.maxMana;
            if (godModeOn == false) {
                godModeOn = true;
            } else {
                godModeOn = false;
            }
        }

    }

    private void pauseState(int code) {
        if (code == KeyEvent.VK_P) {
            gp.gameState = GameState.PLAY;
        }
    }

    private void dialogueState(int code) {
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
    }

    private void characterState(int code) {
        if (code == KeyEvent.VK_C) {
            gp.gameState = GameState.PLAY;
        }
        if (code == KeyEvent.VK_ENTER) {
            gp.player.selectItem();
        }
        playerInventory(code);
    }

    // Move the cursor in inventory
    public void playerInventory(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (gp.ui.playerSlotRow != 0) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.playerSlotRow--;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (gp.ui.playerSlotRow != 3) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.playerSlotRow++;
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (gp.ui.playerSlotCol != 0) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.playerSlotCol--;
            }
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (gp.ui.playerSlotCol != 4) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.playerSlotCol++;
            }
        }
    }

    public void npcInventory(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (gp.ui.npcSlotRow != 0) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.npcSlotRow--;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (gp.ui.npcSlotRow != 3) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.npcSlotRow++;
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (gp.ui.npcSlotCol != 0) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.npcSlotCol--;
            }
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (gp.ui.npcSlotCol != 4) {
                gp.playSoundEffect(Sound.FX__CURSOR);
                gp.ui.npcSlotCol++;
            }
        }
    }


    private void optionsState(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GameState.PLAY;
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        int maxCommandNum = 0;
        switch (gp.ui.subState) {
            case 0: maxCommandNum = 5; break; // max 
            case 3: maxCommandNum = 1; break; // max 
        }
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.commandNum--;
            gp.playSoundEffect(Sound.FX__CURSOR);
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = maxCommandNum; // loop the cursor
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.commandNum++;
            gp.playSoundEffect(Sound.FX__CURSOR);
            if (gp.ui.commandNum > maxCommandNum) { 
                gp.ui.commandNum = 0;
            }
        }

        // Volume desrease
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (gp.ui.subState == 0) {
                if (gp.ui.commandNum == 1 && gp.music.volumeScale > 0) {
                    gp.music.volumeScale--;
                    gp.music.checkVolume();
                    gp.playSoundEffect(Sound.FX__CURSOR);
                }
                if (gp.ui.commandNum == 2 && gp.soundEffect.volumeScale > 0) {
                    gp.soundEffect.volumeScale--;
                    // gp.soundEffect.checkVolume();
                    gp.playSoundEffect(Sound.FX__CURSOR);
                }
            }
        }
        // Volume desrease
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (gp.ui.subState == 0) {
                if (gp.ui.commandNum == 1 && gp.music.volumeScale < 5) {
                    gp.music.volumeScale++;
                    gp.music.checkVolume();
                    gp.playSoundEffect(Sound.FX__CURSOR);
                }
                if (gp.ui.commandNum == 2 && gp.soundEffect.volumeScale < 5) {
                    gp.soundEffect.volumeScale++;
                    gp.playSoundEffect(Sound.FX__CURSOR);
                }
            }
        }

    }

    private void gameOverState(int code) {
        // CURSOR
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = 1;
            }
            gp.playSoundEffect(Sound.FX__CURSOR);
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum < 1) {
                gp.ui.commandNum = 0;
            }
            gp.playSoundEffect(Sound.FX__CURSOR);
        }

        if (code == KeyEvent.VK_ENTER) {
            // Retry
            if (gp.ui.commandNum == 0) {
                gp.gameState = GameState.PLAY;
                gp.resetGame(false);
                gp.playBackgroundMusic(Sound.MUSIC__MAIN_THEME);
            }
            // Restore
            else if (gp.ui.commandNum == 1) {
                gp.ui.commandNum = 0;  //
                gp.gameState = GameState.TITLE;
                gp.resetGame(true);
            }
        }
    }

    private void tradeState(int code) {
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        if (gp.ui.subState == 0) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if(gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 2;
                }
                gp.playSoundEffect(Sound.FX__CURSOR);
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 2) {
                    gp.ui.commandNum = 0;
                }
                gp.playSoundEffect(Sound.FX__CURSOR);
            }
        }
        if (gp.ui.subState == 1) {
            npcInventory(code);
            if (code == KeyEvent.VK_ESCAPE) {
                gp.ui.subState = 0;
            }
        }
        if (gp.ui.subState == 2) {
            playerInventory(code);
            if (code == KeyEvent.VK_ESCAPE) {
                gp.ui.subState = 0;
            }
        }
    }
}
