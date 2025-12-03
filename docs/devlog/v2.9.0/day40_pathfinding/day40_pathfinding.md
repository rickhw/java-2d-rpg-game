
https://www.youtube.com/watch?v=Hd0D68guFKg&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=44

# Pathfinding

1. Guide the NPC to a specific location
    - 劇情需要，NPC 先在某個地方等 Player
2. NPC follows the player
    - 劇情需要，NPC 跟著 Player 完成任務
3. Monsters get aggro and start chasing the player, the conditions are:
    - when monster get attack
    - when player close to it.

## A* algorithm

- https://en.wikipedia.org/wiki/A*_search_algorithm
- [Step by Step Explanation of A* Pathfinding Algorithm in Java](https://www.youtube.com/watch?v=2JNEme00ZFA)

---
## Fix by Gemini

✦ I have investigated the codebase and identified the root causes for the NPC getting stuck during
  pathfinding. I have applied fixes to both the entity movement logic and the pathfinding algorithm itself.

  Summary of Changes

   1. Fixed Pathfinding Alignment Issues (`Entity.java`):
       * Problem: The logic to check if an entity was aligned with the path was too strict (< vs <=),
         causing entities with a full-tile hitbox (like 48x48 NPCs) to fail the "straight movement" check.
         This made them unable to move straight Up, Down, Left, or Right, often leaving them stuck or
         relying on diagonal fallback logic which isn't always triggered.
       * Fix: Updated the boundary checks to use inclusive comparison (<=). The NPC can now correctly
         detect when it is aligned with the path and move straight.

   2. Fixed "Stop Early" Behavior & Potential Crash (`Entity.java`):
       * Problem: The entity was programmed to stop one tile before the goal (when the "next step" was the
         goal). Additionally, if the entity actually reached the goal, the path list would be empty,
         causing a potential crash (IndexOutOfBoundsException) when accessing pathList.get(0).
       * Fix:
           * Added a check at the very beginning of searchPath: if the entity is already at the goal
             coordinates (startCol == goalCol), it stops pathfinding immediately (onPath = false).
           * Wrapped the movement logic in a check for pathList.size() > 0 to prevent crashes.
           * Removed the logic that forced the entity to stop one tile early, allowing it to walk all the
             way to the destination.

   3. Added Dynamic Entity Collision to Pathfinding (`PathFinder.java`):
       * Problem: The A* algorithm only considered static map tiles as "solid". It ignored other NPCs,
         Monsters, and the Player. This meant the pathfinder would tell an NPC to walk through the Player,
         but the physics engine (checkCollision) would stop it, causing the NPC to get stuck against
         dynamic objects.
       * Fix: Updated setNodes to mark tiles occupied by NPCs, Monsters, and the Player as solid
         (impassable) during path calculation. The NPC will now find a path around other characters instead
         of trying to walk through them.
       * Optimization: Refactored the setNodes method to move the entity/interactive-tile checks outside
         the main 50x50 tile loop, significantly improving performance (avoiding ~125,000 unnecessary
         iterations per frame).

  The NPC should now follow the path correctly, navigate around you and other monsters, and reach the exact
  target coordinate.


╭─────────────────────────────────────────────────────────────────────────────────────────────────────────╮
│ ✓  Edit src/.../rpg/entity/Entity.java:             // Based on the cu... =>             // Based on t… │
│                                                                                                         │
│ 362                                                                                                     │
│ 363   // Based on the current NPC's position,                                                           │
│ 364   // find out the relative direction of the next node                                               │
│ 365 - if (enTopY > nextY && enLeftX >= nextX && enRightX < nextX + gp.tileSize) {                       │
│ 365 + if (enTopY > nextY && enLeftX >= nextX && enRightX <= nextX + gp.tileSize) {                      │
│ 366       direction = Direction.UP;                                                                     │
│ 367 - } else if (enTopY < nextY && enLeftX >= nextX && enRightX < nextX + gp.tileSize) {                │
│ 367 + } else if (enTopY < nextY && enLeftX >= nextX && enRightX <= nextX + gp.tileSize) {               │
│ 368       direction = Direction.DOWN;                                                                   │
│ 369 - } else if (enTopY >= nextY && enBottomY < nextY + gp.tileSize) {                                  │
│ 369 + } else if (enTopY >= nextY && enBottomY <= nextY + gp.tileSize) {                                 │
│ 370       // left or right                                                                              │
│ 371       if (enLeftX > nextX) { direction = Direction.LEFT; }                                          │
│ 372       if (enLeftX < nextX) { direction = Direction.RIGHT; }                                         │
╰─────────────────────────────────────────────────────────────────────────────────────────────────────────╯
╭─────────────────────────────────────────────────────────────────────────────────────────────────────────╮
│ ✓  ReadFile src/.../gtcafe/rpg/GamePanel.java                                                           │
│                                                                                                         │
╰─────────────────────────────────────────────────────────────────────────────────────────────────────────╯
╭─────────────────────────────────────────────────────────────────────────────────────────────────────────╮
│ ✓  Edit src/.../rpg/entity/Entity.java:     public void searchPath(int... =>     public void searchPat… │
│                                                                                                         │
│ 350           gp.pathFinder.setNodes(startCol, startRow, goalCol, goalRow, this);                       │
│ 351                                                                                                     │
│ 352           if (gp.pathFinder.search() == true) {                                                     │
│ 353 +                                                                                                   │
│ 354               // Next worldX & worldY                                                               │
│ 355 +             if (gp.pathFinder.pathList.size() > 0) {                                              │
│ 356                   int nextX = gp.pathFinder.pathList.get(0).col * gp.tileSize;                      │
│ 357                   int nextY = gp.pathFinder.pathList.get(0).row * gp.tileSize;                      │
│ 358                                                                                                     │
│ ═══════════════════════════════════════════════════════════════════════════════════════════════════════ │
│ 400                   int nextCol = gp.pathFinder.pathList.get(0).col;                                  │
│ 401                   int nextRow = gp.pathFinder.pathList.get(0).row;                                  │
│ 402                   if (nextCol == goalCol && nextRow == goalRow) {                                   │
│ 401 -                 onPath = false;                                                                   │
│ 403 +                     // onPath = false; // Let it reach the goal                                   │
│ 404                   }                                                                                 │
│ 405               }                                                                                     │
│ 406           }                                                                                         │
│ 407       }                                                                                             │
│ 408 + }                                                                                                 │
╰─────────────────────────────────────────────────────────────────────────────────────────────────────────╯
╭─────────────────────────────────────────────────────────────────────────────────────────────────────────╮
│ ✓  Edit src/.../rpg/entity/Entity.java:     public void searchPath(int... =>     public void searchPat… │
│                                                                                                         │
│ 347   int startCol = (worldX + solidArea.x) / gp.tileSize;                                              │
│ 348   int startRow = (worldY + solidArea.y) / gp.tileSize;                                              │
│ 349                                                                                                     │
│ 350 + if (startCol == goalCol && startRow == goalRow) {                                                 │
│ 351 +     onPath = false;                                                                               │
│ 352 +     return;                                                                                       │
│ 353 + }                                                                                                 │
│ 354 +                                                                                                   │
│ 355   gp.pathFinder.setNodes(startCol, startRow, goalCol, goalRow, this);                               │
│ 356                                                                                                     │
│ 357   if (gp.pathFinder.search() == true) {                                                             │
╰─────────────────────────────────────────────────────────────────────────────────────────────────────────╯
╭─────────────────────────────────────────────────────────────────────────────────────────────────────────╮
│ ✓  Edit src/.../rpg/ai/PathFinder.java:     public void setNodes(int s... =>     public void setNodes(… │
│                                                                                                         │
│  79               node[col][row].solid = true;                                                          │
│  80           }                                                                                         │
│  81                                                                                                     │
│  82 +         // SET COST                                                                               │
│  83 +         getCost(node[col][row]);                                                                  │
│  84 +                                                                                                   │
│  85 +         col++;                                                                                    │
│  86 +         if(col == gp.maxWorldCol) {                                                               │
│  87 +             col = 0;                                                                              │
│  88 +             row++;                                                                                │
│  89 +         }                                                                                         │
│  90 +     }                                                                                             │
│  91 +                                                                                                   │
│  92       // CHECK INTERACTIVE TILES                                                                    │
│  83 -         for (int i=0; i<gp.iTile[1].length; i++) {   // TODO                                      │
│ ═══════════════════════════════════════════════════════════════════════════════════════════════════════ │
│  93 +     for (int i=0; i<gp.iTile[1].length; i++) {                                                    │
│  94           if(gp.iTile[gp.currentMap.index][i] != null &&                                            │
│       gp.iTile[gp.currentMap.index][i].destructible == true) {                                          │
│  95               int itCol = gp.iTile[gp.currentMap.index][i].worldX / gp.tileSize;                    │
│  96               int itRow = gp.iTile[gp.currentMap.index][i].worldY / gp.tileSize;                    │
│  97               node[itCol][itRow].solid = true;                                                      │
│  98           }                                                                                         │
│  99       }                                                                                             │
│  90 -         // SET COST                                                                               │
│  91 -         getCost(node[col][row]);                                                                  │
│ ═══════════════════════════════════════════════════════════════════════════════════════════════════════ │
│ 100                                                                                                     │
│  93 -         col++;                                                                                    │
│  94 -         if(col == gp.maxWorldCol) {                                                               │
│  95 -             col = 0;                                                                              │
│  96 -             row++;                                                                                │
│ 101 +     // CHECK NPC                                                                                  │
│ 102 +     for (int i=0; i<gp.npc[1].length; i++) {                                                      │
│ 103 +         if(gp.npc[gp.currentMap.index][i] != null && gp.npc[gp.currentMap.index][i] != entity) {  │
│ 104 +             int itCol = gp.npc[gp.currentMap.index][i].worldX / gp.tileSize;                      │
│ 105 +             int itRow = gp.npc[gp.currentMap.index][i].worldY / gp.tileSize;                      │
│ 106 +             node[itCol][itRow].solid = true;                                                      │
│ 107           }                                                                                         │
│ 108       }                                                                                             │
│ 109 +                                                                                                   │
│ 110 +     // CHECK MONSTER                                                                              │
│ 111 +     for (int i=0; i<gp.monster[1].length; i++) {                                                  │
│ 112 +         if(gp.monster[gp.currentMap.index][i] != null && gp.monster[gp.currentMap.index][i] !=    │
│     entity) {                                                                                           │
│ 113 +             int itCol = gp.monster[gp.currentMap.index][i].worldX / gp.tileSize;                  │
│ 114 +             int itRow = gp.monster[gp.currentMap.index][i].worldY / gp.tileSize;                  │
│ 115 +             node[itCol][itRow].solid = true;                                                      │
│ 116           }                                                                                         │
│ 117 +     }                                                                                             │
│ 118                                                                                                     │
│ 119 +     // CHECK PLAYER                                                                               │
│ 120 +     if (gp.player != entity) {                                                                    │
│ 121 +         int itCol = gp.player.worldX / gp.tileSize;                                               │
│ 122 +         int itRow = gp.player.worldY / gp.tileSize;                                               │
│ 123 +         node[itCol][itRow].solid = true;                                                          │
│ 124 +     }                                                                                             │
│ 125 + }                                                                                                 │
│ 126 +                                                                                                   │
│ 127   public void getCost(Node node) {                                                                  │
│ 128       // G cost                                                                                     │
│ 129       int xDistance = Math.abs(node.col - startNode.col);                                           │
╰─────────────────────────────────────────────────────────────────────────────────────────────────────────╯


---

# Video Summary

### 🗺️ 路徑尋找演算法
- 本視頻將展示如何實現**路徑尋找演算法**，使角色能在避免固體瓦片的情況下移動。
- 使用的演算法為**A-Star搜索**（A*），視頻中不會詳細解釋該算法，建議觀看之前的基礎教學視頻。

### 👴 NPC行為範例
- 提供了三個範例： 
  - 角色（例如老爺爺）移動至地圖上的特定目標點。
  - 角色跟隨玩家角色移動。
  - 怪物追逐玩家角色。

### 🚧 結構設計
- 創建名為**Node**的類來定義路徑中的各個節點，包含父節點、成本、固體狀態等屬性。
- 創建**PathFinder**類來管理路徑尋找的邏輯，包括節點的重置和搜索。

### 🏃 NPC行動設計
- NPC在玩家觸發對話後開始向特定位置移動，根據目標位置設置路徑。
- NPC能夠追隨玩家並在玩家接近時觸發行為。

### 🧟 怪物行為設計
- 通過檢查玩家距離來決定怪物是否變得**攻擊性（Aggro）**，如在玩家攻擊或接近時開始追逐。
- 可設置隨機機率，使追逐行為不那麼機械化。

### 🎮 增進遊戲體驗
- 經由**A-Star路徑尋找演算法**的實施，NPC和怪物能夠有效地尋找路徑，增強了遊戲的互動性和挑戰性。
- 鼓勵開發者利用該技術以創造更有趣的遊戲機制。