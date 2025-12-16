package gtcafe.rpg.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.UI;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.system.Sound;

public class DialogueScreen {
    
    GamePanel gp;
    UI ui;

    public DialogueScreen(GamePanel gp, UI ui) {
        this.gp = gp;
        this.ui = ui;
    }

    public void draw(Graphics2D g2) {
        // WINDOW
        int x = gp.tileSize * 3;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 5);
        int height = gp.tileSize * 3;
        ui.uiUtil.drawSubWindow(g2, x, y, width, height);

        // Draw Text on DialogWindow
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        x += gp.tileSize;
        y += gp.tileSize;

        if(ui.npc.dialogues[ui.npc.dialogueSet][ui.npc.dialogueIndex] != null) {
            ui.currentDialogue = ui.npc.dialogues[ui.npc.dialogueSet][ui.npc.dialogueIndex];

            char chars[] = ui.npc.dialogues[ui.npc.dialogueSet][ui.npc.dialogueIndex].toCharArray();
            if (ui.charIdx < chars.length) {
                gp.playSoundEffect(Sound.FX__SPEAK);
                String s = String.valueOf(chars[ui.charIdx]);
                ui.combinedText = ui.combinedText + s;
                ui.currentDialogue = ui.combinedText;
                
                ui.charIdx++;
            }

            if (gp.keyHandler.enterPressed == true) {

                ui.charIdx = 0;
                ui.combinedText = "";

                if(gp.gameState == GameState.DIALOGUE || gp.gameState == GameState.CUTSENSE) {
                    ui.npc.dialogueIndex++;
                    gp.keyHandler.enterPressed = false;
                }
            }
        } 
        // this conversion is over
        else {
            ui.npc.dialogueIndex = 0;
            if (gp.gameState == GameState.DIALOGUE) {
                gp.gameState = GameState.PLAY;
            }
            // Cutsense 往下個 Phase 走
            if (gp.gameState == GameState.CUTSENSE) {
                gp.csManager.scenePhase++;
            }
        }

        for(String line : ui.currentDialogue.split("\n")) { 
            g2.drawString(line, x, y);
            y += 40;
        }
    }
}