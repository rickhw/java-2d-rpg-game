
## Release Notes

### v2.14.x (20251213_6): https://www.youtube.com/watch?v=m5suX8MnUfU

1. 推磚塊 解謎小關卡的設計
    - 計算 BigRock 和 MetalPlate 的交互
    - 計算交互後，DoorIron 是否打開
    - BigRock 卡住了，切換場景可以重來
2. Boss: Skeleton Lord (骷髏領主)
    - Rage State: (憤怒階段): 血量少一半的時候，會提升攻擊力和速度
3. New Entity:
    - Pickaxe (十字鎬/鋤頭, Weapon)
    - DestructibleWall (可以敲打的牆壁): 粒子效果 (particles), 隨機掉落一些物件 (像是打怪那樣)
    - MetalPlate (Iteractive Tile): 用來跟 BigRock 互動的機關
    - NPC: BigRock: Player 可以推動到 MetalPlate 上，用來解謎的
    - Iron Door (金屬門): 不能透過鑰匙打開的門，需要透過特定的行為才能打開
    - Bat (蝙蝠): 移動速度快, 靈活的怪物
4. 改善：重構, 新增 GodMode (無敵 + 開燈)

### v2.13.x (20251211_4): https://www.youtube.com/watch?v=w_LL6W3BYFc

- Advacned Dialogues: Multiple Lines, Letter by Letter Effect
- Add New Monster: Red Slime
- Add Dungeon (地下城): B1, B2
- Day and Night Mechanism: Outside, Indoor, Dungeon


### v2.12.x (20251209_2): https://www.youtube.com/watch?v=lmCcBFZLBqQ

- Map Screen and Minimap
    - 按下 M 切換到世界地圖，同時標記 Player 座標
    - 按下 V 持續顯示縮圖，同時標記 Player 座標
- 新增 Monster - Orc (獸人)
    - 具備揮動武器攻擊的特性
    - 攻擊動作依照屬性，揮動武器速度有差異
- Player 依照武器特性，揮動武器的速度有差異
    - 劍：比較快；斧頭：比較慢
- 新增防護 (Guard): 怪物攻擊玩家時，透過防護，玩家傷害降低 1/3
- 新增格擋 (Parry): 怪物攻擊玩家時，可透過格擋，把攻擊反彈回去，玩家本身不會有任何受傷
    - 格擋成功，怪物會退後幾格 (KnockBack)
- Save and Load
    - Player Status, Inventory Status, Object Status

### v2.11.x (20251205_5): https://www.youtube.com/watch?v=g3Il4LmMGh0

- 新增日夜切換循環效果，分成 Day, Dusk (黃昏), Night, Dawn (黎明) 四個狀態循環
- 新增物件 Lantern (燈籠)，裝備後玩家會在固定範圍有照明效果 (Lighting Effect )
- 新增物件：Tent (帳篷)，使用的時候，遊戲狀態切換成 SLEEP
    - 玩家 life & mana 恢復。
    - 跑過場效果：漸進式變成 Night，然後恢復成 Day

### v2.10.x (20251204_4): https://www.youtube.com/watch?v=le5aBmtu5eE

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

### v2.9.x (20251203_3): https://www.youtube.com/watch?v=KWlkeeyyT28

- Pathfinding: NPC follows player, aggro monsters


### v2.8.x (20251202_2): https://www.youtube.com/watch?v=GMPD_8nK_8A

- Transition between maps.
    - Transition effect
- Trade System: Buy and Sell

### v2.7.x (20251201_1): https://www.youtube.com/watch?v=R6vzTUGXtBA

- Full Screen Mode
- Options Menu and Saving Config
- Game Over State

### v2.6.x (20251128_5): https://www.youtube.com/watch?v=i9osbYkOLnw

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

### v2.5.x (20251127_4): https://www.youtube.com/watch?v=N0-kIh-zbuM

- Add projectlies (拋射物), the example is fireball.
- Monster Projectiles and Mana

### v2.4.x (20251125_2): https://www.youtube.com/watch?v=JOQ75xBeIJk

- Scrolling Message
- Leveling Up
- Inventory
    - Inventory size = 4 * 5
    - Select the item and show the description.
- Equipment and Use Items: 
    - New Items: Axe, Blue Shield, Potion
    - Set the equip: sword or shield
    - Use the Potion to recovery life.

### v2.3.0 (20251124_1): https://www.youtube.com/watch?v=F2ePhZjxBxc

- Monster: Green Slime
    - Collision Detection of Player/Monster/NPC/Tiles
- Hit Detection
- Monster Health Bar & Death Animation
- Character Status
- Event: Advanced Mechanics
- Rendering Optimization

### v2.2.0 (20251121_5): https://www.youtube.com/watch?v=JREH7K-hf68

- Custom Font, set the dialog text using pixel font by https://00ff.booth.pm/items/2958237
- Title Screen and Scense
- Player Life
- Event for Damage Pit, Healing Pool, Teleport Tile

### v2.1.0

- Pause Screen, House Keeping
- Add NPC_OldMan, including collision detection with Tiles and Player.

### v1.0.0 (20251115_6): https://www.youtube.com/watch?v=i6CVVRV-nkI

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