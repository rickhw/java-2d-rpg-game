package gtcafe.rpg.state;

import gtcafe.rpg.core.GameContext;
import gtcafe.rpg.entity.Entity;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Comparator;

public class PlayState implements State {
    GameContext context;

    public PlayState(GameContext context) {
        this.context = context;
    }

    @Override
    public void setup() {
        // Setup logic if needed
    }

    @Override
    public void update() {
        int currentMapIndex = context.getCurrentMap().index;

        // 1. PLAYER
        context.getPlayer().update();

        // 2. NPC
        Entity[] npcList = context.getNpc()[currentMapIndex];
        for(int i=0; i<npcList.length; i++) {
            if(npcList[i] != null) {
                npcList[i].update();
            }
        }

        // 3. MONSTER
        Entity[] monsterList = context.getMonster()[currentMapIndex];
        for(int i=0; i<monsterList.length; i++) {
            if(monsterList[i] != null) {
                if (monsterList[i].alive == true && monsterList[i].dying == false) {
                    monsterList[i].update();
                }
                if (monsterList[i].alive == false) {
                    monsterList[i].checkDrop(); // when monster die, check the dropped items.
                    context.getMonster()[currentMapIndex][i] = null;
                }
            }
        }

        // 4. PROJECTILES
        Entity[] projList = context.getProjectile()[currentMapIndex];
        for(int i=0; i<projList.length; i++) {
            Entity pjt = projList[i];
            if(pjt != null) {
                if (pjt.alive == true) {
                    pjt.update();
                }
                if (pjt.alive == false) {
                    context.getProjectile()[currentMapIndex][i] = null;
                }
            }
        }

        // 5. SCAN PARTICLES
        for(int i=0; i<context.getParticleList().size(); i++) {
            if(context.getParticleList().get(i) != null) {
                if (context.getParticleList().get(i).alive == true) {
                    context.getParticleList().get(i).update();
                }
                if (context.getParticleList().get(i).alive == false) {
                    context.getParticleList().remove(i);
                }
            }
        }

        // 6. INTERACTIVE_TILES
        if (context.getInteractiveTile()[currentMapIndex] != null) {
            for (int i=0; i<context.getInteractiveTile()[currentMapIndex].length; i++) {
                if (context.getInteractiveTile()[currentMapIndex][i] != null) {
                    context.getInteractiveTile()[currentMapIndex][i].update();
                }
            }
        }

        context.getEnvironmentManager().update();
    }

    @Override
    public void draw(Graphics2D g2) {
        int currentMapIndex = context.getCurrentMap().index;

        // TILE
        context.getTileManager().draw(g2);
        
        // INTERACTIVE TILES
        if (context.getInteractiveTile()[currentMapIndex] != null) {
            for(int i=0; i<context.getInteractiveTile()[currentMapIndex].length; i++) {
                if(context.getInteractiveTile()[currentMapIndex][i] != null) { 
                    context.getInteractiveTile()[currentMapIndex][i].draw(g2); 
                }
            }
        }

        // ADD ENTITY TO THE LIST
        context.getEntityList().add(context.getPlayer());
        
        Entity[] npcList = context.getNpc()[currentMapIndex];
        for(int i=0; i<npcList.length; i++) {
            if(npcList[i] != null) { context.getEntityList().add(npcList[i]); }
        }
        
        Entity[] objList = context.getObj()[currentMapIndex];
        for(int i=0; i<objList.length; i++) {
            if(objList[i] != null) { context.getEntityList().add(objList[i]); }
        }
        
        Entity[] monsterList = context.getMonster()[currentMapIndex];
        for(int i=0; i<monsterList.length; i++) {
            if(monsterList[i] != null) { context.getEntityList().add(monsterList[i]); }
        }

        Entity[] projList = context.getProjectile()[currentMapIndex];
        for(int i=0; i<projList.length; i++) {    
            if(projList[i] != null) { context.getEntityList().add(projList[i]); }
        }
        
        for(int i=0; i<context.getParticleList().size(); i++) {
            if(context.getParticleList().get(i) != null) { context.getEntityList().add(context.getParticleList().get(i)); }
        }

        // SORT
        Collections.sort(context.getEntityList(), new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                int result = Integer.compare(e1.worldY, e2.worldY);
                return result;
            }
        });

        // DRAW ENTITIES
        for(int i=0; i<context.getEntityList().size(); i++) { context.getEntityList().get(i).draw(g2); }
        // CLEAN ENTITY LIST
        context.getEntityList().clear();

        // ENVIRONMENT
        context.getEnvironmentManager().draw(g2);

        // MINIMAP
        context.getMap().drawMiniMap(g2);
        
        // UI
        context.getGameUI().draw(g2);
    }
}