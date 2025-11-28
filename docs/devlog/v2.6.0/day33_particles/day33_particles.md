

https://www.youtube.com/watch?v=OF41XmRk2wo&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=37

# Particles

- New entity: Particle (粒子效果)
    - 在 DryTree 中設定 粒子的參數：size, maxLife, speed ... etc
    - Implement Gravity
    - disable the transparent the effect (Particles.draw())
- effect partiles effect to:
    - player cuts dry tree using axe
    - player attacks monster with projectiles (fileball)
    - monster attacks player with projectiles (rock)


---

# Video Summary

### 🎮 影片概述
- **目標**: 本影片介紹如何在遊戲中加入**粒子效果**，以增強視覺效果，特別是當玩家用斧頭攻擊樹木時。

### 🌳 樹木類別和粒子生成
- **樹木破壞**: 利用**陣列**來儲存粒子，創建一個名為**Particle**的類別，並使其**繼承**自主要的實體類別。
- **粒子參數**: 粒子類別包含生成器、顏色、大小、速度以及生命週期等參數，這些對粒子的行為有重要影響。

### 🏃‍♂️ 粒子的運動和更新
- **粒子運動**: 每次更新時，根據**速度**和**方向**（xd, yd）來更新粒子的世界坐標。
- **生命週期**: 粒子的生命會隨時間減少，當生命等於0時將停止顯示。

### ⚙️ 附加功能
- **重力效果**: 當粒子的生命小於一半時，讓其在每次更新中稍微向下移動，增強**真實感**。
- **透明效果**: 可選擇是否讓被攻擊的樹變得半透明，這個效果是可選的，根據個人喜好。

### 🔥 擴展粒子系統
- **其他物件**: 實現後，粒子效果也可以應用於其他物件，例如**攻擊的投射物**，讓遊戲效果更具的一致性和豐富性。

### 🛠️ 總結
- **簡單實作**: 影片展示了如何從頭開始實現一個**粒子系統**，適用於多種遊戲元素，提供基礎知識，並鼓勵玩家開發更具創意的效果。


