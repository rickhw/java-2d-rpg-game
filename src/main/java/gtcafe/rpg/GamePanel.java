package gtcafe.rpg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.Player;
import gtcafe.rpg.object.SuperObject;
import gtcafe.rpg.tile.TileManager;

public class GamePanel extends JPanel implements Runnable { 
    // Tile Settings
    final int originalTileSize = 16; // 16x16 pixel
    final int scale = 3;
    
    // SCREEN SETTINGS
    public final int tileSize = originalTileSize * scale; // 48x48 pixel
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD MAP SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // FPS
    public final int FPS = 60;

    // SYSTEM
    TileManager tileManager = new TileManager(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this); // day7-4 add
    public UI ui = new UI(this);
    Thread gameThread;

    // ENTITY and OBJECT
    public Player player = new Player(this, keyHandler);
    public SuperObject obj[] = new SuperObject[10]; // day7-3 add: 10 slot for objects, display the 10 objs at the same time.
    public Entity npc[] = new Entity[10];

    // GAME STATE: for different purpose for game, 
    // for example, guide screen, intro story screen, menu screen ... etc.
    public int gameState;
    public final static int TITLE_STATE = 0;
    public final static int PLAY_STATE = 1;
    public final static int PAUSE_STATE = 2;
    public final static int DIALOGUE_STATE = 3;
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }

    public void setupGame() {
        assetSetter.setObject();
        assetSetter.setNPC();
        playMusic(Sound.MUSIC__MAIN_THEME); // index with 0 => main music
        stopMusic();

        gameState = TITLE_STATE;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    // Game loop 1: Sleep 
    // @Override
    // public void run() {
    //     double drawInterval = 1000000000 / FPS; // 0.016666 seconds per frame
    //     double nextDrawTime = System.nanoTime() + drawInterval;

    //     while(gameThread != null) {

    //         // 1. UPDATE: update informations such as player position
    //         update();

    //         // 2. DRAW: draw the game with updated informations
    //         repaint();

    //         try {
    //             double remainingTime = nextDrawTime - System.nanoTime();
    //             remainingTime = remainingTime/1000000;

    //             if(remainingTime < 0) {
    //                 remainingTime = 0;
    //             }

    //             Thread.sleep((long) remainingTime);

    //             nextDrawTime += drawInterval;
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         }
    //         nextDrawTime = System.nanoTime() + drawInterval;
    //     }
    // }


    // Game loop 2: Delta/Accumulator method
    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; // ~= 16,666,666.6666666667
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            // 畫的時間在 FPS 範圍裡, 也就是 delta 大於 drawInterval
            if(delta >= 1) {
                update();
                repaint();  // call paintComponent by parent class
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.printf("[GamePanel#run] FPS: [%s], drawCount: [%s], timer: [%s] \n", FPS, drawCount, timer);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (gameState == PLAY_STATE) {
            player.update();
            for(int i=0; i<npc.length; i++) {
                if(npc[i] != null) 
                    npc[i].update();
            }
        } else if (gameState == PAUSE_STATE) {
            // nothing, we don't update the player info
        }
    }

    // paint every loop
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // DEBUG
        long drawStart = 0;
        if (keyHandler.checkDrawTime) 
            drawStart = System.nanoTime();

        // TITLE SCREEN
        if (gameState == TITLE_STATE) {
            ui.draw(g2);
        } else {
            // TILE
            tileManager.draw(g2);

            // OBJECT
            for (int i=0; i<obj.length; i++) {
                if (obj[i] != null)
                    obj[i].draw(g2, this);
            }
            
            // NPC
            for (int i=0; i<npc.length; i++) {
                if (npc[i] != null)
                    npc[i].draw(g2);
            }

            // PLAYER
            player.draw(g2);

            // UI
            ui.draw(g2);
        }


        // DEBUG
        if (keyHandler.checkDrawTime) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.red);
            g2.drawString("Draw Time: " + passed, 10, 400);
            System.out.println("[GamePanel#paintComponent] Draw Time: "+passed);
        }

        g2.dispose();

    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        soundEffect.setFile(i);
        soundEffect.play();
    }
}
