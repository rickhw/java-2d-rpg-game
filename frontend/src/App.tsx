import React, { useState } from 'react';
import GameCanvas from './game/GameCanvas';
import type { GameFullState } from './types/game';
import './App.css';

const App: React.FC = () => {
  const [gameState, setGameState] = useState<GameFullState | null>(null);

  return (
    <div className="app-container">
      <div className="game-wrapper">
        <GameCanvas onStateUpdate={setGameState} />
      </div>

      {/* Connection status indicator */}
      <div className="status-bar">
        <span className="status-indicator">
          {gameState ? (
            <>
              <span className="dot connected"></span>
              Connected — {gameState.gameState}
            </>
          ) : (
            <>
              <span className="dot disconnected"></span>
              Connecting...
            </>
          )}
        </span>
        <span className="version-label">Blue Boy Adventure v0.1.0</span>
      </div>
    </div>
  );
};

export default App;
