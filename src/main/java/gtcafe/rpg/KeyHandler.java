package gtcafe.rpg;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    GamePanel gp;

    // DEBUG
    boolean checkDrawTime = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if(gp.ui.titleScreenState == 0) {
            if (gp.gameState == GamePanel.TITLE_STATE) {
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 0) {
                        gp.ui.commandNum = 2;
                    }
                }
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 2) {
                        gp.ui.commandNum = 0;
                    }
                }
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                    if (gp.ui.commandNum == 0) {
                        // gp.gameState = GamePanel.PLAY_STATE;
                        gp.ui.titleScreenState = 1;
                    }
                    if (gp.ui.commandNum == 1) {
                        // TODO
                    }
                    if (gp.ui.commandNum == 2) {
                        System.exit(0);
                    }
                }
            }
        }
        else if(gp.ui.titleScreenState == 1) {
            if (gp.gameState == GamePanel.TITLE_STATE) {
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
                        System.out.print("Do some fighter specific stuff!");
                        gp.gameState = GamePanel.PLAY_STATE;
                        gp.playMusic(Sound.MUSIC__MAIN_THEME);
                    }
                    if (gp.ui.commandNum == 1) {
                        System.out.print("Do some thief specific stuff!");
                        gp.gameState = GamePanel.PLAY_STATE;
                        gp.playMusic(Sound.MUSIC__MAIN_THEME);
                    }
                    if (gp.ui.commandNum == 2) {
                        System.out.print("Do some sorcerer specific stuff!");
                        gp.gameState = GamePanel.PLAY_STATE;
                        gp.playMusic(Sound.MUSIC__MAIN_THEME);
                    }
                    if (gp.ui.commandNum == 3) {
                        gp.ui.titleScreenState = 0;
                    }
                }
            }
        }

        // PLAY STATE
        if (gp.gameState == GamePanel.PLAY_STATE) {
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

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                enterPressed = true;
            }


            if (code == KeyEvent.VK_P) {
                gp.gameState = GamePanel.PAUSE_STATE;
                gp.stopMusic();
            }

            // DEBUG
            if (code == KeyEvent.VK_T) {
                if(checkDrawTime == false) {
                    checkDrawTime = true;
                } else if (checkDrawTime = true) {
                    checkDrawTime = false;
                }
            }
        }
        
        // PAUSE STATE
        else if (gp.gameState == GamePanel.PAUSE_STATE) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = GamePanel.PLAY_STATE;
            }
        }

        // DIALOGUE STATE
        else if (gp.gameState == GamePanel.DIALOGUE_STATE) {
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                gp.gameState = GamePanel.PLAY_STATE;
            }
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
}
