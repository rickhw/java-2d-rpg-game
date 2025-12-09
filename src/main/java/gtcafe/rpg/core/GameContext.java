package gtcafe.rpg.core;

import gtcafe.rpg.system.*;
import gtcafe.rpg.ui.UI;
import gtcafe.rpg.ai.PathFinder;
import gtcafe.rpg.environment.EnvironmentManager;
import gtcafe.rpg.tile.Map;
import gtcafe.rpg.tile.Scense;
import gtcafe.rpg.tile.TileManager;
import gtcafe.rpg.tile.interactive.InteractiveTile;
import gtcafe.rpg.data.SaveLoad;
import gtcafe.rpg.entity.Entity;
import gtcafe.rpg.entity.Player;
import java.util.ArrayList;

import gtcafe.rpg.state.GameState;
import gtcafe.rpg.state.State;

public interface GameContext {
    // Settings
    int getTileSize();
    int getScreenWidth();
    int getScreenHeight();
    int getMaxMap();
    int getMaxWorldCol();
    int getMaxWorldRow();

    Scense getCurrentMap();
    void setCurrentMap(Scense map);
    
    GameState getGameState();
    void setGameState(GameState state);

    // Systems
    TileManager getTileManager();
    KeyHandler getKeyHandler();
    Sound getMusic();
    void playMusic(int i);
    Sound getSoundEffect();
    void playSoundEffect(int i);
    
    boolean isFullScreenOn();
    void setFullScreenOn(boolean on);
    void resetGame(boolean restart);

    CollisionChecker getCollisionChecker();
    AssetSetter getAssetSetter();
    UI getGameUI();
    EventHandler getEventHandler();
    Config getConfig();
    PathFinder getPathFinder();
    EnvironmentManager getEnvironmentManager();
    Map getMap();
    SaveLoad getSaveLoad();

    // Entities & Data
    Player getPlayer();
    Entity[][] getObj();
    Entity[][] getNpc();
    Entity[][] getMonster();
    InteractiveTile[][] getInteractiveTile();
    Entity[][] getProjectile();
    ArrayList<Entity> getParticleList();
    ArrayList<Entity> getEntityList();
}