package gtcafe.rpg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.Player;
import gtcafe.rpg.entity.Projectiles;
import gtcafe.rpg.tile.TileManager;
import gtcafe.rpg.tile.interactive.InteractiveTile;

public class GamePanel extends JPanel implements Runnable {
    // Tile Settings
    final int originalTileSize = 16; // 16x16 pixel
    final int scale = 3;

    // SCREEN SETTINGS
    public final int tileSize = originalTileSize * scale; // 48x48 pixel
    // public final int maxScreenCol = 16;
    public final int maxScreenCol = 20; // for screen size: 16:9 
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD MAP SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // FOR FULL SCREEN
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;

    // FPS
    public final int FPS = 60;
    int drawCount = 0;

    // SYSTEM
    public TileManager tileManager = new TileManager(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    Sound sound = new Sound();
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this); // day7-4 add
    public UI ui = new UI(this);
    public EventHandler eventHandler = new EventHandler(this);
    Thread gameThread;

    // ENTITY and OBJECT
    public Player player = new Player(this, keyHandler);
    public Entity obj[] = new Entity[10];
    public Entity npc[] = new Entity[10];
    public Entity monster[] = new Entity[20];
    public InteractiveTile iTile[] = new InteractiveTile[50];
    public ArrayList<Projectiles> projectilesList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();

    // GAME STATE: for different purpose for game,
    // for example, guide screen, intro story screen, menu screen ... etc.
    public GameState gameState;
    public boolean bgmState = true;
    public boolean debugMode = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(false);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }

    public void setFullScreen() {
        // GET LOCAL SCREEN DEVICE
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        // GET FULL SCREEN WIDTH AND HEIGHT
        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();

        // for MacOS, when setFullScreen get blank.
        Main.window.setVisible(true); 
    }

    public void setupGame() {
        
        assetSetter.setObject();
        assetSetter.setNPC();
        assetSetter.setMonster();
        assetSetter.setInteractiveTiles();

        // playBackgroundMusic(Sound.MUSIC__MAIN_THEME); // index with 0 => main music
        // stopBackgroundMusic();

        gameState = GameState.TITLE_STATE;

        // 避免重算所有的元件, 改用這個螢幕大小的 Graphics2D 畫
        // for Windows: BufferedImage.TYPE_INT_ARGB
        // for Mac: BufferedImage.TYPE_INT_ARGB_PRE
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        g2 = (Graphics2D) tempScreen.getGraphics();

        setFullScreen();
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
        // int drawCount = 0;

        while(gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            // 畫的時間在 FPS 範圍裡, 也就是 delta 大於 drawInterval
            if(delta >= 1) {
                update();
                // repaint();  // call paintComponent by parent class

                // for full screen
                drawToTempScreen(); // draw everything to the buffered image
                drawToScreen();     // draw the buffered image to screen

                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.printf("[GameLoop] FPS: [%s], GameState: [%s], DrawCount: [%s]\n", FPS, gameState, drawCount);
                // drawCount = 0;
                timer = 0;
            }
        }
    }

    // call by GameLoop
    public void update() {
        if (gameState == GameState.PLAY_STATE) {
            // 1. PLAYER
            player.update();

            // 2. NPC
            for(int i=0; i<npc.length; i++) {
                if(npc[i] != null) npc[i].update();
            }

            // 3. MONSTER
            for(int i=0; i<monster.length; i++) {
                if(monster[i] != null) {
                    if (monster[i].alive == true && monster[i].dying == false) {
                        monster[i].update();
                    }
                    if (monster[i].alive == false) {
                        monster[i].checkDrop(); // when monster die, check the dropped items.
                        monster[i] = null;
                    }
                }
            }

            // 4. PROJECTILES
            for(int i=0; i<projectilesList.size(); i++) {
                if(projectilesList.get(i) != null) {
                    if (projectilesList.get(i).alive == true) {
                        projectilesList.get(i).update();
                    }
                    if (projectilesList.get(i).alive == false) {
                        projectilesList.remove(i);
                    }
                }
            }

            // 5. SCAN PARTICLES
            for(int i=0; i<particleList.size(); i++) {
                if(particleList.get(i) != null) {
                    if (particleList.get(i).alive == true) {
                        particleList.get(i).update();
                    }
                    if (particleList.get(i).alive == false) {
                        particleList.remove(i);
                    }
                }
            }

            // 6. INTERACTIVE_TILES
            for (int i=0; i<iTile.length; i++) {
                if (iTile[i] != null) {
                    iTile[i].update();
                }
            }
        } 
        else if (gameState == GameState.PAUSE_STATE) {
            // nothing, we don't update the player info
        }
    }

    // for full screen mode, switch draw from paintComponent() to drawToTempScreen()
    public void drawToTempScreen() {
        g2.clearRect(0, 0, screenWidth2, screenHeight2);

        // 0. DEBUG
        long drawStart = 0;
        if (keyHandler.showDebugText)
            drawStart = System.nanoTime();

        // 1. TITLE SCREEN
        if (gameState == GameState.TITLE_STATE) {
            ui.draw(g2);
        } else {
            // TILE
            tileManager.draw(g2);
            // INTERACTIVE TILES
            for(int i=0; i<iTile.length; i++) {
                if(iTile[i] != null) { iTile[i].draw(g2); }
            }

            // ADD ENTITY TO THE LIST
            entityList.add(player);
            for(int i=0; i<npc.length; i++) {
                if(npc[i] != null) { entityList.add(npc[i]); }
            }
            for(int i=0; i<obj.length; i++) {
                if(obj[i] != null) { entityList.add(obj[i]); }
            }
            for(int i=0; i<monster.length; i++) {
                if(monster[i] != null) { entityList.add(monster[i]); }
            }
            for(int i=0; i<projectilesList.size(); i++) {
                if(projectilesList.get(i) != null) { entityList.add(projectilesList.get(i)); }
            }
            for(int i=0; i<particleList.size(); i++) {
                if(particleList.get(i) != null) { entityList.add(particleList.get(i)); }
            }

            // SORT
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    int result = Integer.compare(e1.worldY, e2.worldY);
                    return result;
                }
            });

            // DRAW ENTITIES
            for(int i=0; i<entityList.size(); i++) { entityList.get(i).draw(g2); }
            // CLEAN ENTITY LIST
            entityList.clear();

            // UI
            ui.draw(g2);
        }

        // DEBUG
        if (keyHandler.showDebugText) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;

            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.red);

            int x = 11;
            int y = 400;
            int lineHeight = 20;
            g2.drawString("WorldX: " + player.worldX, x, y); y += lineHeight;
            g2.drawString("WorldY: " + player.worldY, x, y); y += lineHeight;
            g2.drawString("Col: " + (player.worldX + player.solidArea.x)/tileSize, x, y); y += lineHeight;
            g2.drawString("Row: " + (player.worldY + player.solidArea.y)/tileSize, x, y); y += lineHeight;
            g2.drawString("Draw Count: " + drawCount, x, y);
            // System.out.println("[GamePanel#paintComponent] Draw Time: "+passed);
        }
    }

    public void drawToScreen() {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
        g.dispose();
    }

    // paint every loop
    // public void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     Graphics2D g2 = (Graphics2D) g;

    //     // 0. DEBUG
    //     long drawStart = 0;
    //     if (keyHandler.showDebugText)
    //         drawStart = System.nanoTime();

    //     // 1. TITLE SCREEN
    //     if (gameState == GameState.TITLE_STATE) {
    //         ui.draw(g2);
    //     } else {
    //         // TILE
    //         tileManager.draw(g2);
    //         // INTERACTIVE TILES
    //         for(int i=0; i<iTile.length; i++) {
    //             if(iTile[i] != null) { iTile[i].draw(g2); }
    //         }

    //         // ADD ENTITY TO THE LIST
    //         entityList.add(player);
    //         for(int i=0; i<npc.length; i++) {
    //             if(npc[i] != null) { entityList.add(npc[i]); }
    //         }
    //         for(int i=0; i<obj.length; i++) {
    //             if(obj[i] != null) { entityList.add(obj[i]); }
    //         }
    //         for(int i=0; i<monster.length; i++) {
    //             if(monster[i] != null) { entityList.add(monster[i]); }
    //         }
    //         for(int i=0; i<projectilesList.size(); i++) {
    //             if(projectilesList.get(i) != null) { entityList.add(projectilesList.get(i)); }
    //         }
    //         for(int i=0; i<particleList.size(); i++) {
    //             if(particleList.get(i) != null) { entityList.add(particleList.get(i)); }
    //         }

    //         // SORT
    //         Collections.sort(entityList, new Comparator<Entity>() {
    //             @Override
    //             public int compare(Entity e1, Entity e2) {
    //                 int result = Integer.compare(e1.worldY, e2.worldY);
    //                 return result;
    //             }
    //         });

    //         // DRAW ENTITIES
    //         for(int i=0; i<entityList.size(); i++) { entityList.get(i).draw(g2); }
    //         // CLEAN ENTITY LIST
    //         entityList.clear();

    //         // UI
    //         ui.draw(g2);
    //     }

    //     // DEBUG
    //     if (keyHandler.showDebugText) {
    //         long drawEnd = System.nanoTime();
    //         long passed = drawEnd - drawStart;

    //         g2.setFont(new Font("Arial", Font.PLAIN, 20));
    //         g2.setColor(Color.red);

    //         int x = 11;
    //         int y = 400;
    //         int lineHeight = 20;
    //         g2.drawString("WorldX: " + player.worldX, x, y); y += lineHeight;
    //         g2.drawString("WorldY: " + player.worldY, x, y); y += lineHeight;
    //         g2.drawString("Col: " + (player.worldX + player.solidArea.x)/tileSize, x, y); y += lineHeight;
    //         g2.drawString("Row: " + (player.worldY + player.solidArea.y)/tileSize, x, y); y += lineHeight;
    //         g2.drawString("Draw Time: " + passed, x, y);
    //         // System.out.println("[GamePanel#paintComponent] Draw Time: "+passed);
    //     }

    //     g2.dispose();
    // }

    public void playBackgroundMusic(int i) {
        sound.setFile(i);
        sound.play();
        sound.loop();
    }

    public void stopBackgroundMusic() {
        sound.stop();
    }

    public void playSoundEffect(int i) {
        sound.setFile(i);
        sound.play();
    }
}
