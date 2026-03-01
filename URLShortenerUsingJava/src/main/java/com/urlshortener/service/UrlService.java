package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.entity.Url;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final Random random = new Random();

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.default-expiry-days:1}")
    private int defaultExpiryDays;

    @Value("${app.code-length:6}")
    private int codeLength;

    @Transactional
    public String shortenUrl(ShortenUrlRequest request) {
        String originalUrl = request.getUrl();
        int expiryDays = request.getExpiresInDays() != null ? 
                         request.getExpiresInDays() : defaultExpiryDays;

        // Check if URL already exists and is not expired
        return urlRepository.findByOriginalUrlAndExpiresAtAfter(originalUrl, LocalDateTime.now())
                .map(url -> {
                    log.debug("URL already exists: {}", url.getCode());
                    return baseUrl + "/" + url.getCode();
                })
                .orElseGet(() -> {
                    String code = generateUniqueCode();
                    LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiryDays);

                    Url url = Url.builder()
                            .code(code)
                            .originalUrl(originalUrl)
                            .expiresAt(expiresAt)
                            .build();

                    urlRepository.save(url);
                    log.info("Created short URL: {} -> {}", code, originalUrl);

                    return baseUrl + "/" + code;
                });
    }

    @Cacheable(value = "urls", key = "#code")
    @Transactional(readOnly = true)
    public String getOriginalUrl(String code) {
        log.debug("Fetching URL for code: {}", code);
        
        return urlRepository.findByCodeAndExpiresAtAfter(code, LocalDateTime.now())
                .map(Url::getOriginalUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL not found or has expired"));
    }

    private String generateUniqueCode() {
        int maxAttempts = 5;
        int attempts = 0;

        while (attempts < maxAttempts) {
            String code = generateRandomCode();
            
            if (!urlRepository.existsByCode(code)) {
                return code;
            }
            
            attempts++;
            log.warn("Code collision detected, attempt {}/{}", attempts, maxAttempts);
        }

        throw new RuntimeException("Failed to generate unique code after " + maxAttempts + " attempts");
    }

    private String generateRandomCode() {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder(codeLength);
        
        for (int i = 0; i < codeLength; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
}
