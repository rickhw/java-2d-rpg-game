package gtcafe.rpg.environment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;

import gtcafe.rpg.GamePanel;
import gtcafe.rpg.state.DayState;

public class Lighting {
    GamePanel gp;
    BufferedImage darknessFilter;
    public int dayCounter;
    public float filterAlpha = 0f;

    // Day State
    public DayState dayState = DayState.DAY;


    public Lighting(GamePanel gp) {
        this.gp = gp;
        setLightSource();
    }

    public void setLightSource() {
        // Create a buffered image
        darknessFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB_PRE); // for windows, use TYPE_INT_ARGB
        Graphics2D g2 = (Graphics2D) darknessFilter.getGraphics();
     
        // 
        if(gp.player.currentLight == null) {
            g2.setColor(new Color(0,0,0.1f,0.90f));
        }
        // player has a lantern
        else {
            // Get the center x and y of the light circle
            int centerX = gp.player.screenX + (gp.tileSize) / 2;
            int centerY = gp.player.screenY + (gp.tileSize) / 2;
        
            // Create a gradation (層次) effect within the light circle
            Color color[] = new Color[5];
            float fraction[] = new float[5]; // fraction 分數, distance between the circle

            color[0] = new Color(0,0,0.1f,0f);
            color[1] = new Color(0,0,0.1f,0.25f);
            color[2] = new Color(0,0,0.1f,0.5f);
            color[3] = new Color(0,0,0.1f,0.75f);
            color[4] = new Color(0,0,0.1f,0.90f);

            fraction[0] = 0f;
            fraction[1] = 0.25f;
            fraction[2] = 0.5f;
            fraction[3] = 0.75f;
            fraction[4] = 1f;

            // Create a gradation paint settings for the light circle
            RadialGradientPaint gPaint = new RadialGradientPaint(centerX, centerY, gp.player.currentLight.lightRadius, fraction, color);

            // Set the gradient data on g2
            g2.setPaint(gPaint);
        }

        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
      
        g2.dispose();
    }
    
    public void resetDay() {
        dayState = DayState.DAY;
        filterAlpha = 0f;
    }
    
    public void update() {
        if (gp.player.lightUpdated == true) { // avoid call 60 time per second
            setLightSource();
            gp.player.lightUpdated = false;
        }

        // Check the state of the day
        if (dayState == DayState.DAY) {
            dayCounter++;
            if (dayCounter > 600) { // 600 fps, 10 second
                dayState = DayState.DUSK;
                dayCounter = 0;
            }
        }
        if (dayState == DayState.DUSK) {
            filterAlpha += 0.001f;

            if (filterAlpha > 1f) {
                filterAlpha = 1f;   // max value of alpha
                dayState = DayState.NIGHT;
            }
        }
        if (dayState == DayState.NIGHT) {
            dayCounter++;
            if (dayCounter > 600) {
                dayState = DayState.DAWN;
                dayCounter = 0;
            }
        }

        if (dayState == DayState.DAWN) {
            filterAlpha -= 0.001f;
            if(filterAlpha < 0f) {
                filterAlpha = 0;
                dayState = DayState.DAY;
            }
        }
    } 

    public void draw(Graphics2D g2) {

        if (gp.currentArea == GamePanel.OUTSIDE) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, filterAlpha));    
        }

        if (gp.currentArea == GamePanel.OUTSIDE || gp.currentArea == GamePanel.DUNGEON) {
            g2.drawImage(darknessFilter, 0, 0, null);
        }        

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Show the Day only outsite. 
        if (gp.currentArea == GamePanel.OUTSIDE) {
            String situation = dayState.name;
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50f));
            g2.drawString(situation, gp.tileSize * (gp.maxScreenCol - 2), gp.tileSize * (gp.maxScreenRow - 1));
        }
    }


}
