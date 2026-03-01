package com.urlshortener.service;

import com.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupService {

    private final UrlRepository urlRepository;

    @Scheduled(cron = "${app.cleanup.cron:0 0 0 * * ?}") // Daily at midnight
    @Transactional
    public void cleanupExpiredUrls() {
        log.info("Starting cleanup of expired URLs");
        
        int deletedCount = urlRepository.deleteExpiredUrls(LocalDateTime.now());
        
        log.info("Cleanup completed - {} expired URLs deleted", deletedCount);
    }
}
