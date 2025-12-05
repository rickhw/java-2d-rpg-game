
## Release Notes


### v2.11.x

- Lighting Effect
- Lighting Items


### v2.10.x

- 實作格擋與擊退效果
    - 回擊 Projectiles, 例如 Player 回擊 Monster 丟出來的石頭
    - Knockback Effect (擊退): 當玩家攻擊怪物的時候，會依照武器的擊退值 (KnockBackPower) 計算怪物瞬間退後的效果，增加遊戲的打擊感。
- 實作物件之間的互動
    - 使用鑰匙開門
    - 開啟寶箱：物件裡有物件 (Loot)
- Stackable: Player 口袋的物件，可以用計數方式存放，避免口袋很快就滿了。
    - 撿起物件、開啟寶箱的時候，計數增加
    - 使用物件 (使用恢復劑 / 使用鑰匙)，計數減少
    - 在商店買入同樣物品，計數增加
    - 在商店賣出同樣物品，計數減少


### v2.9.x

- Pathfinding: NPC follows player, aggro monsters


### v2.8.x

- Transition between maps.
    - Transition effect
- Trade System: Buy and Sell

### v2.7.x

- Full Screen Mode
- Options Menu and Saving Config
- Game Over State

### v2.6.x

- Monster Item Drops
    - Coin, Heart, Mana
- Destructible Tiles (Interactive Tiles): Player can destroy the tree.
    - only for Axe
    - Collision detection for NPC and Monster
- Particles (粒子效果)
    - Gravity
    - Particle Effect to: 
        - Play cuts the dry tree
        - Play attacks monster with projectiles (fireball)
        - monster attacks player with projectiles (rock)

### v2.5.x

- Add projectlies (拋射物), the example is fireball.
- Monster Projectiles and Mana

### v2.4.x

- Scrolling Message
- Leveling Up
- Inventory
    - Inventory size = 4 * 5
    - Select the item and show the description.
- Equipment and Use Items: 
    - New Items: Axe, Blue Shield, Potion
    - Set the equip: sword or shield
    - Use the Potion to recovery life.

### v2.3.0

- Monster: Green Slime
    - Collision Detection of Player/Monster/NPC/Tiles
- Hit Detection
- Monster Health Bar & Death Animation
- Character Status
- Event: Advanced Mechanics
- Rendering Optimization

### v2.2.0

- Custom Font, set the dialog text using pixel font by https://00ff.booth.pm/items/2958237
- Title Screen and Scense
- Player Life
- Event for Damage Pit, Healing Pool, Teleport Tile

### v2.1.0

- Pause Screen, House Keeping
- Add NPC_OldMan, including collision detection with Tiles and Player.

### v1.0.0

- Prototype



---
## Youtube Template

Title: `[GameDevLog][v2.2.0]Custom Font, Title Screen, Player Life, Event (20251121)`

Content: 
```bash
Game Learning & DevLog - 2D Adventure RPG
#第九藝術

純手工的 遊戲開發 & 練習紀錄
Version: v2.7.0 20251201

## DevLog

- ...
- ...
- ...

## Reference

- All Playerlist: https://www.youtube.com/playlist?list=PL63J1r2PBvogQ2Un01ytl2bQ1OIrlrnLO
- Learning from RyiSnow https://www.youtube.com/playlist?list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq
```