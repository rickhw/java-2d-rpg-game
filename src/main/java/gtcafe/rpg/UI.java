package gtcafe.rpg;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.ui.*;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    
    // Fonts
    public Font maruMonica, purisaB;

    // Sub-screens
    public TitleScreen titleScreen;
    public PlayScreen playScreen;
    public PauseScreen pauseScreen;
    public DialogueScreen dialogueScreen;
    public CharacterScreen characterScreen;
    public OptionsScreen optionsScreen;
    public GameOverScreen gameOverScreen;
    public TransitionScreen transitionScreen;
    public TradeScreen tradeScreen;
    public SleepScreen sleepScreen;
    public Hud hud;
    public UiUtil uiUtil;

    // State
    public String currentDialogue = "";
    public int commandNum = 0;
    
    // Sub state
    // 0: the first screen
    // 1: the second screen ..
    public int titleScreenState = 0;
    public int subState = 0;

    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;
    
    public Entity npc;
    public int charIdx = 0;
    public String combinedText = "";

    public UI(GamePanel gp) {
        this.gp = gp;
        loadFont();
        
        hud = new Hud(gp);
        titleScreen = new TitleScreen(gp, this);
        playScreen = new PlayScreen(gp, hud);
        pauseScreen = new PauseScreen(gp, this, hud);
        dialogueScreen = new DialogueScreen(gp, this);
        characterScreen = new CharacterScreen(gp, this);
        optionsScreen = new OptionsScreen(gp, this);
        gameOverScreen = new GameOverScreen(gp, this);
        transitionScreen = new TransitionScreen(gp);
        tradeScreen = new TradeScreen(gp, this, dialogueScreen);
        sleepScreen = new SleepScreen(gp);
        uiUtil = new UiUtil();
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
    
    public void addMessage(String text) {
        hud.addMessage(text);
    }
   
    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(maruMonica);
        g2.setColor(Color.white);
        
        // TITLE STATE
        if (gp.gameState == GameState.TITLE) {
            titleScreen.draw(g2);
        }

        // PLAY STATE
        if(gp.gameState == GameState.PLAY) {
            playScreen.draw(g2);
        }

        // PAUSE STATE
        if (gp.gameState == GameState.PAUSE) {
            pauseScreen.draw(g2);
        }

        // DIALOGUE STATE
        if (gp.gameState == GameState.DIALOGUE) {
            dialogueScreen.draw(g2);
        }

        // CHARACTER STATE
        if (gp.gameState == GameState.CHARACTER) {
            characterScreen.draw(g2);
        }
        
        // OPTIONS STATE
        if (gp.gameState == GameState.OPTIONS) {
            optionsScreen.draw(g2);
        }
        // GAME OVER STATE
        if (gp.gameState == GameState.GAME_OVER) {
            gameOverScreen.draw(g2);
        }

        // TRANSITION STATE
        if (gp.gameState == GameState.TRANSITION) {
            transitionScreen.draw(g2);
        }

        // TRADE STATE
        if (gp.gameState == GameState.TRADE) {
            tradeScreen.draw(g2);
        }
        // SLEEP STATE
        if (gp.gameState == GameState.SLEEP) {
            sleepScreen.draw(g2);
        }
         
    }
}