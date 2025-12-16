package gtcafe.rpg.ui;

import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.UI;

public class TradeScreen {
    
    GamePanel gp;
    UI ui;
    DialogueScreen dialogueScreen;

    public TradeScreen(GamePanel gp, UI ui, DialogueScreen dialogueScreen) {
        this.gp = gp;
        this.ui = ui;
        this.dialogueScreen = dialogueScreen;
    }

    public void draw(Graphics2D g2) {
        switch(ui.subState) {
            case 0: trade_select(g2); break;
            case 1: trade_buy(g2); break;
            case 2: trade_sell(g2); break;
        }
        gp.keyHandler.enterPressed = false;
    }

    private void trade_select(Graphics2D g2) {
        ui.npc.dialogueIndex = 0;
        dialogueScreen.draw(g2);

        // DRAW WINDOW
        int x = gp.tileSize * 15;
        int y = gp.tileSize * 4;
        int width = gp.tileSize * 3;
        int height = (int) (gp.tileSize * 3.5);
        ui.uiUtil.drawSubWindow(g2, x, y, width, height);

        // DRAW TEXTS
        x += gp.tileSize;
        y += gp.tileSize;
        g2.drawString("Buy", x, y);
        if(ui.commandNum == 0) {
            g2.drawString(">", x-24, y);
            if (gp.keyHandler.enterPressed == true) {
                ui.subState = 1;
            }
        }
        
        y += gp.tileSize;
        g2.drawString("Sell", x, y);
        if(ui.commandNum == 1) {
            g2.drawString(">", x-24, y);
            if (gp.keyHandler.enterPressed == true) {
                ui.subState = 2;
            }
        }

        y += gp.tileSize;
        g2.drawString("Leave", x, y);
        if(ui.commandNum == 2) {
            g2.drawString(">", x-24, y);
            if (gp.keyHandler.enterPressed == true) {
                ui.commandNum = 0;
                ui.npc.startDialogue(ui.npc, 1);
            }
        }
    }

    private void trade_buy(Graphics2D g2) {
        // DRAW PLAYER INVENTORY
        ui.uiUtil.drawInventory(g2, gp, gp.player, false, ui.playerSlotCol, ui.playerSlotRow, gp.tileSize * 13, gp.tileSize);

        // DRAW NPC INVENTROY
        ui.uiUtil.drawInventory(g2, gp, ui.npc, true, ui.npcSlotCol, ui.npcSlotRow, gp.tileSize * 2, gp.tileSize);

        // DRAW HINT WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize * 10;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 2;
        ui.uiUtil.drawSubWindow(g2, x, y, width, height);
        g2.drawString("[ESC] Back", x+24, y+60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 13;
        y = gp.tileSize * 10;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        ui.uiUtil.drawSubWindow(g2, x, y, width, height);
        g2.drawString("Your Coin: " + gp.player.coin, x+24, y+60);

        // DRAW PRICE WINDOW
        int itemIndex = ui.uiUtil.getItemIndexOnSlot(ui.npcSlotCol, ui.npcSlotRow);
        if (itemIndex < ui.npc.inventory.size()) {
            x = (int) (gp.tileSize * 5.5);
            y = (int) (gp.tileSize * 5.5);
            width = (int) (gp.tileSize * 2.5);
            height = gp.tileSize;
            ui.uiUtil.drawSubWindow(g2, x, y, width, height);
            g2.drawImage(ui.hud.coin, x+10, y+8, 32, 32, null);

            int price = ui.npc.inventory.get(itemIndex).price;
            String text = "" + price;
            x = ui.uiUtil.getXforAlignToRightText(g2, text, gp.tileSize*8 - 20);
            g2.drawString(text, x, y+34);

            // BUY AN ITEM
            if (gp.keyHandler.enterPressed == true) {
                // PLAYER 錢不夠
                if (ui.npc.inventory.get(itemIndex).price > gp.player.coin) {
                    ui.subState = 0;
                    ui.npc.startDialogue(ui.npc, 2);
                    // drawDialogusScreen();
                }
                else {
                    if (gp.player.canObtainItem(ui.npc.inventory.get(itemIndex)) == true) {
                        gp.player.coin -= ui.npc.inventory.get(itemIndex).price; 
                    } 
                    // PLAYER 口袋滿了
                    else {
                        ui.subState = 0;
                        ui.npc.startDialogue(ui.npc, 3);
                    }
                }
            }
        }
    }

    private void trade_sell(Graphics2D g2) {
        // DRAW PLAYER INVENTORY
        ui.uiUtil.drawInventory(g2, gp, gp.player, true, ui.playerSlotCol, ui.playerSlotRow, gp.tileSize * 13, gp.tileSize);

        int x, y, width, height;

        // DRAW HINT WINDOW
        x = gp.tileSize * 2;
        y = gp.tileSize * 10;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        ui.uiUtil.drawSubWindow(g2, x, y, width, height);
        g2.drawString("[ESC] Back", x+24, y+60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 13;
        y = gp.tileSize * 10;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        ui.uiUtil.drawSubWindow(g2, x, y, width, height);
        g2.drawString("Your Coin: " + gp.player.coin, x+24, y+60);

        // DRAW PRICE WINDOW
        int itemIndex = ui.uiUtil.getItemIndexOnSlot(ui.playerSlotCol, ui.playerSlotRow);
        if (itemIndex < gp.player.inventory.size()) {
            x = (int) (gp.tileSize * 16.5);
            y = (int) (gp.tileSize * 5.5);
            width = (int) (gp.tileSize * 2.5);
            height = gp.tileSize;
            ui.uiUtil.drawSubWindow(g2, x, y, width, height);
            g2.drawImage(ui.hud.coin, x+10, y+8, 32, 32, null);

            int price = gp.player.inventory.get(itemIndex).price / 2;
            String text = "" + price;
            x = ui.uiUtil.getXforAlignToRightText(g2, text, gp.tileSize*18);
            g2.drawString(text, x, y+34);

            // SELL AN ITEM
            if (gp.keyHandler.enterPressed == true) {
                // avoid the current used
                if (gp.player.inventory.get(itemIndex) == gp.player.currentShield ||
                    gp.player.inventory.get(itemIndex) == gp.player.currentWeapon) {
                    
                    ui.commandNum = 0;
                    ui.subState = 0;
                    ui.npc.startDialogue(ui.npc, 4);
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