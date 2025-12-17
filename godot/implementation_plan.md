# Godot Game Replication Plan

## Overview
This project aims to replicate a Java-based RPG game into Godot 4.5, utilizing Godot's native engine features (Nodes, Physics, Signals, TileLayers) to replace manual Java logic.

## Phase 1: Project Foundation & Assets
**Goal**: Establish the Godot project structure and configure core settings.
- [x] Create `godot` directory and move assets.
- [ ] Create `project.godot` configuration file.
    - Set Resolution: 1280x768 (Based on 16px tiles * 4 scale * 20 cols).
    - Set Rendering: Nearest Neighbor (Pixel Art).
    - Set Input Map: WASD/Arrows, Enter, Z.
- [ ] Organize directory structure (`scenes`, `scripts`, `assets`).
- [ ] Verify Asset Import settings (Texture2D, Filter: Nearest).

## Phase 2: Map System
**Goal**: Recreate the TileMap utilizing the existing text-based map data.
- [ ] Create `MapManager.gd` to parse `worldmap.txt` and `tiledata.txt`.
- [ ] Create a `TileSet` resource in Godot from `assets/tilesV3`.
- [ ] Implement `MapLoader` scene to dynamically build `TileMapLayer` fromparsed data.
- [ ] Configure Collision Layers (World, Water, Walls).

## Phase 3: Player Controller
**Goal**: Implement the Player movement and animation.
- [ ] Create `Player.tscn` using `CharacterBody2D`.
- [ ] Implement movement logic with `move_and_slide()` (replacing manual coord updates).
- [ ] Setup `AnimatedSprite2D` for walking/idle/attack animations.
- [ ] Add `Camera2D` with smoothing and map boundaries.

## Phase 4: Interaction & Entities
**Goal**: Implement NPC interactions and base entity logic.
- [ ] Create `Entity.tscn` base scene (Area2D interaction, CharacterBody2D physics).
- [ ] Implement `RayCast2D` for "Facing" interaction (Dialogue).
- [ ] Port `NPC` logic and Dialogue UI.
- [ ] Implement "Object" entities (Keys, Doors).

## Phase 5: Combat & UI
**Goal**: Implement combat mechanics and the Heads-Up Display.
- [ ] Implement `Hitbox` and `Hurtbox` Areas for combat.
- [ ] Create `UI.tscn` (CanvasLayer) for Hearts, Mana, Inventory.
- [ ] Implement Monster AI (basic chase/wander).
- [ ] Port Sound/Music system using `AudioStreamPlayer`.

## Phase 6: Polish & Optimization
**Goal**: Final tuning and export.
- [ ] Debug collisions and Z-ordering (Y-Sort).
- [ ] Verify all game states (Title, Play, Pause, Dialogue).
- [ ] Finalize Lighting/Day-Night cycle (if applicable in original).
