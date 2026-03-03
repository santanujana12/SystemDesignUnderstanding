# SPRING BOOT 4.0.3 + JAVA 21 - SETUP GUIDE

## 🎯 Quick Start (3 Steps)

### Prerequisites Check

**1. Verify Java 21 is installed:**
```bash
java -version
```

Should show: `openjdk version "21"` or `"21.0.x"`

**If not installed:**

```bash
# macOS (Homebrew)
brew install openjdk@21
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk \
     /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# Windows
# Download from: https://adoptium.net/temurin/releases/?version=21
# Install and set JAVA_HOME
```

**2. Verify Maven is installed:**
```bash
mvn -version
```

Should show Maven 3.6+ with Java 21

**If not installed:**
```bash
# macOS
brew install maven

# Ubuntu/Debian
sudo apt install maven

# Windows
# Download from: https://maven.apache.org/download.cgi
```

---

## 🚀 Installation Steps

### Step 1: Extract the ZIP
```bash
unzip springboot-urlshortener.zip
cd url-shortener
```

### Step 2: Build the Project
```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile Java 21 code
- Run tests
- Create executable JAR

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 45 s
```

### Step 3: Run the Application
```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/url-shortener-1.0.0.jar
```

**You should see:**
```
  _   _ ____  _       ____  _                _                       
 | | | |  _ \| |     / ___|| |__   ___  _ __| |_ ___ _ __   ___ _ __ 
 | | | | |_) | |     \___ \| '_ \ / _ \| '__| __/ _ \ '_ \ / _ \ '__|
 | |_| |  _ <| |___   ___) | | | | (_) | |  | ||  __/ | | |  __/ |   
  \___/|_| \_\_____| |____/|_| |_|\___/|_|   \__\___|_| |_|\___|_|   
                                                                       
  :: Spring Boot 4.0.3 ::          :: Java 21 ::          :: v1.0.0 ::

Started UrlShortenerApplication in 2.5 seconds
```

✅ **Application is running on http://localhost:3000**

---

## 🧪 Test It Works

### Test 1: Health Check
```bash
curl http://localhost:3000/actuator/health
```

**Expected:**
```json
{"status":"UP"}
```

### Test 2: Shorten a URL
```bash
curl -X POST http://localhost:3000/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://google.com"}'
```

**Expected:**
```json
{"shortenedUrl":"http://localhost:3000/abc123"}
```

### Test 3: Click the shortened URL
```bash
curl -L http://localhost:3000/abc123
```

Should redirect to Google.

### Test 4: View Analytics
```bash
curl http://localhost:3000/analytics/abc123
```

**Expected:**
```json
{
  "url": "https://google.com",
  "created_at": "2026-02-22T...",
  "total_clicks": 1,
  "clicks_by_day": [...],
  "top_referrers": [...]
}
```

---

## 📖 Explore the API

### Swagger UI (Interactive Docs)
Open in browser: **http://localhost:3000/swagger-ui.html**

Here you can:
- See all endpoints
- Test APIs directly
- View request/response schemas
- Download OpenAPI spec

### H2 Database Console
Open in browser: **http://localhost:3000/h2-console**

Credentials:
- JDBC URL: `jdbc:h2:file:./data/urlshortener`
- Username: `sa`
- Password: (leave empty)

View tables:
- `URLS` - Shortened URLs
- `ANALYTICS` - Click tracking data

---

## ⚙️ Configuration

### Change Port
Edit `src/main/resources/application.properties`:
```properties
server.port=8080
```

### Change Base URL (for production)
```properties
app.base-url=https://yourdomain.com
```

### Change Expiry
```properties
app.default-expiry-days=7
```

### Add CORS Origins
```properties
app.cors.allowed-origins=http://localhost:5173,https://yourfrontend.com
```

### Adjust Rate Limits
```properties
# Shorten endpoint: 10 requests per 15 minutes
app.rate-limit.shorten.capacity=10
app.rate-limit.shorten.refill-duration=PT15M

# Redirect endpoint: 30 requests per minute
app.rate-limit.redirect.capacity=30
app.rate-limit.redirect.refill-duration=PT1M
```

---

## 🚀 Running in Different Environments

### Development (with debug logs)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Features:
- Debug logging enabled
- SQL queries visible
- H2 console enabled
- Higher rate limits

### Production
```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/url-shortener-1.0.0.jar --spring.profiles.active=prod
```

Features:
- Info logging only
- H2 console disabled
- Production rate limits
- Optimized performance

---

## 🗄️ Database Options

### H2 (Default - Embedded)
- **Location:** `./data/urlshortener.mv.db`
- **Type:** File-based (like SQLite)
- **Best for:** Development, testing, small deployments
- **Max:** ~100GB database size

### PostgreSQL (Recommended for Production)

**1. Install PostgreSQL:**
```bash
# macOS
brew install postgresql@16
brew services start postgresql@16

# Ubuntu
sudo apt install postgresql-16
sudo service postgresql start

# Windows
# Download from: https://www.postgresql.org/download/windows/
```

**2. Create database:**
```bash
createdb urlshortener
```

**3. Update `pom.xml`:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

**4. Update `application-prod.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**5. Run:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 🐳 Docker Deployment

### Create Dockerfile
Already included in the project root.

### Build and Run
```bash
# Build JAR
mvn clean package -DskipTests

# Build Docker image
docker build -t url-shortener:latest .

# Run container
docker run -p 3000:3000 url-shortener:latest
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    image: url-shortener:latest
    ports:
      - "3000:3000"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - APP_BASE_URL=https://yourdomain.com
    volumes:
      - ./data:/app/data
```

Run:
```bash
docker-compose up -d
```

---

## 🔄 Connecting to React Frontend

Your React frontend works **without any changes**!

**In React `.env`:**
```
VITE_API_URL=http://localhost:3000
```

**In Spring Boot `application.properties`:**
```properties
app.cors.allowed-origins=http://localhost:5173
```

Start both:
```bash
# Terminal 1: Backend
mvn spring-boot:run

# Terminal 2: Frontend
npm run dev
```

---

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:3000/actuator/health
```

### Metrics
```bash
# All metrics
curl http://localhost:3000/actuator/metrics

# JVM memory
curl http://localhost:3000/actuator/metrics/jvm.memory.used

# HTTP requests
curl http://localhost:3000/actuator/metrics/http.server.requests
```

### Prometheus (for Grafana)
```bash
curl http://localhost:3000/actuator/prometheus
```

---

## 🐛 Troubleshooting

### Error: "Java version not compatible"
**Solution:**
```bash
# Check Java version
java -version

# Must be Java 21 or higher
# If not, install Java 21 (see Prerequisites section)
```

### Error: "Port 3000 already in use"
**Solution:**
```bash
# Option 1: Kill process using port 3000
lsof -ti:3000 | xargs kill -9

# Option 2: Change port in application.properties
server.port=8080
```

### Error: "Could not resolve dependencies"
**Solution:**
```bash
# Clear Maven cache and rebuild
rm -rf ~/.m2/repository
mvn clean install -U
```

### Error: "H2 database is locked"
**Solution:**
```bash
# Stop application and delete database
rm -rf data/
# Restart application
```

### Error: "Tests fail during build"
**Solution:**
```bash
# Skip tests during build
mvn clean install -DskipTests
```

---

## 📁 Project Structure

```
url-shortener/
├── pom.xml                          # Maven dependencies (Spring Boot 4.0.3)
├── README.md                        # Full documentation
├── SETUP-GUIDE.md                   # This file
├── Dockerfile                       # Docker configuration
│
├── src/main/
│   ├── java/com/urlshortener/
│   │   ├── UrlShortenerApplication.java
│   │   ├── controller/              # REST API endpoints
│   │   ├── service/                 # Business logic
│   │   ├── repository/              # Database access (JPA)
│   │   ├── entity/                  # Database models
│   │   ├── dto/                     # Request/Response objects
│   │   ├── config/                  # Configuration (CORS, Cache, etc.)
│   │   └── exception/               # Error handling
│   │
│   └── resources/
│       ├── application.properties       # Main config
│       ├── application-dev.properties   # Dev config
│       ├── application-prod.properties  # Prod config
│       └── banner.txt                   # Startup banner
│
└── src/test/java/                   # Unit tests
```

---

## 🎯 What's Different from Spring Boot 3?

| Feature | Spring Boot 3 | Spring Boot 4 |
|---------|---------------|---------------|
| Spring Framework | 6.0.x | 6.2.x |
| Jakarta EE | 10 | 11 |
| Java Version | 17+ | 21+ |
| Virtual Threads | Manual | Auto-enabled |
| Observability | Basic | Enhanced |
| Native Compilation | Experimental | Stable |

---

## ✨ Java 21 Features in This Project

- ✅ **Virtual Threads** - Auto-enabled by Spring Boot 4
- ✅ **Pattern Matching** - Enhanced switch (if used in code)
- ✅ **Record Classes** - Can be used for DTOs
- ✅ **Text Blocks** - Multiline SQL/JSON strings
- ✅ **Sequenced Collections** - Ordered collections

---

## 📚 Next Steps

1. ✅ Run the application
2. ✅ Test with Swagger UI
3. ✅ Connect React frontend
4. ✅ Switch to PostgreSQL for production
5. ✅ Deploy to cloud (Heroku, AWS, Railway)
6. ✅ Set up monitoring (Prometheus + Grafana)

---

## 🆘 Get Help

- **Swagger UI:** http://localhost:3000/swagger-ui.html
- **Actuator:** http://localhost:3000/actuator
- **Spring Boot Docs:** https://docs.spring.io/spring-boot/docs/4.0.x/
- **Java 21 Docs:** https://openjdk.org/projects/jdk/21/

---

**Built with Spring Boot 4.0.3 + Java 21** 🚀

For detailed documentation, see **README.md**
