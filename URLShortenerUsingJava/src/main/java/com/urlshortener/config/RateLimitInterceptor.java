package com.urlshortener.config;

import com.urlshortener.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> shortenBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> redirectBuckets = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.shorten.capacity:10}")
    private long shortenCapacity;

    @Value("${app.rate-limit.shorten.refill-tokens:10}")
    private long shortenRefillTokens;

    @Value("${app.rate-limit.shorten.refill-duration:PT15M}")
    private Duration shortenRefillDuration;

    @Value("${app.rate-limit.redirect.capacity:30}")
    private long redirectCapacity;

    @Value("${app.rate-limit.redirect.refill-tokens:30}")
    private long redirectRefillTokens;

    @Value("${app.rate-limit.redirect.refill-duration:PT1M}")
    private Duration redirectRefillDuration;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        if (path.equals("/shorten") && request.getMethod().equals("POST")) {
            Bucket bucket = shortenBuckets.computeIfAbsent(clientIp, k -> createShortenBucket());
            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceededException("Too many shorten requests. Please try again later.");
            }
        } else if (path.matches("/[a-zA-Z0-9]+") && request.getMethod().equals("GET")) {
            Bucket bucket = redirectBuckets.computeIfAbsent(clientIp, k -> createRedirectBucket());
            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceededException("Too many redirect requests. Please try again later.");
            }
        }

        return true;
    }

    private Bucket createShortenBucket() {
        Bandwidth limit = Bandwidth.classic(shortenCapacity, 
                Refill.intervally(shortenRefillTokens, shortenRefillDuration));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createRedirectBucket() {
        Bandwidth limit = Bandwidth.classic(redirectCapacity, 
                Refill.intervally(redirectRefillTokens, redirectRefillDuration));
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
