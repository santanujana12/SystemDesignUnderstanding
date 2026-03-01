# SPRING BOOT URL SHORTENER - SETUP GUIDE

## 📋 Quick Start (3 Steps)

### Step 1: Extract the ZIP
```bash
unzip springboot-urlshortener.zip
cd url-shortener
```

### Step 2: Build the project
```bash
mvn clean install
```

### Step 3: Run the application
```bash
mvn spring-boot:run
```

✅ Application starts on **http://localhost:3000**

---

## 🧪 Test It Works

### 1. Shorten a URL
```bash
curl -X POST http://localhost:3000/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://google.com"}'
```

**Response:**
```json
{"shortenedUrl":"http://localhost:3000/abc123"}
```

### 2. Click the shortened URL
Open in browser or:
```bash
curl -L http://localhost:3000/abc123
```

### 3. View analytics
```bash
curl http://localhost:3000/analytics/abc123
```

---

## 📖 API Documentation

Visit **http://localhost:3000/swagger-ui.html** for interactive API docs

---

## 🔧 Configuration

Edit `src/main/resources/application.properties`:

```properties
# Change port
server.port=8080

# Change base URL
app.base-url=https://yourdomain.com

# Change expiry
app.default-expiry-days=7

# Add CORS origin
app.cors.allowed-origins=http://localhost:5173,https://yourfrontend.com
```

---

## 🚀 Running in Different Modes

### Development mode (with debug logging)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production mode
```bash
mvn clean package
java -jar target/url-shortener-1.0.0.jar --spring.profiles.active=prod
```

---

## 🗄️ Database

### H2 (Default - embedded, like SQLite)
- Location: `./data/urlshortener.mv.db`
- Console: http://localhost:3000/h2-console
- JDBC URL: `jdbc:h2:file:./data/urlshortener`
- Username: `sa`
- Password: (empty)

### PostgreSQL (Production)

1. Install PostgreSQL
2. Create database: `createdb urlshortener`
3. Update `application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=your_user
spring.datasource.password=your_password
```
4. Add PostgreSQL dependency in `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

---

## 📊 Features Overview

| Feature | Status | Endpoint |
|---------|--------|----------|
| URL Shortening | ✅ | POST /shorten |
| Redirects | ✅ | GET /{code} |
| Analytics | ✅ | GET /analytics/{code} |
| Caching | ✅ | Automatic |
| Rate Limiting | ✅ | Automatic |
| Scheduled Cleanup | ✅ | Daily at midnight |
| CORS | ✅ | Pre-configured |
| Swagger Docs | ✅ | /swagger-ui.html |

---

## 🔄 Connecting to React Frontend

Your React frontend works with zero changes:

```javascript
// In your .env
VITE_API_URL=http://localhost:3000

// The existing React code will work!
```

Make sure CORS is configured in `application.properties`:
```properties
app.cors.allowed-origins=http://localhost:5173
```

---

## 🐛 Troubleshooting

**Port 3000 already in use?**
```bash
# Change port in application.properties
server.port=8080
```

**Maven not found?**
```bash
# Install Maven
# macOS: brew install maven
# Ubuntu: sudo apt install maven
# Windows: Download from maven.apache.org
```

**Java version error?**
```bash
# Check Java version (need 17+)
java -version

# Install Java 17
# macOS: brew install openjdk@17
# Ubuntu: sudo apt install openjdk-17-jdk
```

**H2 database locked?**
```bash
# Stop the application and delete the database
rm -rf data/
```

---

## 📦 Project Structure

```
url-shortener/
├── src/main/java/com/urlshortener/
│   ├── UrlShortenerApplication.java    # Entry point
│   ├── controller/                     # REST APIs
│   ├── service/                        # Business logic
│   ├── repository/                     # Database access
│   ├── entity/                         # Database models
│   ├── dto/                            # Request/Response objects
│   ├── config/                         # Configuration
│   └── exception/                      # Error handling
├── src/main/resources/
│   ├── application.properties          # Main config
│   ├── application-dev.properties      # Dev config
│   └── application-prod.properties     # Prod config
├── src/test/java/                      # Unit tests
├── pom.xml                             # Dependencies
└── README.md                           # Documentation
```

---

## 🎯 What's Next?

1. ✅ Run the application
2. ✅ Test with cURL or Swagger UI
3. ✅ Connect your React frontend
4. ✅ Deploy to production (see README.md)
5. ✅ Switch to PostgreSQL for production

---

## 📝 Key Differences from Node.js Version

| Feature | Node.js | Spring Boot |
|---------|---------|-------------|
| Runtime | JavaScript/Node | Java/JVM |
| Database | SQLite (better-sqlite3) | H2 (can switch to PostgreSQL) |
| Cache | node-cache | Caffeine |
| Rate Limiting | express-rate-limit | Bucket4j |
| ORM | Raw SQL | Spring Data JPA |
| Validation | Manual | Bean Validation |
| Async | setImmediate | @Async |
| Cleanup | node-cron | @Scheduled |

---

## ✨ Production-Ready Features

✅ Layered architecture (MVC)
✅ Dependency injection
✅ Transaction management
✅ Exception handling
✅ Input validation
✅ API documentation
✅ Unit tests
✅ Multiple environments (dev/prod)
✅ Caching
✅ Rate limiting
✅ Async processing
✅ Scheduled tasks
✅ CORS
✅ Logging

---

For detailed documentation, see **README.md**
