package gtcafe.rpg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import gtcafe.rpg.entity.Player;
import gtcafe.rpg.object.SuperObject;
import gtcafe.rpg.tile.TileManager;

public class GamePanel extends JPanel implements Runnable { 
    // Screen settings
    final int originalTileSize = 16; // 16x16
    final int scale = 4;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHight = tileSize * maxScreenRow; // 576 pixels

    // World Map settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    // public final int worldWidth = tileSize * maxWorldCol; // 2400 pixels
    // public final int worldHeight = tileSize * maxWorldRow; // 2400 pixels

    // FPS
    final int FPS = 60;

    // SYSTEM
    TileManager tileManager = new TileManager(this);
    KeyHandler keyHandler = new KeyHandler();
    Sound sound = new Sound();
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this); // day7-4 add
    Thread gameThread;

    // ENTITY and OBJECT
    public Player player = new Player(this, keyHandler);
    public SuperObject obj[] = new SuperObject[10]; // day7-3 add: 10 slot for objects, display the 10 objs at the same time.
    
    // set player's default position
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 5;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }

    // day7-4-1 start
    public void setupGame() {
        assetSetter.setObject();
        playMusic(Sound.MAIN_THEME); // index with 0 => main music
    }
    // day7-4-1 end

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
        double drawInterval = 1000000000 / FPS; // 0.016666 seconds per frame
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

            if(delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        // if(keyHandler.upPressed) {
        //     playerY -= playerSpeed;
        // }
        // if(keyHandler.downPressed) {
        //     playerY += playerSpeed;
        // }
        // if(keyHandler.leftPressed) {
        //     playerX -= playerSpeed;
        // }
        // if(keyHandler.rightPressed) {
        //     playerX += playerSpeed;
        // }
        player.update();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // TILE
        tileManager.draw(g2);

        // day7-4-2 start
        // OBJECT
        for (int i=0; i<obj.length; i++) {
            if (obj[i] != null)
                obj[i].draw(g2, this);
        }
        // day7-4-2 end
        
        // PLAYER
        player.draw(g2);

        g2.dispose();

    }

    //day9-2: start
    public void playMusic(int i) {
        sound.setFile(i);
        sound.play();
        sound.loop();
    }

    public void stopMusic() {
        sound.stop();
    }

    public void playSE(int i) {
        sound.setFile(i);
        sound.play();
    }

    // day9-2 end
}
