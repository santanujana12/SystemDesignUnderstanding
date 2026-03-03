package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlRequest {

    @NotBlank(message = "URL is required")
    @Pattern(
        regexp = "^(http|https|ftp)://[^\\s]+$",
        message = "Invalid URL format"
    )
    private String url;

    private Integer expiresInDays;
}
