import React, { useState } from 'react';
import GameCanvas from './game/GameCanvas';
import './App.css';

const App: React.FC = () => {
  const [showChangelog, setShowChangelog] = useState(false);

  return (
    <div className="app-container">
      <div className="game-wrapper">
        <GameCanvas />
      </div>

      {/* Connection status indicator */}
      <div className="status-bar">
        <span className="status-indicator">
          <span className="dot connected"></span>
          <span>Blue Boy Adventure</span>
        </span>
        <span
          className="version-label clickable"
          onClick={() => setShowChangelog(true)}
        >
          v1.4.1 / 20260226
        </span>
      </div>

      {showChangelog && (
        <div className="changelog-modal-overlay" onClick={() => setShowChangelog(false)}>
          <div className="changelog-modal" onClick={e => e.stopPropagation()}>
            <div className="changelog-header">
              <h3>Version History</h3>
              <button className="close-btn" onClick={() => setShowChangelog(false)}>×</button>
            </div>
            <div className="changelog-content">
              <div className="changelog-item">
                <span className="version">v1.4.1</span>
                <span className="date">2026-02-26</span>
                <ul>
                  <li>Optimized sprite loading for faster startup</li>
                  <li>Fixed empty inventory slot rendering</li>
                </ul>
              </div>
              <div className="changelog-item">
                <span className="version">v1.4.0</span>
                <span className="date">2026-02-26</span>
                <ul>
                  <li>Added Item & Inventory System</li>
                  <li>Implemented Map Transitions & Teleportation</li>
                  <li>Added Interactable Map Objects (Chests, Doors)</li>
                </ul>
              </div>
              <div className="changelog-item">
                <span className="version">v1.3.0</span>
                <span className="date">2026-02-25</span>
                <ul>
                  <li>Added NPC Dialogue System</li>
                  <li>Improved Player Combat mechanics</li>
                  <li>Added Player Status HUD</li>
                </ul>
              </div>
              <div className="changelog-item">
                <span className="version">v1.2.0</span>
                <span className="date">2026-02-24</span>
                <ul>
                  <li>Added Tile Collision limits</li>
                  <li>Added Monster Entities</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default App;
