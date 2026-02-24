import React from 'react';
import GameCanvas from './game/GameCanvas';
import './App.css';

const App: React.FC = () => {
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
        <span className="version-label">v0.1.0</span>
      </div>
    </div>
  );
};

export default App;
