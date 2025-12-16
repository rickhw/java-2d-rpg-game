package gtcafe.rpg.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;

public class UiUtil {

    public void drawSubWindow(Graphics2D g2, int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 180); // Black, alpha value = 200, make the color transparent.
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);  // draw rectangle

        c = new Color(255, 255, 255); // White
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    public int getXforCenterText(Graphics2D g2, GamePanel gp, String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2 ;
        return x;
    }

    public int getXforAlignToRightText(Graphics2D g2, String text, int tailX) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - length;
        return x;
    }

    public int getItemIndexOnSlot(int slotCol, int slotRow) {
        int itemIndex = slotCol + (slotRow * 5);
        return itemIndex;
    }

    public void drawInventory(Graphics2D g2, GamePanel gp, Entity entity, boolean cursor, int slotCol, int slotRow, int frameX, int frameY) {
        int frameWidth = gp.tileSize * 6;
        int frameHeight = gp.tileSize * 5;

        // FRAME
        drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight);

        // SLOT: 5 * 4
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;

        // DRAW PLAYER's ITEMS
        for(int i=0; i< entity.inventory.size(); i++) {

            // EQUIP CURSOR; 用底色強調目前已經安裝的武器/防具
            if (entity.inventory.get(i) == entity.currentWeapon
                    || entity.inventory.get(i) == entity.currentShield
                    || entity.inventory.get(i) == entity.currentLight
            ) {
                g2.setColor(new Color(240,190,90));
                g2.fillRoundRect(slotX, slotY, gp.tileSize, gp.tileSize, 10, 10);
            }

            g2.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);

            // DISPLAY AMOUNT
            if(entity.inventory.get(i).amount > 1) {
                g2.setFont(g2.getFont().deriveFont(32f));
                int amountX, amountY;
                String s = "" + entity.inventory.get(i).amount;
                amountX = getXforAlignToRightText(g2, s, slotX + 44);
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
            drawSubWindow(g2, dFrameX, dFrameY, dFrameWidth, dFrameHeight);
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
}
