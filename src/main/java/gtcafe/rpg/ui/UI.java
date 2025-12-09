package gtcafe.rpg.ui;
import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.core.Main;
import gtcafe.rpg.system.Sound;
import gtcafe.rpg.util.Graphics2DUtils;

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
import gtcafe.rpg.state.DayState;
import gtcafe.rpg.state.GameState;

public class UI {
    GameContext context;
    Graphics2D g2;
    // Font arial_40, arial_80B;
    // Custom Font:
    // - pixel font: https://00ff.booth.pm/items/2958237
    // - https://fontsgeek.com/fonts/purisa-bold
    public Font maruMonica, purisaB;
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
    public int subState = 0;

    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;

    int counter = 0;
    public Entity npc;

    Graphics2DUtils g2Utils = new Graphics2DUtils();

    public UI(GameContext context) {
        this.context = context;
        loadFont();;

        // CREATE HUD OBJECT
        Entity heart = new OBJ_Heart(context);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;

        // MANA OBJECT
        Entity crystal = new OBJ_ManaCrystal(context);
        crystal_full = crystal.image;
        crystal_blank = crystal.image2;
        Entity bronze_coin = new OBJ_Coin_Bronze(context);
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
        if (context.getGameState() == GameState.TITLE) {
            drawTitleScreen();
        }

        // PLAY STATE
        if(context.getGameState() == GameState.PLAYING) {
            drawPlayerLife();
            drawMessage();
        }

        // PAUSE STATE
        if (context.getGameState() == GameState.PAUSE) {
            drawPlayerLife();
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (context.getGameState() == GameState.DIALOGUE) {
            drawDialogusScreen();
        }

        // CHARACTER STATE
        if (context.getGameState() == GameState.CHARACTER) {
            drawCharacterScreen();
            drawInventory(context.getPlayer(), true);
        }
        
        // OPTIONS STATE
        if (context.getGameState() == GameState.OPTIONS) {
            drawOptionsScreen();
        }
        // GAME OVER STATE
        if (context.getGameState() == GameState.GAME_OVER) {
            drawGameOverScreen();
        }

        // TRANSITION STATE
        if (context.getGameState() == GameState.TRANSITION) {
            drawTransition();
        }

        // TRADE STATE
        if (context.getGameState() == GameState.TRADE) {
            drawTradeScreen();
        }
        // SLEEP STATE
        if (context.getGameState() == GameState.SLEEP) {
            drawSleepScreen();
        }
         
    }

    private void drawSleepScreen() {
        counter++;
        if (counter < 120) {    // 120 fps, 2 second
            context.getEnvironmentManager().lighting.filterAlpha += 0.01f;
            if(context.getEnvironmentManager().lighting.filterAlpha > 1f) {
                context.getEnvironmentManager().lighting.filterAlpha = 1f;
            }
        }
        if (counter >= 120) {
            context.getEnvironmentManager().lighting.filterAlpha -= 0.01f;
            if(context.getEnvironmentManager().lighting.filterAlpha <= 0f) {
                context.getEnvironmentManager().lighting.filterAlpha = 0f;
                counter = 0;
                context.getEnvironmentManager().lighting.dayState = DayState.DAY;
                context.getEnvironmentManager().lighting.dayCounter = 0;
                context.setGameState(GameState.PLAYING);
                context.getPlayer().getImages();
            }
        }
    }

    private void drawTransition() {
        counter++;
        g2.setColor(new Color(0,0,0,counter*5));
        g2.fillRect(0, 0, context.getScreenWidth(), context.getScreenHeight());

        if (counter == 50) {
            counter = 0;
            System.out.printf("[Transition] from [%s] to [%s]\n", context.getCurrentMap().name, context.getEventHandler().tempMap.name);
            context.setGameState(GameState.PLAYING);
            context.setCurrentMap(context.getEventHandler().tempMap);
            context.getPlayer().worldX = context.getTileSize() * context.getEventHandler().tempCol;
            context.getPlayer().worldY = context.getTileSize() * context.getEventHandler().tempRow;
            context.getEventHandler().previousEventX = context.getPlayer().worldX;
            context.getEventHandler().previousEventY = context.getPlayer().worldY;
        }
    }

    private void drawGameOverScreen() {
        g2.setColor(new Color(0,0,0, 150));
        g2.fillRect(0, 0, context.getScreenWidth(), context.getScreenHeight());

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

        text = "Game Over";
        // Shadow
        g2.setColor(Color.black);
        x = g2Utils.getXforCenterText(g2, context, text);
        y = context.getTileSize() * 4;
        g2.drawString(text, x, y);
        // Main
        g2.setColor(Color.white);
        g2.drawString(text, x-4, y-4);

        // Option: Retry
        g2.setFont(g2.getFont().deriveFont(50f));
        text = "Retry";
        x = g2Utils.getXforCenterText(g2, context, text);
        y += context.getTileSize() * 4;
        g2.drawString(text, x, y);
        if (commandNum == 0) {
            g2.drawString(">", x-40, y);
        }

        // Option: Back to Title Screen
        text = "Quit";
        x = g2Utils.getXforCenterText(g2, context, text);
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
        int frameX = context.getTileSize() * 6;
        int frameY = context.getTileSize();
        int frameWidth = context.getTileSize() * 8;
        int frameHeight = context.getTileSize() * 10;
        g2Utils.drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight);

        switch (subState) {
            case 0: options_top(frameX, frameY); break;
            case 1: options_fullScreenNotification(frameX, frameY); break;
            case 2: options_control(frameX, frameY); break;
            case 3: options_endGameConfirmation(frameX, frameY); break;
        }

        context.getKeyHandler().enterPressed = false;

    }

    private void options_endGameConfirmation(int frameX, int frameY) {
        int textX = frameX + context.getTileSize();
        int textY = frameY + context.getTileSize() * 2;

        currentDialogue = "Quit the game and \nreturn to the title screen?";

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // YES
        String text = "Yes";
        textX = g2Utils.getXforCenterText(g2, context, text);
        textY += context.getTileSize() * 3;
        g2.drawString(text, textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (context.getKeyHandler().enterPressed == true) {
                subState = 0;
                context.getMusic().stop();
                context.setGameState(GameState.TITLE);
                context.resetGame(true);
            }
        }

        // NO 
        text = "No";
        textX = g2Utils.getXforCenterText(g2, context, text);
        textY += context.getTileSize() ;
        g2.drawString(text, textX, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX-25, textY);
            if (context.getKeyHandler().enterPressed == true) {
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
        textX = g2Utils.getXforCenterText(g2, context, text);
        textY = frameY + context.getTileSize();
        g2.drawString(text, textX, textY);

        // FULL SCREEN ON/OFF
        textX = frameX + context.getTileSize();
        textY += context.getTileSize() * 2;
        g2.drawString("Full Screen", textX, textY);
        if (commandNum == 0) { 
            g2.drawString(">", textX-25, textY); 
            if (context.getKeyHandler().enterPressed == true) {
                context.setFullScreenOn((context.isFullScreenOn() == false ? true : false));
                subState = 1;
            }
        }

        // MUSIC
        textY += context.getTileSize();
        g2.drawString("Music", textX, textY);
        if (commandNum == 1) { g2.drawString(">", textX-25, textY); }

        // Sound Effect
        textY += context.getTileSize();
        g2.drawString("Sound Effect", textX, textY);
        if (commandNum == 2) { g2.drawString(">", textX-25, textY); }

        // Keyboard Control
        textY += context.getTileSize();
        g2.drawString("Control", textX, textY);
        if (commandNum == 3) { 
            g2.drawString(">", textX-25, textY); 
            if(context.getKeyHandler().enterPressed == true) {
                subState = 2;
                commandNum = 0;
            }
        }

        // END Game
        textY += context.getTileSize();
        g2.drawString("End Game", textX, textY);
        if (commandNum == 4) { 
            g2.drawString(">", textX-25, textY); 
            if(context.getKeyHandler().enterPressed == true) {
                subState = 3;
                commandNum = 0;
            }
        }

        // Back
        textY += context.getTileSize() * 2;
        g2.drawString("Back", textX, textY);
        if (commandNum == 5) { 
            g2.drawString(">", textX-25, textY); 
            if (context.getKeyHandler().enterPressed == true) {
                context.setGameState(GameState.PLAYING);
                commandNum = 0;
            }
        }


        // FULL SCREEN CHECKBOX
        textX = frameX + (int) (context.getTileSize() * 4.5);
        textY = frameY + context.getTileSize() * 2 + 24;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(textX, textY, 24, 24);
        if (context.isFullScreenOn() == true) {
            g2.fillRect(textX, textY, 24, 24);
        }

        // MUSIC VOLUME
        textY += context.getTileSize();
        g2.drawRect(textX, textY, 120, 24); // 120 / 5 = 24
        int volumeWidth = 24 * context.getMusic().volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        // SE VOLUME
        textY += context.getTileSize();
        g2.drawRect(textX, textY, 120, 24);
        volumeWidth = 24 * context.getSoundEffect().volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        context.getConfig().saveConfig();
    }

    public void options_fullScreenNotification(int frameX, int frameY) {
        int textX = frameX + context.getTileSize();
        int textY = frameY + context.getTileSize() * 2;

        currentDialogue = "The change will take \neffect after restarting \nthe game.";

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // BACK
        textY += frameY + context.getTileSize() * 3;
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (context.getKeyHandler().enterPressed == true) {
                subState = 0;
            }
        }
    }

    public void options_control(int frameX, int frameY) {
        int textX;
        int textY;

        // TITLE
        String text = "Control";
        textX = g2Utils.getXforCenterText(g2, context, text);
        textY = frameY + context.getTileSize();
        g2.drawString(text, textX, textY);

        textX = frameX + context.getTileSize();
        textY += context.getTileSize();
        g2.drawString("Move", textX, textY); textY += context.getTileSize();
        g2.drawString("Confirm/Attack", textX, textY); textY += context.getTileSize();
        g2.drawString("Shoot/Cast", textX, textY); textY += context.getTileSize();
        g2.drawString("Pause", textX, textY); textY += context.getTileSize();
        g2.drawString("Options", textX, textY); textY += context.getTileSize();


        // DISPLAY THE ASIGNED KEY
        textX = frameX + context.getTileSize() * 6;
        textY = frameY = context.getTileSize() * 3;
        g2.drawString("WASD", textX, textY); textY += context.getTileSize();
        g2.drawString("ENTER/SPACE", textX, textY); textY += context.getTileSize();
        g2.drawString("F/J", textX, textY); textY += context.getTileSize();
        g2.drawString("P", textX, textY); textY += context.getTileSize();
        g2.drawString("ESC", textX, textY); textY += context.getTileSize();

        // BACK
        textX = frameX + context.getTileSize();
        textY = frameY + context.getTileSize() * 6;
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
            if (context.getKeyHandler().enterPressed == true) {
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

        if (entity == context.getPlayer()) {
            frameX = context.getTileSize() * 13;
            frameY = context.getTileSize();
            frameWidth = context.getTileSize() * 6;
            frameHeight = context.getTileSize() * 5;
            slotCol = playerSlotCol;
            slotRow = playerSlotRow;
        } else {
            frameX = context.getTileSize() * 2;
            frameY = context.getTileSize();
            frameWidth = context.getTileSize() * 6;
            frameHeight = context.getTileSize() * 5;
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
        int slotSize = context.getTileSize() + 3;

        // DRAW PLAYER's ITEMS
        for(int i=0; i< entity.inventory.size(); i++) {

            // EQUIP CURSOR; 用底色強調目前已經安裝的武器/防具
            if (entity.inventory.get(i) == entity.currentWeapon
                    || entity.inventory.get(i) == entity.currentShield
                    || entity.inventory.get(i) == entity.currentLight
            ) {
                g2.setColor(new Color(240,190,90));
                g2.fillRoundRect(slotX, slotY, context.getTileSize(), context.getTileSize(), 10, 10);
            }

            g2.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);

            // DISPLAY AMOUNT
            if(entity.inventory.get(i).amount > 1) {
                g2.setFont(g2.getFont().deriveFont(32f));
                int amountX, amountY;
                String s = "" + entity.inventory.get(i).amount;
                amountX = g2Utils.getXforAlignToRightText(g2, s, slotX + 44);
                amountY = slotY + context.getTileSize();
                
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
            int cursorWidth = context.getTileSize();
            int cursorHeight = context.getTileSize();
            // DRAW CURSOR
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

            // DESCRIPTION FRAME
            int dFrameX = frameX;
            int dFrameY = frameY + frameHeight;
            int dFrameWidth = frameWidth;
            int dFrameHeight = context.getTileSize() * 4;
            g2Utils.drawSubWindow(g2, dFrameX, dFrameY, dFrameWidth, dFrameHeight);
            // DRAW DESCRIPTION TEXT
            int textX = dFrameX + 20;
            int textY = dFrameY + context.getTileSize();
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
        int msgX = context.getTileSize();
        int msgY = context.getTileSize() * 4;
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
        final int frameX = context.getTileSize();
        final int frameY = context.getTileSize();
        final int frameWidth = context.getTileSize() * 5;
        final int frameHeight = context.getTileSize() * 10;

        g2Utils.drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight);

        // DISPLAY CHARACTER ATTRIBUTES
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        int textX = frameX + 20;
        int textY = frameY + context.getTileSize();
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
        textY = frameY + context.getTileSize();
        String value;

        value = String.valueOf(context.getPlayer().level);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().life + "/" + context.getPlayer().maxLife);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().mana + "/" + context.getPlayer().maxMana);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().strength);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().dexterity);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().attack);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().defense);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().exp);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().nextLevelExp);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(context.getPlayer().coin);
        textX = g2Utils.getXforAlignToRightText(g2, value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        // Draw weapon & sheild images
        g2.drawImage(context.getPlayer().currentWeapon.down1, tailX - context.getTileSize(), textY-24, null);
        textY += context.getTileSize();

        g2.drawImage(context.getPlayer().currentShield.down1, tailX - context.getTileSize(), textY-24, null);
    }

    private void drawPlayerLife() {

        // context.getPlayer().life = 5; // For Test.

        int x = context.getTileSize() / 2;
        int y = context.getTileSize() / 2;
        int i = 0;

        // DRAW MAX HEART
        while (i < context.getPlayer().maxLife / 2 ) {
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += context.getTileSize();
        }

        // RESET
        x = context.getTileSize() / 2;
        y = context.getTileSize() / 2;
        i = 0;

        // DRAW CURRENT LIFE
        while (i < context.getPlayer().life) {
            g2.drawImage(heart_half, x, y, null);
            i++;

            // replace heart_helf
            if (i < context.getPlayer().life) {
                g2.drawImage(heart_full, x, y, null);
            }
            i++;
            x += context.getTileSize();
        }

        // DRAW BLANK MANA
        x = context.getTileSize() / 2 - 5;
        y = (int) (context.getTileSize() * 1.5);
        i = 0;
        while(i < context.getPlayer().maxMana) {
            g2.drawImage(crystal_blank, x, y, null);
            i++;
            x += 35;
        }

        // DRAW FULL MANA
        x = context.getTileSize() / 2 - 5;
        y = (int) (context.getTileSize() * 1.5);
        i = 0;
        while(i < context.getPlayer().mana) {
            g2.drawImage(crystal_full, x, y, null);
            i++;
            x += 35;
        }
    }

    private void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, context.getScreenWidth(), context.getScreenHeight());

            // TITLE NAME
            g2.setFont(purisaB);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "M/A Legend";
            int x = g2Utils.getXforCenterText(g2, context, text);
            int y = context.getTileSize() * 3;

            // SHADOW
            g2.setColor(Color.gray);
            g2.drawString(text, x+5, y+5);

            // MAIN COLOR
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // BLUE BOY IMAGE
            x = context.getScreenWidth() / 2 - (context.getTileSize() * 2) / 2;
            y += context.getTileSize() * 2;
            g2.drawImage(context.getPlayer().down1, x, y, context.getTileSize() * 2, context.getTileSize() * 2, null);

            // MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F)); 

            text = "NEW GAME";
            x = g2Utils.getXforCenterText(g2, context, text);
            y += context.getTileSize() * 3.5;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x-context.getTileSize(), y);
            }

            text = "LOAD GAME";
            x = g2Utils.getXforCenterText(g2, context, text);
            y += context.getTileSize();
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x-context.getTileSize(), y);
            }

            text = "QUIT";
            x = g2Utils.getXforCenterText(g2, context, text);
            y += context.getTileSize();
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x-context.getTileSize(), y);
            }
        }
        if (titleScreenState == 1) {

            // CLASS SELECTION SCREEN
            g2.setColor(Color.white);   
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select your class!";
            int x = g2Utils.getXforCenterText(g2, context, text);
            int y = context.getTileSize() * 3;
            g2.drawString(text, x, y);

            text = "Fighter";
            x = g2Utils.getXforCenterText(g2, context, text);
            y += context.getTileSize() * 3;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - context.getTileSize(), y);
            }

            text = "Thief";
            x = g2Utils.getXforCenterText(g2, context, text);
            y += context.getTileSize();
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - context.getTileSize(), y);
            }

            text = "Sorcerer";
            x = g2Utils.getXforCenterText(g2, context, text);
            y += context.getTileSize();
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - context.getTileSize(), y);
            }

            text = "Back";
            x = g2Utils.getXforCenterText(g2, context, text);
            y += context.getTileSize() * 2;
            g2.drawString(text, x, y);
            if (commandNum == 3) {
                g2.drawString(">", x - context.getTileSize(), y);
            }
        }
    }

    public void drawDialogusScreen() {
        // WINDOW
        int x = context.getTileSize() * 3;
        int y = context.getTileSize() / 2;
        int width = context.getScreenWidth() - (context.getTileSize() * 5);
        int height = context.getTileSize() * 3;
        g2Utils.drawSubWindow(g2, x, y, width, height);

        // Draw Text on DialogWindow
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        x += context.getTileSize();
        y += context.getTileSize();
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
        int x = g2Utils.getXforCenterText(g2, context, text);
        int y = context.getScreenHeight() / 2 ;
        
        g2.drawString(text, x, y);
    }

    private void drawTradeScreen() {
        switch(subState) {
            case 0: trade_select(); break;
            case 1: trade_buy(); break;
            case 2: trade_sell(); break;
        }
        context.getKeyHandler().enterPressed = false;
    }

    private void trade_select() {
        drawDialogusScreen();

        // DRAW WINDOW
        int x = context.getTileSize() * 15;
        int y = context.getTileSize() * 4;
        int width = context.getTileSize() * 3;
        int height = (int) (context.getTileSize() * 3.5);
        g2Utils.drawSubWindow(g2, x, y, width, height);

        // DRAW TEXTS
        x += context.getTileSize();
        y += context.getTileSize();
        g2.drawString("Buy", x, y);
        if(commandNum == 0) {
            g2.drawString(">", x-24, y);
            if (context.getKeyHandler().enterPressed == true) {
                subState = 1;
            }
        }
        
        y += context.getTileSize();
        g2.drawString("Sell", x, y);
        if(commandNum == 1) {
            g2.drawString(">", x-24, y);
            if (context.getKeyHandler().enterPressed == true) {
                subState = 2;
            }
        }

        y += context.getTileSize();
        g2.drawString("Leave", x, y);
        if(commandNum == 2) {
            g2.drawString(">", x-24, y);
            if (context.getKeyHandler().enterPressed == true) {
                commandNum = 0;
                context.setGameState(GameState.DIALOGUE);
                currentDialogue = "Come again, hehe!!";
            }
        }
    }

    private void trade_buy() {
        // DRAW PLAYER INVENTORY
        drawInventory(context.getPlayer(), false);

        // DRAW NPC INVENTROY
        drawInventory(npc, true);

        // DRAW HINT WINDOW
        int x = context.getTileSize() * 2;
        int y = context.getTileSize() * 10;
        int width = context.getTileSize() * 6;
        int height = context.getTileSize() * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("[ESC] Back", x+24, y+60);

        // DRAW PLAYER COIN WINDOW
        x = context.getTileSize() * 13;
        y = context.getTileSize() * 10;
        width = context.getTileSize() * 6;
        height = context.getTileSize() * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("Your Coin: " + context.getPlayer().coin, x+24, y+60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSlot(npcSlotCol, npcSlotRow);
        if (itemIndex < npc.inventory.size()) {
            x = (int) (context.getTileSize() * 5.5);
            y = (int) (context.getTileSize() * 5.5);
            width = (int) (context.getTileSize() * 2.5);
            height = context.getTileSize();
            g2Utils.drawSubWindow(g2, x, y, width, height);
            g2.drawImage(coin, x+10, y+8, 32, 32, null);

            int price = npc.inventory.get(itemIndex).price;
            String text = "" + price;
            x = g2Utils.getXforAlignToRightText(g2, text, context.getTileSize()*8 - 20);
            g2.drawString(text, x, y+34);

            // BUY AN ITEM
            if (context.getKeyHandler().enterPressed == true) {
                // PLAYER 錢不夠
                if (npc.inventory.get(itemIndex).price > context.getPlayer().coin) {
                    subState = 0;
                    context.setGameState(GameState.DIALOGUE);
                    currentDialogue = "You need more coin to buy that!";
                    drawDialogusScreen();
                }
                else {
                    if (context.getPlayer().canObtainItem(npc.inventory.get(itemIndex)) == true) {
                        context.getPlayer().coin -= npc.inventory.get(itemIndex).price; 
                    } 
                    // PLAYER 口袋滿了
                    else {
                        subState = 0;
                        context.setGameState(GameState.DIALOGUE);
                        currentDialogue = "You cannot carry any more!";
                    }
                }
            }
        }
    }

    private void trade_sell() {
        // DRAW PLAYER INVENTORY
        drawInventory(context.getPlayer(), true);

        int x, y, width, height;

        // DRAW HINT WINDOW
        x = context.getTileSize() * 2;
        y = context.getTileSize() * 10;
        width = context.getTileSize() * 6;
        height = context.getTileSize() * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("[ESC] Back", x+24, y+60);

        // DRAW PLAYER COIN WINDOW
        x = context.getTileSize() * 13;
        y = context.getTileSize() * 10;
        width = context.getTileSize() * 6;
        height = context.getTileSize() * 2;
        g2Utils.drawSubWindow(g2, x, y, width, height);
        g2.drawString("Your Coin: " + context.getPlayer().coin, x+24, y+60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSlot(playerSlotCol, playerSlotRow);
        if (itemIndex < context.getPlayer().inventory.size()) {
            x = (int) (context.getTileSize() * 16.5);
            y = (int) (context.getTileSize() * 5.5);
            width = (int) (context.getTileSize() * 2.5);
            height = context.getTileSize();
            g2Utils.drawSubWindow(g2, x, y, width, height);
            g2.drawImage(coin, x+10, y+8, 32, 32, null);

            int price = context.getPlayer().inventory.get(itemIndex).price / 2;
            String text = "" + price;
            x = g2Utils.getXforAlignToRightText(g2, text, context.getTileSize()*18);
            g2.drawString(text, x, y+34);

            // SELL AN ITEM
            if (context.getKeyHandler().enterPressed == true) {
                // avoid the current used
                if (context.getPlayer().inventory.get(itemIndex) == context.getPlayer().currentShield ||
                    context.getPlayer().inventory.get(itemIndex) == context.getPlayer().currentWeapon) {
                    
                    commandNum = 0;
                    subState = 0;
                    context.setGameState(GameState.DIALOGUE);
                    currentDialogue = "You cannot sell in equipped item!!";

                } else {
                    if (context.getPlayer().inventory.get(itemIndex).amount > 1) {
                        context.getPlayer().inventory.get(itemIndex).amount--;
                    } 
                    else {
                        context.getPlayer().inventory.remove(itemIndex);
                    }
                    context.getPlayer().coin += price;
                }
            }
        }
    }
}
