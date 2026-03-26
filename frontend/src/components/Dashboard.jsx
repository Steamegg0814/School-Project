import React, { useState } from 'react';
import { useDogData, useHealthConcerns } from '../hooks/useDogData';
import BreedChart from './BreedChart';
import useStore from '../store/useStore';
import './Dashboard.css';

const Dashboard = () => {
  const { breedStatistics, loading, error, refetch } = useDogData();
  const { getTotalDogs, getTopBreeds } = useStore();
  const [selectedBreed, setSelectedBreed] = useState(null);
  
  const { healthConcerns, loading: concernsLoading } = useHealthConcerns(selectedBreed);

  const handleBreedSelect = (breed) => {
    setSelectedBreed(breed === selectedBreed ? null : breed);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>載入中...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <h3>⚠️ 載入失敗</h3>
        <p>{error.message || '無法載入資料'}</p>
        <button onClick={refetch} className="retry-button">
          重試
        </button>
      </div>
    );
  }

  const totalDogs = getTotalDogs();
  const topBreeds = getTopBreeds(5);

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>🐕 犬隻健康管理系統</h1>
        <p>統計儀表板</p>
      </header>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">📊</div>
          <div className="stat-content">
            <h3>總犬隻數</h3>
            <p className="stat-value">{totalDogs}</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🏆</div>
          <div className="stat-content">
            <h3>犬種數量</h3>
            <p className="stat-value">
              {breedStatistics ? Object.keys(breedStatistics).length : 0}
            </p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">⭐</div>
          <div className="stat-content">
            <h3>最受歡迎犬種</h3>
            <p className="stat-value">
              {topBreeds.length > 0 ? topBreeds[0].breed : 'N/A'}
            </p>
          </div>
        </div>
      </div>

      <div className="chart-section">
        <div className="section-header">
          <h2>犬種分布統計</h2>
          <button onClick={refetch} className="refresh-button">
            🔄 重新整理
          </button>
        </div>
        <BreedChart data={breedStatistics} />
      </div>

      <div className="breeds-section">
        <h2>前五名犬種</h2>
        <div className="breed-list">
          {topBreeds.map(({ breed, count }, index) => (
            <div
              key={breed}
              className={`breed-item ${selectedBreed === breed ? 'selected' : ''}`}
              onClick={() => handleBreedSelect(breed)}
            >
              <div className="breed-rank">#{index + 1}</div>
              <div className="breed-info">
                <h3>{breed}</h3>
                <p>{count} 隻</p>
              </div>
              <div className="breed-arrow">
                {selectedBreed === breed ? '▼' : '▶'}
              </div>
            </div>
          ))}
        </div>
      </div>

      {selectedBreed && (
        <div className="health-concerns-section">
          <h2>{selectedBreed} - 健康注意事項</h2>
          {concernsLoading ? (
            <div className="loading-small">載入中...</div>
          ) : healthConcerns && healthConcerns.length > 0 ? (
            <ul className="concerns-list">
              {healthConcerns.map((concern, index) => (
                <li key={index} className="concern-item">
                  <span className="concern-icon">⚠️</span>
                  {concern}
                </li>
              ))}
            </ul>
          ) : (
            <p className="no-data">目前沒有健康資訊</p>
          )}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
