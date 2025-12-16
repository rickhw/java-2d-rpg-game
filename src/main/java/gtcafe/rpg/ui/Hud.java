package gtcafe.rpg.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.object.OBJ_Coin_Bronze;
import gtcafe.rpg.entity.object.OBJ_Heart;
import gtcafe.rpg.entity.object.OBJ_ManaCrystal;

public class Hud {
    
    GamePanel gp;
    BufferedImage heart_full, heart_half, heart_blank;
    BufferedImage crystal_full, crystal_blank;
    BufferedImage coin;

    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public boolean messageOn = false;

    public Hud(GamePanel gp) {
        this.gp = gp;
        
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

    public void addMessage(String text) {
        message.add(text);
        messageCounter.add(0);
    }

    public void draw(Graphics2D g2) {
        drawPlayerLife(g2);
        drawMonsterLife(g2);
        drawMessage(g2);
    }

    public void drawPlayerLife(Graphics2D g2) {

        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int i = 0;
        int iconSize = 32;
        // int manaStartX = (gp.tileSize / 2) - 5;
        // int manaStartY = 0;

        // DRAW MAX HEART
        while (i < gp.player.maxLife / 2 ) {
            g2.drawImage(heart_blank, x, y, iconSize, iconSize, null);
            i++;
            x += iconSize;
            // manaStartY = y + 32;

            if (i % 8 == 0) {
                x = gp.tileSize / 2;
                y += iconSize;
            }
        }

        // RESET
        x = gp.tileSize / 2;
        y = gp.tileSize / 2;
        i = 0;

        // DRAW CURRENT LIFE
        while (i < gp.player.life) {
            g2.drawImage(heart_half, x, y, iconSize, iconSize, null);
            i++;

            // replace heart_helf
            if (i < gp.player.life) {
                g2.drawImage(heart_full, x, y, iconSize, iconSize, null);
            }
            i++;
            x += iconSize;

            if (i % 16 == 0) {
                x = gp.tileSize / 2;
                y += iconSize;
            }
        }

        // DRAW BLANK MANA
        x = gp.tileSize / 2 - 5;
        y = (int) (gp.tileSize * 1.5);
        i = 0;

        while(i < gp.player.maxMana) {
            g2.drawImage(crystal_blank, x, y, iconSize, iconSize, null);
            i++;
            x += 25;
        }

        // DRAW FULL MANA
        x = gp.tileSize / 2 - 5;
        y = (int) (gp.tileSize * 1.5);
        i = 0;
        while(i < gp.player.mana) {
            g2.drawImage(crystal_full, x, y, iconSize, iconSize, null);
            i++;
            x += 25;
        }
    }

    public void drawMonsterLife(Graphics2D g2) {
        // scan 
        for(int i=0; i<gp.monster[1].length; i++) {
            Entity monster = gp.monster[gp.currentMap.index][i];

            if (monster != null && monster.inCamera()) {
                // normal monster
                if (monster.hpBarOn == true && monster.boss == false) {

                    double oneScale = (double) gp.tileSize / monster.maxLife;
                    double hpBarValue = oneScale * monster.life;    // find the col length of bar

                    g2.setColor(new Color(35,35,30)); 
                    g2.fillRect(monster.getScreenX() - 1 , monster.getScreenY() - 16, gp.tileSize+2, 10+2);

                    g2.setColor(new Color(255,0,30)); 
                    g2.fillRect(monster.getScreenX(), monster.getScreenY() - 15, (int)hpBarValue, 10);

                    monster.hpBarCounter ++;

                    // hpBar disapper after 5s
                    if (monster.hpBarCounter > 300) {
                        monster.hpBarCounter = 0;
                        monster.hpBarOn = false;
                    }
                }
                // boss
                else if(monster.boss) {

                    double oneScale = (double) gp.tileSize*8 / monster.maxLife;
                    double hpBarValue = oneScale * monster.life;

                    int x = gp.screenWidth / 2 - gp.tileSize * 4;
                    int y = gp.tileSize * 10;

                    g2.setColor(new Color(35,35,30)); 
                    g2.fillRect(x - 1 , y - 1, gp.tileSize*8+2, 22);

                    g2.setColor(new Color(255,0,30)); 
                    g2.fillRect(x, y, (int)hpBarValue, 20);

                    // display boss name
                    g2.setFont(g2.getFont().deriveFont(25f));
                    g2.setColor(Color.white);
                    g2.drawString(monster.name, x + 4, y - 10);
                }
            }
        }
    }

    public void drawMessage(Graphics2D g2) {
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
}
