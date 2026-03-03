package com.urlshortener.service;

import com.urlshortener.dto.AnalyticsResponse;
import com.urlshortener.entity.Analytics;
import com.urlshortener.entity.Url;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.repository.AnalyticsRepository;
import com.urlshortener.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final UrlRepository urlRepository;

    @Async
    @Transactional
    public void trackClick(String code, HttpServletRequest request) {
        try {
            String ipAddress = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String referrer = request.getHeader("Referer");
            
            if (referrer == null || referrer.isEmpty()) {
                referrer = "direct";
            }

            Analytics analytics = Analytics.builder()
                    .code(code)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .referrer(referrer)
                    .build();

            analyticsRepository.save(analytics);
            log.debug("Analytics tracked for code: {}", code);
            
        } catch (Exception e) {
            log.error("Failed to track analytics for code: {}", code, e);
            // Don't throw - never let analytics break the redirect
        }
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(String code) {
        Url url = urlRepository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));

        long totalClicks = analyticsRepository.countByCode(code);
        
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> clicksByDayData = analyticsRepository.findClicksByDay(code, sevenDaysAgo);
        
        List<AnalyticsResponse.ClicksByDay> clicksByDay = clicksByDayData.stream()
                .map(row -> AnalyticsResponse.ClicksByDay.builder()
                        .date(((Date) row[0]).toLocalDate())
                        .clicks((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        List<Object[]> topReferrersData = analyticsRepository.findTopReferrers(code);
        
        List<AnalyticsResponse.TopReferrer> topReferrers = topReferrersData.stream()
                .limit(5)
                .map(row -> AnalyticsResponse.TopReferrer.builder()
                        .referrer((String) row[0])
                        .clicks((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        return AnalyticsResponse.builder()
                .url(url.getOriginalUrl())
                .createdAt(url.getCreatedAt())
                .totalClicks(totalClicks)
                .clicksByDay(clicksByDay)
                .topReferrers(topReferrers)
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Return first IP if multiple IPs in X-Forwarded-For
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
