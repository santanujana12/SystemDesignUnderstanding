package com.urlshortener.controller;

import com.urlshortener.dto.AnalyticsResponse;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.service.AnalyticsService;
import com.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "URL Shortener", description = "URL shortening and analytics APIs")
public class UrlController {

    private final UrlService urlService;
    private final AnalyticsService analyticsService;

    @PostMapping("/shorten")
    @Operation(summary = "Shorten a URL", description = "Creates a shortened URL for the given original URL")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        log.info("Shorten URL request: {}", request.getUrl());
        
        String shortenedUrl = urlService.shortenUrl(request);
        
        ShortenUrlResponse response = ShortenUrlResponse.builder()
                .shortenedUrl(shortenedUrl)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Redirect to original URL", description = "Redirects to the original URL and tracks analytics")
    public void redirect(
            @PathVariable String code,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        log.info("Redirect request for code: {}", code);
        
        // Track analytics asynchronously (non-blocking)
        analyticsService.trackClick(code, request);
        
        // Get original URL (from cache or DB)
        String originalUrl = urlService.getOriginalUrl(code);
        
        // Perform redirect
        response.sendRedirect(originalUrl);
    }

    @GetMapping("/analytics/{code}")
    @Operation(summary = "Get analytics", description = "Retrieves click analytics for a shortened URL")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable String code) {
        log.info("Analytics request for code: {}", code);
        
        AnalyticsResponse analytics = analyticsService.getAnalytics(code);
        
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns service health status")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
