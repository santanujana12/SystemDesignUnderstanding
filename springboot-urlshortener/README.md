# URL Shortener - Spring Boot 4.0.3 + Java 21

A production-grade URL shortener built with **Spring Boot 4.0.3** and **Java 21**, featuring analytics, caching, rate limiting, and scheduled cleanup.

## 🚀 What's New in This Version

- ✅ **Spring Boot 4.0.3** - Latest Spring Boot release
- ✅ **Java 21** - Latest LTS Java version with modern features
- ✅ **Virtual Threads** - Improved performance (Project Loom)
- ✅ **Pattern Matching** - Modern Java syntax
- ✅ **Records** - Immutable data carriers
- ✅ **Actuator** - Production-ready metrics and health checks

## 📋 Prerequisites

- **Java 21** or higher ([Download](https://adoptium.net/))
- **Maven 3.9+**
- (Optional) IDE: IntelliJ IDEA, Eclipse, or VS Code

## 🛠️ Tech Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 4.0.3 | Application framework |
| Java | 21 (LTS) | Programming language |
| Spring Data JPA | 4.0.3 | Database access |
| H2 Database | Latest | Embedded database |
| Caffeine | 3.1.8 | In-memory cache |
| Bucket4j | 8.10.1 | Rate limiting |
| Springdoc OpenAPI | 2.6.0 | API documentation |
| Lombok | Latest | Reduces boilerplate |

## 📦 Quick Start

### 1. Check Java Version

```bash
java -version
# Should show: openjdk version "21" or higher
```

If not installed:
```bash
# macOS (Homebrew)
brew install openjdk@21

# Ubuntu/Debian
sudo apt install openjdk-21-jdk

# Windows
# Download from https://adoptium.net/
```

### 2. Build the Project

```bash
cd url-shortener
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR:
```bash
java -jar target/url-shortener-1.0.0.jar
```

✅ Application starts on **http://localhost:3000**

## 📖 API Documentation

Once running, visit:
- **Swagger UI**: http://localhost:3000/swagger-ui.html
- **API Docs**: http://localhost:3000/v3/api-docs
- **H2 Console**: http://localhost:3000/h2-console
- **Health Check**: http://localhost:3000/actuator/health
- **Metrics**: http://localhost:3000/actuator/metrics

### H2 Database Console
- JDBC URL: `jdbc:h2:file:./data/urlshortener`
- Username: `sa`
- Password: (leave empty)

## 🔌 API Endpoints

### POST /shorten
Create a shortened URL

**Request:**
```json
{
  "url": "https://example.com/very/long/url",
  "expiresInDays": 7
}
```

**Response:**
```json
{
  "shortenedUrl": "http://localhost:3000/abc123"
}
```

**cURL:**
```bash
curl -X POST http://localhost:3000/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://google.com","expiresInDays":7}'
```

### GET /{code}
Redirect to original URL (tracks analytics)

**Example:** http://localhost:3000/abc123

**cURL:**
```bash
curl -L http://localhost:3000/abc123
```

### GET /analytics/{code}
Get analytics for a shortened URL

**Response:**
```json
{
  "url": "https://example.com/very/long/url",
  "created_at": "2026-02-22T10:30:00",
  "total_clicks": 42,
  "clicks_by_day": [
    { "date": "2026-02-22", "clicks": 15 }
  ],
  "top_referrers": [
    { "referrer": "twitter.com", "clicks": 20 }
  ]
}
```

**cURL:**
```bash
curl http://localhost:3000/analytics/abc123
```

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=3000

# Application
app.base-url=http://localhost:3000
app.default-expiry-days=1
app.code-length=6

# CORS
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000

# Rate Limiting
app.rate-limit.shorten.capacity=10
app.rate-limit.shorten.refill-duration=PT15M
app.rate-limit.redirect.capacity=30
app.rate-limit.redirect.refill-duration=PT1M

# Cleanup (cron format)
app.cleanup.cron=0 0 0 * * ?
```

## 🏗️ Project Structure

```
src/main/java/com/urlshortener/
├── UrlShortenerApplication.java    # Main application
├── config/
│   ├── CacheConfig.java            # Caffeine cache
│   ├── RateLimitInterceptor.java   # Bucket4j rate limiting
│   └── WebConfig.java              # CORS & async
├── controller/
│   └── UrlController.java          # REST endpoints
├── dto/
│   ├── ShortenUrlRequest.java      # Request DTO
│   ├── ShortenUrlResponse.java     # Response DTO
│   ├── AnalyticsResponse.java      # Analytics DTO
│   └── ErrorResponse.java          # Error DTO
├── entity/
│   ├── Url.java                    # URL entity (JPA)
│   └── Analytics.java              # Analytics entity (JPA)
├── exception/
│   ├── UrlNotFoundException.java   # Custom exceptions
│   └── GlobalExceptionHandler.java # Global error handling
├── repository/
│   ├── UrlRepository.java          # URL repository (JPA)
│   └── AnalyticsRepository.java    # Analytics repository (JPA)
└── service/
    ├── UrlService.java             # URL business logic
    ├── AnalyticsService.java       # Analytics + tracking
    └── CleanupService.java         # Scheduled cleanup
```

## 🧪 Running Tests

```bash
mvn test
```

## 🐳 Docker Deployment

### Create Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/url-shortener-1.0.0.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and Run

```bash
# Build the application
mvn clean package

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
    build: .
    ports:
      - "3000:3000"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - APP_BASE_URL=https://yourdomain.com
    volumes:
      - ./data:/app/data
```

## 🔄 Switching to PostgreSQL

### 1. Add PostgreSQL Dependency

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

### 2. Update Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Create Database

```bash
createdb urlshortener
```

## 📊 Monitoring & Observability

### Health Check
```bash
curl http://localhost:3000/actuator/health
```

### Metrics
```bash
curl http://localhost:3000/actuator/metrics
curl http://localhost:3000/actuator/metrics/jvm.memory.used
curl http://localhost:3000/actuator/metrics/http.server.requests
```

### Prometheus Export
```bash
curl http://localhost:3000/actuator/prometheus
```

## 🚀 Deployment

### Heroku

```bash
heroku create url-shortener
heroku addons:create heroku-postgresql:mini
git push heroku main
```

### AWS Elastic Beanstalk

```bash
eb init -p corretto-21 url-shortener
eb create url-shortener-env
eb deploy
```

### Railway

```bash
railway login
railway init
railway up
```

## 🔐 Production Checklist

- [ ] Change H2 to PostgreSQL
- [ ] Set `spring.h2.console.enabled=false`
- [ ] Configure proper CORS origins
- [ ] Set strong database password
- [ ] Enable HTTPS
- [ ] Configure proper logging
- [ ] Set up monitoring (Prometheus/Grafana)
- [ ] Enable rate limiting
- [ ] Configure backup strategy

## 🤝 Connecting to React Frontend

Your React frontend works without any changes:

```javascript
// .env
VITE_API_URL=http://localhost:3000

// No code changes needed!
```

Make sure CORS is configured in `application.properties`:
```properties
app.cors.allowed-origins=http://localhost:5173,https://yourfrontend.com
```

## 🆕 Java 21 Features Used

- **Virtual Threads** - Improved concurrency (Spring Boot 4 auto-enables)
- **Pattern Matching** - Enhanced switch statements
- **Record Classes** - Immutable DTOs (can be used instead of Lombok)
- **Text Blocks** - Multiline strings
- **Sealed Classes** - Restricted inheritance

## 📝 Spring Boot 4 Features

- **Spring Framework 6.2** - Latest Spring features
- **Jakarta EE 11** - Latest Jakarta specifications
- **Native Compilation** - GraalVM support
- **Observability** - Enhanced metrics and tracing
- **Virtual Threads** - Project Loom integration

## 🐛 Troubleshooting

### Java Version Error
```bash
# Check Java version
java -version

# Should show Java 21 or higher
# If not, install Java 21 from https://adoptium.net/
```

### Port Already in Use
```properties
# Change port in application.properties
server.port=8080
```

### Database Locked
```bash
# Stop application and delete database
rm -rf data/
```

### Maven Build Fails
```bash
# Clean and rebuild
mvn clean install -U
```

## 📚 Additional Resources

- [Spring Boot 4 Documentation](https://docs.spring.io/spring-boot/docs/4.0.x/reference/)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

## 📜 License

MIT

## 🙋 Support

- **Swagger UI**: http://localhost:3000/swagger-ui.html
- **Health Check**: http://localhost:3000/actuator/health
- **GitHub Issues**: Create an issue for bug reports

---

**Built with Spring Boot 4.0.3 + Java 21** 🚀
