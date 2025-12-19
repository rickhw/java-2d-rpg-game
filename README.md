# 學習心得

紀錄從 RyiSnow 的 [Java 2D Game](https://www.youtube.com/playlist?list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq) 課程，手工完成 RPG Game 的歷程與心得，相關紀錄放在 [Game Learning and DevLog](https://www.youtube.com/playlist?list=PL63J1r2PBvogQ2Un01ytl2bQ1OIrlrnLO) 這個播放清單裡。


寫一個 RPG Game 是小時候的夢想。小時候家裡還沒有電腦，只有紅白機可以玩 [吞食天地](https://zh.wikipedia.org/zh-tw/%E5%90%9E%E9%A3%9F%E5%A4%A9%E5%9C%B0_(%E7%BA%A2%E7%99%BD%E6%9C%BA)) 的時候，就在想以後能不能自己寫類似的遊戲。後來因緣際會玩到了 [Final Fantasy 3 (FF3，台灣當時翻譯：太空戰士)](https://zh.wikipedia.org/zh-tw/%E6%9C%80%E7%BB%88%E5%B9%BB%E6%83%B3III)，對整個遊戲的玩法、音樂、畫面震撼到不行，尤其是音樂。永遠忘不了駕著飛空艇，從空浮大陸飛出去的音樂與畫面，那感覺深深烙印在腦海裡，而整個故事的安排與起伏，至今都難以忘懷。從那個時候 (應該是小五？)，我就拿起紙筆寫一些天馬行空的劇本，然後幻想有一天可以寫寫自己的遊戲。

後來國二的時候，因緣機會玩到超任上的 [Final Fantasy 5 (FF5)](https://zh.wikipedia.org/zh-tw/%E6%9C%80%E7%BB%88%E5%B9%BB%E6%83%B3V) ，又再一次的被音樂、故事震撼，國三畢業後，終於買到自己的超任，那時候超任上好像就只玩 FF5 了，其他遊戲玩得很少。

再往下就是第一次接觸電腦的時候了，學習了 QBasic，才知道這東西是可以寫出可以用的工具，或者遊戲。

之後唸書，到工作，一直都在寫商用應用程式、大型的 SaaS 系統，但「寫遊戲」一直都是在心裡的深處。


## 遊戲運作原理

為什麼純手工？沒考慮過 GameEngine？Unity、Unreal？

Unity 有研究過，但是太複雜，或者說花的時間太少，也抓不到重點。Unreal 更複雜，而且我沒有想做那麼「擬真」的遊戲。相對比較好上手的則是 Godot，所以有稍微研究過。

但最核心的，手工做遊戲的目的是：

1. 了解遊戲運作原理
2. 計算機科學的應用
3. 我本身就很喜歡研究科學與應用

`遊戲運作原理` 像是底下的東西：

1. GameLoop
2. TileMap (瓦片地圖), 座標系統
    - https://yorubox.eu/what-is-tile-tileset-or-tilemap-game-terms-explaination/
    - https://www.mapeditor.org/
3. Graphics2D 2D 繪圖
4. Collision 碰撞偵測
5. PathFinding 路徑規劃
6. Practicle 粒子效果

還有其他更多就不列了，但這些在 2D RPG 裡算是很基本的概念。


## 遊戲製作的四個面向

撇除營運面的，像是銷售、行銷 ... 等，這次的學習歷程，我很粗淺的分析遊戲製作 (開發) 可以分成以下四個部分：

1. 技術/遊戲性: GameEngine / Game Development
2. 故事/劇本
3. 美術/視覺
4. 音樂/氛圍

而這次學習的部分，主要是 1) 的部分。


## 原始碼

這次的學習都來自於 RyiSnow 的 [Java 2D Game](https://www.youtube.com/playlist?list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq) 課程，大部分的 Coding Style 都保留 RyiSnow 的寫法，我很少把個人的想法置入，保留存粹感。

程式主要開發環境是在 MacOS Tahoe，Java 環境則是 Java 17/21 都有。

```bash
❯ java -version
openjdk version "21.0.2" 2024-01-16
OpenJDK Runtime Environment GraalVM CE 21.0.2+13.1 (build 21.0.2+13-jvmci-23.1-b30)
OpenJDK 64-Bit Server VM GraalVM CE 21.0.2+13.1 (build 21.0.2+13-jvmci-23.1-b30, mixed mode, sharing)

❯ java -version
openjdk version "17.0.9" 2023-10-17
OpenJDK Runtime Environment GraalVM CE 17.0.9+9.1 (build 17.0.9+9-jvmci-23.0-b22)
OpenJDK 64-Bit Server VM GraalVM CE 17.0.9+9.1 (build 17.0.9+9-jvmci-23.0-b22, mixed mode, sharing)
```

### Branch

整個課程過程，在 Branch 有保留所有的紀錄，對應到 RyiSnow 的內容，學習上比較好查閱與理解。

其中有透過 gemini 幫忙重構 (branch: day57_refactor-by-gemini_ui-class, day57_refactor-by-antigravity)，但效果沒有預期得好。


### 用 AI 重寫: Java to Godot

整個課程完成後，我嘗試在 Antigravity (Google 的 AI IDE) 做類似逆向工程的事情，使用不同的 LLM，讓他們從理解現在的 Java 程式，然後在 Godo做完整的 Remake (復刻版)，不過目前為止都還沒有讓我感到很滿意的結果。有興趣可以看看底下的 Branch:

- day57_java-to-godot-by-antigravity-claude-opus-4.5-thinking
- day57_java-to-godot-by-antigravity-v2-claude-sonnet-45
- day57_refactor-by-antigravity

底下是 prmopt:

```bash
請了解現在的程式碼的架構與功能，了解之後，使用原本的素材，包含圖檔、音效、字型等資源 (放在 resources 目錄裡)，用 godot 復刻一模一樣功能的遊戲出來。

復刻的時候，要利用 godot v4.5 遊戲引擎的特性，避免使用原本自己圖形計算的方法，像是原本 Java 有很多計算座標、縮放比例 (Scale)、計算碰撞 ... 等邏輯，這我想像應該是使用 Godot 有現成的 "機制" 即可。

請建立一個 godot 目錄，將復刻的程式碼以及素材放在 godot 目錄中。

請分析完之後，展開工作階段計劃、以及每個階段的驗收流程，把這些文件放在 godot 目錄裡。

沒問題後我們就開始。
```

測試 AI 模型在以下 5 個關鍵維度 的指標能力：

1. 跨語言與跨範式的「語義翻譯」能力 (Semantic Translation)
2. 架構重構能力 (Architectural Refactoring)
3. API 映射與領域知識 (Domain Knowledge Mapping)
4. 知識庫的時效性與幻覺 (Knowledge Cutoff & Hallucination)
5. 長文本上下文關聯 (Long Context Reasoning)

> 驗證模型在軟體開發上，必須是是「工程師思維」而非單純的「翻譯機功能」。


---
# Release Notes

## v2.16.x

1. Bug fix
    - return to dungeon B2, boss become sleep states
    - return to title screen, the music keep playing
2. Ending Scene

## v2.15.x (20251215_1): https://www.youtube.com/watch?v=IsNZ4z4wEOo

1. Debug: 顯示 SolidArea, AttackArea 座標
2. 調整 Player Life Bar 的大小, 8 個換行
2. Boss Health Bar
3. 過場動畫 (Cutsense): Phase 0 - 4

## v2.14.x (20251213_6): https://www.youtube.com/watch?v=m5suX8MnUfU

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

## v2.13.x (20251211_4): https://www.youtube.com/watch?v=w_LL6W3BYFc

- Advacned Dialogues: Multiple Lines, Letter by Letter Effect
- Add New Monster: Red Slime
- Add Dungeon (地下城): B1, B2
- Day and Night Mechanism: Outside, Indoor, Dungeon


## v2.12.x (20251209_2): https://www.youtube.com/watch?v=lmCcBFZLBqQ

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

## v2.11.x (20251205_5): https://www.youtube.com/watch?v=g3Il4LmMGh0

- 新增日夜切換循環效果，分成 Day, Dusk (黃昏), Night, Dawn (黎明) 四個狀態循環
- 新增物件 Lantern (燈籠)，裝備後玩家會在固定範圍有照明效果 (Lighting Effect )
- 新增物件：Tent (帳篷)，使用的時候，遊戲狀態切換成 SLEEP
    - 玩家 life & mana 恢復。
    - 跑過場效果：漸進式變成 Night，然後恢復成 Day

## v2.10.x (20251204_4): https://www.youtube.com/watch?v=le5aBmtu5eE

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

## v2.9.x (20251203_3): https://www.youtube.com/watch?v=KWlkeeyyT28

- Pathfinding: NPC follows player, aggro monsters


## v2.8.x (20251202_2): https://www.youtube.com/watch?v=GMPD_8nK_8A

- Transition between maps.
    - Transition effect
- Trade System: Buy and Sell

## v2.7.x (20251201_1): https://www.youtube.com/watch?v=R6vzTUGXtBA

- Full Screen Mode
- Options Menu and Saving Config
- Game Over State

## v2.6.x (20251128_5): https://www.youtube.com/watch?v=i9osbYkOLnw

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

## v2.5.x (20251127_4): https://www.youtube.com/watch?v=N0-kIh-zbuM

- Add projectlies (拋射物), the example is fireball.
- Monster Projectiles and Mana

## v2.4.x (20251125_2): https://www.youtube.com/watch?v=JOQ75xBeIJk

- Scrolling Message
- Leveling Up
- Inventory
    - Inventory size = 4 * 5
    - Select the item and show the description.
- Equipment and Use Items: 
    - New Items: Axe, Blue Shield, Potion
    - Set the equip: sword or shield
    - Use the Potion to recovery life.

## v2.3.0 (20251124_1): https://www.youtube.com/watch?v=F2ePhZjxBxc

- Monster: Green Slime
    - Collision Detection of Player/Monster/NPC/Tiles
- Hit Detection
- Monster Health Bar & Death Animation
- Character Status
- Event: Advanced Mechanics
- Rendering Optimization

## v2.2.0 (20251121_5): https://www.youtube.com/watch?v=JREH7K-hf68

- Custom Font, set the dialog text using pixel font by https://00ff.booth.pm/items/2958237
- Title Screen and Scense
- Player Life
- Event for Damage Pit, Healing Pool, Teleport Tile

## v2.1.0

- Pause Screen, House Keeping
- Add NPC_OldMan, including collision detection with Tiles and Player.

## v1.0.0 (20251115_6): https://www.youtube.com/watch?v=i6CVVRV-nkI

- Prototype

