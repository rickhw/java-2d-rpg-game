package gtcafe.rpg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;
import gtcafe.rpg.state.GameState;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    // Font arial_40, arial_80B;
    // Custom Font:
    // - pixel font: https://00ff.booth.pm/items/2958237
    // - https://fontsgeek.com/fonts/purisa-bold
    Font maruMonica, purisaB;
    BufferedImage heart_full, heart_half, heart_blank;
    BufferedImage crystal_full, crystal_blank;
    BufferedImage coin;

    // status
    public boolean messageOn = false;
    public boolean gameFinished = false;
    public String currentDialogue;
    
    public int commandNum = 0;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();

    // a sub state
    // 0: the first screen
    // 1: the second screen ..
    public int titleScreenState = 0;
    int subState = 0;

    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;

    int counter = 0;
    public Entity npc;

    Graphics2DUtils g2Utils = new Graphics2DUtils();

    public UI (GamePanel gp) {
        this.gp = gp;
        loadFont();;

        // CREATE HUD OBJECT
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;

        // MANA OBJECT
        Entity crystal = new OBJ_ManaCrystal(gp);
        crystal_full = crystal.image;
        crystal_blank = crystal.image2;
        Entity bronze_coin = new OBJ_Coin_Bronze(gp);
        coin = bronze_coin.down1;
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/gtcafe/rpg/assets/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);

            is = getClass().getResourceAsStream("/gtcafe/rpg/assets/font/Purisa Bold.ttf");
            purisaB = Font.createFont(Font.TRUETYPE_FONT, is);
        }  catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    // include game loop: 60 times per second
    // the objects in this method should not inistate each time;
    public void draw(Graphics2D g2) {

        this.g2 = g2;
        g2.setFont(maruMonica);
        // g2.setFont(purisaB);
        g2.setColor(Color.white);
        
        // TITLE STATE
        if (gp.gameState == GameState.TITLE_STATE) {
            drawTitleScreen();
        }

        // PLAY STATE
        if(gp.gameState == GameState.PLAY_STATE) {
            drawPlayerLife();
            drawMessage();
        }

        // PAUSE STATE
        if (gp.gameState == GameState.PAUSE_STATE) {
            drawPlayerLife();
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (gp.gameState == GameState.DIALOGUE_STATE) {
            drawDialogusScreen();
        }

        // CHARACTER STATE
        if (gp.gameState == GameState.CHARACTER_STATE) {
            drawCharacterScreen();
            drawInventory(gp.player, true);
        }
        
        // OPTIONS STATE
        if (gp.gameState == GameState.OPTIONS_STATE) {
            drawOptionsScreen();
        }
        // GAME OVER STATE
        if (gp.gameState == GameState.GAME_OVER_STATE) {
            drawGameOverScreen();
        }

        // TRANSITION STATE
        if (gp.gameState == GameState.TRANSITION_STATE) {
            drawTransition();
        }

        // TRADE STATE
        if (gp.gameState == GameState.TRADE_STATE) {
            drawTradeScreen();
        }
    }

    private void drawTransition() {
        counter++;
        g2.setColor(new Color(0,0,0,counter*5));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (counter == 50) {
            counter = 0;
            System.out.printf("[Transition] from [%s] to [%s]\n", gp.currentMap.name, gp.eventHandler.tempMap.name);
            gp.gameState = GameState.PLAY_STATE;
            gp.currentMap = gp.eventHandler.tempMap;
            gp.player.worldX = gp.tileSize * gp.eventHandler.tempCol;
            gp.player.worldY = gp.tileSize * gp.eventHandler.tempRow;
            gp.eventHandler.previousEventX = gp.player.worldX;
            gp.eventHandler.previousEventY = gp.player.worldY;
        }
    }

    private void drawGameOverScreen() {
        g2.setColor(new Color(0,0,0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

        text = "Game Over";
        // Shadow
        g2.setColor(Color.black);
        x = g2Utils.getXforCenterText(g2, gp, text);
        y = gp.tileSize * 4;
        g2.drawString(text, x, y);
        // Main
        g2.setColor(Color.white);
        g2.drawString(text, x-4, y-4);

        // Option: Retry
        g2.setFont(g2.getFont().deriveFont(50f));
        text = "Retry";
        x = g2Utils.getXforCenterText(g2, gp, text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if (commandNum == 0) {
            g2.drawString(">", x-40, y);
        }

        // Option: Back to Title Screen
        text = "Quit";
        x = g2Utils.getXforCenterText(g2, gp, text);
        y += 55;
        g2.drawString(text, x, y);
        if (commandNum == 1) {
            g2.drawString(">", x-40, y);
        }

    }

    private void drawOptionsScreen() {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        // SUB WINDOW
        int frameX = gp.tileSize * 6;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;
        g2Utils.drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight);

        switch (subState) {
            case 0: options_top(frameX, frameY); break;
            case 1: options_fullScreenNotification(frameX, frameY); break;
            case 2: options_control(frameX, frameY); break;
            case 3: options_endGameConfirmation(frameX, frameY); break;
        }

        gp.keyHandler.enterPressed = false;

    }

    private void options_endGameConfirmation(int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 2;

        currentDialogue = "Quit the game and \nreturn to the title screen?";

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // YES
        String text = "Yes";
        textX = g2Utils.getXforCenterText(g2, gp, text);
        textY += gp.tileSize * 3;
        g2.drawString(text, textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                subState = 0;
                gp.stopBackgroundMusic();
                gp.gameState = GameState.TITLE_STATE;
            }
        }

        // NO 
        text = "No";
        textX = g2Utils.getXforCenterText(g2, gp, text);
        textY += gp.tileSize ;
        g2.drawString(text, textX, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                subState = 0;
                commandNum = 4;
            }
        }
    }

    public void options_top(int frameX, int frameY) {
        int textX;
        int textY;

        // TITLE
        String text = "Options";
        textX = g2Utils.getXforCenterText(g2, gp, text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        // FULL SCREEN ON/OFF
        textX = frameX + gp.tileSize;
        textY += gp.tileSize * 2;
        g2.drawString("Full Screen", textX, textY);
        if (commandNum == 0) { 
            g2.drawString(">", textX-25, textY); 
            if (gp.keyHandler.enterPressed == true) {
                gp.fullScreenOn = (gp.fullScreenOn == false ? true : false);
                subState = 1;
            }
        }

        // MUSIC
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if (commandNum == 1) { g2.drawString(">", textX-25, textY); }

        // Sound Effect
        textY += gp.tileSize;
        g2.drawString("Sound Effect", textX, textY);
        if (commandNum == 2) { g2.drawString(">", textX-25, textY); }

        // Keyboard Control
        textY += gp.tileSize;
        g2.drawString("Control", textX, textY);
        if (commandNum == 3) { 
            g2.drawString(">", textX-25, textY); 
            if(gp.keyHandler.enterPressed == true) {
                subState = 2;
                commandNum = 0;
            }
        }

        // END Game
        textY += gp.tileSize;
        g2.drawString("End Game", textX, textY);
        if (commandNum == 4) { 
            g2.drawString(">", textX-25, textY); 
            if(gp.keyHandler.enterPressed == true) {
                subState = 3;
                commandNum = 0;
            }
        }

        // Back
        textY += gp.tileSize * 2;
        g2.drawString("Back", textX, textY);
        if (commandNum == 5) { 
            g2.drawString(">", textX-25, textY); 
            if (gp.keyHandler.enterPressed == true) {
                gp.gameState = GameState.PLAY_STATE;
                commandNum = 0;
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

        gp.config.saveConfig();
    }

    public void options_fullScreenNotification(int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 2;

        currentDialogue = "The change will take \neffect after restarting \nthe game.";

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // BACK
        textY += frameY + gp.tileSize * 3;
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                subState = 0;
            }
        }
    }

    public void options_control(int frameX, int frameY) {
        int textX;
        int textY;

        // TITLE
        String text = "Control";
        textX = g2Utils.getXforCenterText(g2, gp, text);
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
        textY = frameY = gp.tileSize * 3;
        g2.drawString("WASD", textX, textY); textY += gp.tileSize;
        g2.drawString("ENTER/SPACE", textX, textY); textY += gp.tileSize;
        g2.drawString("F/J", textX, textY); textY += gp.tileSize;
        g2.drawString("P", textX, textY); textY += gp.tileSize;
        g2.drawString("ESC", textX, textY); textY += gp.tileSize;

        // BACK
        textX = frameX + gp.tileSize;
        textY = frameY + gp.tileSize * 6;
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (gp.keyHandler.enterPressed == true) {
                subState = 0;
            }
        }
    }

    /**
     * 
     * @param entity which entity is inventory for
     * @param cursor decide if show the cursor or not
     */
    private void drawInventory(Entity entity, boolean cursor) {

        int frameX = 0, frameY = 0;
        int frameWidth, frameHeight;
        int slotCol = 0, slotRow = 0;

        if (entity == gp.player) {
            frameX = gp.tileSize * 13;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize * 6;
            frameHeight = gp.tileSize * 5;
            slotCol = playerSlotCol;
            slotRow = playerSlotRow;
        } else {
            frameX = gp.tileSize * 2;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize * 6;
            frameHeight = gp.tileSize * 5;
            slotCol = npcSlotCol;
            slotRow = npcSlotRow;  
        }

        // FRAME
        g2Utils.drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight);

        // SLOT: 5 * 4
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;

        // DRAW PLAYER's ITEMS
        for(int i=0; i< entity.inventory.size(); i++) {

            // EQUIP CURSOR; 用底色強調目前已經安裝的武器/防具
            if (entity.inventory.get(i) == entity.currentWeapon ||
                    entity.inventory.get(i) == entity.currentShield) {
                g2.setColor(new Color(240,190,90));
                g2.fillRoundRect(slotX, slotY, gp.tileSize, gp.tileSize, 10, 10);
            }

            g2.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);

            // DISPLAY AMOUNT
            if(entity.inventory.get(i).amount > 1) {
                g2.setFont(g2.getFont().deriveFont(32f));
                int amountX, amountY;
                String s = "" + entity.inventory.get(i).amount;
                amountX = g2Utils.getXforAlignToRightText(g2, s, slotX + 44);
                amountY = slotY + gp.tileSize;
                
                // SHADOW
                g2.setColor(new Color(60,60,60));
                g2.drawString(s, amountX, amountY);

                // NUMBER
                g2.setColor(Color.white);
                g2.drawString(s, amountX-3, amountY-3);

            }


            slotX += slotSize;

            // next row
            if (i % 5 == 4) {
                slotX = slotXstart;
                slotY += slotSize;
            }
        }

        // CURSOR
        if (cursor == true) {
            int cursorX = slotXstart + (slotSize * slotCol);
            int cursorY = slotYstart + (slotSize * slotRow);
            int cursorWidth = gp.tileSize;
            int cursorHeight = gp.tileSize;
            // DRAW CURSOR
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

            // DESCRIPTION FRAME
            int dFrameX = frameX;
            int dFrameY = frameY + frameHeight;
            int dFrameWidth = frameWidth;
            int dFrameHeight = gp.tileSize * 4;
            g2Utils.drawSubWindow(g2, dFrameX, dFrameY, dFrameWidth, dFrameHeight);
            // DRAW DESCRIPTION TEXT
            int textX = dFrameX + 20;
            int textY = dFrameY + gp.tileSize;
            g2.setFont(g2.getFont().deriveFont(28F));

            int itemIndex = getItemIndexOnSlot(slotCol, slotRow);
            if (itemIndex < entity.inventory.size()) {
                String desc = entity.inventory.get(itemIndex).description;
                for (String line: desc.split("\n")) {
                    g2.drawString(line, textX, textY);
                    textY += 32;
                }
            }
        }
    }

    public int getItemIndexOnSlot(int slotCol, int slotRow) {
        int itemIndex = slotCol + (slotRow * 5);
        return itemIndex;
    }

    private void drawMessage() {
        int msgX = gp.tileSize;
        int msgY = gp.tileSize * 4;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));

        for (int i=0; i<message.size(); i++) {
            if (message.get(i) != null) {
                // shadow
                g2.setColor(Color.black);
                g2.drawString(message.get(i), msgX+2, msgY+2);

                g2.setColor(Color.white);
                g2.drawString(message.get(i), msgX, msgY);

                int counter = messageCounter.get(i) + 1; // messageCounter++
                messageCounter.set(i, counter); // increase the counter number
                msgY += 30; // for next message

                // remove message after 180 frame (3 second)
                if (messageCounter.get(i) > 180) {
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }

    private void drawCharacterScreen() {
        // CREATE A FRAME
        final int frameX = gp.tileSize;
        final int frameY = gp.tileSize;
        final int frameWidth = gp.tileSize * 5;
        final int frameHeight = gp.tileSize * 10;

        g2Utils.drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight);

        // DISPLAY CHARACTER ATTRIBUTES
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        int textX = frameX + 20;
        int textY = frameY + gp.tileSize;
        final int lineHeight = 32;

        // NAMES
        g2.drawString("Level", textX, textY); textY += lineHeight;
        g2.drawString("Life", textX, textY); textY += lineHeight;
        g2.drawString("Mana", textX, textY); textY += lineHeight;
        g2.drawString("Strength", textX, textY); textY += lineHeight;
        g2.drawString("Dexterity", textX, textY); textY += lineHeight;
        g2.drawString("Attack", textX, textY); textY += lineHeight;
        g2.drawString("Defense", textX, textY); textY += lineHeight;
        g2.drawString("Exp", textX, textY); textY += lineHeight;
        g2.drawString("Next Level", textX, textY); textY += lineHeight;
        g2.drawString("Coin", textX, textY); textY += lineHeight + 10;
        g2.drawString("Weapon", textX, textY); textY += lineHeight + 15;
        g2.drawString("Shield", textX, textY);

        // VALUES
        int tailX = (frameX + frameWidth) - 30;
        // Reset textY
        textY = frameY + gp.tileSize;
        String value;

        value = String.valueOf(gp.player.level);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.life + "/" + gp.player.maxLife);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.mana + "/" + gp.player.maxMana);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.strength);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.dexterity);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.attack);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.defense);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.exp);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.nextLevelExp);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.coin);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        // Draw weapon & sheild images
        g2.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize, textY-24, null);
        textY += gp.tileSize;

        g2.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize, textY-24, null);
    }

    private void drawPlayerLife() {

        // gp.player.life = 5; // For Test.

        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int i = 0;

        // DRAW MAX HEART
        while (i < gp.player.maxLife / 2 ) {
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += gp.tileSize;
        }

        // RESET
        x = gp.tileSize / 2;
        y = gp.tileSize / 2;
        i = 0;

        // DRAW CURRENT LIFE
        while (i < gp.player.life) {
            g2.drawImage(heart_half, x, y, null);
            i++;

            // replace heart_helf
            if (i < gp.player.life) {
                g2.drawImage(heart_full, x, y, null);
            }
            i++;
            x += gp.tileSize;
        }

        // DRAW BLANK MANA
        x = gp.tileSize / 2 - 5;
        y = (int) (gp.tileSize * 1.5);
        i = 0;
        while(i < gp.player.maxMana) {
            g2.drawImage(crystal_blank, x, y, null);
            i++;
            x += 35;
        }

        // DRAW FULL MANA
        x = gp.tileSize / 2 - 5;
        y = (int) (gp.tileSize * 1.5);
        i = 0;
        while(i < gp.player.mana) {
            g2.drawImage(crystal_full, x, y, null);
            i++;
            x += 35;
        }
    }

    private void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // TITLE NAME
            g2.setFont(purisaB);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "M/A Legend";
            int x = g2Utils.getXforCenterText(g2, gp, text);
            int y = gp.tileSize * 3;

            // SHADOW
            g2.setColor(Color.gray);
            g2.drawString(text, x+5, y+5);

            // MAIN COLOR
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // BLUE BOY IMAGE
            x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2;
            y += gp.tileSize * 2;
            g2.drawImage(gp.player.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);

            // MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F)); 

            text = "NEW GAME";
            x = g2Utils.getXforCenterText(g2, gp, text);
            y += gp.tileSize * 3.5;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "LOAD GAME";
            x = g2Utils.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "QUIT";
            x = g2Utils.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x-gp.tileSize, y);
            }
        }
        if (titleScreenState == 1) {

            // CLASS SELECTION SCREEN
            g2.setColor(Color.white);   
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select your class!";
            int x = g2Utils.getXforCenterText(g2, gp, text);
            int y = gp.tileSize * 3;
            g2.drawString(text, x, y);

            text = "Fighter";
            x = g2Utils.getXforCenterText(g2, gp, text);
            y += gp.tileSize * 3;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Thief";
            x = g2Utils.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Sorcerer";
            x = g2Utils.getXforCenterText(g2, gp, text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Back";
            x = g2Utils.getXforCenterText(g2, gp, text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNum == 3) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        }
    }

    public void drawDialogusScreen() {
        // WINDOW
        int x = gp.tileSize * 3;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 5);
        int height = gp.tileSize * 3;
        g2Utils.drawSubWindow(g2, x, y, width, height);

        // Draw Text on DialogWindow
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        x += gp.tileSize;
        y += gp.tileSize;
        for(String line : currentDialogue.split("\n")) { 
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void addMessage(String text) {
        message.add(text);
        messageCounter.add(0);
    }

    public void drawPauseScreen() {
        g2.setFont(purisaB);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));

        String text = "PAUSED";
        int x = g2Utils.getXforCenterText(g2, gp, text);
        int y = gp.screenHeight / 2 ;
        
        g2.drawString(text, x, y);
    }

    private void drawTradeScreen() {
        switch(subState) {
            case 0: trade_select(); break;
            case 1: trade_buy(); break;
            case 2: trade_sell(); break;
        }
        gp.keyHandler.enterPressed = false;
    }

    private void trade_select() {
        drawDialogusScreen();

        // DRAW WINDOW
        int x = gp.tileSize * 15;
        int y = gp.tileSize * 4;
        int width = gp.tileSize * 3;
        int height = (int) (gp.tileSize * 3.5);
        g2Utils.drawSubWindow(g2, x, y, width, height);

        // DRAW TEXTS
        x += gp.tileSize;
        y += gp.tileSize;
        g2.drawString("Buy", x, y);
        if(commandNum == 0) {
            g2.drawString(">", x-24, y);
            if (gp.keyHandler.enterPressed == true) {
                subState = 1;
            }
        }
        
        y += gp.tileSize;
        g2.drawString("Sell", x, y);
        if(commandNum == 1) {
            g2.drawString(">", x-24, y);
            if (gp.keyHandler.enterPressed == true) {
                subState = 2;
            }
        }

        y += gp.tileSize;
        g2.drawString("Leave", x, y);
        if(commandNum == 2) {
            g2.drawString(">", x-24, y);
            if (gp.keyHandler.enterPressed == true) {
                commandNum = 0;
                gp.gameState = GameState.DIALOGUE_STATE;
                currentDialogue = "Come again, hehe!!";
            }
        }
    }

    private void trade_buy() {
        // DRAW PLAYER INVENTORY
        drawInventory(gp.player, false);

        // DRAW NPC INVENTROY
        drawInventory(npc, true);

        // DRAW HINT WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize * 10;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("[ESC] Back", x+24, y+60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 13;
        y = gp.tileSize * 10;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("Your Coin: " + gp.player.coin, x+24, y+60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSlot(npcSlotCol, npcSlotRow);
        if (itemIndex < npc.inventory.size()) {
            x = (int) (gp.tileSize * 5.5);
            y = (int) (gp.tileSize * 5.5);
            width = (int) (gp.tileSize * 2.5);
            height = gp.tileSize;
            g2Utils.drawSubWindow(g2, x, y, width, height);
            g2.drawImage(coin, x+10, y+8, 32, 32, null);

            int price = npc.inventory.get(itemIndex).price;
            String text = "" + price;
            x = g2Utils.getXforAlignToRightText(g2, text, gp.tileSize*8 - 20);
            g2.drawString(text, x, y+34);

            // BUY AN ITEM
            if (gp.keyHandler.enterPressed == true) {
                // PLAYER 錢不夠
                if (npc.inventory.get(itemIndex).price > gp.player.coin) {
                    subState = 0;
                    gp.gameState = GameState.DIALOGUE_STATE;
                    currentDialogue = "You need more coin to buy that!";
                    drawDialogusScreen();
                }
                else {
                    if (gp.player.canObtainItem(npc.inventory.get(itemIndex)) == true) {
                        gp.player.coin -= npc.inventory.get(itemIndex).price; 
                    } 
                    // PLAYER 口袋滿了
                    else {
                        subState = 0;
                        gp.gameState = GameState.DIALOGUE_STATE;
                        currentDialogue = "You cannot carry any more!";
                    }
                }
            }
        }
    }

    private void trade_sell() {
        // DRAW PLAYER INVENTORY
        drawInventory(gp.player, true);

        int x, y, width, height;

        // DRAW HINT WINDOW
        x = gp.tileSize * 2;
        y = gp.tileSize * 10;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("[ESC] Back", x+24, y+60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 13;
        y = gp.tileSize * 10;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("Your Coin: " + gp.player.coin, x+24, y+60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSlot(playerSlotCol, playerSlotRow);
        if (itemIndex < gp.player.inventory.size()) {
            x = (int) (gp.tileSize * 16.5);
            y = (int) (gp.tileSize * 5.5);
            width = (int) (gp.tileSize * 2.5);
            height = gp.tileSize;
            g2Utils.drawSubWindow(g2, x, y, width, height);
            g2.drawImage(coin, x+10, y+8, 32, 32, null);

            int price = gp.player.inventory.get(itemIndex).price / 2;
            String text = "" + price;
            x = g2Utils.getXforAlignToRightText(g2, text, gp.tileSize*18);
            g2.drawString(text, x, y+34);

            // SELL AN ITEM
            if (gp.keyHandler.enterPressed == true) {
                // avoid the current used
                if (gp.player.inventory.get(itemIndex) == gp.player.currentShield ||
                    gp.player.inventory.get(itemIndex) == gp.player.currentWeapon) {
                    
                    commandNum = 0;
                    subState = 0;
                    gp.gameState = GameState.DIALOGUE_STATE;
                    currentDialogue = "You cannot sell in equipped item!!";

                } else {
                    if (gp.player.inventory.get(itemIndex).amount > 1) {
                        gp.player.inventory.get(itemIndex).amount--;
                    } 
                    else {
                        gp.player.inventory.remove(itemIndex);
                    }
                    gp.player.coin += price;
                }
            }
        }
    }
}
