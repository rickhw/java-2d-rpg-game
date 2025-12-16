package gtcafe.rpg.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.UI;
import gtcafe.rpg.state.GameState;

public class OptionsScreen {
    
    GamePanel gp;
    UI ui;

    public OptionsScreen(GamePanel gp, UI ui) {
        this.gp = gp;
        this.ui = ui;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        // SUB WINDOW
        int frameX = gp.tileSize * 6;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;
        ui.uiUtil.drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight);

        switch (ui.subState) {
            case 0: options_top(g2, frameX, frameY); break;
            case 1: options_fullScreenNotification(g2, frameX, frameY); break;
            case 2: options_control(g2, frameX, frameY); break;
            case 3: options_endGameConfirmation(g2, frameX, frameY); break;
        }

        gp.keyHandler.enterPressed = false;
    }

    public void options_top(Graphics2D g2, int frameX, int frameY) {
        int textX;
        int textY;

        // TITLE
        String text = "Options";
        textX = ui.uiUtil.getXforCenterText(g2, gp, text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        // FULL SCREEN ON/OFF
        textX = frameX + gp.tileSize;
        textY += gp.tileSize * 2;
        g2.drawString("Full Screen", textX, textY);
        if (ui.commandNum == 0) { 
            g2.drawString(">", textX-25, textY); 
            if (gp.keyHandler.enterPressed == true) {
                gp.fullScreenOn = (gp.fullScreenOn == false ? true : false);
                ui.subState = 1;
            }
        }

        // MUSIC
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if (ui.commandNum == 1) { g2.drawString(">", textX-25, textY); }

        // Sound Effect
        textY += gp.tileSize;
        g2.drawString("Sound Effect", textX, textY);
        if (ui.commandNum == 2) { g2.drawString(">", textX-25, textY); }

        // Keyboard Control
        textY += gp.tileSize;
        g2.drawString("Control", textX, textY);
        if (ui.commandNum == 3) { 
            g2.drawString(">", textX-25, textY); 
            if(gp.keyHandler.enterPressed == true) {
                ui.subState = 2;
                ui.commandNum = 0;
            }
        }

        // END Game
        textY += gp.tileSize;
        g2.drawString("End Game", textX, textY);
        if (ui.commandNum == 4) { 
            g2.drawString(">", textX-25, textY); 
            if(gp.keyHandler.enterPressed == true) {
                ui.subState = 3;
                ui.commandNum = 0;
            }
        }

        // Back
        textY += gp.tileSize * 2;
        g2.drawString("Back", textX, textY);
        if (ui.commandNum == 5) { 
            g2.drawString(">", textX-25, textY); 
            if (gp.keyHandler.enterPressed == true) {
                gp.gameState = GameState.PLAY;
                ui.commandNum = 0;
            }
        }


        // FULL SCREEN CHECKBOX
        textX = frameX + (int) (gp.tileSize * 4.5);
        textY = frameY + gp.tileSize * 2 + 24;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(textX, textY, 24, 24);
        if (gp.fullScreenOn == true) {
            g2.fillRect(textX, textY, 24, 24);
        }

        // MUSIC VOLUME
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 120, 24); // 120 / 5 = 24
        int volumeWidth = 24 * gp.music.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        // SE VOLUME
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 120, 24);
        volumeWidth = 24 * gp.soundEffect.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

    }

    public void options_fullScreenNotification(Graphics2D g2, int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 2;

        ui.currentDialogue = "The change will take \neffect after restarting \nthe game.";

        for (String line : ui.currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // BACK
        textY += frameY + gp.tileSize * 3;
        g2.drawString("Back", textX, textY);
        if (ui.commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                ui.subState = 0;
            }
        }
    }

    public void options_control(Graphics2D g2, int frameX, int frameY) {
        int textX;
        int textY;

        // TITLE
        String text = "Control";
        textX = ui.uiUtil.getXforCenterText(g2, gp, text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        textX = frameX + gp.tileSize;
        textY += gp.tileSize;
        g2.drawString("Move", textX, textY); textY += gp.tileSize;
        g2.drawString("Confirm/Attack", textX, textY); textY += gp.tileSize;
        g2.drawString("Shoot/Cast", textX, textY); textY += gp.tileSize;
        g2.drawString("Pause", textX, textY); textY += gp.tileSize;
        g2.drawString("Options", textX, textY); textY += gp.tileSize;


        // DISPLAY THE ASIGNED KEY
        textX = frameX + gp.tileSize * 6;
        textY = frameY + gp.tileSize * 3; 
        g2.drawString("WASD", textX, textY); textY += gp.tileSize;
        g2.drawString("ENTER/SPACE", textX, textY); textY += gp.tileSize;
        g2.drawString("F/J", textX, textY); textY += gp.tileSize;
        g2.drawString("P", textX, textY); textY += gp.tileSize;
        g2.drawString("ESC", textX, textY); textY += gp.tileSize;

        // BACK
        textX = frameX + gp.tileSize;
        textY = frameY + gp.tileSize * 6;
        g2.drawString("Back", textX, textY);
        if (ui.commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                ui.subState = 0;
            }
        }
    }

    private void options_endGameConfirmation(Graphics2D g2, int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 2;

        ui.currentDialogue = "Quit the game and \nreturn to the title screen?";

        for (String line : ui.currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // YES
        String text = "Yes";
        textX = ui.uiUtil.getXforCenterText(g2, gp, text);
        textY += gp.tileSize * 3;
        g2.drawString(text, textX, textY);
        if (ui.commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                ui.subState = 0;
                gp.stopBackgroundMusic();
                gp.gameState = GameState.TITLE;
                gp.resetGame(true);
            }
        }

        // NO 
        text = "No";
        textX = ui.uiUtil.getXforCenterText(g2, gp, text);
        textY += gp.tileSize ;
        g2.drawString(text, textX, textY);
        if (ui.commandNum == 1) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                ui.subState = 0;
                ui.commandNum = 4;
            }
        }
    }
}