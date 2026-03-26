# DOGG健康管理系統 

## 📊 Data flow

```
Line User → Webhook → Spring Boot Controller
                            ↓
                      Service Layer
                    (Functional Logic)
                            ↓
                    MongoDB Repository
                            ↓
                    Reactive MongoDB
                            ↓
                    統計資料 → React Frontend
```

## 🔧 Tech Stack
phase II LLM..

### Backend
- Spring Boot 3.2.0
- Java 17
- Vavr 0.10.4 
- MongoDB Reactive Driver
- Line Bot SDK 4.3.0
- Resilience4j
- JUnit 5 + Mockito

### Frontend
- React 18
- Vite
- Zustand 
- React Query
- Recharts
- Axios

### Infrastructure
- Docker & Docker Compose
- MongoDB 7.0
- Nginx


## 📝 API 

### 犬種資訊
- `GET /api/breeds` - 所有犬種
- `GET /api/breeds/{name}` - 特定犬種
- `GET /api/breeds/search?query=黃金` - 搜尋
- `POST /api/breeds` - 新增犬種

### 統計
- `GET /api/statistics/breeds` - 犬種統計
- `GET /api/statistics/health-concerns/{breed}` - 健康問題

### Line Webhook
- `POST /callback` - Line webhook
- `POST /callback/events` - 處理事件


## 📦 專案結構

```
dog-health-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/doghealth/
│   │   │   │   ├── controller/      # REST Controllers
│   │   │   │   ├── service/         # Business Logic
│   │   │   │   ├── repository/      # MongoDB Repositories
│   │   │   │   ├── model/           # Domain Models 
│   │   │   │   ├── exception/       # Error Handling
│   │   │   │   └── dto/             # Data Transfer Objects
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/                    # TDD Tests
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/              # React Components
│   │   ├── hooks/                   # Custom Hooks
│   │   ├── services/                # API Services
│   │   ├── store/                   # Zustand Store
│   │   └── App.jsx
│   ├── Dockerfile
│   └── package.json
├── docker/
│   └── init-mongo.js               # MongoDB 初始化
├── docker-compose.yml
├── .env.example
└── README.md
```

