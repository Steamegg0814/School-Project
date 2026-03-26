# 🐕 犬隻健康管理系統

一個結合 Line ChatBot、Spring Boot、React 和 MongoDB 的犬隻健康管理平台。

## 🎯 功能特色

- ✅ Line ChatBot 整合 - 透過 Line 聊天機器人互動
- ✅ 犬種辨識 - 自動辨識犬種並提供相關資訊
- ✅ RER 計算 - 計算靜息能量需求（Resting Energy Requirement)
- ✅ DER 計算 - 根據活動量計算每日能量需求
- ✅ 健康建議 - 使用 Claude AI 提供個人化建議
- ✅ 統計儀表板 - React 網頁顯示犬種統計
- ✅ 完整錯誤處理 - Circuit Breaker、Retry、Error Boundaries
- ✅ Functional Programming - 使用 Vavr 和函數式編程概念
- ✅ TDD 開發 - 完整的單元測試

## 🏗️ 技術架構

### Backend
- **Framework**: Spring Boot 3.2.0 (WebFlux - Reactive)
- **Database**: MongoDB (Free Atlas or Local)
- **Language**: Java 17
- **Functional**: Vavr 0.10.4
- **Line SDK**: Line Bot SDK 7.4.0
- **Resilience**: Resilience4j (Circuit Breaker)
- **Testing**: JUnit 5, Mockito

### Frontend
- **Framework**: React 18
- **Build Tool**: Vite
- **State Management**: Zustand
- **Data Fetching**: React Query
- **Charts**: Recharts
- **HTTP Client**: Axios
- **Testing**: Vitest, React Testing Library

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Web Server**: Nginx (for React)
- **Database**: MongoDB 7.0

## 📦 專案結構

```
dog-health-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/doghealth/
│   │   │   │   ├── config/         # 配置類
│   │   │   │   ├── controller/     # REST Controllers
│   │   │   │   ├── dto/            # Data Transfer Objects
│   │   │   │   ├── exception/      # 自定義異常
│   │   │   │   ├── model/          # Domain Models
│   │   │   │   ├── repository/     # MongoDB Repositories
│   │   │   │   └── service/        # Business Logic
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/                   # 單元測試
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/             # React Components
│   │   ├── hooks/                  # Custom Hooks
│   │   ├── services/               # API Services
│   │   ├── store/                  # Zustand Store
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml
├── .env.example
└── README.md
```

## 🚀 快速開始

### 前置需求

- Docker & Docker Compose
- Java 17 (如果本地開發)
- Node.js 18+ (如果本地開發)
- MongoDB Atlas 帳號（免費方案）或 Local MongoDB
- Line Developer 帳號
- Anthropic API Key

### 1. 複製專案

```bash
git clone <repository-url>
cd dog-health-system
```

### 2. 設定環境變數

```bash
cp .env.example .env
```

編輯 `.env` 檔案，填入以下資訊：

```env
LINE_CHANNEL_TOKEN=your_line_channel_token
LINE_CHANNEL_SECRET=your_line_channel_secret
ANTHROPIC_API_KEY=your_anthropic_api_key
```

### 3. 使用 Docker Compose 啟動

```bash
docker-compose up -d
```

服務將在以下端口啟動：
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- MongoDB: localhost:27017

**重要：犬種資料自動初始化**

應用程式啟動時會自動初始化 10 種常見犬種資料到 MongoDB：
- Golden Retriever (黃金獵犬)
- Labrador Retriever (拉布拉多)
- Poodle (貴賓犬)
- Chihuahua (吉娃娃)
- German Shepherd (德國牧羊犬)
- Bulldog (鬥牛犬)
- Beagle (米格魯)
- Shiba Inu (柴犬)
- Pembroke Welsh Corgi (柯基)
- Pomeranian (博美犬)

如果需要手動初始化或添加更多犬種，可以：

**方法 1：使用 API**
```bash
curl -X POST http://localhost:8080/api/breeds \
  -H "Content-Type: application/json" \
  -d '{
    "breed": "Husky",
    "aliases": ["哈士奇", "西伯利亞雪橇犬"],
    "commonDiseases": ["Hip Dysplasia", "Eye Problems"],
    "temperament": "Outgoing, Alert, Friendly",
    "averageWeight": 25.0,
    "averageLifespan": 13,
    "exerciseNeeds": ["Very high energy", "Running", "Outdoor activities"],
    "dietaryRecommendations": ["High protein", "Active dog formula"]
  }'
```

**方法 2：使用 MongoDB Shell**
```bash
docker exec -it dog-health-mongodb mongosh doghealth /docker-entrypoint-initdb.d/init-mongo.js
```

### 4. 設定 Line Webhook

在 Line Developer Console 中設定 Webhook URL：
```
https://your-domain.com/callback
```

### 5. 驗證安裝

**檢查犬種資料：**
```bash
curl http://localhost:8080/api/breeds
```

**檢查特定犬種：**
```bash
curl http://localhost:8080/api/breeds/Golden%20Retriever
```

**查看統計：**
打開瀏覽器訪問 http://localhost:3000

## 🧪 本地開發

### Backend 開發

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend 開發

```bash
cd frontend
npm install
npm run dev
```

### 執行測試

Backend:
```bash
cd backend
mvn test
```

Frontend:
```bash
cd frontend
npm test
```

## 📊 API 端點

### 犬種資訊 API

```bash
# 取得所有犬種
GET /api/breeds

# 取得特定犬種資訊
GET /api/breeds/{breedName}

# 搜尋犬種（支援中英文）
GET /api/breeds/search?query=黃金

# 新增犬種資訊
POST /api/breeds
Content-Type: application/json
{
  "breed": "Husky",
  "aliases": ["哈士奇"],
  "commonDiseases": ["Hip Dysplasia"],
  "temperament": "Friendly",
  "averageWeight": 25.0,
  "averageLifespan": 13,
  "exerciseNeeds": ["High"],
  "dietaryRecommendations": ["High protein"]
}
```

### 統計 API

```bash
# 取得犬種統計
GET /api/statistics/breeds

# 取得特定犬種健康問題
GET /api/statistics/health-concerns/{breed}
```

## 📱 Line Bot 使用指南

### 可用指令

1. **註冊狗狗**
   ```
   註冊 黃金獵犬 30 5
   ```
   格式：`註冊 [犬種] [體重kg] [年齡]`

2. **查詢我的狗**
   ```
   我的狗
   ```

3. **查詢犬種資訊**
   ```
   犬種 黃金獵犬
   ```

4. **獲取健康建議**
   ```
   建議 <狗狗ID>
   ```

## 🔧 配置說明

### MongoDB Atlas (免費方案)

1. 註冊 MongoDB Atlas: https://www.mongodb.com/cloud/atlas
2. 創建免費 M0 Cluster
3. 創建數據庫用戶
4. 獲取連接字串
5. 更新 `application.yml` 或設定環境變數

### Resilience4j Circuit Breaker

LLM 服務配置了 Circuit Breaker：
- 滑動窗口：10 次請求
- 失敗率閾值：50%
- 開啟狀態等待時間：30 秒
- 半開啟狀態允許請求數：3

## 🎨 功能示例

### MongoDB 資料架構

**breed_info Collection:**
```json
{
  "_id": "ObjectId",
  "breed": "Golden Retriever",
  "aliases": ["黃金獵犬", "金毛尋回犬"],
  "commonDiseases": ["Hip Dysplasia", "Cancer"],
  "temperament": "Friendly, Intelligent",
  "averageWeight": 30.0,
  "averageLifespan": 12,
  "exerciseNeeds": ["High energy", "Swimming"],
  "dietaryRecommendations": ["High-quality protein"],
  "description": "...",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

**dogs Collection:**
```json
{
  "_id": "ObjectId",
  "userId": "U1234567890",
  "breed": "Golden Retriever",
  "weight": 30.0,
  "age": 5,
  "tags": ["vaccinated", "healthy"],
  "healthConcerns": ["hip dysplasia"],
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

**health_records Collection:**
```json
{
  "_id": "ObjectId",
  "dogId": "ObjectId",
  "userId": "U1234567890",
  "calculatedRER": 1091.3,
  "calculatedDER": 1746.0,
  "recommendation": "...",
  "recordedAt": "2024-01-01T00:00:00Z"
}
```

### RER 計算

```java
Dog dog = Dog.create("user123", "Golden Retriever", 30.0, 5).get();
Double rer = dog.calculateRER().get();
// RER = 70 * (weight^0.75)
```

### DER 計算

```java
Double der = dog.calculateDER(1.6).get();
// DER = RER * activity_factor
```

### 從 MongoDB 讀取犬種資訊

```java
// 使用犬種名稱搜尋
Mono<BreedInfo> info = breedInfoRepository.findByBreedIgnoreCase("Golden Retriever");

// 使用別名搜尋（支援中文）
Mono<BreedInfo> info = breedInfoRepository.findByBreedOrAliasContaining("黃金獵犬");

// 取得所有犬種
Flux<BreedInfo> allBreeds = breedInfoRepository.findAllByOrderByBreedAsc();
```

### 犬種資訊

應用程式會自動初始化以下犬種（可擴展）：
- 黃金獵犬 (Golden Retriever)
- 拉布拉多 (Labrador)
- 貴賓犬 (Poodle)
- 吉娃娃 (Chihuahua)

## 🛡️ 錯誤處理

### Backend
- 全局異常處理器 (`GlobalExceptionHandler`)
- 自定義異常類型 (`DogHealthException`)
- Try-Catch 包裝所有外部調用
- Circuit Breaker for LLM 服務

### Frontend
- Error Boundaries
- React Query retry 機制
- Axios interceptors
- 友善的錯誤訊息

## 📊 統計網頁功能

- 犬種分布圖表
- 總犬隻數統計
- 前五名熱門犬種
- 犬種健康注意事項
- 即時數據刷新

## 🔐 安全性

- 環境變數管理敏感資訊
- API Key 不寫入程式碼
- MongoDB 連接字串加密
- CORS 配置

## 📝 開發規範

### Backend
- Functional Programming 使用 Vavr
- Immutable Data Structures
- Pure Functions
- TDD 開發流程

### Frontend
- Functional Components
- Custom Hooks
- Immutable State Updates
- Component Testing

## 🤝 貢獻

歡迎提交 Pull Requests！

## 📄 授權

MIT License

## 📞 聯絡

如有問題請開 Issue。

---

**注意事項**:
- 請確保 API Keys 安全
- MongoDB Atlas 免費方案有 512MB 儲存限制
- Line Webhook 需要 HTTPS
- Claude API 需要付費帳號
