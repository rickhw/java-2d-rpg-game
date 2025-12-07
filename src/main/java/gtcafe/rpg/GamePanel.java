package gtcafe.rpg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JPanel;

import gtcafe.rpg.ai.PathFinder;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.Player;
import gtcafe.rpg.entity.Projectile;
import gtcafe.rpg.environment.EnvironmentManager;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.tile.Map;
import gtcafe.rpg.tile.Scense;
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
    public final int maxMap = 10; // map list
    public Scense currentMap = Scense.WORLD_MAP;      // indicate current map number

    // FOR FULL SCREEN
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    public Graphics2D g2;
    public boolean fullScreenOn = false;

    // FPS
    public final int FPS = 60;
    int drawCount = 0;

    // SYSTEM
    public TileManager tileManager = new TileManager(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this); // day7-4 add
    public UI ui = new UI(this);
    public EventHandler eventHandler = new EventHandler(this);
    Config config = new Config(this);
    public PathFinder pathFinder = new PathFinder(this);
    EnvironmentManager eManager = new EnvironmentManager(this);
    Map map = new Map(this);
    Thread gameThread;

    // ENTITY and OBJECT
    public Player player = new Player(this, keyHandler);
    public Entity obj[][] = new Entity[maxMap][20];     // [mapIndex][index]
    public Entity npc[][] = new Entity[maxMap][10];     // [mapIndex][index]
    public Entity monster[][] = new Entity[maxMap][20]; // [mapIndex][index]
    public InteractiveTile iTile[][] = new InteractiveTile[maxMap][50]; // [mapIndex][index]
    public Entity projectile[][] = new Entity[maxMap][20];  // for CollisionChecker.checkEntity() signature
    // public ArrayList<Projectiles> projectilesList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();

    // GAME STATE: for different purpose for game,
    public GameState gameState;

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

        eManager.setup();

        playBackgroundMusic(Sound.MUSIC__MAIN_THEME); // index with 0 => main music
        stopBackgroundMusic();

        gameState = GameState.TITLE;

        // 避免重算所有的元件, 改用這個螢幕大小的 Graphics2D 畫
        // for Windows: BufferedImage.TYPE_INT_ARGB
        // for Mac: BufferedImage.TYPE_INT_ARGB_PRE
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        g2 = (Graphics2D) tempScreen.getGraphics();

        if (fullScreenOn == true) {
            setFullScreen();
        }
    }

    public void retry() {
        player.setDefaultPosition();
        player.restoreLifeAndMana();        
        assetSetter.setNPC();
        assetSetter.setMonster();
    }

    public void restart() {
        player.setDefaultValues();
        player.setDefaultPosition();
        player.restoreLifeAndMana();
        player.setItems();

        assetSetter.setObject();
        assetSetter.setNPC();
        assetSetter.setMonster();
        assetSetter.setInteractiveTiles();
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
                drawToBufferedScreen(); // draw everything to the buffered image
                drawToScreen();     // draw the buffered image to screen

                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                System.out.printf("%s [GameLoop] FPS: [%s], State: [%s], Scense: [%s], Position: [%s,%s]\n", sdFormat.format(new Date()), FPS, gameState.name, currentMap.name, (player.worldX + player.solidArea.x)/tileSize, (player.worldY + player.solidArea.y)/tileSize);
                // drawCount = 0;
                timer = 0;
            }
        }
    }

    // call by GameLoop
    public void update() {
        if (gameState == GameState.PLAYING) {
            // 1. PLAYER
            player.update();

            // 2. NPC
            for(int i=0; i<npc[1].length; i++) {
                if(npc[currentMap.index][i] != null) {
                    npc[currentMap.index][i].update();
                }
            }

            // 3. MONSTER
            for(int i=0; i<monster[1].length; i++) {
                if(monster[currentMap.index][i] != null) {
                    if (monster[currentMap.index][i].alive == true && monster[currentMap.index][i].dying == false) {
                        monster[currentMap.index][i].update();
                    }
                    if (monster[currentMap.index][i].alive == false) {
                        monster[currentMap.index][i].checkDrop(); // when monster die, check the dropped items.
                        monster[currentMap.index][i] = null;
                    }
                }
            }

            // 4. PROJECTILES
            for(int i=0; i<projectile[1].length; i++) {
                Entity pjt = projectile[currentMap.index][i];
                if(pjt != null) {
                    if (pjt.alive == true) {
                        pjt.update();
                    }
                    if (pjt.alive == false) {
                        projectile[currentMap.index][i] = null;
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
            for (int i=0; i<iTile[1].length; i++) {
                if (iTile[currentMap.index][i] != null) {
                    iTile[currentMap.index][i].update();
                }
            }

            eManager.update();
        } 
        else if (gameState == GameState.PAUSE) {
            // nothing, we don't update the player info
        }
    }

    // for full screen mode, switch draw from paintComponent() to drawToTempScreen()
    public void drawToBufferedScreen() {
        g2.clearRect(0, 0, screenWidth2, screenHeight2);

        // 0. DEBUG
        long drawStart = 0;
        if (keyHandler.showDebugText)
            drawStart = System.nanoTime();

        // 1. TITLE SCREEN
        if (gameState == GameState.TITLE) {
            ui.draw(g2);
        } else if (gameState == GameState.DISPLAY_MAP) {
            map.drawFullMapScreen(g2);
        } else {
            // TILE
            tileManager.draw(g2);
            // INTERACTIVE TILES
            for(int i=0; i<iTile[1].length; i++) {
                if(iTile[currentMap.index][i] != null) { iTile[currentMap.index][i].draw(g2); }
            }

            // ADD ENTITY TO THE LIST
            entityList.add(player);
            for(int i=0; i<npc[1].length; i++) {
                if(npc[currentMap.index][i] != null) { entityList.add(npc[currentMap.index][i]); }
            }
            for(int i=0; i<obj[1].length; i++) {
                if(obj[currentMap.index][i] != null) { entityList.add(obj[currentMap.index][i]); }
            }
            for(int i=0; i<monster[1].length; i++) {
                if(monster[currentMap.index][i] != null) { entityList.add(monster[currentMap.index][i]); }
            }

            for(int i=0; i<projectile[1].length; i++) {    // @TODO: change dimension 1
                if(projectile[currentMap.index][i] != null) { entityList.add(projectile[currentMap.index][i]); }
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

            // ENVIRONMENT
            eManager.draw(g2);

            // MINIMAP
            map.drawMiniMap(g2);
            
            // UI
            ui.draw(g2);
        }

        // DEBUG
        if (keyHandler.showDebugText) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;

            g2.setFont(g2.getFont().deriveFont(24f));
            g2.setColor(Color.red);

            int x = 11;
            int y = 400;
            int lineHeight = 20;
            g2.drawString("Map: " + currentMap.name, x, y); y += lineHeight;
            g2.drawString("WorldX: " + player.worldX, x, y); y += lineHeight;
            g2.drawString("WorldY: " + player.worldY, x, y); y += lineHeight;
            g2.drawString("Col: " + (player.worldX + player.solidArea.x)/tileSize, x, y); y += lineHeight;
            g2.drawString("Row: " + (player.worldY + player.solidArea.y)/tileSize, x, y); y += lineHeight;
            g2.drawString("State: " + gameState.name, x, y);
            // g2.drawString("Draw Count: " + drawCount, x, y);
            // System.out.println("[GamePanel#paintComponent] Draw Time: "+passed);
        }
    }

    public void drawToScreen() {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
        g.dispose();
    }

    public void playBackgroundMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopBackgroundMusic() {
        music.stop();
    }

    public void playSoundEffect(int i) {
        soundEffect.setFile(i);
        soundEffect.play();
    }
}
