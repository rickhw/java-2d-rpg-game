package gtcafe.rpg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

import javax.swing.JPanel;

import gtcafe.rpg.ai.PathFinder;
import gtcafe.rpg.data.SaveLoad;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.EntityGenerator;
import gtcafe.rpg.entity.Player;
import gtcafe.rpg.environment.EnvironmentManager;
import gtcafe.rpg.state.GameState;
import gtcafe.rpg.system.CollisionChecker;
import gtcafe.rpg.system.Config;
import gtcafe.rpg.system.EventHandler;
import gtcafe.rpg.system.KeyHandler;
import gtcafe.rpg.system.Sound;
import gtcafe.rpg.system.GameConfig;
import gtcafe.rpg.tile.MapManager;
import gtcafe.rpg.tile.Map;
import gtcafe.rpg.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    // Tile Settings
    // REMOVED: originalTileSize, scale (Moved to GameConfig)

    // SCREEN SETTINGS
    public final int tileSize = GameConfig.tileSize; // 48x48 pixel
    // public final int maxScreenCol = 16;
    public final int maxScreenCol = GameConfig.maxScreenCol; // for screen size: 16:9
    public final int maxScreenRow = GameConfig.maxScreenRow;
    public final int screenWidth = GameConfig.screenWidth; // 768 pixels
    public final int screenHeight = GameConfig.screenHeight; // 576 pixels

    // WORLD MAP SETTINGS
    public int maxWorldCol;
    public int maxWorldRow;
    public final int maxMap = GameConfig.maxMap; // map list
    public Map currentMap = Map.WORLD_MAP; // indicate current map number

    // FOR FULL SCREEN
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    public Graphics2D g2;
    public boolean fullScreenOn = false;

    // FPS
    public final int FPS = GameConfig.FPS;
    int drawCount = 0;

    // SYSTEM
    public TileManager tileManager = new TileManager(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    public Sound music = new Sound();
    public Sound soundEffect = new Sound();
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this); // day7-4 add
    public UI ui = new UI(this);
    public EventHandler eventHandler = new EventHandler(this);
    Config config = new Config(this);
    public PathFinder pathFinder = new PathFinder(this);
    EnvironmentManager eManager = new EnvironmentManager(this);
    public MapManager map = new MapManager(this);
    public EntityGenerator eGenerator = new EntityGenerator(this);
    public SaveLoad saveLoad = new SaveLoad(this, eGenerator);
    public CutsenseManager csManager = new CutsenseManager(this);

    // Thread
    Thread gameThread;

    // ENTITY and OBJECT
    public Player player = new Player(this, keyHandler);
    public ArrayList<Entity> obj[] = new ArrayList[maxMap];
    public ArrayList<Entity> npc[] = new ArrayList[maxMap];
    public ArrayList<Entity> monster[] = new ArrayList[maxMap];
    public ArrayList<Entity> iTile[] = new ArrayList[maxMap];
    public ArrayList<Entity> projectile[] = new ArrayList[maxMap];
    // public ArrayList<Projectiles> projectilesList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();

    // GAME STATE: for different purpose for game,
    public GameState gameState;
    public boolean bossBattleOn = false;

    // AREA
    public int currentArea;
    public int nextArea;
    public static final int OUTSIDE = 50;
    public static final int INDOOR = 51;
    public static final int DUNGEON = 52;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        initGameInfo();
    }

    public void initGameInfo() {

        for (int i = 0; i < maxMap; i++) {
            obj[i] = new ArrayList<Entity>();
            npc[i] = new ArrayList<Entity>();
            monster[i] = new ArrayList<Entity>();
            iTile[i] = new ArrayList<Entity>();
            projectile[i] = new ArrayList<Entity>();
        }
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
        currentArea = OUTSIDE; // return the default area

        assetSetter.setObject();
        assetSetter.setNPC();
        assetSetter.setMonster();
        assetSetter.setInteractiveTiles();

        eManager.setup();

        playBackgroundMusic(Sound.MUSIC__MAIN_THEME); // index with 0 => main music
        stopBackgroundMusic();

        gameState = GameState.TITLE;
        currentArea = OUTSIDE;

        // 避免重算所有的元件, 改用這個螢幕大小的 Graphics2D 畫
        // for Windows: BufferedImage.TYPE_INT_ARGB
        // for Mac: BufferedImage.TYPE_INT_ARGB_PRE
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        g2 = (Graphics2D) tempScreen.getGraphics();

        if (fullScreenOn == true) {
            setFullScreen();
        }
    }

    public void resetGame(boolean restart) {
        stopBackgroundMusic();
        currentArea = OUTSIDE;

        removeTempEntity();
        bossBattleOn = false;

        player.setDefaultPosition();
        player.restoreStatus();
        player.resetCounter();
        assetSetter.setNPC();
        assetSetter.setMonster();

        if (restart) {
            player.setDefaultValues();
            assetSetter.setObject();
            assetSetter.setInteractiveTiles();
            eManager.lighting.resetDay();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Game loop 1: Sleep
    // @Override
    // public void run() {
    // double drawInterval = 1000000000 / FPS; // 0.016666 seconds per frame
    // double nextDrawTime = System.nanoTime() + drawInterval;

    // while(gameThread != null) {

    // // 1. UPDATE: update informations such as player position
    // update();

    // // 2. DRAW: draw the game with updated informations
    // repaint();

    // try {
    // double remainingTime = nextDrawTime - System.nanoTime();
    // remainingTime = remainingTime/1000000;

    // if(remainingTime < 0) {
    // remainingTime = 0;
    // }

    // Thread.sleep((long) remainingTime);

    // nextDrawTime += drawInterval;
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // nextDrawTime = System.nanoTime() + drawInterval;
    // }
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

        int fpsCount = 0;
        long totalDrawTime = 0;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            // 畫的時間在 FPS 範圍裡, 也就是 delta 大於 drawInterval
            if (delta >= 1) {
                long drawStart = System.nanoTime();
                update();
                // repaint(); // call paintComponent by parent class

                // for full screen
                drawToBufferedScreen(); // draw everything to the buffered image
                drawToScreen(); // draw the buffered image to screen
                long drawEnd = System.nanoTime();
                long drawTime = drawEnd - drawStart;

                totalDrawTime += drawTime;
                fpsCount++;

                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                // average draw time in one second
                long averageDrawTime = totalDrawTime / fpsCount;
                long remainingTime = (long) (drawInterval - averageDrawTime);
                // 剩下的時間百分比
                double remainingPercent = (double) remainingTime / drawInterval * 100;

                SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                System.out.printf(
                        "%s [GameLoop] FPS: [%s], Remaining Percent: [%.2f], State: [%s], Map: [%s], Position: [%s,%s]\n",
                        sdFormat.format(new Date()), fpsCount, remainingPercent, gameState.name, currentMap.name,
                        (player.getWorldX() + player.solidArea.x) / tileSize,
                        (player.getWorldY() + player.solidArea.y) / tileSize);
                // drawCount = 0;
                timer = 0;

                fpsCount = 0;
                totalDrawTime = 0;
            }
        }
    }

    // call by GameLoop
    public void update() {
        if (gameState == GameState.PLAY) {
            // 1. PLAYER
            player.update();

            // 2. NPC
            for (int i = 0; i < npc[currentMap.index].size(); i++) {
                Entity n = npc[currentMap.index].get(i);
                if (n != null) {
                    if (n.alive == true && n.dying == false) {
                        n.update();
                    }
                    if (n.alive == false) {
                        n.checkDrop();
                        npc[currentMap.index].remove(i);
                        i--;
                    }
                }
            }

            // 3. MONSTER
            for (int i = 0; i < monster[currentMap.index].size(); i++) {
                Entity m = monster[currentMap.index].get(i);
                if (m != null) {
                    if (m.alive == true && m.dying == false) {
                        m.update();
                    }
                    if (m.alive == false) {
                        m.checkDrop();
                        monster[currentMap.index].remove(i);
                        i--;
                    }
                }
            }

            // 4. PROJECTILES
            for (int i = 0; i < projectile[currentMap.index].size(); i++) {
                Entity p = projectile[currentMap.index].get(i);
                if (p != null) {
                    if (p.alive == true) {
                        p.update();
                    }
                    if (p.alive == false) {
                        projectile[currentMap.index].remove(i);
                        i--;
                    }
                }
            }

            // 5. SCAN PARTICLES
            for (int i = 0; i < particleList.size(); i++) {
                if (particleList.get(i) != null) {
                    if (particleList.get(i).alive == true) {
                        particleList.get(i).update();
                    }
                    if (particleList.get(i).alive == false) {
                        particleList.remove(i);
                    }
                }
            }

            // 6. INTERACTIVE_TILES
            for (int i = 0; i < iTile[currentMap.index].size(); i++) {
                Entity tiles = iTile[currentMap.index].get(i);
                if (tiles != null) {
                    tiles.update();
                }
            }

            eManager.update();
        } else if (gameState == GameState.PAUSE) {
            // nothing, we don't update the player info
        }
    }

    // for full screen mode, switch draw from paintComponent() to drawToTempScreen()
    public void drawToBufferedScreen() {
        g2.clearRect(0, 0, screenWidth2, screenHeight2);

        // 0. DEBUG
        // 0. DEBUG
        // long drawStart = 0;
        // if (keyHandler.showDebugText)
        // drawStart = System.nanoTime();

        // 1. TITLE SCREEN
        if (gameState == GameState.TITLE) {
            ui.draw(g2);
        } else if (gameState == GameState.DISPLAY_MAP) {
            map.drawFullMapScreen(g2);
        } else {
            // TILE
            tileManager.draw(g2);

            // INTERACTIVE TILES
            for (int i = 0; i < iTile[currentMap.index].size(); i++) {
                Entity tiles = iTile[currentMap.index].get(i);
                if (tiles != null) {
                    tiles.draw(g2);
                }
            }

            // ADD ENTITY TO THE LIST (This section is replaced by direct drawing loops)
            entityList.add(player); // Player is added to entityList, but then drawn directly later. This might be a
                                    // logical inconsistency from the user's snippet.
                                    // Following the user's snippet, the entityList is not used for drawing here.

            // NPC
            for (int i = 0; i < npc[currentMap.index].size(); i++) {
                Entity n = npc[currentMap.index].get(i);
                if (n != null) {
                    n.draw(g2);
                }
            }

            // MONSTER
            for (int i = 0; i < monster[currentMap.index].size(); i++) {
                Entity m = monster[currentMap.index].get(i);
                if (m != null) {
                    m.draw(g2);
                }
            }

            // OBJECTS
            for (int i = 0; i < obj[currentMap.index].size(); i++) {
                if (obj[currentMap.index].get(i) != null) {
                    obj[currentMap.index].get(i).draw(g2);
                }
            }

            // PROJECTILES
            // NOTE: we need check the size of array list to avoid IndexOutOfBoundsException
            for (int i = 0; i < projectile[currentMap.index].size(); i++) {
                Entity p = projectile[currentMap.index].get(i);
                if (p != null) {
                    p.draw(g2);
                }
            }

            // PARTICLE
            for (int i = 0; i < particleList.size(); i++) {
                Entity p = particleList.get(i);
                if (p != null) {
                    p.draw(g2);
                }
            }

            player.draw(g2); // Player is drawn directly here

            // ENVIRONMENT
            eManager.draw(g2);

            // MINIMAP
            map.drawMiniMap(g2);

            // CUTSCENE
            csManager.draw(g2);

            // UI
            ui.draw(g2);
        }

        // DEBUG
        if (keyHandler.showDebugText) {
            // long drawEnd = System.nanoTime();

            g2.setFont(g2.getFont().deriveFont(24f));
            g2.setColor(Color.red);

            int x = 11;
            int y = 400;
            int lineHeight = 20;
            g2.drawString("Map: " + currentMap.name, x, y);
            y += lineHeight;
            g2.drawString("WorldX: " + player.getWorldX(), x, y);
            y += lineHeight;
            g2.drawString("WorldY: " + player.getWorldY(), x, y);
            y += lineHeight;
            g2.drawString("Col: " + (player.getWorldX() + player.solidArea.x) / tileSize, x, y);
            y += lineHeight;
            g2.drawString("Row: " + (player.getWorldY() + player.solidArea.y) / tileSize, x, y);
            y += lineHeight;
            g2.drawString("State: " + gameState.name, x, y);
            y += lineHeight;
            g2.drawString("God Mode: " + keyHandler.godModeOn, x, y);
            y += lineHeight;
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

    public void chagneArea() {
        if (nextArea != currentArea) {
            stopBackgroundMusic();

            if (nextArea == OUTSIDE) {
                playBackgroundMusic(Sound.MUSIC__MAIN_THEME);
            }
            if (nextArea == INDOOR) {
                playBackgroundMusic(Sound.MUSIC__MERCHANT);
            }
            if (nextArea == DUNGEON) {
                playBackgroundMusic(Sound.MUSIC__DUNGEON);
            }
            // reset NPCs object
            assetSetter.setNPC();
        }

        currentArea = nextArea;

        // Monsters respawn (重生)
        assetSetter.setMonster();
    }

    public void removeTempEntity() {
        // scan the tempEntity, 例如 boss battle 暫時的 鐵門
        for (int mapNum = 0; mapNum < maxMap; mapNum++) {
            for (int i = 0; i < obj[mapNum].size(); i++) {
                if (obj[mapNum].get(i) != null && obj[mapNum].get(i).tempObj == true) {
                    obj[mapNum].remove(i);
                }
            }
        }
    }
}
