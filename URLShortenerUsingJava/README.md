# URL Shortener - Spring Boot Edition

A production-grade URL shortener built with Spring Boot 3.x, featuring analytics, caching, rate limiting, and scheduled cleanup.

## 🚀 Features

- ✅ **URL Shortening** - Convert long URLs to short codes
- ✅ **Analytics Tracking** - Track clicks, referrers, and time-series data
- ✅ **Caching** - Caffeine in-memory cache for fast redirects
- ✅ **Rate Limiting** - Bucket4j token bucket algorithm
- ✅ **Scheduled Cleanup** - Automatic deletion of expired URLs
- ✅ **CORS Support** - Pre-configured for frontend integration
- ✅ **Swagger UI** - Interactive API documentation
- ✅ **Best Practices** - Layered architecture, DTOs, validation, exception handling

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- (Optional) IDE: IntelliJ IDEA, Eclipse, or VS Code

## 🛠️ Tech Stack

- **Spring Boot 3.2.2** - Application framework
- **Spring Data JPA** - Database access
- **H2 Database** - Embedded database (like SQLite)
- **Caffeine** - In-memory cache
- **Bucket4j** - Rate limiting
- **Lombok** - Reduces boilerplate
- **Springdoc OpenAPI** - API documentation

## 📦 Installation

### 1. Clone or extract the project

```bash
cd url-shortener
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/url-shortener-1.0.0.jar
```

The application will start on **http://localhost:3000**

## 📖 API Documentation

Once running, visit:
- **Swagger UI**: http://localhost:3000/swagger-ui.html
- **H2 Console**: http://localhost:3000/h2-console
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

### GET /{code}
Redirect to original URL (tracks analytics)

**Example:** http://localhost:3000/abc123

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

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=3000

# Base URL for shortened links
app.base-url=http://localhost:3000

# Default expiry (days)
app.default-expiry-days=1

# CORS origins
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000

# Rate limiting
app.rate-limit.shorten.capacity=10
app.rate-limit.shorten.refill-duration=PT15M
app.rate-limit.redirect.capacity=30
app.rate-limit.redirect.refill-duration=PT1M

# Cleanup schedule (cron)
app.cleanup.cron=0 0 0 * * ?
```

## 🏗️ Project Structure

```
src/main/java/com/urlshortener/
├── UrlShortenerApplication.java    # Main application
├── config/
│   ├── CacheConfig.java            # Cache configuration
│   ├── RateLimitInterceptor.java   # Rate limiting
│   └── WebConfig.java              # CORS & web config
├── controller/
│   └── UrlController.java          # REST endpoints
├── dto/
│   ├── ShortenUrlRequest.java      # Request DTO
│   ├── ShortenUrlResponse.java     # Response DTO
│   ├── AnalyticsResponse.java      # Analytics DTO
│   └── ErrorResponse.java          # Error DTO
├── entity/
│   ├── Url.java                    # URL entity
│   └── Analytics.java              # Analytics entity
├── exception/
│   ├── UrlNotFoundException.java   # Custom exceptions
│   └── GlobalExceptionHandler.java # Exception handling
├── repository/
│   ├── UrlRepository.java          # URL repository
│   └── AnalyticsRepository.java    # Analytics repository
└── service/
    ├── UrlService.java             # URL business logic
    ├── AnalyticsService.java       # Analytics logic
    └── CleanupService.java         # Scheduled cleanup
```

## 🧪 Testing with cURL

**Shorten a URL:**
```bash
curl -X POST http://localhost:3000/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://google.com"}'
```

**Click the shortened URL:**
```bash
curl -L http://localhost:3000/abc123
```

**Get analytics:**
```bash
curl http://localhost:3000/analytics/abc123
```

## 🔄 Switching to PostgreSQL

To use PostgreSQL instead of H2:

1. Add dependency in `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## 📊 Monitoring

- **Logs**: Check console or `logs/` directory
- **Cache stats**: Enabled via Caffeine configuration
- **Health**: GET http://localhost:3000/health

## 🐳 Docker Deployment

Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/url-shortener-1.0.0.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
mvn clean package
docker build -t url-shortener .
docker run -p 3000:3000 url-shortener
```

## 📝 Best Practices Implemented

- ✅ Layered architecture (Controller → Service → Repository)
- ✅ DTOs for request/response separation
- ✅ Bean Validation for input validation
- ✅ Global exception handling
- ✅ Async analytics tracking (non-blocking)
- ✅ Caching for performance
- ✅ Rate limiting for security
- ✅ Scheduled tasks for maintenance
- ✅ Lombok for code reduction
- ✅ Swagger for API docs
- ✅ Proper logging
- ✅ Transaction management
- ✅ Index optimization

## 🤝 Connecting to React Frontend

Your React frontend will work with these endpoints:

```javascript
const API_BASE = 'http://localhost:3000';

// Shorten URL
fetch(`${API_BASE}/shorten`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ url: 'https://example.com' })
});

// Get analytics
fetch(`${API_BASE}/analytics/abc123`);
```

CORS is pre-configured for `http://localhost:5173` (Vite dev server).

## 📜 License

MIT

## 🙋 Support

For issues or questions, check the Swagger UI documentation at http://localhost:3000/swagger-ui.html
