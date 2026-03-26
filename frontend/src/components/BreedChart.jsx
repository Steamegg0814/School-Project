import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const BreedChart = ({ data }) => {
  if (!data || Object.keys(data).length === 0) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: '#868e96' }}>
        目前沒有犬種統計資料
      </div>
    );
  }

  const chartData = Object.entries(data)
    .map(([breed, count]) => ({
      breed: breed.length > 15 ? breed.substring(0, 15) + '...' : breed,
      count,
      fullBreed: breed
    }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 10);

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <div style={{
          backgroundColor: 'white',
          padding: '10px',
          border: '1px solid #ccc',
          borderRadius: '4px',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          <p style={{ margin: 0, fontWeight: 'bold' }}>
            {payload[0].payload.fullBreed}
          </p>
          <p style={{ margin: '5px 0 0 0', color: '#4dabf7' }}>
            數量: {payload[0].value}
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <ResponsiveContainer width="100%" height={400}>
      <BarChart
        data={chartData}
        margin={{ top: 20, right: 30, left: 20, bottom: 80 }}
      >
        <CartesianGrid strokeDasharray="3 3" stroke="#e9ecef" />
        <XAxis
          dataKey="breed"
          angle={-45}
          textAnchor="end"
          height={100}
          tick={{ fill: '#495057', fontSize: 12 }}
        />
        <YAxis
          tick={{ fill: '#495057', fontSize: 12 }}
          label={{ value: '數量', angle: -90, position: 'insideLeft' }}
        />
        <Tooltip content={<CustomTooltip />} />
        <Legend wrapperStyle={{ paddingTop: '20px' }} />
        <Bar
          dataKey="count"
          fill="#4dabf7"
          name="犬隻數量"
          radius={[8, 8, 0, 0]}
        />
      </BarChart>
    </ResponsiveContainer>
  );
};

export default BreedChart;
