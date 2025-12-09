package gtcafe.rpg.system;
import gtcafe.rpg.core.GameContext;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gtcafe.rpg.state.GameState;
import gtcafe.rpg.tile.Scense;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, musicPressed;
    public boolean shotKeyPressed; // means shot projectiles
    public boolean spacePressed;
    GameContext context;

    // DEBUG
    public boolean showDebugText = false;

    public KeyHandler(GameContext context) {
        this.context = context;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (context.getGameState() == GameState.TITLE) {
            titleState(code);
        }

        // PLAY STATE
        else if (context.getGameState() == GameState.PLAYING) {
            playState(code);
        }
        // PAUSE STATE
        else if (context.getGameState() == GameState.PAUSE) {
           pauseState(code);
        }
        // DIALOGUE STATE
        else if (context.getGameState() == GameState.DIALOGUE) {
            dialogueState(code);
        }
        // CHARACTER STATE
        else if (context.getGameState() == GameState.CHARACTER) {
            characterState(code);
        }

        // OPTIONS STATE
        else if (context.getGameState() == GameState.OPTIONS) {
            optionsState(code);
        }

        // Trade STATE
        else if (context.getGameState() == GameState.TRADE) {
            tradeState(code);
        }

        // GAME OVER STATE
        else if (context.getGameState() == GameState.GAME_OVER) {
            gameOverState(code);
        }

        // MAP STATE
        else if (context.getGameState() == GameState.DISPLAY_MAP) {
            mapState(code);
        }

        // For Debugging
        // refresh the map data
        if (code == KeyEvent.VK_R) {
            switch(context.getCurrentMap()) {
                case WORLD_MAP:
                    context.getTileManager().loadMap("/gtcafe/rpg/assets/maps/worldV3.txt", Scense.WORLD_MAP);
                    break;
                case STORE:
                    context.getTileManager().loadMap("/gtcafe/rpg/assets/maps/interior01.txt", Scense.STORE); 
                    break;
            }
        }
    }


    private void mapState(int code) {
        if (code == KeyEvent.VK_M) {
            context.setGameState(GameState.PLAYING);
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
            context.getGameUI().commandNum--;
            context.playSoundEffect(Sound.FX__CURSOR);
            if (context.getGameUI().commandNum < 0) {
                context.getGameUI().commandNum = 2;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            context.getGameUI().commandNum++;
            context.playSoundEffect(Sound.FX__CURSOR);
            if (context.getGameUI().commandNum > 2) {
                context.getGameUI().commandNum = 0;
            }
        }

        // Handle the options
        if (code == KeyEvent.VK_ENTER) {
            if (context.getGameUI().commandNum == 0) {
                context.setGameState(GameState.PLAYING);
                context.playMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (context.getGameUI().commandNum == 1) {
                context.getSaveLoad().load();
                context.setGameState(GameState.PLAYING);
                context.playMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (context.getGameUI().commandNum == 2) {
                context.setGameState(GameState.PLAYING);
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
            context.setGameState(GameState.CHARACTER);
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        if (code == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }

        if (code == KeyEvent.VK_P) {
            context.setGameState(GameState.PAUSE);
            context.getMusic().stop();
        }

        if (code == KeyEvent.VK_ESCAPE) {
            context.setGameState(GameState.OPTIONS);
        }

        if (code == KeyEvent.VK_M) {
            context.setGameState(GameState.DISPLAY_MAP);
        }
        if (code == KeyEvent.VK_V) {
            if(context.getMap().miniMapOn == false) {
                context.getMap().miniMapOn = true;
            } else {
                context.getMap().miniMapOn = false;
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
            context.getAssetSetter().setMonster();
        }

        if (code == KeyEvent.VK_X) {
            context.getPlayer().life = context.getPlayer().maxLife;
            context.getPlayer().mana = context.getPlayer().maxMana;
        }

    }

    private void pauseState(int code) {
        if (code == KeyEvent.VK_P) {
            context.setGameState(GameState.PLAYING);
        }
    }

    private void dialogueState(int code) {
        if (code == KeyEvent.VK_ENTER) {
            context.setGameState(GameState.PLAYING);
        }
    }

    private void characterState(int code) {
        if (code == KeyEvent.VK_C) {
            context.setGameState(GameState.PLAYING);
        }
        if (code == KeyEvent.VK_ENTER) {
            context.getPlayer().selectItem();
        }
        playerInventory(code);
    }

    // Move the cursor in inventory
    public void playerInventory(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (context.getGameUI().playerSlotRow != 0) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().playerSlotRow--;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (context.getGameUI().playerSlotRow != 3) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().playerSlotRow++;
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (context.getGameUI().playerSlotCol != 0) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().playerSlotCol--;
            }
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (context.getGameUI().playerSlotCol != 4) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().playerSlotCol++;
            }
        }
    }

    public void npcInventory(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (context.getGameUI().npcSlotRow != 0) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().npcSlotRow--;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (context.getGameUI().npcSlotRow != 3) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().npcSlotRow++;
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (context.getGameUI().npcSlotCol != 0) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().npcSlotCol--;
            }
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (context.getGameUI().npcSlotCol != 4) {
                context.playSoundEffect(Sound.FX__CURSOR);
                context.getGameUI().npcSlotCol++;
            }
        }
    }


    private void optionsState(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            context.setGameState(GameState.PLAYING);
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        int maxCommandNum = 0;
        switch (context.getGameUI().subState) {
            case 0: maxCommandNum = 5; break; // max 
            case 3: maxCommandNum = 1; break; // max 
        }
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            context.getGameUI().commandNum--;
            context.playSoundEffect(Sound.FX__CURSOR);
            if (context.getGameUI().commandNum < 0) {
                context.getGameUI().commandNum = maxCommandNum; // loop the cursor
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            context.getGameUI().commandNum++;
            context.playSoundEffect(Sound.FX__CURSOR);
            if (context.getGameUI().commandNum > maxCommandNum) { 
                context.getGameUI().commandNum = 0;
            }
        }

        // Volume desrease
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (context.getGameUI().subState == 0) {
                if (context.getGameUI().commandNum == 1 && context.getMusic().volumeScale > 0) {
                    context.getMusic().volumeScale--;
                    context.getMusic().checkVolume();
                    context.playSoundEffect(Sound.FX__CURSOR);
                }
                if (context.getGameUI().commandNum == 2 && context.getSoundEffect().volumeScale > 0) {
                    context.getSoundEffect().volumeScale--;
                    // context.getSoundEffect().checkVolume();
                    context.playSoundEffect(Sound.FX__CURSOR);
                }
            }
        }
        // Volume desrease
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (context.getGameUI().subState == 0) {
                if (context.getGameUI().commandNum == 1 && context.getMusic().volumeScale < 5) {
                    context.getMusic().volumeScale++;
                    context.getMusic().checkVolume();
                    context.playSoundEffect(Sound.FX__CURSOR);
                }
                if (context.getGameUI().commandNum == 2 && context.getSoundEffect().volumeScale < 5) {
                    context.getSoundEffect().volumeScale++;
                    context.playSoundEffect(Sound.FX__CURSOR);
                }
            }
        }

    }

    private void gameOverState(int code) {
        // CURSOR
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            context.getGameUI().commandNum--;
            if (context.getGameUI().commandNum < 0) {
                context.getGameUI().commandNum = 1;
            }
            context.playSoundEffect(Sound.FX__CURSOR);
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            context.getGameUI().commandNum++;
            if (context.getGameUI().commandNum < 1) {
                context.getGameUI().commandNum = 0;
            }
            context.playSoundEffect(Sound.FX__CURSOR);
        }

        if (code == KeyEvent.VK_ENTER) {
            // Retry
            if (context.getGameUI().commandNum == 0) {
                context.setGameState(GameState.PLAYING);
                context.resetGame(false);
                context.playMusic(Sound.MUSIC__MAIN_THEME);
            }
            // Restore
            else if (context.getGameUI().commandNum == 1) {
                context.getGameUI().commandNum = 0;  //
                context.setGameState(GameState.TITLE);
                context.resetGame(true);
            }
        }
    }

    private void tradeState(int code) {
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        if (context.getGameUI().subState == 0) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                context.getGameUI().commandNum--;
                if(context.getGameUI().commandNum < 0) {
                    context.getGameUI().commandNum = 2;
                }
                context.playSoundEffect(Sound.FX__CURSOR);
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                context.getGameUI().commandNum++;
                if (context.getGameUI().commandNum > 2) {
                    context.getGameUI().commandNum = 0;
                }
                context.playSoundEffect(Sound.FX__CURSOR);
            }
        }
        if (context.getGameUI().subState == 1) {
            npcInventory(code);
            if (code == KeyEvent.VK_ESCAPE) {
                context.getGameUI().subState = 0;
            }
        }
        if (context.getGameUI().subState == 2) {
            playerInventory(code);
            if (code == KeyEvent.VK_ESCAPE) {
                context.getGameUI().subState = 0;
            }
        }
    }
}
