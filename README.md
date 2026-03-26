# 🐕 狗狗健康管理系統
### Backend
- **Framework**: Spring Boot 3.2.0
- **Database**: MongoDB (Free Atlas or Local)
- **Language**: Java 17
- **Functional**: Vavr 0.10.4
- **Line SDK**: Line Bot SDK 4.3.0
- **Resilience**: Resilience4j
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

```
dog-health-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/doghealth/
│   │   │   │   ├── config/         
│   │   │   │   ├── controller/     # REST Controllers
│   │   │   │   ├── dto/            # Data Transfer Objects
│   │   │   │   ├── exception/      
│   │   │   │   ├── model/          # Domain Models
│   │   │   │   ├── repository/     # MongoDB Repositories
│   │   │   │   └── service/        # Business Logic
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/                   
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/             # React Components
│   │   ├── hooks/                  # Custom Hooks
│   │   ├── services/               # API Services
│   │   ├── store/                  
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


