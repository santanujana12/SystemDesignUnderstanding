package com.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlResponse {
    
    @JsonProperty("shortenedUrl")
    private String shortenedUrl;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AnalyticsResponse {
    
    private String url;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("total_clicks")
    private Long totalClicks;
    
    @JsonProperty("clicks_by_day")
    private List<ClicksByDay> clicksByDay;
    
    @JsonProperty("top_referrers")
    private List<TopReferrer> topReferrers;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClicksByDay {
        private LocalDate date;
        private Long clicks;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopReferrer {
        private String referrer;
        private Long clicks;
    }
}
