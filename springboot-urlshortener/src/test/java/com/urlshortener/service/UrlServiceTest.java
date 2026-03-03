package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.entity.Url;
import com.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shortenUrl_NewUrl_CreatesShortUrl() {
        // Arrange
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(urlService, "defaultExpiryDays", 1);
        ReflectionTestUtils.setField(urlService, "codeLength", 6);

        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");

        when(urlRepository.findByOriginalUrlAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(urlRepository.existsByCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        String result = urlService.shortenUrl(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("http://localhost:3000/"));
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    void shortenUrl_ExistingUrl_ReturnsExistingCode() {
        // Arrange
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:3000");

        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");

        Url existingUrl = Url.builder()
                .code("abc123")
                .originalUrl("https://example.com")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(urlRepository.findByOriginalUrlAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(existingUrl));

        // Act
        String result = urlService.shortenUrl(request);

        // Assert
        assertEquals("http://localhost:3000/abc123", result);
        verify(urlRepository, never()).save(any(Url.class));
    }

    @Test
    void getOriginalUrl_ValidCode_ReturnsUrl() {
        // Arrange
        Url url = Url.builder()
                .code("abc123")
                .originalUrl("https://example.com")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(urlRepository.findByCodeAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(url));

        // Act
        String result = urlService.getOriginalUrl("abc123");

        // Assert
        assertEquals("https://example.com", result);
    }
}
