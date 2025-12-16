package gtcafe.rpg;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {

    public static JFrame window;
    public final static String GAME_TITLE = "M/A Legend";

    public static void main(String[] args) {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle(GAME_TITLE);
        // window.setUndecorated(true); // for full screen mode.
        new Main().setIcon();

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        gamePanel.config.loadConfig();
        if (gamePanel.fullScreenOn == true) {
            window.setUndecorated(true);
        }

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }

    public void setIcon() {
        ImageIcon icon = new ImageIcon(
                getClass().getClassLoader().getResource("gtcafe/rpg/assets/player/walking/boy_up_1.png"));
        window.setIconImage(icon.getImage());
    }
}
